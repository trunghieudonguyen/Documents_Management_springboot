package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy thông báo theo trạng thái (nếu muốn highlight màu)
    List<Notification> findByStatus(String status);

    // Lấy thông báo chưa đọc (nếu muốn đếm số thông báo mới)
    List<Notification> findByIsRead(Boolean isRead);

    // Lấy thông báo chưa đọc (nếu muốn đếm số thông báo mới)
    List<Notification> findByCreateDate(LocalDate createDate);

    // Lấy thông báo trong khoảng ngày
    List<Notification> findByCreateDateBetween(LocalDate startDate, LocalDate endDate);
}

