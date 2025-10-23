package com.example.documentsmanagement.controller;


import com.example.documentsmanagement.dto.LoginRequest;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.LibrarianRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final LibrarianRepository librarianRepository;


    public AuthController(LibrarianRepository librarianRepository) {
        this.librarianRepository = librarianRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing username or password"));
        }


// Call repository method that asks Oracle to hash the incoming password with SHA-256 and compare
        Optional<Librarian> librarianOpt = librarianRepository.findByUsernameAndPasswordHashed(request.getUsername(), request.getPassword());


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