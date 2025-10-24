package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.DocumentDuration;
import com.example.documentsmanagement.repository.DocumentDurationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class providing business logic for managing {@link DocumentDuration} entities.
 */
@Service
public class DocumentDurationService {

    private final DocumentDurationRepository repository;

    @Autowired
    public DocumentDurationService(DocumentDurationRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieve all DocumentDuration records.
     *
     * @return list of all document extensions
     */
    public List<DocumentDuration> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieve a DocumentDuration by its ID.
     *
     * @param id ID of the document extension
     * @return DocumentDuration found
     * @throws EntityNotFoundException if not found
     */
    public DocumentDuration getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentDuration not found with ID: " + id));
    }

    /**
     * Create a new DocumentDuration record.
     *
     * @param documentDuration entity to create
     * @return saved DocumentDuration
     */
    public DocumentDuration create(DocumentDuration documentDuration) {
        return repository.save(documentDuration);
    }

    /**
     * Update an existing DocumentDuration record.
     *
     * @param id                ID of the record to update
     * @param documentDuration new values to apply
     * @return updated DocumentDuration
     * @throws EntityNotFoundException if the record does not exist
     */
    public DocumentDuration update(Long id, DocumentDuration documentDuration) {
        DocumentDuration existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentDuration not found with ID: " + id));

        existing.setExtensionCount(documentDuration.getExtensionCount());
        existing.setExtensionDate(documentDuration.getExtensionDate());
        existing.setAttachedImage(documentDuration.getAttachedImage());
        existing.setRequestDocument(documentDuration.getRequestDocument());

        return repository.save(existing);
    }

    /**
     * Delete a DocumentDuration by ID.
     *
     * @param id ID of the document extension to delete
     * @throws EntityNotFoundException if not found
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("DocumentDuration not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Get all extensions associated with a specific RequestDocument ID.
     *
     * @param requestDocumentId ID of the RequestDocument
     * @return list of extensions related to the RequestDocument
     */
    public List<DocumentDuration> getByRequestDocumentId(Long requestDocumentId) {
        return repository.findByRequestDocument_IdRequestDocument(requestDocumentId);
    }

    /**
     * Count how many extensions a specific RequestDocument has.
     *
     * @param requestDocumentId ID of the RequestDocument
     * @return number of extensions
     */
    public long countExtensionsForDocument(Long requestDocumentId) {
        return repository.countExtensionsByRequestDocumentId(requestDocumentId);
    }
}
