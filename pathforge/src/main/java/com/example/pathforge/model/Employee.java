package com.example.pathforge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Employee {
    @Id
    private String empId;

    private String firstName;

    private String lastName;

    private String email;

    private String position;

    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    public enum Qualification {
        DIPLOMA("Diploma"),
        BACHELOR("Bachelor"),
        MASTERS("Masters"),
        PHD("PhD");

        private final String displayName;

        Qualification(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Employee() {}

    public Employee(String empId, String firstName, String lastName, String email, String position, Qualification qualification) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.position = position;
        this.qualification = qualification;
    }

    // Getters
    public String getEmpId() {
        return empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public Qualification getQualification() {
        return qualification;
    }

    // Setters
    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId='" + empId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", qualification=" + qualification +
                '}';
    }
}