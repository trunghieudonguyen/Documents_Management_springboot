package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.RequestDocument;
import java.util.List;

public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {
    @Query("SELECT r FROM RequestDocument r " +
            "WHERE LOWER(r.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.signer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RequestDocument> searchByKeyword(String keyword);
}
