package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.service.RequestDocumentService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/request-documents")
@CrossOrigin(origins = "http://localhost:3000")

public class RequestDocumentController {

    private final RequestDocumentService service;

    public RequestDocumentController(RequestDocumentService service) {
        this.service = service;
    }

    // 🟢 Tạo mới yêu cầu mượn tài liệu
    @PostMapping
    public ResponseEntity<RequestDocument> create(@RequestBody RequestDocument requestDocument) {
        RequestDocument created = service.create(requestDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🟢 Lấy yêu cầu mượn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RequestDocument> getById(@PathVariable Long id) {
        Optional<RequestDocument> found = service.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟢 Lấy toàn bộ danh sách yêu cầu mượn
    @GetMapping
    public ResponseEntity<List<RequestDocument>> getAll() {
        List<RequestDocument> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/history/document/{documentId}")
    public ResponseEntity<List<RequestDocument>> getBorrowHistoryByDocument(@PathVariable Long documentId) {
        List<RequestDocument> history = service.findHistoryByDocumentId(documentId);
        return ResponseEntity.ok(history);
    }

    // 🟢 Cập nhật yêu cầu mượn tài liệu
    @PutMapping("/{id}")
    public ResponseEntity<RequestDocument> update(@PathVariable Long id, @RequestBody RequestDocument updatedData) {
        Optional<RequestDocument> updated = service.update(id, updatedData);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟢 Xóa yêu cầu mượn tài liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 🟢 Tìm kiếm theo từ khóa (mã tài liệu hoặc người ký)
    @GetMapping("/search")
    public ResponseEntity<List<RequestDocument>> search(
            @RequestParam(value = "q", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    // 🟢 Thống kê số lượng yêu cầu mượn tài liệu
    @GetMapping("/stats/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=request_documents.xlsx";
        response.setHeader(headerKey, headerValue);

        List<RequestDocument> list = service.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("RequestDocuments");

        // ===== Font và style tiêu đề =====
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setFontName("Times New Roman");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Thêm viền cho tiêu đề
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ===== Font và style nội dung =====
        Font contentFont = workbook.createFont();
        contentFont.setFontHeightInPoints((short) 14);
        contentFont.setFontName("Times New Roman");

        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setFont(contentFont);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Thêm viền cho nội dung
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);

        // ===== Tiêu đề tiếng Việt =====
        String[] columns = {
                "STT", "Ngày mượn", "Ngày trả", "Hạn trả", "Loại tài liệu",
                "Người ký phiếu mượn", "Họ và tên người mượn", "Họ và tên thủ thư", "Ghi chú"
        };

        // ===== Tạo dòng tiêu đề =====
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // ===== Ghi dữ liệu =====
        int rowNum = 1;
        int stt = 1;
        for (RequestDocument doc : list) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(stt++); // STT
            row.createCell(1).setCellValue(doc.getBorrowDate() != null ? doc.getBorrowDate().toString() : "");
            row.createCell(2).setCellValue(doc.getReturnDate() != null ? doc.getReturnDate().toString() : "");
            row.createCell(3).setCellValue(doc.getReturnDeadline() != null ? doc.getReturnDeadline().toString() : "");

            String copyType = doc.getCopyType();
            if ("original".equalsIgnoreCase(copyType)) {
                copyType = "Bản gốc";
            }
            row.createCell(4).setCellValue(copyType != null ? copyType : "");

            row.createCell(5).setCellValue(doc.getSigner() != null ? doc.getSigner() : "");
            row.createCell(6).setCellValue(doc.getBorrower() != null ? doc.getBorrower().getFullName() : "");
            row.createCell(7).setCellValue(doc.getLibrarian() != null ? doc.getLibrarian().getFullName() : "");
            row.createCell(8).setCellValue(doc.getNote() != null ? doc.getNote() : "");

            for (int i = 0; i < columns.length; i++) {
                row.getCell(i).setCellStyle(contentStyle);
            }
        }

        // ===== Tự động căn cột =====
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}