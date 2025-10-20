package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.DocumentSet;
import java.util.List;

public interface DocumentSetRepository extends JpaRepository<DocumentSet, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        select e from DocumentSet e where lower(COALESCE(e.documentCode, '')) like lower(concat('%', :q, '%'))
    """)
    List<DocumentSet> searchByName(@Param("q") String q);
}
