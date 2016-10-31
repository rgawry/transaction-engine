package com.gft.digitalbank.exchange.solution.processors;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.models.StockMessage;

public interface MessageProcessor {
    void process(StockMessage message);

    MessageType getProcessorType();
}
