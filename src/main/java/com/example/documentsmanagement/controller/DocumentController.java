package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/documents")

public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // LẤY DANH SÁCH TOÀN BỘ TÀI LIỆU (có Category)
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.findAll();
        return documents.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(documents);
    }

    // LẤY CHI TIẾT TÀI LIỆU THEO ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.findById(id);
        return document
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // TẠO MỚI TÀI LIỆU (tự sinh mã documentCode)
    @PostMapping
    public ResponseEntity<?> createDocument(@RequestBody Document document) {
        try {
            Document created = documentService.create(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Lỗi khi tạo tài liệu: " + e.getMessage());
        }
    }

    // CẬP NHẬT TÀI LIỆU THEO ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        try {
            Optional<Document> updated = documentService.update(id, document);
            return updated
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Không tìm thấy tài liệu có ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi cập nhật tài liệu: " + e.getMessage());
        }
    }

    // XÓA TÀI LIỆU THEO ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Không thể xóa tài liệu: " + e.getMessage());
        }
    }

    // TÌM KIẾM TÀI LIỆU THEO TỪ KHÓA (title, code, status, dept, area)
    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(
            @RequestParam(value = "q", required = false, defaultValue = "") String keyword) {

        List<Document> results = documentService.search(keyword);
        return results.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(results);
    }

    // LỌC THEO TRẠNG THÁI
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> getByStatus(@PathVariable String status) {
        List<Document> docs = documentService.findByStatus(status);
        return docs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(docs);
    }

    // LỌC THEO PHÒNG BAN
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Document>> getByDepartment(@PathVariable String department) {
        List<Document> docs = documentService.findByDepartment(department);
        return docs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(docs);
    }

    // LỌC THEO DANH MỤC CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Document>> getByCategory(@PathVariable Long categoryId) {
        List<Document> docs = documentService.findByCategoryId(categoryId);
        return docs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(docs);
    }

    // ĐẾM SỐ LƯỢNG TÀI LIỆU
    @GetMapping("/count")
    public ResponseEntity<Long> countDocuments() {
        return ResponseEntity.ok(documentService.count());
    }
}