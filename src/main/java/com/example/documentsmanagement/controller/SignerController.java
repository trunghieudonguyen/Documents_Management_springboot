package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.Signer;
import com.example.documentsmanagement.service.SignerService;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/signers")
@CrossOrigin(origins = "http://localhost:3000")
public class SignerController {
    private final SignerService service;

    public SignerController(SignerService service) { 
        this.service = service; 
    }

    @GetMapping
    public List<Signer> getAll() { return service.findAll(); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Signer signer) {
        try {
            Signer created = service.create(signer);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataIntegrityViolationException e) { 
            return ResponseEntity.ok(Map.of("isError", true, "message", "Số hiệu cán bộ đã tồn tại!"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Signer signer) {
        try {
            Signer updated = service.update(id, signer);
            return ResponseEntity.ok(updated);
        } catch (DataIntegrityViolationException e) { 
            return ResponseEntity.ok(Map.of("isError", true, "message", "Số hiệu cán bộ đã tồn tại!"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isError", true, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}