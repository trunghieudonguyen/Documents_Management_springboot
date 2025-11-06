package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.Borrower;
import com.example.documentsmanagement.service.BorrowerService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/borrower")
public class BorrowerController {

    private final BorrowerService service;

    public BorrowerController(BorrowerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Borrower> create(@RequestBody Borrower entity) {
        Borrower created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrower> getById(@PathVariable("id") Long id) {
        Optional<Borrower> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Borrower> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrower> update(@PathVariable("id") Long id, @RequestBody Borrower changes) {
        Borrower updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Borrower> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}
