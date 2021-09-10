package ru.itmo.sd.stockexchangeemulator.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.sd.stockexchangeemulator.dao.StocksDao;

@RestController
public class StocksController {
    private final StocksDao stocksDao = new StocksDao();

    @GetMapping(value = "/add-company/{company}/{stocks}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String addCompany(@PathVariable String company, @PathVariable int stocks) {
        stocksDao.addCompany(company, stocks);
        return "Company added";
    }

    @GetMapping(value = "/get-number-of-stocks/{company}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getNumberOfStocks(@PathVariable String company) {
        return String.valueOf(stocksDao.getNumberOfStocks(company));
    }

    @GetMapping(value = "/get-stock-price/{company}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getStockPrice(@PathVariable String company) {
        return String.valueOf(stocksDao.getStockPrice(company));
    }

    @GetMapping(value = "/buy-stocks/{company}/{stocks}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String buyStocks(@PathVariable String company, @PathVariable int stocks) {
        return String.valueOf(stocksDao.buyStocks(company, stocks));
    }

    @GetMapping(value = "/sell-stocks/{company}/{stocks}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sellStocks(@PathVariable String company, @PathVariable int stocks) {
        return String.valueOf(stocksDao.sellStocks(company, stocks));
    }
}
