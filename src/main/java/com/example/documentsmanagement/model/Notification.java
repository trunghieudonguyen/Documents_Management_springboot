package com.example.documentsmanagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICATION")
    private Long idNotification;

    @Column(name = "CONTENT", nullable = false, length = 255)
    private String content;

    @Column(name = "CREATE_DATE", length = 255)
    private LocalDate createDate;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "STATUS", length = 255)
    private String status;

    @Column(name = "TYPE", length = 255)
    private String type;

    // ==========================
    // Constructors
    // ==========================
    public Notification() {}

    public Notification(String content, LocalDate createDate, Boolean isRead, String status,  String type) {
        this.content = content;
        this.createDate = createDate;
        this.isRead = isRead;
        this.status = status;
        this.type = type;
    }

    // ==========================
    // Getters & Setters
    // ==========================
    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}
}

