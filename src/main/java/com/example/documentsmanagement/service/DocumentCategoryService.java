package com.example.documentsmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.DocumentCategory;
import com.example.documentsmanagement.repository.DocumentCategoryRepository;

@Service
@Transactional
public class DocumentCategoryService {

    private final DocumentCategoryRepository repository;

    public DocumentCategoryService(DocumentCategoryRepository repository) {
        this.repository = repository;
    }

    public DocumentCategory create(DocumentCategory entity) {
        return repository.save(entity);
    }

    public Optional<DocumentCategory> getById(Long id) {
        return repository.findById(id);
    }

    public List<DocumentCategory> listAll() {
        return repository.findAll();
    }

    public DocumentCategory update(Long id, DocumentCategory changes) {
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("idDocument_Category");
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

    public List<DocumentCategory> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }
}
