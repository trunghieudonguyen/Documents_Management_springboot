package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // Lấy danh sách document sắp hết hạn trong khoảng ngày và hết hạn
    List<Document> findByExpirationDateBetween(LocalDate start, LocalDate end);
    List<Document> findByExpirationDateBefore(LocalDate date);


    List<Document> findByStatus(String status);


    List<Document> findByDepartment(String department);


    List<Document> findByCategory_IdDocumentCategory(Long categoryId);


    @Query("""
        SELECT MAX(d.documentCode)
        FROM Document d
        WHERE d.documentCode LIKE CONCAT(:prefix, '%')
    """)
    String findMaxDocumentCodeByPrefix(@Param("prefix") String prefix);


    boolean existsByDocumentCode(String documentCode);
}
