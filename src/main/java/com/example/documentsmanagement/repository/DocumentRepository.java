package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // =========================================================================
    // 🔍 TÌM KIẾM TOÀN VĂN (title, documentCode) - không phân biệt hoa thường
    // =========================================================================
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.documentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.area) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.status) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Document> searchByKeyword(@Param("keyword") String keyword);

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
    // ⚡ LẤY MÃ DOCUMENT LỚN NHẤT THEO PREFIX (tối ưu cho sinh code)
    // =========================================================================
    @Query("SELECT MAX(d.documentCode) FROM Document d WHERE d.documentCode LIKE CONCAT(:prefix, '%')")
    String findMaxDocumentCodeByPrefix(@Param("prefix") String prefix);

    // =========================================================================
    // 🔎 HÀM SEARCH CHUNG CHO SERVICE
    // =========================================================================
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.title) LIKE :keyword
           OR LOWER(d.documentCode) LIKE :keyword
           OR LOWER(d.department) LIKE :keyword
           OR LOWER(d.area) LIKE :keyword
           OR LOWER(d.status) LIKE :keyword
    """)
    List<Document> findDocumentsByKeyword(@Param("keyword") String keyword);
}
