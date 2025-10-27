package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.repository.RequestDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RequestDocumentService {

    private final RequestDocumentRepository repository;

    public RequestDocumentService(RequestDocumentRepository repository) {
        this.repository = repository;
    }

    // Lấy toàn bộ danh sách
    public List<RequestDocument> findAll() {
        return repository.findAll();
    }

    // Tìm theo ID
    public Optional<RequestDocument> findById(Long id) {
        return repository.findById(id);
    }

    // Thêm mới yêu cầu mượn tài liệu
    public RequestDocument create(RequestDocument requestDocument) {
        requestDocument.setIdRequestDocument(null); // đảm bảo tạo mới
        return repository.save(requestDocument);
    }

    // Cập nhật yêu cầu mượn
    public Optional<RequestDocument> update(Long id, RequestDocument incoming) {
        return repository.findById(id).map(existing -> {
            existing.setDocumentNumber(incoming.getDocumentNumber());
            existing.setBorrowDate(incoming.getBorrowDate());
            existing.setCopyType(incoming.getCopyType());
            existing.setReturnDeadline(incoming.getReturnDeadline());
            existing.setExtensionCount(incoming.getExtensionCount());
            existing.setSigner(incoming.getSigner());
            existing.setAttachmentPath(incoming.getAttachmentPath());
            existing.setLibrarian(incoming.getLibrarian());
            existing.setBorrower(incoming.getBorrower());
            existing.setDocument(incoming.getDocument());
            existing.setReturnDate(incoming.getReturnDate());
            return repository.save(existing);
        });
    }

    // Xóa yêu cầu mượn
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Tìm kiếm theo mã số tài liệu hoặc người ký
    public List<RequestDocument> search(String keyword) {
        return repository.searchByKeyword(keyword == null ? "" : keyword);
    }

    // Đếm tổng số yêu cầu
    public long count() {
        return repository.count();
    }
}
