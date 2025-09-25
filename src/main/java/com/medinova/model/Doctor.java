package com.medinova.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Doctor model class
 */
public class Doctor {
    private int id;
    private int userId;
    private String doctorId;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private String department;
    private BigDecimal consultationFee;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    
    // Associated user information
    private User user;
    
    public Doctor() {}
    
    public Doctor(int userId, String doctorId, String specialization, 
                  String qualification, String licenseNumber) {
        this.userId = userId;
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.qualification = qualification;
        this.licenseNumber = licenseNumber;
        this.consultationFee = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public BigDecimal getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }
    
    public LocalTime getAvailableFrom() {
        return availableFrom;
    }
    
    public void setAvailableFrom(LocalTime availableFrom) {
        this.availableFrom = availableFrom;
    }
    
    public LocalTime getAvailableTo() {
        return availableTo;
    }
    
    public void setAvailableTo(LocalTime availableTo) {
        this.availableTo = availableTo;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }
    
    public String getDisplayName() {
        return "Dr. " + getFullName() + " (" + specialization + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return id == doctor.id && Objects.equals(doctorId, doctor.doctorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, doctorId);
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", doctorId='" + doctorId + '\'' +
                ", specialization='" + specialization + '\'' +
                ", qualification='" + qualification + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", department='" + department + '\'' +
                ", consultationFee=" + consultationFee +
                '}';
    }
}