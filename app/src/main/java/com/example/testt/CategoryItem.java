package com.example.testt;

public class CategoryItem {
    private String id;
    private String name;

    public CategoryItem() {
    }

    public CategoryItem(String name) {
        this.name = name;
    }

    public CategoryItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
