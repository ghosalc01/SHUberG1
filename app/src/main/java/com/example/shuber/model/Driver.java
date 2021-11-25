package com.example.shuber.model;

public class Driver {
    private String email, name, password, carType, carNumber, idToken;
    public Driver(){};

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCarType() {
        return carType;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getIdToken() {
        return idToken;
    }
}
