package ru.itmo.sd.useraccount.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final int id;
    private final String name;
    private int balance = 0;
    private final List<StockPackage> stocks = new ArrayList<>();

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<StockPackage> getStocks() {
        return stocks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
