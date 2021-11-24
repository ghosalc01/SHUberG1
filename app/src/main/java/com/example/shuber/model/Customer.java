package com.example.shuber.model;

public class Customer {
    private String email, name, password, cardnumber, cardType, idToken;
    public Customer(){};

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPassword() {
        return password;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public String getCardType() {
        return cardType;
    }

    public String getIdToken() {
        return idToken;
    }
}
