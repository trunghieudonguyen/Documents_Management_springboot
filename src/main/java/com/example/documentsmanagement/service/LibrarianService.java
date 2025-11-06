package com.example.documentsmanagement.service;

import ch.qos.logback.core.boolex.Matcher;
import com.example.documentsmanagement.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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
        // Basic update: ensure id is set on entity and save.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("idLibrarian");
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