package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.DocumentCategory;
import com.example.documentsmanagement.model.DocumentDuration;
import com.example.documentsmanagement.model.RequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link DocumentDuration} entities.
 * Provides CRUD operations and custom query methods.
 */

public interface DocumentDurationRepository extends JpaRepository<DocumentDuration, Long>{

    List<DocumentDuration> findByRequestDocument(RequestDocument requestDocument);

    List<DocumentDuration> findByRequestDocument_IdRequestDocument(Long idRequestDocument);


    List<DocumentDuration> findByDurationDate(LocalDate DurationDate);


    List<DocumentDuration> findByDurationCountGreaterThanEqual(Integer count);


    @Query("SELECT COUNT(de) FROM DocumentDuration de WHERE de.requestDocument.idRequestDocument = :idRequestDocument")
    long countDurationsByRequestDocumentId(Long idRequestDocument);


    @Query("SELECT de FROM DocumentDuration de WHERE de.requestDocument.idRequestDocument = :idRequestDocument ORDER BY de.durationDate DESC LIMIT 1")
    DocumentDuration findLatestDurationByRequestDocumentId(Long idRequestDocument);
}