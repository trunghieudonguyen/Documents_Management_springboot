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
        Optional<Borrower> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người mượn với ID: " + id);
        }

        Borrower existing = existingOpt.get();
        existing.setFullName(changes.getFullName());
        existing.setDepartment(changes.getDepartment());
        existing.setPosition(changes.getPosition());
        existing.setIdCardNumber(changes.getIdCardNumber());
        existing.setPhoneNumber(changes.getPhoneNumber());
        existing.setEmployeeCode(changes.getEmployeeCode());
        return repository.save(existing);
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
