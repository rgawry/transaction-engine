package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.broker.ObservableBrokerImp;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.services.MessageConsumerService;
import com.gft.digitalbank.exchange.test.helpers.TestInternalMessageConsumer;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ObservableBrokerTest {
    private Message message = mock(Message.class);
    private StockMessage stockMessage = StockMessage.builder().type("test-message-type").body("test-body").build();
    private MessageConsumer messageConsumer = new TestInternalMessageConsumer();
    private MessageConsumerService messageConsumerService = mock(MessageConsumerService.class);
    private StockMessageMapper stockMessageMapper = mock(StockMessageMapper.class);
    private ObservableBrokerImp observableBroker = new ObservableBrokerImp(stockMessageMapper, messageConsumerService, "test-destination");
    private TestSubscriber<StockMessage> testSubscriber = new TestSubscriber<>();

    @Before
    public void setUp() throws Exception {
        when(messageConsumerService.create(any())).thenReturn(messageConsumer);
        observableBroker.initialize();
    }

    @Test
    public void getMessageStream_WhenMessagePushedToStream_SubscriberShouldReceiveMessage() throws JMSException {
        when(message.getStringProperty(any())).thenReturn("test-stockMessage-type");
        when(stockMessageMapper.toStockMessage(any(Message.class))).thenReturn(stockMessage);

        observableBroker.getMessageStream().subscribe(testSubscriber);
        messageConsumer.getMessageListener().onMessage(message);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(stockMessage));
    }

    @Test
    public void getMessageStream_WhenShutdownNotificationReceived_ShouldInvokeSubscriberOnCompleted() throws JMSException {
        when(message.getStringProperty(any())).thenReturn(MessageType.SHUTDOWN_NOTIFICATION.name());

        observableBroker.getMessageStream().subscribe(testSubscriber);
        messageConsumer.getMessageListener().onMessage(message);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    @Test
    public void getMessageStream_WhenShutdownNotificationReceived_ShouldCloseConnection() throws JMSException {
        when(message.getStringProperty(any())).thenReturn(MessageType.SHUTDOWN_NOTIFICATION.name());

        observableBroker.getMessageStream().subscribe(testSubscriber);
        messageConsumer.getMessageListener().onMessage(message);

        verify(messageConsumerService).close(any());
    }
}
