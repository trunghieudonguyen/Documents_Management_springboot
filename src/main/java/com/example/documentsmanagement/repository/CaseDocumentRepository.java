package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.CaseDocument;
import java.util.List;

public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        select e from CaseDocument e where lower(COALESCE(e.title, '')) like lower(concat('%', :q, '%'))
    """)
    List<CaseDocument> searchByName(@Param("q") String q);
}
