package com.gft.digitalbank.exchange.solution.injector;

import com.gft.digitalbank.exchange.solution.TransactionEngine;
import com.gft.digitalbank.exchange.solution.factories.*;
import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapperImp;
import com.gft.digitalbank.exchange.solution.processors.CancelMessageProcessorImp;
import com.gft.digitalbank.exchange.solution.processors.MessageProcessor;
import com.gft.digitalbank.exchange.solution.processors.ModificationMessageProcessorImp;
import com.gft.digitalbank.exchange.solution.processors.PositionOrderMessageProcessorImp;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxyImp;
import com.gft.digitalbank.exchange.solution.resolvers.PositionOrderStrategyResolver;
import com.gft.digitalbank.exchange.solution.resolvers.PositionOrderStrategyResolverImp;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolver;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolverImp;
import com.gft.digitalbank.exchange.solution.services.*;
import com.gft.digitalbank.exchange.solution.strategies.*;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import javax.jms.ConnectionFactory;

public class AppInjector extends AbstractModule {
    @Override
    protected void configure() {
        bindListener(new GuiceSubclassMatcher<>(Initializable.class), new InitializableTypeListener());
        bind(ConnectionFactory.class).to(ConnectionFactoryImp.class);
        bind(MessageConsumerFactory.class).to(MessageConsumerFactoryImp.class);
        bind(MessageConsumerService.class).to(MessageConsumerServiceImp.class).in(Singleton.class);
        bind(ObservableBrokerProxy.class).to(ObservableBrokerProxyImp.class);
        bind(ObservableBrokerFactory.class).to(ObservableBrokerFactoryImp.class);
        bind(StockMessageMapper.class).to(StockMessageMapperImp.class);
        bind(MessageProcessor.class).to(PositionOrderMessageProcessorImp.class);
        bindProcessors();
        bind(MessageHandlerFactory.class).to(MessageHandlerFactoryImp.class);
        bindStrategies();
        bind(PositionOrderStrategyResolver.class).to(PositionOrderStrategyResolverImp.class);
        bind(TransactionIdResolver.class).to(TransactionIdResolverImp.class).in(Singleton.class);
        bind(TransactionService.class).to(TransactionServiceImp.class).in(Singleton.class);
        bind(OrderBookService.class).to(OrderBookServiceImp.class).in(Singleton.class);
        bind(MessageHandlerService.class).to(MessageHandlerServiceImp.class).in(Singleton.class);
        bind(TransactionEngine.class);
    }

    private void bindProcessors() {
        Multibinder<MessageProcessor> processorsBinder = Multibinder.newSetBinder(binder(), MessageProcessor.class);
        processorsBinder.addBinding().to(PositionOrderMessageProcessorImp.class);
        processorsBinder.addBinding().to(CancelMessageProcessorImp.class);
        processorsBinder.addBinding().to(ModificationMessageProcessorImp.class);
    }

    private void bindStrategies() {
        Multibinder<PositionOrderStrategy> strategiesBinder = Multibinder.newSetBinder(binder(), PositionOrderStrategy.class);
        strategiesBinder.addBinding().to(BuyWhenSellSideIsEmptyPositionOrderStrategy.class);
        strategiesBinder.addBinding().to(BuyWhenSellSideDoesNotMatchPositionOrderStrategy.class);
        strategiesBinder.addBinding().to(BuyWhenSellSideMatchPositionOrderStrategy.class);
        strategiesBinder.addBinding().to(SellWhenBuySideIsEmptyPositionOrderStrategy.class);
        strategiesBinder.addBinding().to(SellWhenBuySideDoesNotMatchPositionOrderStrategy.class);
        strategiesBinder.addBinding().to(SellWhenBuySideMatchPositionOrderStrategy.class);
    }
}
