package com.gft.digitalbank.exchange.solution.proxy;

import com.gft.digitalbank.exchange.solution.broker.ObservableBroker;

import java.util.List;

public interface ObservableBrokerProxy extends ObservableBroker {
    void mergeStreams(List<ObservableBroker> streams);
}
