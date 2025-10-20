package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.PersonDocument;
import com.example.documentsmanagement.service.PersonDocumentService;

@RestController
@RequestMapping("/api/persondocument")
public class PersonDocumentController {

    private final PersonDocumentService service;

    public PersonDocumentController(PersonDocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PersonDocument> create(@RequestBody PersonDocument entity) {
        PersonDocument created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDocument> getById(@PathVariable("id") Long id) {
        Optional<PersonDocument> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<PersonDocument> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDocument> update(@PathVariable("id") Long id, @RequestBody PersonDocument changes) {
        PersonDocument updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PersonDocument> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
