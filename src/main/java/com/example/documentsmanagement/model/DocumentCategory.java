package com.example.documentsmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DOCUMENT_CATEGORY")
public class DocumentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT_CATEGORY")
    private Long idDocumentCategory;

    @Column(name = "SYMBOL", nullable = false, length = 50)
    private String symbol;

    @Column(name = "CONTENT", nullable = false, length = 255)
    private String content;

    @Column(name = "RETENTION_PERIOD", length = 100)
    private String retentionPeriod;

    @Column(name = "NOTE", length = 255)
    private String note;

    // 🔹 Constructors
    public DocumentCategory() {}

    public DocumentCategory(String symbol, String content, String retentionPeriod, String note) {
        this.symbol = symbol;
        this.content = content;
        this.retentionPeriod = retentionPeriod;
        this.note = note;
    }

    // 🔹 Getters & Setters
    public Long getIdDocumentCategory() {
        return idDocumentCategory;
    }

    public void setIdDocumentCategory(Long idDocumentCategory) {
        this.idDocumentCategory = idDocumentCategory;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
}
