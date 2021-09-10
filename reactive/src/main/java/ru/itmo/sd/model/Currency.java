package  ru.itmo.sd.model;

enum Currency {
    DOLLARS("USD"),
    EUROS("EUR"),
    ROUBLES("RUB");

    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return currency;
    }
}
