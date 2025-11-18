package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.service.RequestDocumentService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<RequestDocument> create(
            @RequestPart("request") RequestDocument requestDocument,
            @RequestPart("file") MultipartFile file) {
        
        RequestDocument created = service.create(requestDocument, file); 
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
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate startDate,

                              @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate endDate,

                              @RequestParam(defaultValue = "all") String type)
            throws IOException {

        // ✅ Kiểm tra đầu vào hợp lệ
        if ((type.equalsIgnoreCase("day") ||
                type.equalsIgnoreCase("month") ||
                type.equalsIgnoreCase("year")) && startDate == null) {
            throw new IllegalArgumentException("Vui lòng nhập ngày cho loại báo cáo: " + type);
        }

        if (type.equalsIgnoreCase("range") && (startDate == null || endDate == null)) {
            throw new IllegalArgumentException("Vui lòng cung cấp cả ngày bắt đầu và ngày kết thúc cho báo cáo.");
        }

        // ✅ Gọi service xuất Excel
        service.exportToExcel(response, startDate, endDate, type);
    }

    @GetMapping(value = "/preview-excel", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> previewExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "all") String type) throws Exception {

        // Validate like exportToExcel
        if ((type.equalsIgnoreCase("day") ||
                type.equalsIgnoreCase("month") ||
                type.equalsIgnoreCase("year")) && startDate == null) {
            throw new IllegalArgumentException("Vui lòng nhập ngày cho loại báo cáo: " + type);
        }
        if (type.equalsIgnoreCase("range") && (startDate == null || endDate == null)) {
            throw new IllegalArgumentException("Vui lòng cung cấp cả ngày bắt đầu và ngày kết thúc cho báo cáo.");
        }

        String html = service.previewExcelAsHtml(startDate, endDate, type);
        return ResponseEntity.ok(html);
    }

    @PostMapping("/upload-photo/{id}")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable("id") Long requestId,
            @RequestBody Map<String, String> body
    ) {
        System.out.println("🟢 Nhận upload-photo: id=" + requestId);
        System.out.println("🟢 Body nhận được: " + body);

        try {
            String base64Image = body.get("image");
            String photoPath = service.saveCapturedPhoto(requestId, base64Image);
            return ResponseEntity.ok(Map.of(
                    "message", "Ảnh đã được lưu thành công",
                    "photoPath", photoPath
            ));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi khi lưu ảnh: " + e.getMessage()));
        }
    }

}