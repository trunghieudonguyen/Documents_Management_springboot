package com.example.documentsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * ✅ Dịch vụ gửi mã OTP qua Email (SMTP) và SMS (REST API)
 * Hỗ trợ Gmail và nhà mạng, xử lý an toàn, có log.
 */
@Slf4j
@Service
public class OtpSenderService {

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate = new RestTemplate();

    // --- Email & SMS cấu hình ---
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.sms.api.url:}")
    private String smsApiUrl;

    @Value("${app.sms.api.key:}")
    private String smsApiKey;

    public OtpSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi OTP qua Email (SMTP)
     * @param toEmail địa chỉ email người nhận
     * @param subject tiêu đề email
     * @param body nội dung có chứa mã OTP
     */
    public boolean sendEmailOtp(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("✅ Gửi email OTP thành công tới {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("❌ Lỗi gửi email OTP tới {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gửi OTP qua SMS (REST API)
     * @param toPhone số điện thoại người nhận
     * @param message nội dung tin nhắn có mã OTP
     */
    public boolean sendSmsOtp(String toPhone, String message) {
        if (smsApiUrl == null || smsApiUrl.isBlank()) {
            log.warn("⚠️ Không cấu hình API SMS, bỏ qua gửi SMS cho {}", toPhone);
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (smsApiKey != null && !smsApiKey.isBlank()) {
                headers.set("Authorization", "Bearer " + smsApiKey);
            }

            Map<String, String> payload = Map.of(
                    "to", toPhone,
                    "message", message
            );

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(smsApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Gửi SMS OTP thành công tới {}", toPhone);
                return true;
            } else {
                log.error("❌ Gửi SMS thất bại (HTTP {}): {}", response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi SMS OTP tới {}: {}", toPhone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gửi OTP qua email hoặc điện thoại (tự động chọn)
     */
    public boolean sendOtp(String emailOrPhone, String otp) {
        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            log.warn("⚠️ Không có email hoặc số điện thoại để gửi OTP.");
            return false;
        }

        String message = String.format("Mã OTP của bạn là: %s\nOTP có hiệu lực trong 10 phút.", otp);
        if (emailOrPhone.contains("@")) {
            return sendEmailOtp(emailOrPhone, "Mã xác thực OTP", message);
        } else {
            return sendSmsOtp(emailOrPhone, message);
        }
    }
}
