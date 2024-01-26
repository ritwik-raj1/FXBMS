package com.ritwik.fxbms.Models;

import com.ritwik.fxbms.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private String accountNumber; // New variable to store account number

    private Model() {
        this.viewFactory = new ViewFactory();
    }
    public static synchronized Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }
    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public synchronized String getAccountNumber() {
        return accountNumber;
    }

    public synchronized void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}