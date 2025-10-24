package com.example.documentsmanagement.model;

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

    @Column(name = "RETENTION_PERIOD", length = 100)
    private String retentionPeriod;

    @Column(name = "NOTE", length = 255)
    private String note;

    // 🔗 One Category has many Documents
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    // Constructors
    public DocumentCategory() {}

    public DocumentCategory(String sign, String content, String retentionPeriod, String note) {
        this.sign = sign;
        this.content = content;
        this.retentionPeriod = retentionPeriod;
        this.note = note;
    }

    // Getters & Setters
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

    public String getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(String retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
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

    // Helper methods for bidirectional relationship
    public void addDocument(Document document) {
        documents.add(document);
        document.setCategory(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setCategory(null);
    }
}
