package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.dto.LoginRequest;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.LibrarianRepository;
import com.example.documentsmanagement.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.documentsmanagement.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final LibrarianRepository librarianRepository;
    private final PasswordResetService passwordResetService;

    public AuthController(LibrarianRepository librarianRepository,
                          PasswordResetService passwordResetService,
                          JwtUtil jwtUtil) {
        this.librarianRepository = librarianRepository;
        this.passwordResetService = passwordResetService;
        this.jwtUtil = jwtUtil;
    }

    // =============================
    // 🔹 Đăng nhập
    // =============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tên người dùng hoặc mật khẩu"));
        }

        try {
            Optional<Librarian> librarianOpt =
                    librarianRepository.findByUsernameAndPasswordHashed(username, password);

            if (librarianOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Sai tên đăng nhập hoặc mật khẩu"));
            }

            Librarian librarian = librarianOpt.get();

            // ✅ Sinh token JWT
            String token = jwtUtil.generateToken(librarian.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công!",
                    "username", librarian.getUsername(),
                    "fullName", librarian.getFullName(),
                    "email", librarian.getEmail(),
                    "token", token
            ));
        } catch (Exception ex) {
            logger.error("❌ Lỗi đăng nhập: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Lỗi hệ thống trong quá trình đăng nhập"));
        }
    }

    // =============================
    // 🔹 Quên mật khẩu (Gửi OTP)
    // =============================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String emailOrPhone = body.get("emailOrPhone");

        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Trường emailOrPhone là bắt buộc"));
        }

        try {
            Optional<Librarian> opt = librarianRepository.findByEmail(emailOrPhone);
            if (opt.isEmpty()) {
                opt = librarianRepository.findByPhoneNumber(emailOrPhone);
            }

            // ✅ Không tiết lộ tài khoản tồn tại hay không
            if (opt.isPresent()) {
                try {
                    passwordResetService.createAndSendOtp(opt.get());
                } catch (Exception e) {
                    logger.error("❌ Lỗi khi gửi OTP: {}", e.getMessage(), e);
                    return ResponseEntity.internalServerError()
                            .body(Map.of("message", "Không thể gửi OTP: " + e.getMessage()));
                }
            }

            return ResponseEntity.ok(Map.of("message", "Nếu tài khoản tồn tại, mã OTP đã được gửi"));
        } catch (Exception ex) {
            logger.error("❌ Lỗi khi gửi OTP: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Không thể gửi OTP: " + ex.getMessage()));
        }
    }

    // =============================
    // 🔹 Xác minh OTP (tránh lỗi “No static resource /verify-otp”)
    // =============================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String emailOrPhone = body.get("emailOrPhone");
        String otp = body.get("otp");

        if (emailOrPhone == null || emailOrPhone.isBlank()
                || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cần cung cấp emailOrPhone và otp"));
        }

        try {
            boolean valid = passwordResetService.verifyOtp(emailOrPhone, otp);
            if (valid) {
                return ResponseEntity.ok(Map.of("message", "OTP hợp lệ"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "OTP không đúng hoặc đã hết hạn"));
            }
        } catch (Exception ex) {
            logger.error("❌ Lỗi xác minh OTP: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Không thể xác minh OTP: " + ex.getMessage()));
        }
    }

    // =============================
    // 🔹 Đặt lại mật khẩu
    // =============================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String emailOrPhone = body.get("emailOrPhone");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");

        if (emailOrPhone == null || emailOrPhone.isBlank()
                || otp == null || otp.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Các trường emailOrPhone, otp, newPassword đều bắt buộc"));
        }

        try {
            boolean ok = passwordResetService.verifyOtpAndResetPassword(emailOrPhone, otp, newPassword);
            if (ok) {
                return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "OTP không hợp lệ hoặc đã hết hạn"));
            }
        } catch (Exception ex) {
            logger.error("❌ Lỗi khi đặt lại mật khẩu: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Không thể đặt lại mật khẩu: " + ex.getMessage()));
        }
    }
}
