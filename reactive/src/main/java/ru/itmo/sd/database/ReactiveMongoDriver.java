package ru.itmo.sd.database;

import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import ru.itmo.sd.model.Product;
import ru.itmo.sd.model.User;
import rx.Observable;

public class ReactiveMongoDriver {
    private static final MongoDatabase database = MongoClients
            .create("mongodb://localhost:27017")
            .getDatabase("catalog");

    public static Observable<Product> getAllProducts() {
        MongoCollection<Document> collection = database.getCollection("product");
        return collection.find().toObservable().map(Product::new);
    }

    public static Observable<User> getUser(int userId) {
        MongoCollection<Document> collection = database.getCollection("user");
        return collection.find().toObservable().map(User::new)
                .firstOrDefault(new User(-1, "RUB"), user -> user.id == userId);
    }

    public static Observable<Product> getProduct(int id) {
        return getAllProducts()
                .firstOrDefault(new Product(-1, -1, -1), product -> product.ownerId == id);
    }

    public static Observable<Success> addProduct(Product product) {
        MongoCollection<Document> collection = database.getCollection("product");
        return collection.insertOne(Document.parse(product.toString())).asObservable();
    }

    public static Observable<Success> registerUser(User user) {
        MongoCollection<Document> collection = database.getCollection("user");
        return collection.insertOne(Document.parse(user.toString())).asObservable();
    }
}
