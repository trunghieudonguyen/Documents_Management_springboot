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

    // =====================================================================
    // Các hàm CRUD cơ bản
    // =====================================================================
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

        // Lưu tạm để có ID
        Document saved = repository.save(document);

        // Sinh mã tài liệu tối ưu
        String code = generateDocumentCode(saved);
        saved.setDocumentCode(code);

        // Lưu lại mã code
        return repository.save(saved);
    }

    public Optional<Document> update(Long id, Document updatedData) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updatedData.getTitle());
            existing.setDescription(updatedData.getDescription());
            existing.setStatus(updatedData.getStatus());
            existing.setCreatedDate(updatedData.getCreatedDate());
            existing.setEventDate(updatedData.getEventDate());
            existing.setNote(updatedData.getNote());
            existing.setDepartment(updatedData.getDepartment());
            existing.setArea(updatedData.getArea());
            existing.setCategory(updatedData.getCategory());
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

    // =====================================================================
    // SINH MÃ DOCUMENT CODE TỐI ƯU
    // =====================================================================
    private String generateDocumentCode(Document d) {
        String sign = (d.getCategory() != null && d.getCategory().getSign() != null)
                ? d.getCategory().getSign().toUpperCase()
                : "XX";

        String year = (d.getCreatedDate() != null)
                ? String.valueOf(d.getCreatedDate().getYear()).substring(2)
                : String.valueOf(LocalDate.now().getYear()).substring(2);

        String department = formatDepartment(d.getDepartment());
        String area = formatArea(d.getArea());

        // Lấy số thứ tự tiếp theo bằng truy vấn nhanh
        int nextNumber = getNextSequence(sign, year, department, area);

        return sign + year + department + area + String.format("%04d", nextNumber);
    }

    /**
     * Hàm này chỉ query đúng nhóm tài liệu có cùng prefix và lấy mã lớn nhất bằng SQL -> cực nhanh kể cả 100k+ bản ghi
     */
    private int getNextSequence(String sign, String year, String dept, String area) {
        String prefix = sign + year + dept + area;

        // Dùng repository query để lấy documentCode lớn nhất
        String maxCode = repository.findMaxDocumentCodeByPrefix(prefix);

        if (maxCode == null) {
            return 1; // chưa có -> bắt đầu từ 0001
        }

        try {
            // Lấy 4 số cuối ra
            String numPart = maxCode.substring(prefix.length());
            return Integer.parseInt(numPart) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    // =====================================================================
    // Format hỗ trợ
    // =====================================================================
    private String formatArea(String area) {
        if (area == null) return "X";
        String lower = area.toLowerCase();

        // Hà Nội
        if (lower.contains("hà nội")
                || lower.contains("tp hà nội")
                || lower.contains("tp. hà nội")
                || lower.contains("tp.hn")
                || lower.contains("tphn")
                || lower.contains("tp hn")
                || lower.contains("hn")) {
            return "A";
        }

        // TP.HCM
        if (lower.contains("tphcm")
                || lower.contains("tp.hcm")
                || lower.contains("tp hcm")
                || lower.contains("tp hồ chí minh")
                || lower.contains("tp. hồ chí minh")
                || lower.contains("tp.hồ chí minh")
                || lower.contains("thành phố hồ chí minh")
                || lower.contains("hồ chí minh")
                || lower.contains("hcm")) {
            return "B";
        }
        return "X";
    }

    private String formatDepartment(String department) {
        if (department == null || department.isBlank()) return "XX";
        return department.replace("Phòng", "P")
                .replace("phòng", "P")
                .replaceAll("\\s+", "");
    }
}
