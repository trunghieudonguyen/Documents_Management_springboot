package com.example.documentsmanagement.service;

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

    public DocumentCategoryService(DocumentCategoryRepository repository) {
        this.repository = repository;
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
        // Cập nhật cơ bản: đảm bảo id được đặt trên thực thể và lưu.
        try {
            java.lang.reflect.Field idField = changes.getClass().getDeclaredField("idDocumentCategory");
            idField.setAccessible(true);
            Object val = idField.get(changes);
            if (val == null) {
                // set id value reflectively
                idField.set(changes, id);
            }
        } catch (Exception ex) {
            //Nếu tham chiếu không thành công, bỏ qua; repository.save vẫn hoạt động nếu thực thể có id được đặt
        }
        return repository.save(changes);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<DocumentCategory> search(String q) {
        return repository.searchByName(q);
    }

    // Đếm
    public long count() {
        return repository.count();
    }
}
