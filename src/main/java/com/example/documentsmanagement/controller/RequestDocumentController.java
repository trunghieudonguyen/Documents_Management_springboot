package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.service.RequestDocumentService;

@RestController
@RequestMapping("/api/requestdocument")
public class RequestDocumentController {

    private final RequestDocumentService service;

    public RequestDocumentController(RequestDocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RequestDocument> create(@RequestBody RequestDocument entity) {
        RequestDocument created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDocument> getById(@PathVariable("id") Long id) {
        Optional<RequestDocument> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<RequestDocument> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestDocument> update(@PathVariable("id") Long id, @RequestBody RequestDocument changes) {
        RequestDocument updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<RequestDocument> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
