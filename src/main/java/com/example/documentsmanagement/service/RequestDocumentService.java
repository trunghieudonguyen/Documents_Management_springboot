package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.repository.RequestDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< HEAD
=======
import com.example.documentsmanagement.model.Borrower;
import com.example.documentsmanagement.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
>>>>>>> origin/main

import java.util.List;
import java.util.Optional;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentRepository;

import java.util.stream.Collectors;

@Service
@Transactional
public class RequestDocumentService {

    private final RequestDocumentRepository repository;
    private final DocumentRepository documentRepository;

<<<<<<< HEAD
    public RequestDocumentService(RequestDocumentRepository repository, DocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
=======
    private final BorrowerRepository borrowerRepository;

    public RequestDocumentService(RequestDocumentRepository repository,
                                  DocumentRepository documentRepository,
                                  BorrowerRepository borrowerRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
        this.borrowerRepository = borrowerRepository;
>>>>>>> origin/main
    }

    // Lấy toàn bộ danh sách
    public List<RequestDocument> findAll() {
        return repository.findAll();
    }

    // Tìm theo ID
    public Optional<RequestDocument> findById(Long id) {
        return repository.findById(id);
    }

    private void hydrateDocuments(RequestDocument requestDocument) {
        if (requestDocument.getDocuments() != null) {
            List<Long> docIds = requestDocument.getDocuments().stream()
                    .map(Document::getIdDocument)
                    .collect(Collectors.toList());

            List<Document> fullDocuments = documentRepository.findAllById(docIds);
            requestDocument.setDocuments(fullDocuments);
        }
    }

    // Thêm mới yêu cầu mượn tài liệu
    public RequestDocument create(RequestDocument requestDocument) {
<<<<<<< HEAD
        requestDocument.setIdRequestDocument(null); // đảm bảo tạo mới
        hydrateDocuments(requestDocument);
=======
        requestDocument.setIdRequestDocument(null);

        // --- Xử lý Borrower ---
        Borrower borrowerToSave = null;
        if (requestDocument.getBorrower() != null) {
            // Frontend gửi ID người mượn đã có
            if (requestDocument.getBorrower().getId_borrower() != null) {
                Long borrowerId = requestDocument.getBorrower().getId_borrower();
                borrowerToSave = borrowerRepository.findById(borrowerId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Borrower với ID: " + borrowerId));
            }
            // Frontend gửi thông tin người mượn mới (không có ID)
            else if (requestDocument.getBorrower().getEmployeeCode() != null) {
                Optional<Borrower> existingByCode = borrowerRepository.findByEmployeeCode(requestDocument.getBorrower().getEmployeeCode());
                if (existingByCode.isPresent()) {
                    borrowerToSave = existingByCode.get();
                    System.out.println("Sử dụng Borrower đã tồn tại với mã: " + borrowerToSave.getEmployeeCode());
                } else {
                    Borrower newBorrowerData = requestDocument.getBorrower();
                    newBorrowerData.setId_borrower(null);
                    borrowerToSave = borrowerRepository.save(newBorrowerData);
                    System.out.println("Đã tạo Borrower mới với mã: " + borrowerToSave.getEmployeeCode());
                }
            } else {
                throw new IllegalArgumentException("Thiếu thông tin ID hoặc Mã cán bộ (Employee Code) của người mượn.");
            }
        } else {
            throw new IllegalArgumentException("Thiếu thông tin người mượn (Borrower).");
        }
        requestDocument.setBorrower(borrowerToSave);
        requestDocument.setNote(requestDocument.getNote());

        hydrateDocuments(requestDocument);

>>>>>>> origin/main
        return repository.save(requestDocument);
    }

    // Cập nhật yêu cầu mượn
    public Optional<RequestDocument> update(Long id, RequestDocument incoming) {
        return repository.findById(id).map(existing -> {
<<<<<<< HEAD
            existing.setDocumentNumber(incoming.getDocumentNumber());
            existing.setBorrowDate(incoming.getBorrowDate());
            existing.setCopyType(incoming.getCopyType());
            existing.setReturnDeadline(incoming.getReturnDeadline());
            existing.setExtensionCount(incoming.getExtensionCount());
            existing.setSigner(incoming.getSigner());
            existing.setAttachmentPath(incoming.getAttachmentPath());
            existing.setLibrarian(incoming.getLibrarian());
            existing.setBorrower(incoming.getBorrower());
            existing.setDocuments(incoming.getDocuments());
            existing.setReturnDate(incoming.getReturnDate());
=======
            if (incoming.getReturnDeadline() != null) existing.setReturnDeadline(incoming.getReturnDeadline());
            existing.setExtensionCount(incoming.getExtensionCount());
            if (incoming.getReturnDate() != null) existing.setReturnDate(incoming.getReturnDate());
            if (incoming.getSigner() != null) existing.setSigner(incoming.getSigner());
            if (incoming.getAttachmentPath() != null) existing.setAttachmentPath(incoming.getAttachmentPath());
            if (incoming.getCopyType() != null) existing.setCopyType(incoming.getCopyType());

            if (incoming.getBorrower() != null) {
                Borrower borrowerToSet = null;
                if (incoming.getBorrower().getId_borrower() != null) {
                    borrowerToSet = borrowerRepository.findById(incoming.getBorrower().getId_borrower())
                            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Borrower ID: " + incoming.getBorrower().getId_borrower()));
                }

                if (borrowerToSet != null) {
                    existing.setBorrower(borrowerToSet);
                }
            }

            if (incoming.getDocuments() != null) {
                hydrateDocuments(incoming);
                existing.setDocuments(incoming.getDocuments());
            }

            if (incoming.getNote() != null || existing.getNote() != null) {
                existing.setNote(incoming.getNote());
            }

>>>>>>> origin/main
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
