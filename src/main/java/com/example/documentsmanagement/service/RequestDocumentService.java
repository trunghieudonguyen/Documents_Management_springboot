package com.example.documentsmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.repository.RequestDocumentRepository;

@Service
@Transactional
public class RequestDocumentService {

    private final RequestDocumentRepository repository;

    public RequestDocumentService(RequestDocumentRepository repository) {
        this.repository = repository;
    }

    public RequestDocument create(RequestDocument entity) {
        return repository.save(entity);
    }

    public Optional<RequestDocument> getById(Long id) {
        return repository.findById(id);
    }

    public List<RequestDocument> listAll() {
        return repository.findAll();
    }

    public RequestDocument update(Long id, RequestDocument changes) {
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("idRequestDocument");
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

    public List<RequestDocument> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }
}
