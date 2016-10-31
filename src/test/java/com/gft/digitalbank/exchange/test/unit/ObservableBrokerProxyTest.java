package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;
import com.gft.digitalbank.exchange.solution.factories.MessageHandlerFactory;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxyImp;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObservableBrokerProxyTest {
    private final ObservableBrokerProxyImp observableBrokerProxy = new ObservableBrokerProxyImp();
    private ObservableBroker observableBroker = mock(ObservableBroker.class);
    private StockMessage stockMessage = StockMessage.builder().type(MessageType.ORDER.name()).body("{ product: \"test-product\", id: " + 1 + " }").build();
    private TestSubscriber<StockMessage> testSubscriber;
    private OrderBookService orderBookService = mock(OrderBookService.class);
    private MessageHandlerFactory messageHandlerFactory = mock(MessageHandlerFactory.class);

    @Before
    public void setUp() throws Exception {
        when(observableBroker.getMessageStream()).thenReturn(Observable.just(stockMessage));
        when(orderBookService.getOrderBook(any())).thenReturn(new OrderBookModel());
        observableBrokerProxy.setOrderBookService(orderBookService);
        observableBrokerProxy.setMessageHandlerFactory(messageHandlerFactory);
        observableBrokerProxy.setSchedulerObserveOn(Schedulers.immediate());
        observableBrokerProxy.setSchedulerSubscribeOn(Schedulers.immediate());
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void getMessageStream_WhenSubscribe_ShouldSentItemsToSubscribers() throws Exception {
        observableBrokerProxy.getMessageStream().subscribe(testSubscriber);
        observableBrokerProxy.mergeStreams(Collections.singletonList(observableBroker));

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(stockMessage));
        testSubscriber.assertCompleted();
    }
}