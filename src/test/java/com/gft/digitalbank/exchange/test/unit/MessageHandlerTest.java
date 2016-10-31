package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.handler.MessageHandlerImp;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.processors.MessageProcessor;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class MessageHandlerTest {
    private StockMessage stockMessage1 = StockMessage.builder().type(MessageType.ORDER.name()).body("test-body").build();
    private StockMessage stockMessage2 = StockMessage.builder().type(MessageType.ORDER.name()).body("test-body").build();
    private StockMessage stockMessage3 = StockMessage.builder().type(MessageType.CANCEL.name()).body("test-body").build();
    private MessageProcessor messageProcessor = mock(MessageProcessor.class);
    private ObservableBrokerProxy observableBrokerProxy = mock(ObservableBrokerProxy.class);
    private MessageHandlerService messageHandlerService = mock(MessageHandlerService.class);
    private MessageHandlerImp messageHandler;
    private TestSubscriber<StockMessage> testSubscriber;
    private Set<MessageProcessor> messageProcessors = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        stockMessage1.setProduct("A");
        stockMessage2.setProduct("A");
        stockMessage3.setProduct("A");
        when(messageProcessor.getProcessorType()).thenReturn(MessageType.ORDER);
        when(observableBrokerProxy.getMessageStream()).thenReturn(Observable.from(Arrays.asList(stockMessage1, stockMessage2, stockMessage3)));
        testSubscriber = new TestSubscriber<>();
        messageHandler = new MessageHandlerImp("A", messageProcessors, messageHandlerService);
        messageHandler.initialize();
    }

    @Test
    public void subscribe_WhenReceivingMessages_ShouldFilterMessagesByMessageType() {
        messageHandler.setObserver(testSubscriber);

        messageHandler.subscribe(observableBrokerProxy);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Arrays.asList(stockMessage1, stockMessage2, stockMessage3));
        testSubscriber.assertCompleted();
    }

    @Test
    public void subscribe_WenSubscribing_ShouldRegister() {
        messageHandler.subscribe(observableBrokerProxy);

        verify(messageHandlerService).register();
    }

    @Test
    public void onCompleted_WhenOnCompleted_ShouldUnregister() {
        messageHandler.subscribe(observableBrokerProxy);

        verify(messageHandlerService).unregister();
    }
}
