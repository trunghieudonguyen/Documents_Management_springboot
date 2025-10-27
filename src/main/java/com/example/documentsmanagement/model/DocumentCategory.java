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

    // Ký hiệu danh mục (ví dụ: QĐ, CV, HD...)
    @Column(name = "SIGN", nullable = false, length = 50)
    private String sign;

    // Nội dung mô tả danh mục tài liệu
    @Column(name = "CONTENT", nullable = false, length = 255)
    private String content;

    // Thời hạn lưu trữ (tính bằng năm)
    @Column(name = "DURATION")
    private Integer duration;

    // Ghi chú thêm (nếu có)
    @Column(name = "NOTE", length = 255)
    private String note;

    // Một danh mục có thể chứa nhiều tài liệu
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Duy trì liên kết chính khi serialize JSON để tránh vòng lặp vô hạn
    private List<Document> documents = new ArrayList<>();

    // =========================================================
    // CONSTRUCTORS — Hàm khởi tạo
    // =========================================================
    public DocumentCategory() {}

    public DocumentCategory(String sign, String content, Integer duration, String note) {
        this.sign = sign;
        this.content = content;
        this.duration = duration;
        this.note = note;
    }

    // =========================================================
    // GETTERS & SETTERS — Các phương thức truy cập
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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
    /** Thêm tài liệu vào danh mục */
    public void addDocument(Document document) {
        documents.add(document);
        document.setCategory(this);
    }

    /** Xóa tài liệu khỏi danh mục */
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setCategory(null);
    }
}
