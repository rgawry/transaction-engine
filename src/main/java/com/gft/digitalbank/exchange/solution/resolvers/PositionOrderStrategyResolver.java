package com.gft.digitalbank.exchange.solution.resolvers;

import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.strategies.PositionOrderStrategy;

public interface PositionOrderStrategyResolver {
    PositionOrderStrategy resolve(OrderBookModel orderBook, OrderMessage orderMessage);
}
