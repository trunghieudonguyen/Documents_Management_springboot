package com.example.documentsmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Mã tài liệu (duy nhất, có thể null khi tạo mới)
    @Column(name = "DOCUMENT_CODE", unique = true, length = 50)
    private String documentCode;

    // Tiêu đề tài liệu
    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    // Trạng thái tài liệu (VD: Active, Expired, Pending, ...)
    @Column(name = "STATUS", length = 50)
    private String status;

    // Ngày tạo tài liệu
    @Column(name = "CREATED_DATE")
    private LocalDate createdDate;

    // Ngày diễn ra sự kiện hoặc ngày ban hành
    @Column(name = "EVENT_DATE")
    private LocalDate eventDate;

    // ✅ Ngày hết hạn tài liệu
    @Column(name = "EXPIRATION_DATE")
    private LocalDate expirationDate;

    // Ghi chú hoặc mô tả chi tiết (dạng văn bản dài)
    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;

    // Phòng ban hoặc đơn vị quản lý tài liệu
    @Column(name = "DEPARTMENT", length = 150)
    private String department;

    // Khu vực hoặc phạm vi áp dụng của tài liệu
    @Column(name = "AREA", length = 150)
    private String area;

    // Mối quan hệ: Nhiều Document thuộc về 1 Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOCUMENT_CATEGORY", referencedColumnName = "ID_DOCUMENT_CATEGORY")
    @JsonBackReference // Ngăn vòng lặp vô hạn khi serialize JSON (Document ↔ Category)
    private DocumentCategory category;

    // Mối quan hệ: 1 Document có thể có nhiều RequestDocument (nếu có bảng này)
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Không trả danh sách RequestDocument khi gọi API Document
    private List<RequestDocument> requests;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
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

    // =========================================================
    // GETTERS & SETTERS
    // =========================================================
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

    public List<RequestDocument> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestDocument> requests) {
        this.requests = requests;
    }
}
