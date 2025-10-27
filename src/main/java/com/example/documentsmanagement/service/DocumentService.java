package com.example.documentsmanagement.service;

import com.example.documentsmanagement.model.Document;
import com.example.documentsmanagement.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    // =========================================================
    // CRUD CƠ BẢN
    // =========================================================

    public List<Document> findAll() {
        return repository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public Document create(Document document) {
        if (document.getCreatedDate() == null) {
            document.setCreatedDate(LocalDate.now());
        }

        // Lưu trước để sinh ID (nếu cần)
        Document saved = repository.save(document);

        // 🔹 Sinh mã tài liệu duy nhất (theo năm + loại)
        String code = generateDocumentCode(saved);
        saved.setDocumentCode(code);

        // Cập nhật lại mã chính thức
        return repository.save(saved);
    }

    public Optional<Document> update(Long id, Document updatedData) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updatedData.getTitle());
            existing.setStatus(updatedData.getStatus());
            existing.setCreatedDate(updatedData.getCreatedDate());
            existing.setEventDate(updatedData.getEventDate());
            existing.setNote(updatedData.getNote());
            existing.setDepartment(updatedData.getDepartment());
            existing.setArea(updatedData.getArea());
            existing.setCategory(updatedData.getCategory());
            // Sinh lại mã nếu thay đổi năm hoặc loại tài liệu
            existing.setDocumentCode(generateDocumentCode(existing));
            return repository.save(existing);
        });
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu có ID: " + id);
        }
        repository.deleteById(id);
    }

    // =========================================================
    // SINH MÃ DOCUMENT CODE — CỰC TỐI ƯU
    // =========================================================
    private synchronized String generateDocumentCode(Document d) {
        // 1️⃣ Thành phần cơ bản
        String sign = (d.getCategory() != null && d.getCategory().getSign() != null)
                ? d.getCategory().getSign().toUpperCase()
                : "XX";

        String year = (d.getCreatedDate() != null)
                ? String.valueOf(d.getCreatedDate().getYear()).substring(2)
                : String.valueOf(LocalDate.now().getYear()).substring(2);

        String dept = formatDepartment(d.getDepartment());
        String area = formatArea(d.getArea());

        // 2️⃣ Tiền tố
        String prefix = sign + year + dept + area;

        // 3️⃣ Tìm mã lớn nhất theo prefix (chỉ 1 query, O(1) nếu có index)
        String maxCode = repository.findMaxDocumentCodeByPrefix(prefix);

        // 4️⃣ Tính số thứ tự kế tiếp — reset mỗi năm & mỗi loại (sign)
        int nextNumber = extractNextNumber(maxCode, prefix);

        // 5️⃣ Trả về mã hoàn chỉnh
        return prefix + String.format("%05d", nextNumber); // 00001–99999
    }

    /**
     * Xử lý phần số thứ tự kế tiếp (tăng dần, reset mỗi năm + loại)
     */
    private int extractNextNumber(String maxCode, String prefix) {
        if (maxCode == null || maxCode.isBlank()) return 1;
        try {
            String numPart = maxCode.substring(prefix.length());
            return Integer.parseInt(numPart) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    // =========================================================
    // FORMAT DỮ LIỆU ĐẦU VÀO
    // =========================================================
    private String formatArea(String area) {
        if (area == null) return "X";
        String a = area.toLowerCase();

        if (a.contains("hà nội") || a.contains("hn") || a.contains("tphn")) return "A";
        if (a.contains("hcm") || a.contains("hồ chí minh")) return "B";
        return "X";
    }

    private String formatDepartment(String department) {
        if (department == null || department.isBlank()) return "XX";
        return department.replace("Phòng", "P")
                .replace("phòng", "P")
                .replaceAll("\\s+", "");
    }

    // =========================================================
    // CÁC CHỨC NĂNG PHỤ KHÁC
    // =========================================================
    public List<Document> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        return repository.searchByKeyword(keyword.toLowerCase());
    }

    public Long count() {
        return repository.count();
    }

    public List<Document> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Document> findByDepartment(String department) {
        return repository.findByDepartment(department);
    }
}
