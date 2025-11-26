package com.example.documentsmanagement.model;
import jakarta.persistence.*;

@Entity
@Table(name = "borrower")
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_borrower;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "department")
    private String department;

    @Column(name = "rank")
    private String rank;

    @Column(name = "position")
    private String position;

    @Column(name = "phone_number")
    private String phoneNumber;

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Column(name = "staff_code", unique = true)
    private String staffCode;


    // Constructors
    public Borrower() {}

    public Borrower(String staffCode, String fullName, String department, String rank, String position, String phoneNumber) {
        this.staffCode = staffCode;
        this.fullName = fullName;
        this.department = department;
        this.rank = rank;
        this.position = position;
        this.phoneNumber = phoneNumber;
    }

    // Getters & Setters
    public Long getId_borrower() {
        return id_borrower;
    }

    public void setId_borrower(Long id_borrower) {
        this.id_borrower = id_borrower;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
