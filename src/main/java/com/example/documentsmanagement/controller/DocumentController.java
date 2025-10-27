package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*") // Cho phép gọi API từ bất kỳ frontend nào
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    // =========================================================
    // LẤY DANH SÁCH TOÀN BỘ TÀI LIỆU
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Document>> getAll() {
        List<Document> documents = service.findAll();
        if (documents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(documents);
    }

    // =========================================================
    // LẤY CHI TIẾT TÀI LIỆU THEO ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        Optional<Document> document = service.findById(id);
        return document.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =========================================================
    // TẠO MỚI TÀI LIỆU (tự sinh mã documentCode)
    // =========================================================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Document document) {
        try {
            Document created = service.create(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi tạo tài liệu: " + e.getMessage());
        }
    }

    // =========================================================
    // CẬP NHẬT TÀI LIỆU THEO ID
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Document document) {
        Optional<Document> updated = service.update(id, document);
        return updated
                .<ResponseEntity<?>>map(doc -> ResponseEntity.ok(doc))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy tài liệu có ID: " + id));
    }

    // =========================================================
    // XÓA TÀI LIỆU THEO ID
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa tài liệu: " + e.getMessage());
        }
    }

    // =========================================================
    // TÌM KIẾM TÀI LIỆU THEO TỪ KHÓA (title, code, status, dept, area)
    // =========================================================
    @GetMapping("/search")
    public ResponseEntity<List<Document>> search(
            @RequestParam(value = "q", required = false, defaultValue = "") String q) {
        List<Document> results = service.search(q);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    // =========================================================
    // LẤY TỔNG SỐ LƯỢNG TÀI LIỆU
    // =========================================================
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    // =========================================================
    // LỌC THEO TRẠNG THÁI
    // =========================================================
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> findByStatus(@PathVariable String status) {
        List<Document> docs = service.findByStatus(status);
        if (docs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(docs);
    }

    // =========================================================
    // LỌC THEO PHÒNG BAN
    // =========================================================
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Document>> findByDepartment(@PathVariable String department) {
        List<Document> docs = service.findByDepartment(department);
        if (docs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(docs);
    }
}
