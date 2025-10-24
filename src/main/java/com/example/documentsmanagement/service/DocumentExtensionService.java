package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.DocumentExtension;
import com.example.documentsmanagement.repository.DocumentExtensionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class providing business logic for managing {@link DocumentExtension} entities.
 */
@Service
public class DocumentExtensionService {

    private final DocumentExtensionRepository repository;

    @Autowired
    public DocumentExtensionService(DocumentExtensionRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieve all DocumentExtension records.
     *
     * @return list of all document extensions
     */
    public List<DocumentExtension> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieve a DocumentExtension by its ID.
     *
     * @param id ID of the document extension
     * @return DocumentExtension found
     * @throws EntityNotFoundException if not found
     */
    public DocumentExtension getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentExtension not found with ID: " + id));
    }

    /**
     * Create a new DocumentExtension record.
     *
     * @param documentExtension entity to create
     * @return saved DocumentExtension
     */
    public DocumentExtension create(DocumentExtension documentExtension) {
        return repository.save(documentExtension);
    }

    /**
     * Update an existing DocumentExtension record.
     *
     * @param id                ID of the record to update
     * @param documentExtension new values to apply
     * @return updated DocumentExtension
     * @throws EntityNotFoundException if the record does not exist
     */
    public DocumentExtension update(Long id, DocumentExtension documentExtension) {
        DocumentExtension existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentExtension not found with ID: " + id));

        existing.setExtensionCount(documentExtension.getExtensionCount());
        existing.setExtensionDate(documentExtension.getExtensionDate());
        existing.setAttachedImage(documentExtension.getAttachedImage());
        existing.setRequestDocument(documentExtension.getRequestDocument());

        return repository.save(existing);
    }

    /**
     * Delete a DocumentExtension by ID.
     *
     * @param id ID of the document extension to delete
     * @throws EntityNotFoundException if not found
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("DocumentExtension not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Get all extensions associated with a specific RequestDocument ID.
     *
     * @param requestDocumentId ID of the RequestDocument
     * @return list of extensions related to the RequestDocument
     */
    public List<DocumentExtension> getByRequestDocumentId(Long requestDocumentId) {
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
