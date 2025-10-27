package com.example.documentsmanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DOCUMENT_CATEGORY")
public class DocumentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT_CATEGORY")
    private Long idDocumentCategory;

    @Column(name = "SIGN", nullable = false, length = 50)
    private String sign;

    @Column(name = "CONTENT", nullable = false, length = 255)
    private String content;

    @Column(name = "DURATION", length = 100)
    private String duration;

    @Column(name = "NOTE", length = 255)
    private String note;

    // One Category has many Documents
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Giữ liên kết chính khi serialize JSON
    private List<Document> documents = new ArrayList<>();

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
    public DocumentCategory() {}

    public DocumentCategory(String sign, String content, String duration, String note) {
        this.sign = sign;
        this.content = content;
        this.duration = duration;
        this.note = note;
    }

    // =========================================================
    // GETTERS & SETTERS
    // =========================================================
    public Long getIdDocumentCategory() {
        return idDocumentCategory;
    }

    public void setIdDocumentCategory(Long idDocumentCategory) {
        this.idDocumentCategory = idDocumentCategory;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    // =========================================================
    // QUẢN LÝ QUAN HỆ HAI CHIỀU
    // =========================================================
    public void addDocument(Document document) {
        documents.add(document);
        document.setCategory(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setCategory(null);
    }
}
