package com.example.documentsmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.Document;
import java.util.List;


public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 🔍 Tìm kiếm theo tiêu đề hoặc mô tả (không phân biệt hoa thường)
    @Query("SELECT d FROM Document d " +
            "WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(d.documentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchByTitle(@Param("keyword") String keyword);

    // 🔍 Kiểm tra tồn tại theo mã tài liệu
    boolean existsByDocumentCode(String documentCode);

    // 🔍 Lấy danh sách tài liệu theo trạng thái
    List<Document> findByStatus(String status);

    // 🔍 Lấy danh sách tài liệu theo phòng ban
    List<Document> findByDepartment(String department);

    // 🔍 Lấy danh sách tài liệu theo mã danh mục
    @Query("SELECT d FROM Document d WHERE d.category.idDocumentCategory = :categoryId")
    List<Document> findByCategoryId(@Param("categoryId") Long categoryId);
}