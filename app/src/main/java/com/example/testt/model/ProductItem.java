package com.example.testt.model;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

public class ProductItem {
    private String id;
    private String categoryId;
    private String name;
    private String price;
    private String imageUrl;

    public ProductItem() {
    }

    public ProductItem(String name, String price) {
        this.id = generateIdFromName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = "";
    }

    public ProductItem(String id, String categoryId, String name, String price, String imageUrl) {
        this.id = id != null && !id.isEmpty() ? id : generateIdFromName(name);
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl != null ? imageUrl : "";
    }

    public String getId() {
        return id != null && !id.isEmpty() ? id : generateIdFromName(name);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String generateIdFromName(String name) {
        if (name == null || name.isEmpty()) {
            return "unknown_product";
        }
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }
}
