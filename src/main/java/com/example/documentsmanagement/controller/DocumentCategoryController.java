package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.DocumentCategory;
import com.example.documentsmanagement.service.DocumentCategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000") // cho phép frontend React truy cập

public class DocumentCategoryController {

    private final DocumentCategoryService service;
    @Autowired

    public DocumentCategoryController(DocumentCategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DocumentCategory> create(@RequestBody DocumentCategory entity) {
        DocumentCategory created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentCategory> getById(@PathVariable("id") Long id) {
        Optional<DocumentCategory> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DocumentCategory> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentCategory> update(@PathVariable("id") Long id, @RequestBody DocumentCategory changes) {
        DocumentCategory updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<DocumentCategory> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
