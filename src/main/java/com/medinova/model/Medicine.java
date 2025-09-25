package com.medinova.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Medicine model class for pharmacy inventory
 */
public class Medicine {
    private int id;
    private String medicineCode;
    private String name;
    private String manufacturer;
    private String category;
    private String dosageForm;
    private String strength;
    private BigDecimal unitPrice;
    private int stockQuantity;
    private int minimumStock;
    private LocalDate expiryDate;
    private String batchNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Medicine() {}
    
    public Medicine(String medicineCode, String name, String manufacturer, 
                   String category, BigDecimal unitPrice, int stockQuantity) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.manufacturer = manufacturer;
        this.category = category;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.stockQuantity = stockQuantity;
        this.minimumStock = 10; // Default minimum stock
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMedicineCode() {
        return medicineCode;
    }
    
    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDosageForm() {
        return dosageForm;
    }
    
    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }
    
    public String getStrength() {
        return strength;
    }
    
    public void setStrength(String strength) {
        this.strength = strength;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public int getMinimumStock() {
        return minimumStock;
    }
    
    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getBatchNumber() {
        return batchNumber;
    }
    
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isLowStock() {
        return stockQuantity <= minimumStock;
    }
    
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
    
    public boolean isExpiringSoon(int days) {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public String getDisplayName() {
        StringBuilder display = new StringBuilder(name);
        if (strength != null && !strength.trim().isEmpty()) {
            display.append(" (").append(strength).append(")");
        }
        if (dosageForm != null && !dosageForm.trim().isEmpty()) {
            display.append(" - ").append(dosageForm);
        }
        return display.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return id == medicine.id && Objects.equals(medicineCode, medicine.medicineCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, medicineCode);
    }
    
    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", medicineCode='" + medicineCode + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", category='" + category + '\'' +
                ", unitPrice=" + unitPrice +
                ", stockQuantity=" + stockQuantity +
                ", minimumStock=" + minimumStock +
                ", expiryDate=" + expiryDate +
                '}';
    }
}