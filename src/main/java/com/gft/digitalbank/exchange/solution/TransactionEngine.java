package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.model.OrderBook;
import com.gft.digitalbank.exchange.model.OrderEntry;
import com.gft.digitalbank.exchange.model.SolutionResult;
import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;
import com.gft.digitalbank.exchange.solution.factories.ObservableBrokerFactory;
import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionEngine implements Initializable {
    private MessageHandlerService messageHandlerService;
    private TransactionService transactionService;
    private OrderBookService orderBookService;
    private ObservableBrokerFactory observableBrokerFactory;
    private ObservableBrokerProxy observableBrokerProxy;
    private ProcessingListener processingListener;
    private List<String> destinations;

    @Inject
    public TransactionEngine(MessageHandlerService messageHandlerService) {
        this.messageHandlerService = messageHandlerService;
    }

    @Override
    public void initialize() {
        setCompletedListener();
    }

    @Inject
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Inject
    public void setOrderBookService(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @Inject
    public void setObservableBrokerFactory(ObservableBrokerFactory observableBrokerFactory) {
        this.observableBrokerFactory = observableBrokerFactory;
    }

    @Inject
    public void setObservableBrokerProxy(ObservableBrokerProxy observableBrokerProxy) {
        this.observableBrokerProxy = observableBrokerProxy;
    }

    public void setProcessingListener(ProcessingListener processingListener) {
        this.processingListener = processingListener;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public void start() {
        createMergedMessageStream();
    }

    private void createMergedMessageStream() {
        List<ObservableBroker> streams = destinations.stream().map(observableBrokerFactory::create).collect(Collectors.toList());
        observableBrokerProxy.mergeStreams(streams);
    }

    private void setCompletedListener() {
        messageHandlerService.setCompletedListener(() -> processingListener.processingDone(getSolutionResult()));
    }

    private SolutionResult getSolutionResult() {
        return SolutionResult.builder()
                .transactions(transactionService.getTransactions())
                .orderBooks(getOrderBooks())
                .build();
    }

    private List<OrderBook> getOrderBooks() {
        List<OrderBook> orderBooks = new ArrayList<>();
        Set<OrderBookModel> orderBookModels = orderBookService.getOrderBooks();

        orderBookModels.stream().forEach(orderBook -> {

            Set<OrderEntry> buyEntries = new HashSet<>();
            int id = 1;
            for (OrderMessage buyPositionOrder : orderBook.getBuyEntries()) {
                OrderEntry buyEntry = new OrderEntry(id, buyPositionOrder.getBroker(), buyPositionOrder.getDetails().getAmount(), buyPositionOrder.getDetails().getPrice(), buyPositionOrder.getClient());
                buyEntries.add(buyEntry);
                id++;
            }

            Set<OrderEntry> sellEntries = new HashSet<>();
            id = 1;
            for (OrderMessage sellPositionOrder : orderBook.getSellEntries()) {
                OrderEntry sellEntry = new OrderEntry(id, sellPositionOrder.getBroker(), sellPositionOrder.getDetails().getAmount(), sellPositionOrder.getDetails().getPrice(), sellPositionOrder.getClient());
                sellEntries.add(sellEntry);
                id++;
            }

            if (buyEntries.size() > 0 || sellEntries.size() > 0)
                orderBooks.add(OrderBook.builder()
                        .product(orderBook.getProduct())
                        .buyEntries(buyEntries.stream().sorted((x, y) -> x.getId() - y.getId()).collect(Collectors.toList()))
                        .sellEntries(sellEntries.stream().sorted((x, y) -> x.getId() - y.getId()).collect(Collectors.toList()))
                        .build());
        });

        return orderBooks;
    }
}

