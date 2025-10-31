package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.PasswordResetToken;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.PasswordResetTokenRepository;
import com.example.documentsmanagement.repository.LibrarianRepository;
import com.example.documentsmanagement.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final LibrarianRepository librarianRepo;
    private final OtpSenderService otpSender;
    private final Random random = new Random();

    public PasswordResetService(PasswordResetTokenRepository tokenRepo,
                                LibrarianRepository librarianRepo,
                                OtpSenderService otpSender) {
        this.tokenRepo = tokenRepo;
        this.librarianRepo = librarianRepo;
        this.otpSender = otpSender;
    }

    // =====================================
    // 🔹 Sinh mã OTP ngẫu nhiên 6 chữ số
    // =====================================
    private String generateOtp() {
        int n = 100000 + random.nextInt(900000);
        return String.valueOf(n);
    }

    // =====================================
    // 🔹 Gửi OTP qua email hoặc SMS
    // =====================================
    @Transactional
    public void createAndSendOtp(Librarian librarian) throws Exception {
        String otp = generateOtp();

        PasswordResetToken token = new PasswordResetToken();
        token.setOtp(otp);
        token.setLibrarian(librarian);
        token.setExpiryAt(LocalDateTime.now().plusMinutes(10)); // hết hạn 10 phút
        token.setUsed(false);
        tokenRepo.save(token);

        String message = "Mã xác thực (OTP) của bạn là: " + otp + ". Vui lòng sử dụng trong vòng 10 phút.";

        if (librarian.getEmail() != null && !librarian.getEmail().isBlank()) {
            otpSender.sendEmailOtp(librarian.getEmail(), "OTP đổi mật khẩu", message);
        }
        if (librarian.getPhoneNumber() != null && !librarian.getPhoneNumber().isBlank()) {
            otpSender.sendSmsOtp(librarian.getPhoneNumber(), message);
        }
    }

    // =====================================
    // 🔹 Xác minh OTP (cho /verify-otp)
    // =====================================
    @Transactional(readOnly = true)
    public boolean verifyOtp(String emailOrPhone, String otp) {
        Optional<Librarian> opt = librarianRepo.findByEmail(emailOrPhone);
        if (opt.isEmpty()) {
            opt = librarianRepo.findByPhoneNumber(emailOrPhone);
            if (opt.isEmpty()) return false;
        }

        Librarian lib = opt.get();
        Optional<PasswordResetToken> tokenOpt =
                tokenRepo.findByLibrarian_IdlibrarianAndOtpAndUsedFalse(lib.getIdlibrarian(), otp);

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken token = tokenOpt.get();
        return !token.getExpiryAt().isBefore(LocalDateTime.now());
    }

    // =====================================
    // 🔹 Kiểm tra OTP và đặt lại mật khẩu
    // =====================================
    @Transactional
    public boolean verifyOtpAndResetPassword(String emailOrPhone, String otp, String newPassword) {
        Optional<Librarian> opt = librarianRepo.findByEmail(emailOrPhone);
        if (opt.isEmpty()) {
            opt = librarianRepo.findByPhoneNumber(emailOrPhone);
            if (opt.isEmpty()) return false;
        }

        Librarian lib = opt.get();
        Optional<PasswordResetToken> tokenOpt =
                tokenRepo.findByLibrarian_IdlibrarianAndOtpAndUsedFalse(lib.getIdlibrarian(), otp);

        if (tokenOpt.isEmpty()) return false;
        PasswordResetToken token = tokenOpt.get();

        if (token.getExpiryAt().isBefore(LocalDateTime.now())) return false;

        // ✅ Băm SHA-256 rồi chuyển thành chữ HOA như RAWTOHEX(STANDARD_HASH(...))
        String hashed = PasswordUtil.sha256Hex(newPassword).toUpperCase();

        lib.setPassword(hashed);
        librarianRepo.save(lib);

        token.setUsed(true);
        tokenRepo.save(token);
        return true;
    }
}
