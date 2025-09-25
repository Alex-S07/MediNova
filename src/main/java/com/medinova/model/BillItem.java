package com.medinova.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * BillItem model class for individual items in a bill
 */
public class BillItem {
    private int id;
    private int billId;
    private ItemType itemType;
    private Integer itemId;
    private String itemName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    public enum ItemType {
        CONSULTATION("Consultation"),
        MEDICINE("Medicine"),
        TEST("Test"),
        PROCEDURE("Procedure"),
        OTHER("Other");
        
        private final String displayName;
        
        ItemType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public BillItem() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
    }
    
    public BillItem(int billId, ItemType itemType, String itemName, 
                   int quantity, BigDecimal unitPrice) {
        this.billId = billId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        calculateTotalPrice();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getBillId() {
        return billId;
    }
    
    public void setBillId(int billId) {
        this.billId = billId;
    }
    
    public ItemType getItemType() {
        return itemType;
    }
    
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
    public Integer getItemId() {
        return itemId;
    }
    
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        calculateTotalPrice();
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    private void calculateTotalPrice() {
        if (unitPrice != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            totalPrice = BigDecimal.ZERO;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillItem billItem = (BillItem) o;
        return id == billItem.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + id +
                ", billId=" + billId +
                ", itemType=" + itemType +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}