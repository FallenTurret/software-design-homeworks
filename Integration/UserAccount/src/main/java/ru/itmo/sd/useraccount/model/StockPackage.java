package ru.itmo.sd.useraccount.model;

public class StockPackage {
    private final String company;
    private int amount;
    private int currentPrice;

    public StockPackage(String company, int amount) {
        this.company = company;
        this.amount = amount;
    }

    public String getCompany() {
        return company;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getTotalPrice() {
        return amount * currentPrice;
    }

    @Override
    public String toString() {
        return "StockPackage{" +
                "company='" + company + '\'' +
                ", amount=" + amount +
                ", currentPrice=" + currentPrice +
                '}';
    }
}
