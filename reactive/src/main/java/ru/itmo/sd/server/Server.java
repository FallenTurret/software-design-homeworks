package ru.itmo.sd.server;

import io.reactivex.netty.protocol.http.server.HttpServer;
import ru.itmo.sd.database.ReactiveMongoDriver;
import ru.itmo.sd.model.PriceConverter;
import ru.itmo.sd.model.Product;
import ru.itmo.sd.model.User;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class Server {
    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    String query = req.getDecodedPath().substring(1);
                    var response = handleQuery(query, req.getQueryParameters());
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    private static Observable<String> handleQuery(String query, Map<String, List<String>> parameters) {
        switch (query) {
            case "view":
                if (parameters.containsKey("id")) {
                    return view(Integer.parseInt(parameters.get("id").get(0)));
                } else {
                    return Observable.just("User id is not specified");
                }
            case "register":
                if (parameters.containsKey("id") && parameters.containsKey("currency")) {
                    return registerUser(
                            Integer.parseInt(parameters.get("id").get(0)),
                            parameters.get("currency").get(0));
                } else {
                    return Observable.just("User registration: insufficient data provided");
                }
            case "add":
                if (parameters.containsKey("owner") && parameters.containsKey("id") && parameters.containsKey("price")) {
                    return addProduct(
                            Integer.parseInt(parameters.get("owner").get(0)),
                            Integer.parseInt(parameters.get("id").get(0)),
                            Double.parseDouble(parameters.get("price").get(0))
                    );
                } else {
                    return Observable.just("New product: insufficient data provided");
                }
            default:
                return Observable.just("Unsupported query requested");
        }
    }

    private static Observable<String> view(int id) {
        return ReactiveMongoDriver.getUser(id).flatMap(user -> {
            if (user.id == -1) {
                return Observable.just("User with given id is not registered");
            } else {
                return ReactiveMongoDriver
                        .getAllProducts()
                        .map(product -> new Product(
                                product.ownerId,
                                product.id,
                                PriceConverter.fromRoubles(product.price, user.currency)))
                        .map(Product::toString)
                        .flatMap(product -> Observable.just(product, "\n"));
            }
        });
    }

    private static Observable<String> registerUser(int id, String currency) {
        return ReactiveMongoDriver.getUser(id).flatMap(user -> {
            if (user.id != -1) {
                return Observable.just("User with given id is already have been registered");
            } else {
                return ReactiveMongoDriver
                        .registerUser(new User(id, currency))
                        .map(s -> "Registration successful");
            }
        });
    }

    private static Observable<String> addProduct(int ownerId, int id, double price) {
        return ReactiveMongoDriver.getUser(ownerId).flatMap(user -> {
            if (user.id == -1) {
                return Observable.just("User with given id is not registered");
            } else {
                return ReactiveMongoDriver.getProduct(id).flatMap(product -> {
                    if (product.id != -1) {
                        return Observable.just("Product with given id is already have been added");
                    } else {
                        return ReactiveMongoDriver
                                .addProduct(new Product(
                                        ownerId,
                                        id,
                                        PriceConverter.toRoubles(price, user.currency)))
                                .map(s -> "Product added successfully");
                    }
                });
            }
        });
    }
}
