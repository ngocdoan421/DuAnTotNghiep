package com.example.testt.model;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

import com.google.firebase.firestore.Exclude;

public class CartItem {
    private String productId;
    private String name;
    private String price;
    private int quantity;
    private String imageUrl;

    public CartItem() {} // bắt buộc cho Firestore

    public CartItem(String productId, String name, String price, int quantity, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPriceAsLong(long priceAsLong) { /* Ignore - required to prevent Firestore mapping warnings */ }

    // Tính giá số để cộng tổng (bỏ "đ" và dấu chấm)
    @Exclude
    public long getPriceAsLong() {
        try {
            return Long.parseLong(price.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}