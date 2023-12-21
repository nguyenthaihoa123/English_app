package com.example.englishapp.model;

public class User {
    private String username;
    private String pwd;
    private String ID;
    private String email;
    private String OTP;
    private String address;
    private String number;
    private String img;
    private int score;
    private int stt;

    public User(String username, String pwd, String ID, String email, String address, String number) {
        this.username = username;
        this.pwd = pwd;
        this.ID = ID;
        this.email = email;
        this.address = address;
        this.number = number;
    }

    public User(String username, String pwd, String ID, String email, String address, String number, String img) {
        this.username = username;
        this.pwd = pwd;
        this.ID = ID;
        this.email = email;
        this.address = address;
        this.number = number;
        this.img = img;
    }

    public User(String username, String pwd, String ID, String email, String OTP, String address, String number, String img) {
        this.username = username;
        this.pwd = pwd;
        this.ID = ID;
        this.email = email;
        this.OTP = OTP;
        this.address = address;
        this.number = number;
        this.img = img;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public User(String username, String ID, String img, int stt, int score) {
        this.username = username;
        this.ID = ID;
        this.img = img;
        this.stt = stt;
        this.score = score;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
