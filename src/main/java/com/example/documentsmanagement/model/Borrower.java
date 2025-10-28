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

    @Column(name = "position")
    private String position;

    @Column(name = "id_card_number")
    private String idCardNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    @Column(name = "employee_code", unique = true)
    private String employeeCode;


    // Constructors
    public Borrower() {}

    public Borrower(String fullName, String department, String position, String idCardNumber, String phoneNumber) {
        this.fullName = fullName;
        this.department = department;
        this.position = position;
        this.idCardNumber = idCardNumber;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
