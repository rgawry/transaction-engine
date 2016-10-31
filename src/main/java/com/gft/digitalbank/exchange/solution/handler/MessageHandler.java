package com.gft.digitalbank.exchange.solution.handler;

import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;

public interface MessageHandler {
    void subscribe(ObservableBrokerProxy stream);
}
