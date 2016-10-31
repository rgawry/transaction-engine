package com.gft.digitalbank.exchange.solution.strategies;

import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;

public interface PositionOrderStrategy {
    boolean canProcess(OrderBookModel orderBook, OrderMessage orderMessage);

    void process(OrderBookModel orderBook, OrderMessage orderMessage);
}
