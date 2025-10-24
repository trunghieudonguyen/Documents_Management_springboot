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
@CrossOrigin(origins = "*") // Cho phép frontend truy cập API
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    // 🟩 Lấy tất cả Document
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Document> documents = service.findAll();
        if (documents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No documents found.");
        }
        return ResponseEntity.ok(documents);
    }

    // 🟦 Lấy Document theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Document> document = service.findById(id);
        return document.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Document not found with ID: " + id));
    }

    // 🟨 Tạo mới Document
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Document document) {
        try {
            Document saved = service.create(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create document: " + e.getMessage());
        }
    }

    // 🟧 Cập nhật Document theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Document document) {
        Optional<Document> updated = service.update(id, document);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Document not found with ID: " + id));
    }

    // 🟥 Xóa Document theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Document> existing = service.findById(id);
        if (existing.isPresent()) {
            service.delete(id);
            return ResponseEntity.ok("Document deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Document not found with ID: " + id);
        }
    }

    // 🔍 Tìm kiếm theo từ khóa (title, code, description)
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        List<Document> results = service.search(q);
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No matching documents found.");
        }
        return ResponseEntity.ok(results);
    }

    // 🔢 Đếm tổng số Document
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        long total = service.count();
        return ResponseEntity.ok(total);
    }
}
