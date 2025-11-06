package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle tự sinh IDENTITY
    @Column(name = "ID")
    private Long id;

    @Column(name = "OTP", nullable = false, length = 6)
    private String otp;

    @Column(name = "EXPIRY_AT", nullable = false)
    private LocalDateTime expiryAt;

    @Column(name = "USED", nullable = false)
    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIBRARIAN_ID")
    private Librarian librarian;

    @PrePersist
    protected void onCreate() {
        if (expiryAt == null) {
            expiryAt = LocalDateTime.now().plusMinutes(10);
        }
    }

    // Getters / Setters
    public Long getId() { return id; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getExpiryAt() { return expiryAt; }
    public void setExpiryAt(LocalDateTime expiryAt) { this.expiryAt = expiryAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Librarian getLibrarian() { return librarian; }
    public void setLibrarian(Librarian librarian) { this.librarian = librarian; }
}