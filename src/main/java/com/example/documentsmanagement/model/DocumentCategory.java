package com.example.documentsmanagement.model;
import jakarta.persistence.*;

@Entity
@Table(name = "DocumentCategory")

public class DocumentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument_Category;

    private String shortName;

    private String fullName;

    private String description;

    // Constructors
    public DocumentCategory() {}

    public DocumentCategory(String shortName, String fullName, String description) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.description = description;
    }

    // Getters & Setters
    public Long getIdDocument_Category() {
        return idDocument_Category;
    }

    public void setIdDocument_Category(Long idDocument_Category) {
        this.idDocument_Category = idDocument_Category;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
