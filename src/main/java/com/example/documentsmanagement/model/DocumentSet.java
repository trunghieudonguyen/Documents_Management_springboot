package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DOCUMENT_SET")
public class DocumentSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT_SET")
    private Long idDocumentSet;

    @Column(name = "DOCUMENT_CODE", nullable = false, unique = true, length = 50)
    private String documentCode;

    @Column(name = "OPENED_DATE")
    private LocalDate openedDate;

    @Column(name = "DEPARTMENT", length = 100)
    private String department;

    @Column(name = "AREA", length = 100)
    private String area;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;

    // Relationship: Many documents belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private DocumentCategory category;

    // Constructors
    public DocumentSet() {}

    public DocumentSet(String documentCode, LocalDate openedDate, String department, String area, String status, String note, DocumentCategory category) {
        this.documentCode = documentCode;
        this.openedDate = openedDate;
        this.department = department;
        this.area = area;
        this.status = status;
        this.note = note;
        this.category = category;
    }

    // Getters & Setters
    public Long getIdDocumentSet() {
        return idDocumentSet;
    }

    public void setIdDocumentSet(Long idDocumentSet) {
        this.idDocumentSet = idDocumentSet;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }
}
