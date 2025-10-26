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
@CrossOrigin(origins = "*") // Cho phép frontend gọi API
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    // 🟢 Lấy tất cả tài liệu
    @GetMapping
    public ResponseEntity<List<Document>> getAll() {
        List<Document> documents = service.findAll();
        return documents.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(documents);
    }

    // 🟢 Lấy tài liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        Optional<Document> document = service.findById(id);
        return document.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟢 Tạo mới tài liệu (sinh mã tự động)
    @PostMapping
    public ResponseEntity<Document> create(@RequestBody Document document) {
        try {
            Document created = service.create(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 🟢 Cập nhật tài liệu
    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable Long id, @RequestBody Document document) {
        Optional<Document> updated = service.update(id, document);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟢 Xóa tài liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🟢 Tìm kiếm theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<Document>> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        List<Document> results = service.search(q);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    // 🟢 Lấy tổng số tài liệu
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    // 🟢 Lọc theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> findByStatus(@PathVariable String status) {
        List<Document> docs = service.findByStatus(status);
        return docs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(docs);
    }

    // 🟢 Lọc theo phòng ban
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Document>> findByDepartment(@PathVariable String department) {
        List<Document> docs = service.findByDepartment(department);
        return docs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(docs);
    }
}
