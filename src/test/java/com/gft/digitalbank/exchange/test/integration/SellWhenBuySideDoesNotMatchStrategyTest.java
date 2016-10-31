package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.solution.models.Details;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.strategies.SellWhenBuySideDoesNotMatchPositionOrderStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SellWhenBuySideDoesNotMatchStrategyTest {
    private SellWhenBuySideDoesNotMatchPositionOrderStrategy strategy;
    private OrderBookModel orderBookModel;

    @Before
    public void setUp() throws Exception {
        strategy = new SellWhenBuySideDoesNotMatchPositionOrderStrategy();
        orderBookModel = new OrderBookModel("TEST", new ArrayList<>(), new ArrayList<>());
    }

    @Test
    public void canProcess_ShouldReturnTrueForSellOrderWhenBuySideDoesntMatch() throws Exception {
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
                .details(new Details(1, 200))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertTrue(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForBuyOrder() throws Exception {
        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertFalse(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForSellOrderWhenBuySideIsMatch() throws Exception {
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
                .details(new Details(1, 50))
                .build();

        boolean result = strategy.canProcess(orderBookModel, orderMessage);

        assertFalse(result);
    }

    @Test
    public void canProcess_ShouldReturnFalseForSellOrderWhenBuySideOneMatchAndOneNot() throws Exception {
        OrderMessage orderMessageBuy1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBookModel.getBuyEntries().add(orderMessageBuy1);

        OrderMessage orderMessageBuy2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 20))
                .build();

        orderBookModel.getBuyEntries().add(orderMessageBuy2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 50))
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