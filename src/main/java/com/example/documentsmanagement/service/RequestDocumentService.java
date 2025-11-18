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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils; 

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
    public RequestDocument create(RequestDocument requestDocument, MultipartFile file) {
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

        // Lưu file đính kèm
        if (file != null && !file.isEmpty()) {
            try {
                String folder = "uploads/";
                Files.createDirectories(Paths.get(folder));
                
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                
                String fileName = System.currentTimeMillis() + "_" + originalFileName;
                Path path = Paths.get(folder + fileName);
                
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                requestDocument.setAttachmentPath(fileName); 

            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu file đính kèm: " + e.getMessage());
            }
        } else {
            requestDocument.setAttachmentPath(null);
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
    @Transactional
    public void delete(Long id) {
        RequestDocument requestToDelete = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));

        boolean isOriginal = "original".equalsIgnoreCase(requestToDelete.getCopyType());
        boolean isActiveLoan = requestToDelete.getReturnDate() == null;

        if (isOriginal && isActiveLoan) {
            Hibernate.initialize(requestToDelete.getDocuments());
            List<Document> documents = requestToDelete.getDocuments();

            if (documents != null && !documents.isEmpty()) {
                for (Document doc : documents) {
                    doc.setStatus("available");
                    documentRepository.save(doc); 
                }
            }
        }

        repository.delete(requestToDelete);
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


    // ================================
    // Tạo Workbook cho Request Document
    // ================================
    private Workbook buildWorkbookForReport(List<RequestDocument> list,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            String type) {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo mượn hồ sơ");
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
        contentFont.setFontHeightInPoints((short) 14);
        contentFont.setFontName("Times New Roman");

        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setFont(contentFont);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setWrapText(true);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(contentStyle);
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        // === Tiêu đề cột ===
        String[] columns = {
                "STT", "Ngày mượn", "Ngày trả", "Hạn trả", "Loại tài liệu",
                "Người ký phiếu mượn", "Người mượn", "Thủ thư phụ trách", "Ghi chú"
        };

        // === Tiêu đề báo cáo ===
        Row titleRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);

        DateTimeFormatter viewFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String titleText = switch (type.toLowerCase()) {
            case "day" -> "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ NGÀY " + startDate.format(viewFormatter);
            case "month" -> "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ THÁNG " + startDate.getMonthValue() + "/" + startDate.getYear();
            case "year" -> "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ NĂM " + startDate.getYear();
            case "range" -> "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ TỪ " +
                    startDate.format(viewFormatter) + " ĐẾN " + endDate.format(viewFormatter);
            default -> "BÁO CÁO HOẠT ĐỘNG MƯỢN HỒ SƠ (TẤT CẢ DỮ LIỆU)";
        };

        titleCell.setCellValue(titleText);

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
            int col = 0;

            Cell cell = row.createCell(col++);
            cell.setCellValue(stt++);
            cell.setCellStyle(contentStyle);

            // Ngày mượn
            cell = row.createCell(col++);
            if (doc.getBorrowDate() != null) {
                cell.setCellValue(java.sql.Date.valueOf(doc.getBorrowDate()));
                cell.setCellStyle(dateStyle);
            } else cell.setCellStyle(contentStyle);

            // Ngày trả
            cell = row.createCell(col++);
            if (doc.getReturnDate() != null) {
                cell.setCellValue(java.sql.Date.valueOf(doc.getReturnDate()));
                cell.setCellStyle(dateStyle);
            } else cell.setCellStyle(contentStyle);

            // Hạn trả
            cell = row.createCell(col++);
            if (doc.getReturnDeadline() != null) {
                cell.setCellValue(java.sql.Date.valueOf(doc.getReturnDeadline()));
                cell.setCellStyle(dateStyle);
            } else cell.setCellStyle(contentStyle);

            // Loại tài liệu
            String copyType = doc.getCopyType();
            if (copyType != null) {
                if (copyType.equalsIgnoreCase("original")) copyType = "Bản gốc";
                else if (copyType.equalsIgnoreCase("copy") || copyType.equalsIgnoreCase("photo")) copyType = "Bản photo";
            }

            cell = row.createCell(col++);
            cell.setCellValue(copyType != null ? copyType : "");
            cell.setCellStyle(contentStyle);

            // Người ký
            cell = row.createCell(col++);
            cell.setCellValue(doc.getSigner() != null ? doc.getSigner() : "");
            cell.setCellStyle(contentStyle);

            // Người mượn
            cell = row.createCell(col++);
            cell.setCellValue(doc.getBorrower() != null ? doc.getBorrower().getFullName() : "");
            cell.setCellStyle(contentStyle);

            // Thủ thư
            cell = row.createCell(col++);
            cell.setCellValue(doc.getLibrarian() != null ? doc.getLibrarian().getFullName() : "");
            cell.setCellStyle(contentStyle);

            // Ghi chú
            cell = row.createCell(col++);
            cell.setCellValue(doc.getNote() != null ? doc.getNote() : "");
            cell.setCellStyle(contentStyle);
        }

        // === Auto-width ===
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    public String previewExcel(LocalDate startDate,
                               LocalDate endDate,
                               String type) {

        List<RequestDocument> list = switch (type.toLowerCase()) {
            case "day" -> findByDate(startDate);
            case "month" -> findByMonth(startDate.getMonthValue(), startDate.getYear());
            case "year" -> findByYear(startDate.getYear());
            case "range" -> findByDateRange(startDate, endDate);
            default -> findAll();
        };

        try {
            Workbook workbook = buildWorkbookForReport(list, startDate, endDate, type);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            return Base64.getEncoder().encodeToString(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo file xem trước Excel", e);
        }
    }

    public String previewExcelAsHtml(LocalDate startDate, LocalDate endDate, String type) throws IOException {
        // Lấy dữ liệu giống exportToExcel
        List<RequestDocument> list = switch (type.toLowerCase()) {
            case "day" -> findByDate(startDate);
            case "month" -> findByMonth(startDate.getMonthValue(), startDate.getYear());
            case "year" -> findByYear(startDate.getYear());
            case "range" -> findByDateRange(startDate, endDate);
            default -> findAll();
        };

        // Build workbook (bạn đã có phương thức này)
        Workbook workbook = buildWorkbookForReport(list, startDate, endDate, type);

        // Convert workbook -> HTML
        String html = convertWorkbookToHtml(workbook);

        workbook.close();
        return html;
    }


    private String convertWorkbookToHtml(Workbook workbook) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!doctype html><html><head><meta charset='utf-8'>");
        sb.append("<title>Báo cáo xem trước Excel</title>");
        sb.append("<style>");
        sb.append("body{font-family:'Times New Roman', serif; padding:12px;font-size:14px;} ");
        sb.append("table{border-collapse: collapse; width: 100%; margin-top:16px;} ");
        sb.append("th, td{border:1px solid #000; padding:6px; vertical-align:middle;} ");
        sb.append("th{background:#f2f2f2; font-weight: bold; text-align:center;} ");
        sb.append("</style></head><body>");

        DataFormatter fm = new DataFormatter();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            sb.append("<p>Không có dữ liệu</p></body></html>");
            return sb.toString();
        }

        // === Lấy danh sách cell merge ===
        List<CellRangeAddress> merges = sheet.getMergedRegions();

        sb.append("<table>");

        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();

        for (int r = firstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            sb.append("<tr>");

            int lastCol = row.getLastCellNum();
            if (lastCol < 0) lastCol = 1;

            for (int c = 0; c < lastCol; c++) {

                CellRangeAddress merged = getMergedRegion(merges, r, c);

                if (merged != null) {
                    if (merged.getFirstColumn() != c || merged.getFirstRow() != r) continue;

                    int rowspan = merged.getLastRow() - merged.getFirstRow() + 1;
                    int colspan = merged.getLastColumn() - merged.getFirstColumn() + 1;

                    Cell cell = row.getCell(c);
                    String text = cell == null ? "" : fm.formatCellValue(cell, evaluator);

                    sb.append("<td rowspan='").append(rowspan)
                            .append("' colspan='").append(colspan)
                            .append("' style=\"font-family:'Times New Roman';")
                            .append(getHtmlStyle(cell))
                            .append("\">")
                            .append(escapeHtml(text))
                            .append("</td>");
                } else {
                    Cell cell = row.getCell(c);
                    String text = cell == null ? "" : fm.formatCellValue(cell, evaluator);

                    sb.append("<td style=\"font-family:'Times New Roman';")
                            .append(getHtmlStyle(cell))
                            .append("\">")
                            .append(escapeHtml(text))
                            .append("</td>");
                }
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("</body></html>");

        return sb.toString();
    }

    /**
     * Lấy merge region (nếu ô thuộc vùng merge)
     */
    private CellRangeAddress getMergedRegion(List<CellRangeAddress> merges, int row, int col) {
        for (CellRangeAddress region : merges) {
            if (region.isInRange(row, col)) return region;
        }
        return null;
    }

    /**
     * Convert style Excel → HTML inline
     */
    private String getHtmlStyle(Cell cell) {
        if (cell == null || cell.getCellStyle() == null) return "";

        CellStyle style = cell.getCellStyle();
        StringBuilder css = new StringBuilder();

        // ALIGN
        switch (style.getAlignment()) {
            case CENTER -> css.append("text-align:center;");
            case RIGHT -> css.append("text-align:right;");
            default -> css.append("text-align:left;");
        }

        // VERTICAL ALIGN
        switch (style.getVerticalAlignment()) {
            case CENTER -> css.append("vertical-align:middle;");
            case TOP -> css.append("vertical-align:top;");
            case BOTTOM -> css.append("vertical-align:bottom;");
        }

        // FONT
        Font font = cell.getSheet().getWorkbook().getFontAt(style.getFontIndex());
        if (font != null) {
            css.append("font-size:").append(font.getFontHeightInPoints()).append("px;");
            if (font.getBold()) css.append("font-weight:bold;");
        }

        // BORDER
        css.append("border:1px solid #000;");

        // WRAP TEXT
        if (style.getWrapText()) css.append("white-space:pre-line;");

        return css.toString();
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }


    public void exportToExcel(HttpServletResponse response,
                              LocalDate startDate,
                              LocalDate endDate,
                              String type) throws IOException {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd_MM_yyyy");

        String fileName = switch (type.toLowerCase()) {
            case "day" -> "Báo cáo mượn hồ sơ ngày " + startDate.format(df) + ".xlsx";
            case "month" -> "Báo cáo mượn hồ sơ tháng " + startDate.getMonthValue() + "_" + startDate.getYear() + ".xlsx";
            case "year" -> "Báo cáo mượn hồ sơ năm " + startDate.getYear() + ".xlsx";
            case "range" -> "Báo cáo mượn hồ sơ từ " + startDate.format(df) +
                    " đến " + endDate.format(df) + ".xlsx";
            default -> "Báo cáo mượn hồ sơ tất cả_" + LocalDate.now().format(df) + ".xlsx";
        };

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        // === Lấy dữ liệu ===
        List<RequestDocument> list = switch (type.toLowerCase()) {
            case "day" -> findByDate(startDate);
            case "month" -> findByMonth(startDate.getMonthValue(), startDate.getYear());
            case "year" -> findByYear(startDate.getYear());
            case "range" -> findByDateRange(startDate, endDate);
            default -> findAll();
        };

        Workbook workbook = buildWorkbookForReport(list, startDate, endDate, type);

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public String saveCapturedPhoto(Long requestId, String base64Image) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Không có ảnh để lưu");
        }

        Optional<RequestDocument> optional = repository.findById(requestId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy hồ sơ mượn có ID: " + requestId);
        }

        RequestDocument request = optional.get();

        // === Giải mã ảnh từ Base64 ===
        String[] parts = base64Image.split(",");
        byte[] imageBytes = Base64.getDecoder().decode(parts.length > 1 ? parts[1] : parts[0]);

        // === Tạo thư mục lưu trữ ===
        String folder = "uploads/photos/";
        Files.createDirectories(Paths.get(folder));

        // === Đặt tên file ảnh ===
        String fileName = "photo_" + requestId + "_" + System.currentTimeMillis() + ".png";
        Path path = Paths.get(folder + fileName);

        // === Ghi file ra đĩa ===
        Files.write(path, imageBytes);

        // === Lưu đường dẫn vào DB ===
        request.setAttachmentPath("/" + folder + fileName.replace("\\", "/"));
        repository.save(request);

        return request.getAttachmentPath();
    }


}