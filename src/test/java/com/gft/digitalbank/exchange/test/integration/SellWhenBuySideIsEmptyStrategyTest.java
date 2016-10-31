package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.solution.models.Details;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.strategies.SellWhenBuySideIsEmptyPositionOrderStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SellWhenBuySideIsEmptyStrategyTest {
    private SellWhenBuySideIsEmptyPositionOrderStrategy strategy;
    private OrderBookModel orderBookModel;

    @Before
    public void setUp() throws Exception {
        strategy = new SellWhenBuySideIsEmptyPositionOrderStrategy();
        orderBookModel = new OrderBookModel("TEST", new ArrayList<>(), new ArrayList<>());
    }

    @Test
    public void canProcess_ShouldReturnTrueForSellOrderAndEmptyBuySideInOrderBook() throws Exception {
        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertTrue(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForBuyOrderAndEmptyBuySideInOrderBook() throws Exception {
        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertFalse(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForSellOrderAndNotEmptyBuySideInOrderBook() throws Exception {
        OrderMessage orderMessageBuy = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBookModel.getBuyEntries().add(orderMessageBuy);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertFalse(result);
    }

    @Test
    public void process_ShouldAddOrderMessageToOrderBookAndSortByPriceDescending() throws Exception {
        OrderMessage orderMessagePrice200 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        orderBookModel.getSellEntries().add(orderMessagePrice200);

        OrderMessage orderMessagePrice100 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        strategy.process(orderBookModel, orderMessagePrice100);

        assertEquals(2, orderBookModel.getSellEntries().size());
        assertEquals(orderMessagePrice100, orderBookModel.getSellEntries().stream().findFirst().get());
    }
}