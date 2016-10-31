package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.models.Details;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolver;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolverImp;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.gft.digitalbank.exchange.solution.services.TransactionServiceImp;
import com.gft.digitalbank.exchange.solution.strategies.SellWhenBuySideMatchPositionOrderStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SellWhenBuySideMatchStrategyTest {
    private SellWhenBuySideMatchPositionOrderStrategy strategy;
    private OrderBookModel orderBookModel;
    private TransactionService transactionService;
    private TransactionIdResolver transactionIdResolver;

    private ArgumentCaptor<Transaction> argTransaction;

    @Before
    public void setUp() throws Exception {
        orderBookModel = new OrderBookModel("TEST", new ArrayList<>(), new ArrayList<>());
        transactionIdResolver = Mockito.spy(new TransactionIdResolverImp());
        transactionService = Mockito.spy(new TransactionServiceImp(transactionIdResolver));
        strategy = new SellWhenBuySideMatchPositionOrderStrategy(transactionService);
        argTransaction = ArgumentCaptor.forClass(Transaction.class);
    }

    @Test
    public void canProcess_ShouldReturnTrueForSellOrderWhenBuySideMatch() throws Exception {
        OrderMessage buyMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 100))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage);

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
    public void canProcess_ShouldReturnFalseForSellOrderWhenBuySideDoesntMatch() throws Exception {
        OrderMessage buyMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("BROKER")
                .client("CLIENT")
                .product("PRODUCT")
                .details(new Details(1, 50))
                .build();

        orderBookModel.getSellEntries().add(buyMessage);

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
    public void canProcess_ShouldReturnFalseForBuyOrder() throws Exception {
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

    // Sell Scenario 01
    // OrderBookModel - buy side
    //    100 x 10pln
    // Sell order - 100 x 10pln
    @Test
    public void process_SellScenario01() throws Exception {
        OrderMessage buyMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(0, orderBookModel.getSellEntries().size());
        assertEquals(0, orderBookModel.getBuyEntries().size());

        verify(transactionService).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getValue().getProduct());
        assertEquals("BuyBroker", argTransaction.getValue().getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getValue().getBrokerSell());
        assertEquals("BuyClient", argTransaction.getValue().getClientBuy());
        assertEquals("SellClient", argTransaction.getValue().getClientSell());
        assertEquals(100, argTransaction.getValue().getAmount());
        assertEquals(10, argTransaction.getValue().getPrice());
    }

    // Sell Scenario 02
    // OrderBookModel - buy side
    //    50 x 10pln
    //    50 x 10pln
    // Sell order - 100 x 10pln
    @Test
    public void process_SellScenario02() throws Exception {
        OrderMessage buyMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker1")
                .client("BuyClient1")
                .product("PRODUCT")
                .details(new Details(50, 10))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage1);

        OrderMessage buyMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker2")
                .client("BuyClient2")
                .product("PRODUCT")
                .details(new Details(50, 10))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(0, orderBookModel.getSellEntries().size());
        assertEquals(0, orderBookModel.getBuyEntries().size());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker1", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient1", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker2", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient2", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(1).getPrice());
    }

    // Sell Scenario 03
    // OrderBookModel - buy side
    //    50 x 100pln
    //    25 x  50pln
    //    25 x  10pln
    // Sell order - 100 x 10pln
    @Test
    public void process_SellScenario03() throws Exception {
        OrderMessage buyMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker1")
                .client("BuyClient1")
                .product("PRODUCT")
                .details(new Details(50, 100))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage1);

        OrderMessage buyMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker2")
                .client("BuyClient2")
                .product("PRODUCT")
                .details(new Details(25, 50))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage2);

        OrderMessage buyMessage3 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker3")
                .client("BuyClient3")
                .product("PRODUCT")
                .details(new Details(25, 10))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage3);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(0, orderBookModel.getSellEntries().size());
        assertEquals(0, orderBookModel.getBuyEntries().size());

        verify(transactionService, times(3)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker1", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient1", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(100, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker2", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient2", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(25, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(50, argTransaction.getAllValues().get(1).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(2).getProduct());
        assertEquals("BuyBroker3", argTransaction.getAllValues().get(2).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(2).getBrokerSell());
        assertEquals("BuyClient3", argTransaction.getAllValues().get(2).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(2).getClientSell());
        assertEquals(25, argTransaction.getAllValues().get(2).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(2).getPrice());
    }

    // Sell Scenario 04
    // OrderBookModel - buy side
    //    50 x 20pln
    //   150 x 10pln
    // Buy order - 100 x 10pln
    @Test
    public void process_SellScenario04() throws Exception {
        OrderMessage buyMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker1")
                .client("BuyClient1")
                .product("PRODUCT")
                .details(new Details(50, 20))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage1);

        OrderMessage buyMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker2")
                .client("BuyClient2")
                .product("PRODUCT")
                .details(new Details(150, 10))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage2);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(1, orderBookModel.getBuyEntries().size());
        assertEquals(0, orderBookModel.getSellEntries().size());
        assertEquals(100, orderBookModel.getBuyEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(10, orderBookModel.getBuyEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker1", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient1", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(20, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker2", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient2", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(50, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(10, argTransaction.getAllValues().get(1).getPrice());
    }

    // Sell Scenario 05
    // OrderBookModel - buy side
    //    50 x  15pln
    // Sell order - 100 x 10pln
    @Test
    public void process_SellScenario05() throws Exception {
        OrderMessage buyMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker")
                .client("BuyClient")
                .product("PRODUCT")
                .details(new Details(50, 15))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage1);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(100, 10))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(0, orderBookModel.getBuyEntries().size());
        assertEquals(1, orderBookModel.getSellEntries().size());
        assertEquals(50, orderBookModel.getSellEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(10, orderBookModel.getSellEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getValue().getProduct());
        assertEquals("BuyBroker", argTransaction.getValue().getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getValue().getBrokerSell());
        assertEquals("BuyClient", argTransaction.getValue().getClientBuy());
        assertEquals("SellClient", argTransaction.getValue().getClientSell());
        assertEquals(50, argTransaction.getValue().getAmount());
        assertEquals(15, argTransaction.getValue().getPrice());
    }

    // Sell Scenario 06
    // OrderBookModel - buy side
    //    200 x 100pln
    //    300 x 100pln
    //    100 x  90pln
    // Sell order - 1000 x 95pln
    @Test
    public void process_SellScenario06() throws Exception {
        OrderMessage buyMessage1 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker1")
                .client("BuyClient1")
                .product("PRODUCT")
                .details(new Details(200, 100))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage1);

        OrderMessage buyMessage2 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker2")
                .client("BuyClient2")
                .product("PRODUCT")
                .details(new Details(300, 100))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage2);

        OrderMessage buyMessage3 = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("BUY")
                .broker("BuyBroker3")
                .client("BuyClient3")
                .product("PRODUCT")
                .details(new Details(100, 90))
                .build();

        orderBookModel.getBuyEntries().add(buyMessage3);

        OrderMessage orderMessage = OrderMessage
                .builder()
                .messageType("OrderMessage")
                .side("SELL")
                .broker("SellBroker")
                .client("SellClient")
                .product("PRODUCT")
                .details(new Details(1000, 95))
                .build();

        strategy.process(orderBookModel, orderMessage);

        assertEquals(1, orderBookModel.getBuyEntries().size());
        assertEquals(1, orderBookModel.getSellEntries().size());
        assertEquals(100, orderBookModel.getBuyEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(90, orderBookModel.getBuyEntries().stream().findFirst().get().getDetails().getPrice());
        assertEquals(500, orderBookModel.getSellEntries().stream().findFirst().get().getDetails().getAmount());
        assertEquals(95, orderBookModel.getSellEntries().stream().findFirst().get().getDetails().getPrice());

        verify(transactionService, times(2)).add(argTransaction.capture());

        assertEquals("TEST", argTransaction.getAllValues().get(0).getProduct());
        assertEquals("BuyBroker1", argTransaction.getAllValues().get(0).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(0).getBrokerSell());
        assertEquals("BuyClient1", argTransaction.getAllValues().get(0).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(0).getClientSell());
        assertEquals(200, argTransaction.getAllValues().get(0).getAmount());
        assertEquals(100, argTransaction.getAllValues().get(0).getPrice());

        assertEquals("TEST", argTransaction.getAllValues().get(1).getProduct());
        assertEquals("BuyBroker2", argTransaction.getAllValues().get(1).getBrokerBuy());
        assertEquals("SellBroker", argTransaction.getAllValues().get(1).getBrokerSell());
        assertEquals("BuyClient2", argTransaction.getAllValues().get(1).getClientBuy());
        assertEquals("SellClient", argTransaction.getAllValues().get(1).getClientSell());
        assertEquals(300, argTransaction.getAllValues().get(1).getAmount());
        assertEquals(100, argTransaction.getAllValues().get(1).getPrice());
    }
}