package com.example.documentsmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.CaseDocument;
import com.example.documentsmanagement.repository.CaseDocumentRepository;

@Service
@Transactional
public class CaseDocumentService {

    private final CaseDocumentRepository repository;

    public CaseDocumentService(CaseDocumentRepository repository) {
        this.repository = repository;
    }

    public CaseDocument create(CaseDocument entity) {
        return repository.save(entity);
    }

    public Optional<CaseDocument> getById(Long id) {
        return repository.findById(id);
    }

    public List<CaseDocument> listAll() {
        return repository.findAll();
    }

    public CaseDocument update(Long id, CaseDocument changes) {
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("id");
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

    public List<CaseDocument> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }
}
