package com.gft.digitalbank.exchange.solution.proxy;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.Json;
import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;
import com.gft.digitalbank.exchange.solution.factories.MessageHandlerFactory;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.google.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.*;
import java.util.stream.Collectors;

public class ObservableBrokerProxyImp implements ObservableBrokerProxy {
    private Scheduler observeOnScheduler = Schedulers.newThread();
    private Scheduler subscribeOnScheduler = Schedulers.newThread();
    private OrderBookService orderBookService;
    private MessageHandlerFactory messageHandlerFactory;
    private Map<Integer, String> orderToProductNameMap = new HashMap<>();
    private PublishSubject<StockMessage> subject = PublishSubject.create();

    @Inject
    public void setOrderBookService(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @Inject
    public void setMessageHandlerFactory(MessageHandlerFactory messageHandlerFactory) {
        this.messageHandlerFactory = messageHandlerFactory;
    }

    public void setSchedulerObserveOn(Scheduler scheduler) {
        observeOnScheduler = scheduler;
    }

    public void setSchedulerSubscribeOn(Scheduler scheduler) {
        subscribeOnScheduler = scheduler;
    }

    @Override
    public void mergeStreams(List<ObservableBroker> streams) {
        Observable<ArrayList<StockMessage>> merged = Observable
                .merge(streams.stream().map(ObservableBroker::getMessageStream).collect(Collectors.toList()))
                .onBackpressureBuffer()
                .collect(ArrayList<StockMessage>::new, ArrayList::add)
                .doOnNext(Collections::sort);
        merged.concatMapIterable(stockMessages -> stockMessages)
                .map(this::extractProduct)
                .subscribe(subject);
        subject.observeOn(observeOnScheduler)
                .subscribeOn(subscribeOnScheduler);
    }

    @Override
    public Observable<StockMessage> getMessageStream() {
        return subject;
    }

    private StockMessage extractProduct(StockMessage stockMessage) {
        if (stockMessage.getType().equals(MessageType.ORDER.name())) {
            String productName = Json.getValue("product", stockMessage.getBody());
            stockMessage.setProduct(productName);
            OrderBookModel orderBook = orderBookService.getOrderBook(productName);
            if (orderBook == null) {
                orderBookService.createOrderBook(productName);
                messageHandlerFactory.create(productName).subscribe(this);
            }
            Integer id = Integer.parseInt(Json.getValue("id", stockMessage.getBody()));
            orderToProductNameMap.put(id, productName);
        }

        if (stockMessage.getType().equals(MessageType.CANCEL.name())) {
            Integer cancelledOrderId = Integer.parseInt(Json.getValue("cancelledOrderId", stockMessage.getBody()));
            String productName = orderToProductNameMap.get(cancelledOrderId);
            stockMessage.setProduct(productName);
        }

        if (stockMessage.getType().equals(MessageType.MODIFICATION.name())) {
            Integer modifiedOrderId = Integer.parseInt(Json.getValue("modifiedOrderId", stockMessage.getBody()));
            String productName = orderToProductNameMap.get(modifiedOrderId);
            stockMessage.setProduct(productName);
        }
        return stockMessage;
    }
}
