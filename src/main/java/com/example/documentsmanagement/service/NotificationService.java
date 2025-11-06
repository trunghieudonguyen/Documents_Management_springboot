package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.model.Notification;
import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.repository.DocumentRepository;
import com.example.documentsmanagement.repository.NotificationRepository;
import com.example.documentsmanagement.repository.RequestDocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DocumentRepository documentRepository;
    private final RequestDocumentRepository requestDocumentRepository;

    public NotificationService(NotificationRepository notificationRepository, DocumentRepository documentRepository,  RequestDocumentRepository requestDocumentRepository) {
        this.notificationRepository = notificationRepository;
        this.documentRepository = documentRepository;
        this.requestDocumentRepository = requestDocumentRepository;
    }

    // Lấy thông báo 1 tháng gần đây
    public List<Notification> getRecentNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        return notificationRepository.findByCreateDateBetween(oneMonthAgo, today);
    }

    @Transactional
    public Notification updateNotification(Long id, Notification updatedNotification) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Notification not found with id: " + id);
        }

        Notification notification = optional.get();
        // cập nhật các trường bạn muốn
        notification.setContent(updatedNotification.getContent());
        notification.setStatus(updatedNotification.getStatus());
        notification.setIsRead(updatedNotification.getIsRead());

        return notificationRepository.save(notification);
    }

    // Scheduler kiểm tra document sắp hết hạn và tạo notification
    @Scheduled(cron = "0 30 8 * * ?") // Chạy mỗi ngày 8h30 sáng
    public void notifyExpiringDocuments() {
        LocalDate today = LocalDate.now();
        LocalDate in1Day = today.plusDays(1);
        LocalDate in3Days = today.plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        //Tạo thông báo hồ sơ sắp hết hạn
        List<Document> expiringDocs = documentRepository.findByExpirationDateBetween(in1Day, in3Days);
        for (Document doc : expiringDocs) {
            long daysLeft = ChronoUnit.DAYS.between(today, doc.getExpirationDate()); // tính số ngày còn lại

            Notification notification = new Notification();
            notification.setContent(
                    "Hồ sơ " + doc.getTitle() + " hết hạn ngày " + formatter.format(doc.getExpirationDate())
                            + " (còn " + daysLeft + " ngày)"
            );
            notification.setStatus("expiring");
            notification.setIsRead(false);
            notification.setType("document");
            notification.setCreateDate(today);
            notificationRepository.save(notification);
        }

        //Tạo thông báo hồ sơ hết hạn
        List<Document> expiredDocs = documentRepository.findByExpirationDateBefore(today.plusDays(1));
        for (Document doc : expiredDocs) {
            Notification notification = new Notification();
            notification.setContent("Hồ sơ " + doc.getTitle() + " đã hết hạn (ngày hết hạn: " + formatter.format(doc.getExpirationDate()) + ")");
            notification.setStatus("expired");
            notification.setType("document");
            notification.setIsRead(false); // luôn tạo mới mỗi ngày
            notification.setCreateDate(today);
            notificationRepository.save(notification);
        }

        //Tạo thông báo hồ sơ sắp hết hạn
        List<RequestDocument> expiringRequestDocuments = requestDocumentRepository.findByReturnDeadlineBetween(in1Day, in3Days);
        for (RequestDocument requestDocument : expiringRequestDocuments) {
            long daysLeft = ChronoUnit.DAYS.between(today, requestDocument.getReturnDeadline()); // tính số ngày còn lại

            Notification notification = new Notification();
            notification.setContent(
                    "Cán bộ " + requestDocument.getBorrower().getFullName() + " sắp đến hạn trả ngày "
                            + formatter.format(requestDocument.getReturnDeadline()) + " (còn " + daysLeft + " ngày)"
            );
            notification.setStatus("expiring");
            notification.setIsRead(false);
            notification.setType("requestDocument");
            notification.setCreateDate(today);
            notificationRepository.save(notification);
        }

        //Tạo thông báo hồ sơ hết hạn
        List<RequestDocument> expiredRequestDocuments = requestDocumentRepository.findByReturnDeadlineBefore(today.plusDays(1));
        for (RequestDocument requestDocument : expiredRequestDocuments) {
            Notification notification = new Notification();
            notification.setContent(
                    "Cán bộ " + requestDocument.getBorrower().getFullName() +
                            " đã hết hạn trả (ngày hết hạn: " + formatter.format(requestDocument.getReturnDeadline()) + ")");
            notification.setStatus("expired");
            notification.setIsRead(false); // luôn tạo mới mỗi ngày
            notification.setCreateDate(today);
            notification.setType("requestDocument");
            notificationRepository.save(notification);
        }
    }
}