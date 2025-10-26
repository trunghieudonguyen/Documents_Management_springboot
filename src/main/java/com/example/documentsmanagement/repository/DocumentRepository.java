package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // =========================================================================
    // 🔍 TÌM KIẾM TOÀN VĂN (title, description, documentCode) - không phân biệt hoa thường
    // =========================================================================
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.documentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Document> searchByTitle(@Param("keyword") String keyword);

    // =========================================================================
    // ✅ KIỂM TRA SỰ TỒN TẠI CỦA DOCUMENT CODE (tránh trùng mã)
    // =========================================================================
    boolean existsByDocumentCode(String documentCode);

    // =========================================================================
    // 📋 LỌC THEO TRẠNG THÁI
    // =========================================================================
    List<Document> findByStatus(String status);

    // =========================================================================
    // 🏢 LỌC THEO PHÒNG BAN
    // =========================================================================
    List<Document> findByDepartment(String department);

    // =========================================================================
    // 🗂️ LỌC THEO DANH MỤC CATEGORY
    // =========================================================================
    @Query("SELECT d FROM Document d WHERE d.category.idDocumentCategory = :categoryId")
    List<Document> findByCategoryId(@Param("categoryId") Long categoryId);

    // =========================================================================
    // ⚡ TỐI ƯU: LẤY MÃ DOCUMENT LỚN NHẤT THEO PREFIX
    // -------------------------------------------------------------------------
    // Mục đích: phục vụ hàm sinh documentCode trong DocumentService.
    // Query này chỉ tìm document có mã bắt đầu bằng prefix (VD: "HC23P3A")
    // và lấy ra mã lớn nhất theo thứ tự chữ, cực nhanh nếu có index.
    // =========================================================================
    @Query("SELECT MAX(d.documentCode) FROM Document d WHERE d.documentCode LIKE CONCAT(:prefix, '%')")
    String findMaxDocumentCodeByPrefix(@Param("prefix") String prefix);

    // =========================================================================
    // ⚙️ GỢI Ý (tùy chọn):
    // Có thể thêm index trong database để tối ưu truy vấn findMaxDocumentCodeByPrefix:
    // ALTER TABLE documents ADD INDEX idx_document_code (document_code);
    // =========================================================================
}
