package com.gft.digitalbank.exchange.solution.broker;

import com.gft.digitalbank.exchange.solution.models.StockMessage;
import rx.Observable;

public interface ObservableBroker {
    Observable<StockMessage> getMessageStream();
}
