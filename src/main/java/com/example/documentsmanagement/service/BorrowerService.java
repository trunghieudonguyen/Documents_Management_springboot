package com.example.documentsmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.Borrower;
import com.example.documentsmanagement.repository.BorrowerRepository;

@Service
@Transactional
public class BorrowerService {

    private final BorrowerRepository repository;

    public BorrowerService(BorrowerRepository repository) {
        this.repository = repository;
    }

    public Borrower create(Borrower entity) {
        return repository.save(entity);
    }

    public Optional<Borrower> getById(Long id) {
        return repository.findById(id);
    }

    public List<Borrower> listAll() {
        return repository.findAll();
    }

    public Borrower update(Long id, Borrower changes) {
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("id_borrower");
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

    public List<Borrower> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }
}
