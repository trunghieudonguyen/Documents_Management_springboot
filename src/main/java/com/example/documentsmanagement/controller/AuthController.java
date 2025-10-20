package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.dto.LoginRequest;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.LibrarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000") // Cho phép React truy cập API
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LibrarianRepository librarianRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Librarian request) {
        Optional<Librarian> librarianOpt =
                librarianRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        if (librarianOpt.isPresent()) {
            Librarian librarian = librarianOpt.get();
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công!",
                    "username", librarian.getUsername()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Sai tên đăng nhập hoặc mật khẩu"));
        }
    }
}
