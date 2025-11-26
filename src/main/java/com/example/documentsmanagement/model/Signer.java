package com.example.documentsmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SIGNER")
public class Signer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SIGNER")
    private Long idSigner;

    @Column(name = "STAFF_CODE", unique = true)
    private String staffCode; // Số hiệu

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "RANK")
    private String rank; // Cấp bậc

    @Column(name = "POSITION")
    private String position; // Chức vụ

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    // Constructors
    public Signer() {}

    public Signer(String staffCode, String fullName, String department, String rank, String position, String phoneNumber) {
        this.staffCode = staffCode;
        this.fullName = fullName;
        this.department = department;
        this.rank = rank;
        this.position = position;
        this.phoneNumber = phoneNumber;
    }

    // Getters & Setters
    public Long getIdSigner() { return idSigner; }
    public void setIdSigner(Long idSigner) { this.idSigner = idSigner; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}