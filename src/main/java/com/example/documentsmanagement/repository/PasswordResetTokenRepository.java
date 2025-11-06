package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByLibrarian_IdLibrarianOrderByExpiryAtDesc(Long librarianId);
    Optional<PasswordResetToken> findByLibrarian_IdLibrarianAndOtpAndUsedFalse(Long librarianId, String otp);
    Optional<PasswordResetToken> findTopByLibrarianAndOtpAndUsedFalseOrderByExpiryAtDesc(
            Librarian librarian, String otp
    );
}