package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    // 🟢 Lấy tất cả tài liệu
    public List<Document> findAll() {
        return repository.findAll();
    }

    // 🟢 Lấy tài liệu theo ID
    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    // 🟢 Tạo mới tài liệu
    public Document create(Document document) {
        document.setIdDocument(null); // đảm bảo là entity mới
        return repository.save(document);
    }

    // 🟢 Cập nhật tài liệu
    public Optional<Document> update(Long id, Document updatedData) {
        return repository.findById(id).map(existing -> {
            existing.setDocumentCode(updatedData.getDocumentCode());
            existing.setTitle(updatedData.getTitle());
            existing.setDescription(updatedData.getDescription());
            existing.setStatus(updatedData.getStatus());
            existing.setCreatedDate(updatedData.getCreatedDate());
            existing.setEventDate(updatedData.getEventDate());
            existing.setNote(updatedData.getNote());
            existing.setDepartment(updatedData.getDepartment());
            existing.setArea(updatedData.getArea());
            existing.setCategory(updatedData.getCategory());
            return repository.save(existing);
        });
    }

    // 🟢 Xóa tài liệu theo ID
    public void delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Không tìm thấy tài liệu có ID: " + id);
        }
    }

    // 🟢 Tìm kiếm tài liệu theo tiêu đề hoặc mã (nếu repository có phương thức tìm kiếm)
    public List<Document> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        return repository.searchByTitle(keyword);
    }

    // 🟢 Đếm tổng số tài liệu trong hệ thống
    public long count() {
        return repository.count();
    }

    // 🟢 Kiểm tra tồn tại theo mã tài liệu
    public boolean existsByCode(String documentCode) {
        return repository.existsByDocumentCode(documentCode);
    }

    // 🟢 Lấy danh sách tài liệu theo trạng thái
    public List<Document> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    // 🟢 Lấy danh sách tài liệu theo phòng ban
    public List<Document> findByDepartment(String department) {
        return repository.findByDepartment(department);
    }
}
