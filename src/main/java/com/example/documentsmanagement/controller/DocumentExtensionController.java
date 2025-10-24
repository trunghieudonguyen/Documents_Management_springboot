package com.example.documentsmanagement.controller;

import com.example.documentsmanagement.model.DocumentExtension;
import com.example.documentsmanagement.service.DocumentExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing DocumentExtension entities.
 * Provides endpoints for creating, reading, updating, and deleting document extensions.
 */
@RestController
@RequestMapping("/api/document-extensions")
public class DocumentExtensionController {

    private final DocumentExtensionService service;

    /**
     * Constructor for dependency injection of DocumentExtensionService.
     *
     * @param service the DocumentExtensionService instance
     */
    @Autowired
    public DocumentExtensionController(DocumentExtensionService service) {
        this.service = service;
    }

    /**
     * Get all DocumentExtension records.
     *
     * @return List of all DocumentExtension entities
     */
    @GetMapping
    public ResponseEntity<List<DocumentExtension>> getAll() {
        List<DocumentExtension> extensions = service.getAll();
        return ResponseEntity.ok(extensions);
    }

    /**
     * Get a specific DocumentExtension by its ID.
     *
     * @param id the ID of the DocumentExtension
     * @return DocumentExtension entity if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentExtension> getById(@PathVariable Long id) {
        DocumentExtension documentExtension = service.getById(id);
        if (documentExtension != null) {
            return ResponseEntity.ok(documentExtension);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new DocumentExtension entity.
     *
     * @param documentExtension the DocumentExtension object to create
     * @return the created DocumentExtension entity with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<DocumentExtension> create(@RequestBody DocumentExtension documentExtension) {
        DocumentExtension created = service.create(documentExtension);
        return ResponseEntity
                .created(URI.create("/api/document-extensions/" + created.getIdDocumentExtension()))
                .body(created);
    }

    /**
     * Update an existing DocumentExtension entity by ID.
     *
     * @param id                 the ID of the DocumentExtension to update
     * @param documentExtension  the updated DocumentExtension object
     * @return the updated DocumentExtension entity with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentExtension> update(@PathVariable Long id, @RequestBody DocumentExtension documentExtension) {
        DocumentExtension updated = service.update(id, documentExtension);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a DocumentExtension by its ID.
     *
     * @param id the ID of the DocumentExtension to delete
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all DocumentExtension records associated with a specific DocumentRequest.
     *
     * @param requestId the ID of the related DocumentRequest
     * @return List of DocumentExtension entities linked to the given DocumentRequest
     */
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<DocumentExtension>> getByDocumentRequestId(@PathVariable Long requestId) {
        List<DocumentExtension> extensions = service.getByRequestDocumentId(requestId);
        if (extensions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(extensions);
    }
}
