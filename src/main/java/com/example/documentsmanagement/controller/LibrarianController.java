package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.dto.ChangePasswordRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.service.LibrarianService;

@RestController
@RequestMapping("/api/librarian")
@CrossOrigin(origins = "http://localhost:3000")

public class LibrarianController {

    private final LibrarianService service;

    public LibrarianController(LibrarianService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Librarian> create(@RequestBody Librarian entity) {
        Librarian created = service.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Librarian> getById(@PathVariable("id") Long id) {
        Optional<Librarian> o = service.getById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/auth/{username}")
    public ResponseEntity<Librarian> getByUsername(@PathVariable("username") String username) {
        Optional<Librarian> librarian = service.getByUsername(username);
        return librarian.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Librarian> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Librarian> update(@PathVariable("id") Long id, @RequestBody Librarian changes) {
        Librarian updated = service.update(id, changes);
        return ResponseEntity.ok(updated);
    }

    // endpoint đổi mật khẩu
    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest req) {

        if (service.changePassword(id, req.getOldPassword(), req.getNewPassword()))
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
        else
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu cũ không đúng"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Librarian> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return service.search(q);
    }

    @GetMapping("/stats/count")
    public long count() {
        return service.count();
    }
}