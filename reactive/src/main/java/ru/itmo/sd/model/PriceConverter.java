package ru.itmo.sd.model;

public class PriceConverter {
    public static double DOLLAR = 74.21;
    public static double EURO = 89.44;

    public static double toRoubles(double value, Currency currency) {
        switch (currency) {
            case DOLLARS:
                return value * DOLLAR;
            case EUROS:
                return value * EURO;
            case ROUBLES:
                return value;
            default:
                return -1;
        }
    }

    public static double fromRoubles(double value, Currency currency) {
        switch (currency) {
            case DOLLARS:
                return value / DOLLAR;
            case EUROS:
                return value / EURO;
            case ROUBLES:
                return value;
            default:
                return -1;
        }
    }
}
