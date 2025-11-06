package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.RequestDocument;

import java.time.LocalDate;
import java.util.List;

public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {
    @Query("SELECT r FROM RequestDocument r " +
            "WHERE LOWER(r.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.signer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RequestDocument> searchByKeyword(String keyword);

    // Lấy danh sách phieu muon sắp hết hạn trong khoảng ngày và hết hạn
    List<RequestDocument> findByReturnDeadlineBetween(LocalDate start, LocalDate end);
    List<RequestDocument> findByReturnDeadlineBefore(LocalDate date);

    @Query("""
        SELECT r FROM RequestDocument r
        JOIN FETCH r.documents d
        WHERE d.idDocument = :documentId
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findHistoryByDocumentId(Long documentId);



}