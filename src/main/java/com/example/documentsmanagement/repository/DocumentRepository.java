package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // =========================================================
    // 🔍 TÌM KIẾM TOÀN VĂN (title, code, department, area, status)
    // Không phân biệt hoa thường
    // =========================================================
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.documentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.area) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.status) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    // =========================================================
    // 🔹 LỌC THEO TRẠNG THÁI
    // =========================================================
    List<Document> findByStatus(String status);

    // =========================================================
    // 🔹 LỌC THEO PHÒNG BAN
    // =========================================================
    List<Document> findByDepartment(String department);

    // =========================================================
    // 🔹 LỌC THEO DANH MỤC (Category)
    // Tự động nối quan hệ @ManyToOne(category)
    // =========================================================
    List<Document> findByCategory_IdDocumentCategory(Long categoryId);

    // =========================================================
    // ⚡ LẤY MÃ DOCUMENT LỚN NHẤT THEO PREFIX (phục vụ sinh mã tự động)
    // Nên tạo index cho cột DOCUMENT_CODE để tối ưu:
    // CREATE INDEX idx_document_code ON DOCUMENT (DOCUMENT_CODE);
    // =========================================================
    @Query("""
        SELECT MAX(d.documentCode)
        FROM Document d
        WHERE d.documentCode LIKE CONCAT(:prefix, '%')
    """)
    String findMaxDocumentCodeByPrefix(@Param("prefix") String prefix);

    // =========================================================
    // 🔹 KIỂM TRA MÃ DOCUMENT TRÙNG
    // =========================================================
    boolean existsByDocumentCode(String documentCode);
}
