package com.example.ramoreserrands.model;

public class UserInfo {

    private int user_id;
    private String user_fname;
    private String user_lname;
    private String user_birthday;
    private String user_gender;
    private String user_city;
    private String user_barangay;
    private String user_house_street;
    private String user_email;
    private String user_mobile;
    private String user_password;
    private String user_img;

    public UserInfo(){
    }

    public UserInfo(int user_id, String user_fname, String user_lname, String user_birthday, String user_gender, String user_city, String user_barangay, String user_house_street, String user_email, String user_mobile, String user_password, String user_img) {
        this.user_id = user_id;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_birthday = user_birthday;
        this.user_gender = user_gender;
        this.user_city = user_city;
        this.user_barangay = user_barangay;
        this.user_house_street = user_house_street;
        this.user_email = user_email;
        this.user_mobile = user_mobile;
        this.user_password = user_password;
        this.user_img = user_img;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getUser_city() {
        return user_city;
    }

    public String getUser_barangay() {
        return user_barangay;
    }

    public String getUser_house_street() {
        return user_house_street;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public void setUser_barangay(String user_barangay) {
        this.user_barangay = user_barangay;
    }

    public void setUser_house_street(String user_house_street) {
        this.user_house_street = user_house_street;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }
}
