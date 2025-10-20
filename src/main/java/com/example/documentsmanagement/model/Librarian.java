package com.example.documentsmanagement.model;
import jakarta.persistence.*;

@Entity
@Table(name = "librarian")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idlibrarian;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String rank;

    private String position;

    private String staffCode;
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    // Constructors
    public Librarian() {}

    public Librarian(String fullName, String phoneNumber, String email, String rank, String position, String staffCode, String username, String password) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.rank = rank;
        this.position = position;
        this.staffCode = staffCode;
        this.username = username;
        this.password = password;
    }

    // Getters & Setters
    public Long getIdlibrarian() {
        return idlibrarian;
    }

    public void setIdlibrarian(Long idlibrarian) {
        this.idlibrarian = idlibrarian;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
}
