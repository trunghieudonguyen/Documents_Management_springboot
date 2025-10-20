package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.DocumentSet;
import com.example.documentsmanagement.service.DocumentSetService;

@RestController
@RequestMapping("/api/documentset")
public class DocumentSetController {

    private final DocumentSetService service;

    public DocumentSetController(DocumentSetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DocumentSet> create(@RequestBody DocumentSet entity) {
        DocumentSet created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentSet> getById(@PathVariable("id") Long id) {
        Optional<DocumentSet> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DocumentSet> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentSet> update(@PathVariable("id") Long id, @RequestBody DocumentSet changes) {
        DocumentSet updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<DocumentSet> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
