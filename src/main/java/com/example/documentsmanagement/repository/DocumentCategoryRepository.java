package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.DocumentCategory;
import java.util.List;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        SELECT c FROM DocumentCategory c WHERE LOWER(c.sign) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<DocumentCategory> searchByName(@Param("q") String q);
}
