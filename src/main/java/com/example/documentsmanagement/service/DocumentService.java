package com.example.documentsmanagement.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentRepository;


@Service
@Transactional
public class DocumentService {


    private final DocumentRepository repository;


    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }


    public List<Document> findAll() {
        return repository.findAll();
    }


    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }


    public Document create(Document document) {
        // Ensure id is null so JPA will create a new entity
        document.setIdDocument(null);
        return repository.save(document);
    }


    public Optional<Document> update(Long id, Document incoming) {
        return repository.findById(id).map(existing -> {
            // Copy allowed fields (adjust according to your model fields)
            existing.setTitle(incoming.getTitle());
            existing.setDescription(incoming.getDescription());
            existing.setCreatedDate(incoming.getCreatedDate());
            // add other setters if your concrete Document subclasses include them
            return repository.save(existing);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Document> search(String q) {
        return repository.searchByTitle(q == null ? "" : q);
    }

    public long count() {
        return repository.count();
    }
}