package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.solution.models.Details;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.strategies.BuyWhenSellSideDoesNotMatchPositionOrderStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BuyWhenSellSideDoesNotMatchStrategyTest {
    private BuyWhenSellSideDoesNotMatchPositionOrderStrategy strategy;
    private OrderBookModel orderBook;

    @Before
    public void setUp() throws Exception {
        strategy = new BuyWhenSellSideDoesNotMatchPositionOrderStrategy();
        orderBook = new OrderBookModel("TEST", new ArrayList<>(), new ArrayList<>());
    }

    @Test
    public void canProcess_ShouldReturnTrueForBuyOrderWhenSellSideDoesntMatch() throws Exception {
        OrderMessage orderMessageSell = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        orderBook.getSellEntries().add(orderMessageSell);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBook, orderMessage);

        assertTrue(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForSellOrder() throws Exception {
        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        boolean result = strategy.canProcess(orderBook, orderMessage);

        assertFalse(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForBuyOrderWhenSellSideIsMatch() throws Exception {
        OrderMessage orderMessageSell = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 50))
                .build();

        orderBook.getSellEntries().add(orderMessageSell);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBook, orderMessage);

        assertFalse(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForBuyOrderWhenSellSideOneMatchAndOneNot() throws Exception {
        OrderMessage orderMessageSell1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBook.getSellEntries().add(orderMessageSell1);

        OrderMessage orderMessageSell2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 110))
                .build();

        orderBook.getSellEntries().add(orderMessageSell2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBook, orderMessage);

        assertFalse(result);
    }

    @Test
    public void process_ShouldAddOrderMessageToOrderBookAndSortByPriceAscending() throws Exception {
        OrderMessage orderMessagePrice100 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBook.getBuyEntries().add(orderMessagePrice100);

        OrderMessage orderMessagePrice200 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        strategy.process(orderBook, orderMessagePrice200);

        assertEquals(2, orderBook.getBuyEntries().size());
        assertEquals(orderMessagePrice200, orderBook.getBuyEntries().stream().findFirst().get());
    }
}