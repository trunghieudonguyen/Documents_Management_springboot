package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.DocumentDuration;
import com.example.documentsmanagement.service.DocumentDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/document-extensions")
@CrossOrigin(origins = "http://localhost:3000")

public class DocumentDurationController {

    private final DocumentDurationService service;

    /**
     * Constructor for dependency injection of DocumentDurationService.
     *
     * @param service the DocumentDurationService instance
     */
    @Autowired
    public DocumentDurationController(DocumentDurationService service) {
        this.service = service;
    }

    /**
     * Get all DocumentDuration records.
     *
     * @return List of all DocumentDuration entities
     */
    @GetMapping
    public ResponseEntity<List<DocumentDuration>> getAll() {
        List<DocumentDuration> extensions = service.getAll();
        return ResponseEntity.ok(extensions);
    }

    /**
     * Get a specific DocumentDuration by its ID.
     *
     * @param id the ID of the DocumentDuration
     * @return DocumentDuration entity if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDuration> getById(@PathVariable Long id) {
        DocumentDuration documentExtension = service.getById(id);
        if (documentExtension != null) {
            return ResponseEntity.ok(documentExtension);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new DocumentDuration entity.
     *
     * @param documentExtension the DocumentDuration object to create
     * @return the created DocumentDuration entity with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<DocumentDuration> create(@RequestBody DocumentDuration documentExtension) {
        DocumentDuration created = service.create(documentExtension);
        return ResponseEntity
                .created(URI.create("/api/document-extensions/" + created.getIdDocumentDuration()))
                .body(created);
    }

    /**
     * Update an existing DocumentDuration entity by ID.
     *
     * @param id                 the ID of the DocumentDuration to update
     * @param documentExtension  the updated DocumentDuration object
     * @return the updated DocumentDuration entity with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentDuration> update(@PathVariable Long id, @RequestBody DocumentDuration documentExtension) {
        DocumentDuration updated = service.update(id, documentExtension);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a DocumentDuration by its ID.
     *
     * @param id the ID of the DocumentDuration to delete
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all DocumentDuration records associated with a specific DocumentRequest.
     *
     * @param requestId the ID of the related DocumentRequest
     * @return List of DocumentDuration entities linked to the given DocumentRequest
     */
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<DocumentDuration>> getByDocumentRequestId(@PathVariable Long requestId) {
        List<DocumentDuration> extensions = service.getByRequestDocumentId(requestId);
        if (extensions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(extensions);
    }
}
