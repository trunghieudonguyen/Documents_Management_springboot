package com.example.documentsmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

@Entity
@Table(name = "DOCUMENT")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ngăn lỗi khi serialize entity lazy
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT")
    private Long idDocument;

    // Mã tài liệu (có thể null khi mới tạo)
    @Column(name = "DOCUMENT_CODE", unique = true, length = 50)
    private String documentCode;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate;

    @Column(name = "EVENT_DATE")
    private LocalDate eventDate;

    @Column(name = "EXPIRATION_DATE")
    private LocalDate expirationDate;

    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;

    @Column(name = "DEPARTMENT", length = 150)
    private String department;

    @Column(name = "AREA", length = 150)
    private String area;

    // Nhiều Document thuộc về 1 Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_DOCUMENT_CATEGORY", referencedColumnName = "ID_DOCUMENT_CATEGORY")
    @JsonIgnoreProperties("documents") // Ngăn vòng lặp vô hạn khi trả về JSON
    private DocumentCategory category;

    // Một Document có thể có nhiều RequestDocument (nếu bạn dùng bảng này)
    @ManyToMany(mappedBy = "documents")
    @JsonIgnoreProperties("document")
    private List<RequestDocument> requests = new ArrayList<>();

    // ==========================
    // Constructors
    // ==========================
    public Document() {}

    public Document(String title, DocumentCategory category, String department, String area,
                    LocalDate createdDate, LocalDate eventDate, LocalDate expirationDate,
                    String note, String documentCode, String status) {
        this.documentCode = documentCode;
        this.title = title;
        this.status = status;
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.expirationDate = expirationDate;
        this.note = note;
        this.department = department;
        this.area = area;
        this.category = category;
    }

    // ==========================
    // Getters & Setters
    // ==========================
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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
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

    @JsonIgnore
    public List<RequestDocument> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestDocument> requests) {
        this.requests = requests;
    }
}
