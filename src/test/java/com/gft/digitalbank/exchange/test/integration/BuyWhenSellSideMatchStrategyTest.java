package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.models.Details;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolver;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolverImp;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.gft.digitalbank.exchange.solution.services.TransactionServiceImp;
import com.gft.digitalbank.exchange.solution.strategies.BuyWhenSellSideMatchPositionOrderStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class BuyWhenSellSideMatchStrategyTest {
    private BuyWhenSellSideMatchPositionOrderStrategy strategy;
    private OrderBookModel orderBook;
    private TransactionService transactionService;
    private TransactionIdResolver transactionIdResolver;
    private ArgumentCaptor<Transaction> argTransaction;

    @Before
    public void setUp() throws Exception {
        orderBook = new OrderBookModel("TEST", new ArrayList<>(), new ArrayList<>());
        transactionIdResolver = Mockito.spy(new TransactionIdResolverImp());
        transactionService = Mockito.spy(new TransactionServiceImp(transactionIdResolver));
        strategy = new BuyWhenSellSideMatchPositionOrderStrategy(transactionService);
        argTransaction = ArgumentCaptor.forClass(Transaction.class);
    }

    @Test
    public void canProcess_ShouldReturnTrueForBuyOrderWhenSellSideMatch() throws Exception {
        OrderMessage sellMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBook.getSellEntries().add(sellMessage);

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
    public void canProcess_ShouldReturnFalseForBuyOrderWhenSellSideDoesNotMatch() throws Exception {
        OrderMessage sellMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 200))
                .build();

        orderBook.getSellEntries().add(sellMessage);

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
    public void canProcess_ShouldReturnFalseForSellOrder() throws Exception {
        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        boolean result = strategy.canProcess(orderBook, orderMessage);

        assertFalse(result);
    }

    // Buy Scenario 01
    // OrderBookModel - sell side
    //    100 x 10pln
    // Buy order - 100 x 10pln
    @Test
    public void process_BuyScenario01() throws Exception {
        OrderMessage sellMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        orderBook.getSellEntries().add(sellMessage);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(0, orderBook.getSellEntries().size());
        assertEquals(0, orderBook.getBuyEntries().size());

        verify(transactionService).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getValue().getProduct());
        assertEquals("BuyBroker", argTransaction.getValue().getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getValue().getBrokerSell());
        assertEquals("BuyClient", argTransaction.getValue().getClientBuy());
        assertEquals("SellClient", argTransaction.getValue().getClientSell());
        assertEquals(100, argTransaction.getValue().getAmount());
        assertEquals(10, argTransaction.getValue().getPrice());
    }

    // Buy Scenario 02
    // OrderBookModel - sell side
    //    50 x 10pln
    //    50 x 10pln
    // Buy order - 100 x 10pln
    @Test
    public void process_BuyScenario02() throws Exception {
        OrderMessage sellMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker1")
                .client("SellClient1")
                .product("PRODUCT")
                .details(new Details(50, 10))
                .build();

        orderBook.getSellEntries().add(sellMessage1);

        OrderMessage sellMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker2")
                .client("SellClient2")
                .product("PRODUCT")
                .details(new Details(50, 10))
                .build();

        orderBook.getSellEntries().add(sellMessage2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(0, orderBook.getSellEntries().size());
        assertEquals(0, orderBook.getBuyEntries().size());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker1", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient1", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker2", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient2", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(1).getPrice());
    }

    // Buy Scenario 03
    // OrderBookModel - sell side
    //    25 x  1pln
    //    25 x  5pln
    //    50 x 10pln
    // Buy order - 100 x 10pln
    @Test
    public void process_BuyScenario03() throws Exception {
        OrderMessage sellMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker1")
                .client("SellClient1")
                .product("PRODUCT")
                .details(new Details(25, 1))
                .build();

        orderBook.getSellEntries().add(sellMessage1);

        OrderMessage sellMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker2")
                .client("SellClient2")
                .product("PRODUCT")
                .details(new Details(25, 5))
                .build();

        orderBook.getSellEntries().add(sellMessage2);

        OrderMessage sellMessage3 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker3")
                .client("SellClient3")
                .product("PRODUCT")
                .details(new Details(50, 10))
                .build();

        orderBook.getSellEntries().add(sellMessage3);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(0, orderBook.getSellEntries().size());
        assertEquals(0, orderBook.getBuyEntries().size());

        verify(transactionService, times(3)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker1", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient1", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(25, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(1, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker2", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient2", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(25, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(5, argTransaction.getAllValues().get(1).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(2).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(2).getBrokerBuy());
        assertEquals("SellBroker3", argTransaction.getAllValues().get(2).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(2).getClientBuy());
        assertEquals("SellClient3", argTransaction.getAllValues().get(2).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(2).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(2).getPrice());
    }

    // Buy Scenario 04
    // OrderBookModel - sell side
    //    50 x  5pln
    //   150 x 10pln
    // Buy order - 100 x 10pln
    @Test
    public void process_BuyScenario04() throws Exception {
        OrderMessage sellMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker1")
                .client("SellClient1")
                .product("PRODUCT")
                .details(new Details(50, 5))
                .build();

        orderBook.getSellEntries().add(sellMessage1);

        OrderMessage sellMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker2")
                .client("SellClient2")
                .product("PRODUCT")
                .details(new Details(150, 10))
                .build();

        orderBook.getSellEntries().add(sellMessage2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(1, orderBook.getSellEntries().size());
        assertEquals(0, orderBook.getBuyEntries().size());
        assertEquals(100, orderBook.getSellEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(10, orderBook.getSellEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker1", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient1", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(5, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker2", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient2", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(1).getPrice());
    }

    // Buy Scenario 05
    // OrderBookModel - sell side
    //    50 x  5pln
    // Buy order - 100 x 10pln
    @Test
    public void process_BuyScenario05() throws Exception {
        OrderMessage sellMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker1")
                .client("SellClient1")
                .product("PRODUCT")
                .details(new Details(50, 5))
                .build();

        orderBook.getSellEntries().add(sellMessage1);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(0, orderBook.getSellEntries().size());
        assertEquals(1, orderBook.getBuyEntries().size());
        assertEquals(50, orderBook.getBuyEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(10, orderBook.getBuyEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getValue().getProduct());
        assertEquals("BuyBroker", argTransaction.getValue().getBrokerBuy());
        assertEquals("SellBroker1", argTransaction.getValue().getBrokerSell());
        assertEquals("BuyClient", argTransaction.getValue().getClientBuy());
        assertEquals("SellClient1", argTransaction.getValue().getClientSell());
        assertEquals(50, argTransaction.getValue().getAmount());
        assertEquals(5, argTransaction.getValue().getPrice());
    }

    // Buy Scenario 06
    // OrderBookModel - sell side
    //    200 x  90pln
    //    300 x  90pln
    //    100 x 100pln
    // Buy order - 1000 x 90pln
    @Test
    public void process_BuyScenario06() throws Exception {
        OrderMessage sellMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker1")
                .client("SellClient1")
                .product("PRODUCT")
                .details(new Details(200, 90))
                .build();

        orderBook.getSellEntries().add(sellMessage1);

        OrderMessage sellMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker2")
                .client("SellClient2")
                .product("PRODUCT")
                .details(new Details(300, 90))
                .build();

        orderBook.getSellEntries().add(sellMessage2);

        OrderMessage sellMessage3 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker3")
                .client("SellClient3")
                .product("PRODUCT")
                .details(new Details(100, 100))
                .build();

        orderBook.getSellEntries().add(sellMessage3);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(1000, 90))
                .build();

        strategy.process(orderBook, orderMessage);

        assertEquals(1, orderBook.getSellEntries().size());
        assertEquals(1, orderBook.getBuyEntries().size());
        assertEquals(100, orderBook.getSellEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(100, orderBook.getSellEntries().stream().findFirst().get().getDetails().getPrice());
        assertEquals(500, orderBook.getBuyEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(90, orderBook.getBuyEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker1", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient1", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(200, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(90, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker2", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient2", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(300, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(90, argTransaction.getAllValues().get(1).getPrice());
    }
}