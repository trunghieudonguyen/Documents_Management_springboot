package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.service.RequestDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}