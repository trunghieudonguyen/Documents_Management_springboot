package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Signer;
import com.example.documentsmanagement.repository.SignerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SignerService {
    private final SignerRepository repository;

    public SignerService(SignerRepository repository) {
        this.repository = repository;
    }

    public Signer create(Signer signer) { return repository.save(signer); }
    public List<Signer> findAll() { return repository.findAll(); }
    public Optional<Signer> findById(Long id) { return repository.findById(id); }
    public List<Signer> search(String q) { return repository.search(q); }
    
    public Signer update(Long id, Signer changes) {
        Signer existing = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy người ký"));

        if (changes.getStaffCode() != null && !changes.getStaffCode().trim().isEmpty()) {
            Optional<Signer> duplicate = repository.findByStaffCode(changes.getStaffCode().trim());
            if (duplicate.isPresent() && !duplicate.get().getIdSigner().equals(id)) {
                throw new RuntimeException("Số hiệu trùng với " + duplicate.get().getFullName());
            }
        }

        existing.setFullName(changes.getFullName());
        existing.setStaffCode(changes.getStaffCode());
        existing.setDepartment(changes.getDepartment());
        existing.setRank(changes.getRank());
        existing.setPosition(changes.getPosition());
        existing.setPhoneNumber(changes.getPhoneNumber());
        return repository.save(existing);
    }

    public void delete(Long id) { repository.deleteById(id); }
}