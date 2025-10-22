package com.example.documentsmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.Document;
import java.util.List;


public interface DocumentRepository extends JpaRepository<Document, Long> {


    @Query("""
SELECT d FROM Document d WHERE LOWER(COALESCE(d.title, '')) LIKE LOWER(CONCAT('%', :q, '%'))
""")
    List<Document> searchByTitle(@Param("q") String q);
}