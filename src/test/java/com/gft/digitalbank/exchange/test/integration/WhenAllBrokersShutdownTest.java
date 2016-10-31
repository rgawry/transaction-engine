package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.TransactionEngine;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerServiceImp;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WhenAllBrokersShutdownTest {
    private MessageHandlerService messageHandlerService = new MessageHandlerServiceImp();
    private TransactionService transactionService = mock(TransactionService.class);
    private OrderBookService orderBookService = mock(OrderBookService.class);
    private ProcessingListener processingListener = mock(ProcessingListener.class);
    private TransactionEngine engine = new TransactionEngine(messageHandlerService);

    @Before
    public void setUp() throws Exception {
        engine.initialize();
        engine.setProcessingListener(processingListener);
        engine.setTransactionService(transactionService);
        engine.setOrderBookService(orderBookService);
    }

    @Test
    public void WhenAllBrokersAreShutdown_EngineShouldInvokeProcessingDone() {
        messageHandlerService.register();
        messageHandlerService.unregister();

        verify(processingListener).processingDone(any());
    }
}
