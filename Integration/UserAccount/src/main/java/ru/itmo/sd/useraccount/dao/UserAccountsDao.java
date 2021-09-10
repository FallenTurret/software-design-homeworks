package ru.itmo.sd.useraccount.dao;

import ru.itmo.sd.useraccount.model.StockPackage;
import ru.itmo.sd.useraccount.model.User;
import ru.itmo.sd.useraccount.web.UrlRequest;

import java.util.ArrayList;
import java.util.List;

public class UserAccountsDao {
    private static int curId = 0;
    private final List<User> users = new ArrayList<>();
    private final String stockExchangeUrl;
    private final UrlRequest urlRequest = new UrlRequest();

    public UserAccountsDao(String stockExchangeUrl) {
        this.stockExchangeUrl = stockExchangeUrl;
    }

    public int addUser(String name) {
        users.add(new User(curId, name));
        return curId++;
    }

    public User getUser(int id) {
        return users.get(id);
    }

    public void deposit(int id, int amount) {
        var user = users.get(id);
        user.setBalance(user.getBalance() + amount);
    }

    private void updatePrices(int id) {
        for (var stock: users.get(id).getStocks()) {
            var url = stockExchangeUrl + "/get-stock-price/" + stock.getCompany();
            var price = Integer.parseInt(urlRequest.requestResponse(url));
            stock.setCurrentPrice(price);
        }
    }

    public List<StockPackage> getStocks(int id) {
        updatePrices(id);
        return users.get(id).getStocks();
    }

    public void buyStocks(int id, String company, int amount) {
        var url = stockExchangeUrl + "/buy-stocks/" + company + "/" + amount;
        var totalPrice = Integer.parseInt(urlRequest.requestResponse(url));
        if (totalPrice == -1)
            return;
        var user = users.get(id);
        user.setBalance(user.getBalance() - totalPrice);
        for (var stock : user.getStocks()) {
            if (stock.getCompany().equals(company)) {
                stock.setAmount(stock.getAmount() + amount);
                return;
            }
        }
        user.getStocks().add(new StockPackage(company, amount));
    }

    public void sellStocks(int id, String company, int amount) {
        var user = users.get(id);
        for (var stock: user.getStocks()) {
            if (stock.getCompany().equals(company) && stock.getAmount() >= amount) {
                stock.setAmount(stock.getAmount() - amount);
                var url = stockExchangeUrl + "/sell-stocks/" + company + "/" + amount;
                urlRequest.requestResponse(url);
                return;
            }
        }
    }
}
