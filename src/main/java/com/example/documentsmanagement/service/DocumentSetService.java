package com.example.documentsmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.DocumentSet;
import com.example.documentsmanagement.repository.DocumentSetRepository;

@Service
@Transactional
public class DocumentSetService {

    private final DocumentSetRepository repository;

    public DocumentSetService(DocumentSetRepository repository) {
        this.repository = repository;
    }

    public DocumentSet create(DocumentSet entity) {
        return repository.save(entity);
    }

    public Optional<DocumentSet> getById(Long id) {
        return repository.findById(id);
    }

    public List<DocumentSet> listAll() {
        return repository.findAll();
    }

    public DocumentSet update(Long id, DocumentSet changes) {
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("idDocumentSet");
            idField.setAccessible(true);
            Object val = idField.get(changes);
            if (val == null) {
                // set id value reflectively
                idField.set(changes, id);
            }
        } catch (Exception ex) {
            // If reflection fails, ignore; repository.save will still work if entity has id set.
        }
        return repository.save(changes);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<DocumentSet> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }
}
