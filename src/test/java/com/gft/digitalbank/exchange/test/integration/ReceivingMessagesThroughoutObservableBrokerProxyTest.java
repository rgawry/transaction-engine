package com.gft.digitalbank.exchange.test.integration;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.broker.ObservableBrokerImp;
import com.gft.digitalbank.exchange.solution.factories.MessageHandlerFactory;
import com.gft.digitalbank.exchange.solution.handler.MessageHandlerImp;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.processors.MessageProcessor;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxyImp;
import com.gft.digitalbank.exchange.solution.services.MessageConsumerService;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.gft.digitalbank.exchange.solution.services.OrderBookServiceImp;
import com.gft.digitalbank.exchange.test.helpers.TestInternalMessageConsumer;
import org.junit.Ignore;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceivingMessagesThroughoutObservableBrokerProxyTest {
    @Ignore
    @Test
    public void AfterMergeStreams_SubscribersShouldReceiveMessagesThroughoutProxy() throws Exception {
        //Arrange
        String broker1 = "broker1";
        String broker2 = "broker2";

        MessageConsumer messageConsumer1 = new TestInternalMessageConsumer();
        MessageConsumer messageConsumer2 = new TestInternalMessageConsumer();
        MessageConsumerService messageConsumerService = mock(MessageConsumerService.class);
        when(messageConsumerService.create(broker1)).thenReturn(messageConsumer1);
        when(messageConsumerService.create(broker2)).thenReturn(messageConsumer2);

        StockMessageMapper stockMessageMapper1 = mock(StockMessageMapper.class);
        StockMessageMapper stockMessageMapper2 = mock(StockMessageMapper.class);
        ObservableBrokerImp observableBroker1 = new ObservableBrokerImp(stockMessageMapper1, messageConsumerService, broker1);
        observableBroker1.initialize();
        ObservableBrokerImp observableBroker2 = new ObservableBrokerImp(stockMessageMapper2, messageConsumerService, broker2);
        observableBroker2.initialize();

        TextMessage message1 = mock(TextMessage.class);
        when(message1.getStringProperty("messageType")).thenReturn(MessageType.ORDER.name());
        when(message1.getText()).thenReturn("test-body");
        TextMessage message2 = mock(TextMessage.class);
        when(message2.getStringProperty("messageType")).thenReturn(MessageType.CANCEL.name());
        when(message2.getText()).thenReturn("test-body");
        TextMessage message3 = mock(TextMessage.class);
        when(message3.getStringProperty("messageType")).thenReturn(MessageType.MODIFICATION.name());
        when(message3.getText()).thenReturn("test-body");

        StockMessage stockMessage1 = StockMessage.builder().type(MessageType.ORDER.name()).body("{ product: \"A\" }").build();
        StockMessage stockMessage2 = StockMessage.builder().type(MessageType.CANCEL.name()).body("{ product: \"A\" }").build();
        StockMessage stockMessage3 = StockMessage.builder().type(MessageType.SHUTDOWN_NOTIFICATION.name()).body("{ product: \"B\" }").build();

        when(stockMessageMapper1.toStockMessage(message1)).thenReturn(stockMessage1);
        when(stockMessageMapper1.toStockMessage(message2)).thenReturn(stockMessage2);
        when(stockMessageMapper1.toStockMessage(message3)).thenReturn(stockMessage3);

        when(stockMessageMapper2.toStockMessage(message1)).thenReturn(stockMessage1);
        when(stockMessageMapper2.toStockMessage(message2)).thenReturn(stockMessage2);
        when(stockMessageMapper2.toStockMessage(message3)).thenReturn(stockMessage3);

        OrderBookService orderBookService = new OrderBookServiceImp();
        MessageHandlerFactory messageHandlerFactory = mock(MessageHandlerFactory.class);
        ObservableBrokerProxyImp observableBrokerProxy = new ObservableBrokerProxyImp();
        observableBrokerProxy.setOrderBookService(orderBookService);
        observableBrokerProxy.setMessageHandlerFactory(messageHandlerFactory);
        observableBrokerProxy.setSchedulerSubscribeOn(Schedulers.immediate());
        observableBrokerProxy.setSchedulerObserveOn(Schedulers.immediate());

        MessageProcessor messageProcessor1 = mock(MessageProcessor.class);
        when(messageProcessor1.getProcessorType()).thenReturn(MessageType.ORDER);
        MessageProcessor messageProcessor2 = mock(MessageProcessor.class);
        when(messageProcessor2.getProcessorType()).thenReturn(MessageType.CANCEL);

        TestSubscriber<StockMessage> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<StockMessage> testSubscriber2 = new TestSubscriber<>();

        Set<MessageProcessor> messageProcessors = new HashSet<>();
        messageProcessors.add(messageProcessor1);
        messageProcessors.add(messageProcessor2);
        MessageHandlerService messageHandlerService = mock(MessageHandlerService.class);
        MessageHandlerImp messageHandler1 = new MessageHandlerImp("A", messageProcessors, messageHandlerService);
        when(messageHandlerFactory.create("A")).thenReturn(messageHandler1);
        messageHandler1.setObserver(testSubscriber1);
        messageHandler1.subscribe(observableBrokerProxy);
        MessageHandlerImp messageHandler2 = new MessageHandlerImp("B", messageProcessors, messageHandlerService);
        when(messageHandlerFactory.create("B")).thenReturn(messageHandler2);
        messageHandler2.setObserver(testSubscriber2);
        messageHandler2.subscribe(observableBrokerProxy);
        observableBrokerProxy.mergeStreams(Arrays.asList(observableBroker1, observableBroker2));

        //Act
        messageConsumer1.getMessageListener().onMessage(message1);
        messageConsumer1.getMessageListener().onMessage(message2);
        messageConsumer1.getMessageListener().onMessage(message3);
        messageConsumer2.getMessageListener().onMessage(message1);
        messageConsumer2.getMessageListener().onMessage(message2);
        messageConsumer2.getMessageListener().onMessage(message3);

        //Assert
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertReceivedOnNext(Arrays.asList(stockMessage1, stockMessage2, stockMessage1, stockMessage2));
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertReceivedOnNext(Arrays.asList(stockMessage3, stockMessage3));
    }
}
