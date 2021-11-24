package com.example.shuber.model;

public class Singleton {
    private static final Singleton instance = new Singleton();
    public static String userType;

    private Singleton() {}
    public static Singleton getInstance() {
        return instance;
    }
}
