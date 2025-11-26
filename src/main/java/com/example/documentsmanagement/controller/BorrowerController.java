package com.example.documentsmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
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
    public ResponseEntity<?> create(@RequestBody Borrower entity) {
        try {
            Borrower created = service.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Số hiệu đã tồn tại!"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
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
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody Borrower changes) {
        try {
            Borrower updated = service.update(id, changes);
            return ResponseEntity.ok(updated);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Số hiệu đã tồn tại!"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
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
