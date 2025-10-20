package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.RequestDocument;
import java.util.List;

public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        select e from RequestDocument e where lower(COALESCE(e.documentNumber, '')) like lower(concat('%', :q, '%'))
    """)
    List<RequestDocument> searchByName(@Param("q") String q);
}
