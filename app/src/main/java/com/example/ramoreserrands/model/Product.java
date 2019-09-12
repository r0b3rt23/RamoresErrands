package com.example.ramoreserrands.model;

public class Product {

    private String product_id;
    private String product_name;
    private String product_price;
    private String product_desc;
    private String product_img;
    private String quantity;
    private String subitem_total;
    private String favorite_id;

    private String subcategory_name;
    private String subcategory_img;

    private String guest_cart_id;

    private String brand_name;
    private String brand_available;
    private String brand_img;

    public Product(){
    }

    public Product(String product_id, String product_name, String product_price, String product_desc, String product_img, String quantity, String subitem_total, String favorite_id, String subcategory_name, String subcategory_img, String guest_cart_id, String brand_name, String brand_available, String brand_img) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_desc = product_desc;
        this.product_img = product_img;
        this.quantity = quantity;
        this.subitem_total = subitem_total;
        this.favorite_id = favorite_id;
        this.subcategory_name = subcategory_name;
        this.subcategory_img = subcategory_img;
        this.guest_cart_id = guest_cart_id;
        this.brand_name = brand_name;
        this.brand_available = brand_available;
        this.brand_img = brand_img;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public String getProduct_img() {
        return product_img;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSubitem_total() {
        return subitem_total;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public String getSubcategory_img() {
        return subcategory_img;
    }

    public String getGuest_cart_id() {
        return guest_cart_id;
    }

    public String getFavorite_id() {
        return favorite_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public String getBrand_available() {
        return brand_available;
    }

    public String getBrand_img() {
        return brand_img;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setSubitem_total(String subitem_total) {
        this.subitem_total = subitem_total;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public void setSubcategory_img(String subcategory_img) {
        this.subcategory_img = subcategory_img;
    }

    public void setGuest_cart_id(String guest_cart_id) {
        this.guest_cart_id = guest_cart_id;
    }

    public void setFavorite_id(String favorite_id) {
        this.favorite_id = favorite_id;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public void setBrand_available(String brand_available) {
        this.brand_available = brand_available;
    }

    public void setBrand_img(String brand_img) {
        this.brand_img = brand_img;
    }
}
