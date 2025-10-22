package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT")
    private Long idDocument;

    @Column(name = "DOCUMENT_CODE", nullable = false, unique = true, length = 50)
    private String documentCode;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "TYPE", length = 100)
    private String type;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate;

    @Column(name = "EVENT_DATE")
    private LocalDate eventDate;

    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;

    // Constructors
    public Document() {}

    public Document(String documentCode, String title, String type, String status,
                    LocalDate createdDate, LocalDate eventDate, String note, String description) {
        this.documentCode = documentCode;
        this.title = title;
        this.type = type;
        this.status = status;
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.note = note;
        this.description = description;
    }

    // Getters and setters
    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
