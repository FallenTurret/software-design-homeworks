package ru.itmo.sd.stockexchangeemulator.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StocksDao {
    private final Map<String, Integer> numberOfStocks = new HashMap<>();
    private final Map<String, Integer> priceOfStock = new HashMap<>();
    private final Random randomizer = new Random();

    public StocksDao() {
        var scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updatePrices, 0, 1, TimeUnit.SECONDS);
    }

    public void addCompany(String company, int stocks) {
        numberOfStocks.put(company, stocks);
        synchronized (priceOfStock) {
            priceOfStock.put(company, 100);
        }
    }

    public int getNumberOfStocks(String company) {
        if (numberOfStocks.containsKey(company)) {
            return numberOfStocks.get(company);
        }
        return 0;
    }

    public int getStockPrice(String company) {
        synchronized (priceOfStock) {
            if (priceOfStock.containsKey(company)) {
                return priceOfStock.get(company);
            }
            return -1;
        }
    }

    public int buyStocks(String company, int stocks) {
        var curStocks = getNumberOfStocks(company);
        if (stocks <= 0 || stocks > curStocks) {
            return -1;
        }
        numberOfStocks.put(company, curStocks - stocks);
        return stocks * getStockPrice(company);
    }

    public int sellStocks(String company, int stocks) {
        if (stocks < 0 || !numberOfStocks.containsKey(company)) {
            return -1;
        }
        numberOfStocks.put(company, getNumberOfStocks(company) + stocks);
        return stocks * getStockPrice(company);
    }

    private void updatePrices() {
        synchronized (priceOfStock) {
            priceOfStock.replaceAll((company, price) -> randomizer.nextInt(100) + 100);
        }
    }
}
