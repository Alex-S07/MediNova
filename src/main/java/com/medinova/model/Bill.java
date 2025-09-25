package com.medinova.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bill model class for billing system
 */
public class Bill {
    private int id;
    private String billNumber;
    private int patientId;
    private Integer appointmentId;
    private LocalDate billDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Associated objects
    private Patient patient;
    private Appointment appointment;
    private User createdByUser;
    private List<BillItem> billItems;
    
    public enum PaymentStatus {
        PENDING("Pending"),
        PARTIAL("Partial"),
        PAID("Paid"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Bill() {
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        this.billItems = new ArrayList<>();
    }
    
    public Bill(String billNumber, int patientId, LocalDate billDate, int createdBy) {
        this();
        this.billNumber = billNumber;
        this.patientId = patientId;
        this.billDate = billDate;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getBillNumber() {
        return billNumber;
    }
    
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public Integer getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public LocalDate getBillDate() {
        return billDate;
    }
    
    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Appointment getAppointment() {
        return appointment;
    }
    
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    
    public User getCreatedByUser() {
        return createdByUser;
    }
    
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }
    
    public List<BillItem> getBillItems() {
        return billItems;
    }
    
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
    public void addBillItem(BillItem item) {
        if (billItems == null) {
            billItems = new ArrayList<>();
        }
        billItems.add(item);
        recalculateAmounts();
    }
    
    public void removeBillItem(BillItem item) {
        if (billItems != null) {
            billItems.remove(item);
            recalculateAmounts();
        }
    }
    
    public void recalculateAmounts() {
        if (billItems == null || billItems.isEmpty()) {
            subtotal = BigDecimal.ZERO;
            totalAmount = BigDecimal.ZERO;
            return;
        }
        
        subtotal = billItems.stream()
                .map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total: subtotal + tax - discount
        totalAmount = subtotal.add(taxAmount).subtract(discountAmount);
        
        // Ensure total is not negative
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getAmountDue() {
        return totalAmount;
    }
    
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }
    
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return id == bill.id && Objects.equals(billNumber, bill.billNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, billNumber);
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", patientId=" + patientId +
                ", billDate=" + billDate +
                ", totalAmount=" + totalAmount +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}