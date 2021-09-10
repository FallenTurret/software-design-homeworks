package ru.itmo.sd.useraccount.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.sd.useraccount.dao.UserAccountsDao;

@RestController
public class UsersController {
    public static String stockExchangeUrl;
    private final UserAccountsDao dao = new UserAccountsDao(stockExchangeUrl);

    @GetMapping(value = "/add-user/{name}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String addUser(@PathVariable String name) {
        return getInfo(dao.addUser(name));
    }

    @GetMapping(value = "/deposit/{id}/{amount}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String deposit(@PathVariable int id, @PathVariable int amount) {
        dao.deposit(id, amount);
        return getInfo(id);
    }

    @GetMapping(value = "/view/{id}")
    public String view(@PathVariable int id) {
        return getInfo(id);
    }

    @GetMapping(value = "/buy-stocks/{id}/{company}/{amount}")
    public String buyStocks(@PathVariable int id, @PathVariable String company, @PathVariable int amount) {
        dao.buyStocks(id, company, amount);
        return getInfo(id);
    }

    @GetMapping(value = "/sell-stocks/{id}/{company}/{amount}")
    public String sellStocks(@PathVariable int id, @PathVariable String company, @PathVariable int amount) {
        dao.sellStocks(id, company, amount);
        return getInfo(id);
    }

    private String getInfo(int id) {
        var user = dao.getUser(id);
        var stocks = dao.getStocks(id);
        return "id: " + user.getId() + "\n" +
                "name: " + user.getName() + "\n" +
                "balance(stocks included): " +
                (user.getBalance()
                        +
                stocks.stream().reduce(0, (s, stock) -> s + stock.getTotalPrice(), Integer::sum)) + "\n" +
                "Stocks list:\n" + stocks.toString();

    }
}
