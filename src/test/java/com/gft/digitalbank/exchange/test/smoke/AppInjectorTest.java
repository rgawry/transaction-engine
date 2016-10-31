package com.gft.digitalbank.exchange.test.smoke;

import com.gft.digitalbank.exchange.solution.TransactionEngine;
import com.gft.digitalbank.exchange.solution.factories.MessageConsumerFactory;
import com.gft.digitalbank.exchange.solution.factories.MessageHandlerFactory;
import com.gft.digitalbank.exchange.solution.factories.ObservableBrokerFactory;
import com.gft.digitalbank.exchange.solution.injector.AppInjector;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;
import com.gft.digitalbank.exchange.solution.resolvers.PositionOrderStrategyResolver;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolver;
import com.gft.digitalbank.exchange.solution.services.MessageConsumerService;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class AppInjectorTest {
    @Test
    public void configure() throws Exception {
        Injector injector = Guice.createInjector(new AppInjector());
        ConnectionFactory cf = injector.getInstance(ConnectionFactory.class);
        MessageConsumerFactory mcf = injector.getInstance(MessageConsumerFactory.class);
        MessageConsumerService mcs = injector.getInstance(MessageConsumerService.class);
        ObservableBrokerProxy obp = injector.getInstance(ObservableBrokerProxy.class);
        ObservableBrokerFactory obf = injector.getInstance(ObservableBrokerFactory.class);
        StockMessageMapper smm = injector.getInstance(StockMessageMapper.class);
        MessageHandlerFactory mhf = injector.getInstance(MessageHandlerFactory.class);
        PositionOrderStrategyResolver posr = injector.getInstance(PositionOrderStrategyResolver.class);
        TransactionIdResolver tir = injector.getInstance(TransactionIdResolver.class);
        TransactionService ts = injector.getInstance(TransactionService.class);
        OrderBookService obs = injector.getInstance(OrderBookService.class);
        MessageHandlerService mhs = injector.getInstance(MessageHandlerService.class);
        TransactionEngine e = injector.getInstance(TransactionEngine.class);
    }
}