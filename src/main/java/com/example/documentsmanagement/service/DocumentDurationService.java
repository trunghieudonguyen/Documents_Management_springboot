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


    public List<DocumentDuration> getAll() {
        return repository.findAll();
    }


    public DocumentDuration getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentDuration not found with ID: " + id));
    }


    public DocumentDuration create(DocumentDuration documentDuration) {
        return repository.save(documentDuration);
    }


    public DocumentDuration update(Long id, DocumentDuration documentDuration) {
        DocumentDuration existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentDuration not found with ID: " + id));

        existing.setDurationCount(documentDuration.getDurationCount());
        existing.setDurationDate(documentDuration.getDurationDate());
        existing.setAttachedImage(documentDuration.getAttachedImage());
        existing.setRequestDocument(documentDuration.getRequestDocument());

        return repository.save(existing);
    }


    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("DocumentDuration not found with ID: " + id);
        }
        repository.deleteById(id);
    }


    public List<DocumentDuration> getByRequestDocumentId(Long requestDocumentId) {
        return repository.findByRequestDocument_IdRequestDocument(requestDocumentId);
    }


    public long countDurationsForDocument(Long requestDocumentId) {
        return repository.countDurationsByRequestDocumentId(requestDocumentId);
    }
}
