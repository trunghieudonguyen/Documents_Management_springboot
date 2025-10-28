package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.model.DocumentCategory;
import com.example.documentsmanagement.repository.DocumentRepository;
import com.example.documentsmanagement.repository.DocumentCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository repository;
    private final DocumentCategoryRepository categoryRepository;

    public DocumentService(DocumentRepository repository,
                           DocumentCategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    // =========================================================
    // BASIC CRUD OPERATIONS
    // =========================================================

    public List<Document> findAll() {
        return repository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu có ID: " + id);
        }
        repository.deleteById(id);
    }

    // =========================================================
    // CREATE DOCUMENT — đảm bảo nạp đầy đủ category trước khi sinh mã
    // =========================================================
    public Document create(Document document) {
        // Gán ngày tạo mặc định nếu frontend chưa gửi lên
        if (document.getCreatedDate() == null) {
            document.setCreatedDate(LocalDate.now());
        }

        // Nạp đầy đủ DocumentCategory (nếu chỉ có id được gửi lên)
        if (document.getCategory() != null && document.getCategory().getIdDocumentCategory() != null) {
            Long categoryId = document.getCategory().getIdDocumentCategory();
            DocumentCategory fullCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + categoryId));
            document.setCategory(fullCategory);
        }

        // 🔹 3. Tính ngày hết hạn dựa vào duration (dưới dạng String)
        if (document.getCategory() != null && document.getCategory().getDuration() != null) {
            String durationStr = document.getCategory().getDuration().trim().toLowerCase();

            if (durationStr.contains("vĩnh viễn") || durationStr.contains("không giới hạn")) {
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
        } else {
            // Nếu không có duration thì cũng đặt null
            document.setExpirationDate(null);
        }

        // Lưu lần đầu để sinh ID
        Document saved = repository.save(document);

        // Sinh mã tài liệu tự động (dựa vào category, năm, phòng ban, khu vực...)
        saved.setDocumentCode(generateDocumentCode(saved));

        // Lưu lại bản hoàn chỉnh với mã
        return repository.save(saved);
    }


    // =========================================================
    // UPDATE DOCUMENT
    // =========================================================
    public Optional<Document> update(Long id, Document updatedData) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updatedData.getTitle());
            existing.setStatus(updatedData.getStatus());
            existing.setCreatedDate(updatedData.getCreatedDate());
            existing.setEventDate(updatedData.getEventDate());
            existing.setNote(updatedData.getNote());
            existing.setDepartment(updatedData.getDepartment());
            existing.setArea(updatedData.getArea());

            // Nạp lại Category (nếu có ID)
            if (updatedData.getCategory() != null && updatedData.getCategory().getIdDocumentCategory() != null) {
                Long categoryId = updatedData.getCategory().getIdDocumentCategory();
                DocumentCategory fullCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + categoryId));
                existing.setCategory(fullCategory);
            }

            // Cập nhật ngày hết hạn
            // 🔹 Cập nhật ngày hết hạn linh hoạt hơn
            if (existing.getCategory() != null && existing.getCategory().getDuration() != null) {
                String durationStr = existing.getCategory().getDuration().trim().toLowerCase();

                try {
                    // Nếu là số hoặc chứa ký tự "năm", chỉ lấy phần số
                    durationStr = durationStr.replaceAll("[^0-9]", "");
                    if (!durationStr.isEmpty()) {
                        int years = Integer.parseInt(durationStr);
                        existing.setExpirationDate(existing.getCreatedDate().plusYears(years));
                    } else if (existing.getCategory().getDuration().contains("vĩnh viễn") ||
                            existing.getCategory().getDuration().contains("không giới hạn")) {
                        existing.setExpirationDate(null);
                    } else {
                        existing.setExpirationDate(null);
                    }
                } catch (Exception e) {
                    existing.setExpirationDate(null);
                }
            }

            // Sinh lại mã tài liệu
            existing.setDocumentCode(generateDocumentCode(existing));

            return repository.save(existing);
        });
    }

    // =========================================================
    // 🔹 MÃ TÀI LIỆU TỰ ĐỘNG
    // =========================================================
    private synchronized String generateDocumentCode(Document d) {
        String sign = (d.getCategory() != null && d.getCategory().getSign() != null)
                ? d.getCategory().getSign().toUpperCase()
                : "XX";

        String year = (d.getCreatedDate() != null)
                ? String.valueOf(d.getCreatedDate().getYear()).substring(2)
                : String.valueOf(LocalDate.now().getYear()).substring(2);

        String dept = formatDepartment(d.getDepartment());
        String area = formatArea(d.getArea());

        String prefix = sign + year + dept + area;

        String maxCode = repository.findMaxDocumentCodeByPrefix(prefix);
        int nextNumber = extractNextNumber(maxCode, prefix);

        return prefix + String.format("%05d", nextNumber);
    }

    private int extractNextNumber(String maxCode, String prefix) {
        if (maxCode == null || maxCode.isBlank()) return 1;
        try {
            String numPart = maxCode.substring(prefix.length());
            return Integer.parseInt(numPart) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private String formatArea(String area) {
        if (area == null) return "X";
        return switch (area.trim().toLowerCase()) {
            case "hà nội" -> "A";
            case "thành phố hồ chí minh", "tp. hồ chí minh" -> "B";
            default -> "X";
        };
    }

    private String formatDepartment(String department) {
        if (department == null || department.isBlank()) return "XX";
        return department.replace("Phòng", "P")
                .replace("phòng", "P")
                .replaceAll("\\s+", "");
    }

    // =========================================================
    // 🔹 EXTRA UTILITIES
    // =========================================================
    public List<Document> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return repository.findAll();
        return repository.searchByKeyword(keyword.toLowerCase());
    }

    public List<Document> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Document> findByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    public List<Document> findByCategoryId(Long categoryId) {
        return repository.findByCategory_IdDocumentCategory(categoryId);
    }

    public Long count() {
        return repository.count();
    }
}
