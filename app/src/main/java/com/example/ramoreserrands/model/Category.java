package com.example.ramoreserrands.model;

public class Category {
    private String category_id;
    private String category_name;
    private String category_available;

    public Category(){
    }
    public Category(String category_id, String category_name, String category_available) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_available = category_available;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_available() {
        return category_available;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setCategory_available(String category_available) {
        this.category_available = category_available;
    }
}
