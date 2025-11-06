package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.RequestDocument;
import com.example.documentsmanagement.repository.RequestDocumentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.documentsmanagement.model.Borrower;
import com.example.documentsmanagement.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;


import java.util.List;
import java.util.Optional;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.repository.DocumentRepository;
import com.example.documentsmanagement.repository.LibrarianRepository;

import java.util.stream.Collectors;

@Service
@Transactional
public class RequestDocumentService {

    private final RequestDocumentRepository repository;
    private final DocumentRepository documentRepository;
    private final BorrowerRepository borrowerRepository;
    private final LibrarianRepository librarianRepository;

    public RequestDocumentService(RequestDocumentRepository repository,
                                  DocumentRepository documentRepository,
                                  BorrowerRepository borrowerRepository,
                                  LibrarianRepository librarianRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
        this.borrowerRepository = borrowerRepository;
        this.librarianRepository = librarianRepository;
    }

    // Lấy toàn bộ danh sách
    public List<RequestDocument> findAll() {
        List<RequestDocument> list = repository.findAll();
        list.forEach(rd -> org.hibernate.Hibernate.initialize(rd.getDocuments()));

        return list;
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

    public List<RequestDocument> findHistoryByDocumentId(Long documentId) {
        return repository.findHistoryByDocumentId(documentId);
    }

    // Thêm mới yêu cầu mượn tài liệu
    public RequestDocument create(RequestDocument requestDocument) {
        requestDocument.setIdRequestDocument(null);

        // --- Xử lý Borrower ---
        Borrower borrowerToSave = null;
        if (requestDocument.getBorrower() != null && requestDocument.getBorrower().getEmployeeCode() != null) {

            String employeeCode = requestDocument.getBorrower().getEmployeeCode();
            Borrower borrowerDataFromRequest = requestDocument.getBorrower();

            Optional<Borrower> existingByCode = borrowerRepository.findByEmployeeCode(employeeCode);

            if (existingByCode.isPresent()) {
                borrowerToSave = existingByCode.get();
                borrowerToSave.setFullName(borrowerDataFromRequest.getFullName());
                borrowerToSave.setDepartment(borrowerDataFromRequest.getDepartment());
                borrowerToSave.setPosition(borrowerDataFromRequest.getPosition());
                borrowerToSave.setIdCardNumber(borrowerDataFromRequest.getIdCardNumber());
                borrowerToSave.setPhoneNumber(borrowerDataFromRequest.getPhoneNumber());

                System.out.println("Đã cập nhật Borrower với mã: " + employeeCode);
            } else {
                borrowerDataFromRequest.setId_borrower(null);
                borrowerToSave = borrowerDataFromRequest;

                System.out.println("Đã tạo Borrower mới với mã: " + employeeCode);
            }

            borrowerToSave = borrowerRepository.save(borrowerToSave);

        } else {
            throw new IllegalArgumentException("Thiếu thông tin người mượn (Borrower) hoặc Mã cán bộ (Employee Code).");
        }

        hydrateDocuments(requestDocument);

        if (requestDocument.getLibrarian() != null && requestDocument.getLibrarian().getIdLibrarian() != null) {
            Long librarianId = requestDocument.getLibrarian().getIdLibrarian();
            Librarian managedLibrarian = librarianRepository.findById(librarianId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Thủ thư (Librarian) với ID: " + librarianId));
            requestDocument.setLibrarian(managedLibrarian);
        } else {
            throw new IllegalArgumentException("Thiếu thông tin Thủ thư (Librarian).");
        }

        // --- Cập nhật trạng thái document ---
        boolean isOriginalBorrow = "original".equalsIgnoreCase(requestDocument.getCopyType());

        if (isOriginalBorrow) {
            if (requestDocument.getDocuments() != null) {
                for (Document doc : requestDocument.getDocuments()) {
                    doc.setStatus("borrowed");
                    documentRepository.save(doc);
                }
            }
        }

        requestDocument.setBorrower(borrowerToSave);
        requestDocument.setNote(requestDocument.getNote());

        return repository.save(requestDocument);
    }

    // Cập nhật yêu cầu mượn
    public Optional<RequestDocument> update(Long id, RequestDocument incoming) {
        return repository.findById(id).map(existing -> {
            boolean isCompleting = (incoming.getReturnDate() != null && existing.getReturnDate() == null);
            if (isCompleting) {
                String copyType = existing.getCopyType();

                if ("original".equalsIgnoreCase(copyType)) {
                    Hibernate.initialize(existing.getDocuments());

                    if (existing.getDocuments() != null) {
                        for (Document doc : existing.getDocuments()) {
                            doc.setStatus("available");
                            documentRepository.save(doc);
                        }
                    }
                }
            }

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

            if (incoming.getNote() != null || existing.getNote() != null) {
                existing.setNote(incoming.getNote());
            }

            Hibernate.initialize(existing.getDocuments());
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