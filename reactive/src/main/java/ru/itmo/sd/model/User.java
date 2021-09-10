package ru.itmo.sd.model;

import org.bson.Document;

public class User {
    public final int id;
    public final Currency currency;

    public User(Document document) {
        this(document.getInteger("id"), document.getString("currency"));
    }

    public User(int id, String currency) {
        this.id = id;
        switch (currency) {
            case "USD":
                this.currency = Currency.DOLLARS;
                break;
            case "EUR":
                this.currency = Currency.EUROS;
                break;
            default:
                this.currency = Currency.ROUBLES;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"currency\": \"" + currency + "\"" +
                "}";
    }
}
