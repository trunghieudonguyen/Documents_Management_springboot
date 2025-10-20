package com.example.documentsmanagement.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PersonDocument")
public class PersonDocument extends Document {
    private Long idPersonDocument;

    public Long getIdPersonDocument() {
        return idPersonDocument;
    }

    public void setIdPersonDocument(Long idPersonDocument) {
        this.idPersonDocument = idPersonDocument;
    }

    private String personName;
    private String nationalId;

    // Constructors
    public PersonDocument() {
        super();
    }

    public PersonDocument(String title, String description, LocalDate createdDate, String personName, String nationalId) {
        super(title, description, createdDate);
        this.personName = personName;
        this.nationalId = nationalId;
    }

    // Getters & Setters
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
