package com.example.documentsmanagement.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@MappedSuperclass

public abstract class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_Document;

    private String title;

    private String description;

    private LocalDate createdDate;

    private String filePath;

    // Constructors
    public Document() {}

    public Document(String title, String description, LocalDate createdDate) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.filePath = filePath;
    }

    // Getters & Setters
    public Long getId_Document() {
        return id_Document;
    }

    public void setId_Document(Long idDocument) {
        this.id_Document = id_Document;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

}
