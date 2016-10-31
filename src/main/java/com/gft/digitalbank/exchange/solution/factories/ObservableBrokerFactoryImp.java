package com.gft.digitalbank.exchange.solution.factories;

import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;
import com.gft.digitalbank.exchange.solution.broker.ObservableBrokerImp;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.services.MessageConsumerService;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class ObservableBrokerFactoryImp implements ObservableBrokerFactory {
    private final StockMessageMapper stockMessageMapper;
    private final MessageConsumerService messageConsumerService;

    @Inject
    public ObservableBrokerFactoryImp(StockMessageMapper stockMessageMapper, MessageConsumerService messageConsumerService) {
        Preconditions.checkNotNull(stockMessageMapper, "stockMessageMapper cannot be null");
        Preconditions.checkNotNull(messageConsumerService, "messageConsumerService cannot be null");
        this.stockMessageMapper = stockMessageMapper;
        this.messageConsumerService = messageConsumerService;
    }

    @Override
    public ObservableBroker create(String name) {
        ObservableBrokerImp observableBroker = new ObservableBrokerImp(stockMessageMapper, messageConsumerService, name);
        observableBroker.initialize();
        return observableBroker;
    }
}
