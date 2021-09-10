package ru.itmo.sd.model;

import org.bson.Document;

public class Product {
    public final int ownerId;
    public final int id;
    public final double price;

    public Product(Document document) {
        this(document.getInteger("ownerId"), document.getInteger("id"), document.getDouble("price"));
    }

    public Product(int ownerId, int id, double price) {
        this.ownerId = ownerId;
        this.id = id;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ownerId\": " + ownerId +
                ", \"id\": " + id +
                ", \"price\": " + price +
                "}";
    }
}
