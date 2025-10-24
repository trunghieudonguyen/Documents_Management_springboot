package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate;

    @Column(name = "EVENT_DATE")
    private LocalDate eventDate;

    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;

    @Column(name = "DEPARTMENT", length = 150)
    private String department;

    @Column(name = "AREA", length = 150)
    private String area;

    // Nhiều Documents thuộc về một Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOCUMENT_CATEGORY", referencedColumnName = "ID_DOCUMENT_CATEGORY")
    private DocumentCategory category;

    // Một Document có thể có nhiều RequestDocuments
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestDocument> requests;

    // Constructors
    public Document() {}

    public Document(String documentCode, String title, String description, String status,
                    LocalDate createdDate, LocalDate eventDate, String note,
                    String department, String area, DocumentCategory category) {
        this.documentCode = documentCode;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.note = note;
        this.department = department;
        this.area = area;
        this.category = category;
    }

    // Getters & Setters
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

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public List<RequestDocument> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestDocument> requests) {
        this.requests = requests;
    }
}
