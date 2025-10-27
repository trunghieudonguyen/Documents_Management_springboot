package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /** 🔍 Tìm kiếm toàn văn (title, code, department, area, status) */
    @Query("""
        SELECT d FROM Document d
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.documentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.area) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.status) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    /** 🔹 Lọc theo trạng thái */
    List<Document> findByStatus(String status);

    /** 🔹 Lọc theo phòng ban */
    List<Document> findByDepartment(String department);

    /** 🔹 Lọc theo danh mục (DocumentCategory) */
    @Query("SELECT d FROM Document d WHERE d.category.idDocumentCategory = :categoryId")
    List<Document> findByCategoryId(@Param("categoryId") Long categoryId);

    /** ⚡ Lấy mã Document lớn nhất theo prefix (để sinh mã tự động) */
    @Query("SELECT MAX(d.documentCode) FROM Document d WHERE d.documentCode LIKE CONCAT(:prefix, '%')")
    String findMaxDocumentCodeByPrefix(@Param("prefix") String prefix);

    /** 🔹 Kiểm tra trùng mã tài liệu */
    boolean existsByDocumentCode(String documentCode);
}
