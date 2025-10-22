package com.example.documentsmanagement.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.service.DocumentService;


@RestController
@RequestMapping("/api/document")
public class DocumentController {


    private final DocumentService service;


    public DocumentController(DocumentService service) {
        this.service = service;
    }


    @GetMapping
    public List<Document> getAll() {
        return service.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        Optional<Document> d = service.findById(id);
        return d.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Document> create(@RequestBody Document document) {
        Document saved = service.create(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable Long id, @RequestBody Document document) {
        Optional<Document> updated = service.update(id, document);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    public List<Document> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }


    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}