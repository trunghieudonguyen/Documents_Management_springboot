package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.example.documentsmanagement.model.DocumentCategory;
import com.example.documentsmanagement.repository.DocumentCategoryRepository;

@Service
@Transactional
public class DocumentCategoryService {

    private final DocumentCategoryRepository repository;
    private final DocumentRepository documentRepository;

    public DocumentCategoryService(DocumentCategoryRepository repository,  DocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
    }

    public DocumentCategory create(DocumentCategory entity) {
        return repository.save(entity);
    }

    public Optional<DocumentCategory> getById(Long id) {
        return repository.findById(id);
    }

    public List<DocumentCategory> listAll() {
        return repository.findAll();
    }

    public DocumentCategory update(Long id, DocumentCategory changes) {
        // Tìm category cũ
        DocumentCategory existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại hồ sơ với ID: " + id));

        // Cập nhật các trường cần thiết (không ghi đè documents)
        existing.setContent(changes.getContent());
        existing.setNote(changes.getNote());
        existing.setDuration(changes.getDuration());
        existing.setSign(changes.getSign());

        // Lưu lại category
        DocumentCategory updatedCategory = repository.save(existing);
        // Lưu category trước

        // Nếu category có ngày hết hạn mới, cập nhật luôn cho các document cùng category
        if (updatedCategory.getDuration() != null) {
            String durationStr = updatedCategory.getDuration().trim().toString().toLowerCase();
            List<Document> documents = documentRepository.findByCategory_IdDocumentCategory(id);
            for(Document document: documents){
                // 3. Tính ngày hết hạn dựa vào duration (dưới dạng String)
                if (durationStr.contains("vĩnh viễn")) {
                    // Vĩnh viễn => không hết hạn
                    document.setExpirationDate(null);
                } else {
                    try {
                        int years = Integer.parseInt(durationStr);
                        document.setExpirationDate(document.getCreatedDate().plusYears(years));
                    } catch (NumberFormatException e) {
                        // Nếu duration không hợp lệ (ví dụ "3 năm" thay vì "3") => bỏ qua
                        document.setExpirationDate(null);
                    }
                }
                documentRepository.saveAll(documents);
            }
        }

        return updatedCategory;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<DocumentCategory> search(String q) {
        return repository.searchByKeyword(q);
    }

    // Đếm
    public long count() {
        return repository.count();
    }
}
