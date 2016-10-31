package com.gft.digitalbank.exchange.solution.factories;

import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;

public interface ObservableBrokerFactory {
    ObservableBroker create(String name);
}
