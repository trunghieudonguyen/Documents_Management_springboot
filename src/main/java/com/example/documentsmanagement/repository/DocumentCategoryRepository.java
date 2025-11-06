package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

    /** 🔍 Tìm kiếm theo ký hiệu (sign) hoặc nội dung (content) */
    @Query("""
        SELECT c FROM DocumentCategory c
        WHERE LOWER(c.sign) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<DocumentCategory> searchByKeyword(@Param("keyword") String keyword);

    /** 🔹 Tìm theo ký hiệu */
    Optional<DocumentCategory> findBySign(String sign);

    /** ⚙️ Kiểm tra trùng ký hiệu */
    boolean existsBySign(String sign);

    /** 🔹 Tìm theo ID cột idDocumentCategory (nếu cần tên cụ thể) */
    Optional<DocumentCategory> findByIdDocumentCategory(Long idDocumentCategory);
}
