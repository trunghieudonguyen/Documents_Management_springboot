package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.PersonDocument;
import java.util.List;

public interface PersonDocumentRepository extends JpaRepository<PersonDocument, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        select e from PersonDocument e where lower(COALESCE(e.personName, '')) like lower(concat('%', :q, '%'))
    """)
    List<PersonDocument> searchByName(@Param("q") String q);
}
