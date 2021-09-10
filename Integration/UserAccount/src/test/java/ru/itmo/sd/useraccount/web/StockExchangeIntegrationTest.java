package ru.itmo.sd.useraccount.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class StockExchangeIntegrationTest {
    private static final UrlRequest urlRequest = new UrlRequest();
    private String prefix;

    @Container
    public GenericContainer<?> stockExchangeEmulator =
            new GenericContainer<>(DockerImageName.parse("ru.itmo.sd/stock-exchange-emulator:1"))
            .withExposedPorts(8080);

    @BeforeEach
    public void setUp() {
        prefix = "http://" + stockExchangeEmulator.getHost() + ":" + stockExchangeEmulator.getFirstMappedPort();
    }

    @Test
    public void emulatorReturnsPositiveIntegerStockPrice() {
        urlRequest.requestResponse(prefix + "/add-company/c1/10");
        urlRequest.requestResponse(prefix + "/add-company/c2/50");
        for (int i = 0; i < 10; i++) {
            assertTrue(Integer.parseInt(urlRequest.requestResponse(prefix + "/get-stock-price/c1")) > 0);
            assertTrue(Integer.parseInt(urlRequest.requestResponse(prefix + "/get-stock-price/c2")) > 0);
        }
    }

    @Test
    public void ableToBuyOnlyExistingStocks() {
        assertEquals(-1, Integer.parseInt(urlRequest.requestResponse(prefix + "/buy-stocks/c1/10")));
        urlRequest.requestResponse(prefix + "/add-company/c1/10");
        assertEquals(-1, Integer.parseInt(urlRequest.requestResponse(prefix + "/buy-stocks/c1/11")));
    }

    @Test
    public void totalBuyPriceModPackageSizeEqualsZero() {
        for (int i = 1; i < 10; i++) {
            urlRequest.requestResponse(prefix + "/add-company/c1/10");
            assertEquals(0, Integer.parseInt(urlRequest.requestResponse(prefix + "/buy-stocks/c1/" + i)) % i);
            assertEquals(0, Integer.parseInt(urlRequest.requestResponse(prefix + "/buy-stocks/c1/" + (10 - i))) % (10 - i));
        }
    }

    @Test
    public void ableToSellOnlySavedCompaniesStocks() {
        assertEquals(-1, Integer.parseInt(urlRequest.requestResponse(prefix + "/sell-stocks/c1/1")));
    }

    @Test
    public void totalSellPriceModPackageSizeEqualsZero() {
        urlRequest.requestResponse(prefix + "/add-company/c1/10");
        for (int i = 1; i < 10; i++) {
            assertEquals(0, Integer.parseInt(urlRequest.requestResponse(prefix + "/sell-stocks/c1/" + i)) % i);
            assertEquals(0, Integer.parseInt(urlRequest.requestResponse(prefix + "/sell-stocks/c1/" + (10 - i))) % (10 - i));
        }
    }
}