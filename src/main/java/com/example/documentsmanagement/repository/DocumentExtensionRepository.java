package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.DocumentExtension;
import com.example.documentsmanagement.model.RequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link DocumentExtension} entities.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface DocumentExtensionRepository extends JpaRepository<DocumentExtension, Long> {

    /**
     * Find all document extensions associated with a specific RequestDocument.
     *
     * @param requestDocument the parent RequestDocument entity
     * @return list of DocumentExtension linked to the given RequestDocument
     */
    List<DocumentExtension> findByRequestDocument(RequestDocument requestDocument);

    /**
     * Find all extensions by requestDocument ID.
     *
     * @param idRequestDocument ID of RequestDocument
     * @return list of DocumentExtension belonging to the RequestDocument
     */
    List<DocumentExtension> findByRequestDocument_IdRequestDocument(Long idRequestDocument);

    /**
     * Find all extensions created on a specific date.
     *
     * @param extensionDate the date of the extension
     * @return list of extensions on that date
     */
    List<DocumentExtension> findByExtensionDate(LocalDate extensionDate);

    /**
     * Find all extensions where the extension count is greater than or equal to a given number.
     *
     * @param count threshold number
     * @return list of extensions with count >= given value
     */
    List<DocumentExtension> findByExtensionCountGreaterThanEqual(Integer count);

    /**
     * Count the number of extensions for a specific RequestDocument ID.
     *
     * @param idRequestDocument ID of RequestDocument
     * @return number of extensions
     */
    @Query("SELECT COUNT(de) FROM DocumentExtension de WHERE de.requestDocument.idRequestDocument = :idRequestDocument")
    long countExtensionsByRequestDocumentId(Long idRequestDocument);

    /**
     * Get the latest extension (most recent date) for a given RequestDocument ID.
     *
     * @param idRequestDocument ID of RequestDocument
     * @return the latest DocumentExtension or null if none
     */
    @Query("SELECT de FROM DocumentExtension de WHERE de.requestDocument.idRequestDocument = :idRequestDocument ORDER BY de.extensionDate DESC LIMIT 1")
    DocumentExtension findLatestExtensionByRequestDocumentId(Long idRequestDocument);
}
