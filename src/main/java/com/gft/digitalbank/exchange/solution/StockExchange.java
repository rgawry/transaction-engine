package com.gft.digitalbank.exchange.solution;

import com.gft.digitalbank.exchange.Exchange;
import com.gft.digitalbank.exchange.listener.ProcessingListener;
import com.gft.digitalbank.exchange.solution.injector.AppInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.List;

/**
 * Your solution must implement the {@link Exchange} interface.
 */
public class StockExchange implements Exchange {
    private final TransactionEngine engine;

    public StockExchange() {
        Injector injector = Guice.createInjector(new AppInjector());
        engine = injector.getInstance(TransactionEngine.class);
    }

    @Override
    public void register(ProcessingListener processingListener) {
        engine.setProcessingListener(processingListener);
    }

    @Override
    public void setDestinations(List<String> list) {
        engine.setDestinations(list);
    }

    @Override
    public void start() {
        engine.start();
    }
}
