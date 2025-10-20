package com.example.documentsmanagement.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CaseDocument")
public class CaseDocument extends Document {

    private String caseNumber;
    private String caseType;

    // Constructors
    public CaseDocument() {
        super();
    }

    public CaseDocument(String title, String description, LocalDate createdDate, String caseNumber, String caseType) {
        super(title, description, null);
        this.caseNumber = caseNumber;
        this.caseType = caseType;
    }

    // Getters & Setters
    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
}
