package com.example.documentsmanagement.service;

import ch.qos.logback.core.boolex.Matcher;
import com.example.documentsmanagement.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import com.example.documentsmanagement.model.Borrower;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.LibrarianRepository;

@Service
@Transactional
public class LibrarianService {

    private final LibrarianRepository repository;

    public LibrarianService(LibrarianRepository repository) {
        this.repository = repository;
    }

    public Librarian create(Librarian entity) {
        return repository.save(entity);
    }

    public Optional<Librarian> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<Librarian> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<Librarian> listAll() {
        return repository.findAll();
    }

    public Librarian update(Long id, Librarian changes) {
        Optional<Librarian> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thủ thư với ID: " + id);
        }

        if (changes.getStaffCode() != null && !changes.getStaffCode().trim().isEmpty()) {
            Optional<Librarian> duplicate = repository.findByStaffCode(changes.getStaffCode().trim());
            if (duplicate.isPresent() && !duplicate.get().getIdLibrarian().equals(id)) {
                throw new RuntimeException("Số hiệu trùng với " + duplicate.get().getFullName());
            }
        }

        Librarian existing = existingOpt.get();

        // Cập nhật các trường cho phép thay đổi
        existing.setFullName(changes.getFullName());
        existing.setPhoneNumber(changes.getPhoneNumber());
        existing.setEmail(changes.getEmail());
        existing.setRank(changes.getRank());
        existing.setPosition(changes.getPosition());
        existing.setStaffCode(changes.getStaffCode());
        existing.setUsername(changes.getUsername());

        return repository.save(existing);
    }

    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Librarian librarian = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        oldPassword = PasswordUtil.sha256Hex(oldPassword).toUpperCase();
        newPassword = PasswordUtil.sha256Hex(newPassword).toUpperCase();
        //So sánh mật khẩu cũ đã băm với DB
        if (!oldPassword.equals(librarian.getPassword())) {
            return false;
        }
        // Nếu khớp → cập nhật mật khẩu mới (đã băm)
        librarian.setPassword(newPassword);
        repository.save(librarian);
        return true;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Librarian> search(String q) {
        return repository.searchByName(q);
    }

    // Example statistics: count
    public long count() {
        return repository.count();
    }

}