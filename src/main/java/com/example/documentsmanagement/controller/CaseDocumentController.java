package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.CaseDocument;
import com.example.documentsmanagement.service.CaseDocumentService;

@RestController
@RequestMapping("/api/casedocument")
public class CaseDocumentController {

    private final CaseDocumentService service;

    public CaseDocumentController(CaseDocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CaseDocument> create(@RequestBody CaseDocument entity) {
        CaseDocument created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseDocument> getById(@PathVariable("id") Long id) {
        Optional<CaseDocument> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CaseDocument> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseDocument> update(@PathVariable("id") Long id, @RequestBody CaseDocument changes) {
        CaseDocument updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<CaseDocument> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
