package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.*;
import com.example.documentsmanagement.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.stream.Collectors;

@Service
@Transactional
public class RequestDocumentService {

    private final RequestDocumentRepository repository;
    private final DocumentRepository documentRepository;
    private final BorrowerRepository borrowerRepository;
    private final LibrarianRepository librarianRepository;

    public RequestDocumentService(RequestDocumentRepository repository,
                                  DocumentRepository documentRepository,
                                  BorrowerRepository borrowerRepository,
                                  LibrarianRepository librarianRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
        this.borrowerRepository = borrowerRepository;
        this.librarianRepository = librarianRepository;
    }

    // Lấy toàn bộ danh sách
    public List<RequestDocument> findAll() {
        List<RequestDocument> list = repository.findAll();
        list.forEach(rd -> org.hibernate.Hibernate.initialize(rd.getDocuments()));

        return list;
    }

    // Tìm theo ID
    public Optional<RequestDocument> findById(Long id) {
        return repository.findById(id);
    }

    private void hydrateDocuments(RequestDocument requestDocument) {
        if (requestDocument.getDocuments() != null) {
            List<Long> docIds = requestDocument.getDocuments().stream()
                    .map(Document::getIdDocument)
                    .collect(Collectors.toList());

            List<Document> fullDocuments = documentRepository.findAllById(docIds);
            requestDocument.setDocuments(fullDocuments);
        }
    }

    public List<RequestDocument> findHistoryByDocumentId(Long documentId) {
        return repository.findHistoryByDocumentId(documentId);
    }

    // Thêm mới yêu cầu mượn tài liệu
    public RequestDocument create(RequestDocument requestDocument) {
        requestDocument.setIdRequestDocument(null);

        // --- Xử lý Borrower ---
        Borrower borrowerToSave = null;
        if (requestDocument.getBorrower() != null && requestDocument.getBorrower().getEmployeeCode() != null) {

            String employeeCode = requestDocument.getBorrower().getEmployeeCode();
            Borrower borrowerDataFromRequest = requestDocument.getBorrower();

            Optional<Borrower> existingByCode = borrowerRepository.findByEmployeeCode(employeeCode);

            if (existingByCode.isPresent()) {
                borrowerToSave = existingByCode.get();
                borrowerToSave.setFullName(borrowerDataFromRequest.getFullName());
                borrowerToSave.setDepartment(borrowerDataFromRequest.getDepartment());
                borrowerToSave.setPosition(borrowerDataFromRequest.getPosition());
                borrowerToSave.setIdCardNumber(borrowerDataFromRequest.getIdCardNumber());
                borrowerToSave.setPhoneNumber(borrowerDataFromRequest.getPhoneNumber());

                System.out.println("Đã cập nhật Borrower với mã: " + employeeCode);
            } else {
                borrowerDataFromRequest.setId_borrower(null);
                borrowerToSave = borrowerDataFromRequest;

                System.out.println("Đã tạo Borrower mới với mã: " + employeeCode);
            }

            borrowerToSave = borrowerRepository.save(borrowerToSave);

        } else {
            throw new IllegalArgumentException("Thiếu thông tin người mượn (Borrower) hoặc Mã cán bộ (Employee Code).");
        }

        hydrateDocuments(requestDocument);

        if (requestDocument.getLibrarian() != null && requestDocument.getLibrarian().getIdLibrarian() != null) {
            Long librarianId = requestDocument.getLibrarian().getIdLibrarian();
            Librarian managedLibrarian = librarianRepository.findById(librarianId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Thủ thư (Librarian) với ID: " + librarianId));
            requestDocument.setLibrarian(managedLibrarian);
        } else {
            throw new IllegalArgumentException("Thiếu thông tin Thủ thư (Librarian).");
        }

        // --- Cập nhật trạng thái document ---
        boolean isOriginalBorrow = "original".equalsIgnoreCase(requestDocument.getCopyType());

        if (isOriginalBorrow) {
            if (requestDocument.getDocuments() != null) {
                for (Document doc : requestDocument.getDocuments()) {
                    doc.setStatus("borrowed");
                    documentRepository.save(doc);
                }
            }
        }

        requestDocument.setBorrower(borrowerToSave);
        requestDocument.setNote(requestDocument.getNote());

        return repository.save(requestDocument);
    }

    // Cập nhật yêu cầu mượn
    public Optional<RequestDocument> update(Long id, RequestDocument incoming) {
        return repository.findById(id).map(existing -> {
            boolean isCompleting = (incoming.getReturnDate() != null && existing.getReturnDate() == null);
            if (isCompleting) {
                String copyType = existing.getCopyType();

                if ("original".equalsIgnoreCase(copyType)) {
                    Hibernate.initialize(existing.getDocuments());

                    if (existing.getDocuments() != null) {
                        for (Document doc : existing.getDocuments()) {
                            doc.setStatus("available");
                            documentRepository.save(doc);
                        }
                    }
                }
            }

            if (incoming.getReturnDeadline() != null) existing.setReturnDeadline(incoming.getReturnDeadline());
            existing.setExtensionCount(incoming.getExtensionCount());
            if (incoming.getReturnDate() != null) existing.setReturnDate(incoming.getReturnDate());
            if (incoming.getSigner() != null) existing.setSigner(incoming.getSigner());
            if (incoming.getAttachmentPath() != null) existing.setAttachmentPath(incoming.getAttachmentPath());
            if (incoming.getCopyType() != null) existing.setCopyType(incoming.getCopyType());

            if (incoming.getBorrower() != null) {
                Borrower borrowerToSet = null;
                if (incoming.getBorrower().getId_borrower() != null) {
                    borrowerToSet = borrowerRepository.findById(incoming.getBorrower().getId_borrower())
                            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Borrower ID: " + incoming.getBorrower().getId_borrower()));
                }

                if (borrowerToSet != null) {
                    existing.setBorrower(borrowerToSet);
                }
            }

            if (incoming.getNote() != null || existing.getNote() != null) {
                existing.setNote(incoming.getNote());
            }

            Hibernate.initialize(existing.getDocuments());
            return repository.save(existing);
        });
    }

    // Xóa yêu cầu mượn
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Tìm kiếm theo mã số tài liệu hoặc người ký
    public List<RequestDocument> search(String keyword) {
        return repository.searchByKeyword(keyword == null ? "" : keyword);
    }

    // Đếm tổng số yêu cầu
    public long count() {
        return repository.count();
    }


    public List<RequestDocument> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public List<RequestDocument> findByMonth(int month, int year) {
        return repository.findByMonth(month, year);
    }

    public List<RequestDocument> findByYear(int year) {
        return repository.findByYear(year);
    }

    public List<RequestDocument> findByDateRange(LocalDate start, LocalDate end) {
        return repository.findByDateRange(start, end);
    }

    // ======================= HÀM XUẤT FILE EXCEL =======================
    public void exportToExcel(HttpServletResponse response,
                              LocalDate startDate,
                              LocalDate endDate,
                              String type) throws IOException {

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=request_documents.xlsx";
        response.setHeader(headerKey, headerValue);

        // === Lấy dữ liệu tương ứng với loại báo cáo ===
        List<RequestDocument> list;
        if ("day".equalsIgnoreCase(type)) {
            list = findByDate(startDate);
        } else if ("month".equalsIgnoreCase(type)) {
            list = findByMonth(startDate.getMonthValue(), startDate.getYear());
        } else if ("year".equalsIgnoreCase(type)) {
            list = findByYear(startDate.getYear());
        } else if ("range".equalsIgnoreCase(type)) {
            list = findByDateRange(startDate, endDate);
        } else {
            list = findAll();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo mượn hồ sơ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        CreationHelper creationHelper = workbook.getCreationHelper();

        // === Font và Style ===
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setFontName("Times New Roman");

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setFontName("Times New Roman");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        Font contentFont = workbook.createFont();
        contentFont.setFontHeightInPoints((short) 13);
        contentFont.setFontName("Times New Roman");

        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setFont(contentFont);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(contentStyle);
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        // === Cột ===
        String[] columns = {
                "STT", "Ngày mượn", "Ngày trả", "Hạn trả", "Loại tài liệu",
                "Người ký phiếu mượn", "Họ và tên người mượn", "Họ và tên thủ thư", "Ghi chú"
        };

        // === Tiêu đề lớn ===
        Row titleRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);

        String titleText;
        switch (type.toLowerCase()) {
            case "day" -> titleText = "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ NGÀY " +
                    startDate.format(formatter);
            case "month" -> titleText = "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ THÁNG " +
                    startDate.getMonthValue() + "/" + startDate.getYear();
            case "year" -> titleText = "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ NĂM " +
                    startDate.getYear();
            case "range" -> titleText = "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ TỪ NGÀY " +
                    startDate.format(formatter) + " ĐẾN NGÀY " + endDate.format(formatter);
            default -> titleText = "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ - TẤT CẢ DỮ LIỆU";
        }

        // 🔠 Viết hoa toàn bộ bằng chuẩn tiếng Việt
        titleCell.setCellValue(titleText.toUpperCase(java.util.Locale.forLanguageTag("vi")));

        // === Header cột ===
        Row headerRow = sheet.createRow(2);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // === Ghi dữ liệu ===
        int rowNum = 3;
        int stt = 1;
        for (RequestDocument doc : list) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt++);
            cell0.setCellStyle(contentStyle);

            Cell cell1 = row.createCell(1);
            if (doc.getBorrowDate() != null) {
                cell1.setCellValue(java.sql.Date.valueOf(doc.getBorrowDate()));
                cell1.setCellStyle(dateStyle);
            } else cell1.setCellStyle(contentStyle);

            Cell cell2 = row.createCell(2);
            if (doc.getReturnDate() != null) {
                cell2.setCellValue(java.sql.Date.valueOf(doc.getReturnDate()));
                cell2.setCellStyle(dateStyle);
            } else cell2.setCellStyle(contentStyle);

            Cell cell3 = row.createCell(3);
            if (doc.getReturnDeadline() != null) {
                cell3.setCellValue(java.sql.Date.valueOf(doc.getReturnDeadline()));
                cell3.setCellStyle(dateStyle);
            } else cell3.setCellStyle(contentStyle);

            String copyType = doc.getCopyType();
            if ("original".equalsIgnoreCase(copyType)) copyType = "Bản gốc";
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(copyType != null ? copyType : "");
            cell4.setCellStyle(contentStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(doc.getSigner() != null ? doc.getSigner() : "");
            cell5.setCellStyle(contentStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(doc.getBorrower() != null ? doc.getBorrower().getFullName() : "");
            cell6.setCellStyle(contentStyle);

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(doc.getLibrarian() != null ? doc.getLibrarian().getFullName() : "");
            cell7.setCellStyle(contentStyle);

            Cell cell8 = row.createCell(8);
            cell8.setCellValue(doc.getNote() != null ? doc.getNote() : "");
            cell8.setCellStyle(contentStyle);
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}