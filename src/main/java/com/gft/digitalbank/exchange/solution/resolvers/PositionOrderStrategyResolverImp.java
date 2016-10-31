package com.gft.digitalbank.exchange.solution.resolvers;

import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.strategies.PositionOrderStrategy;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.util.Optional;
import java.util.Set;

public class PositionOrderStrategyResolverImp implements PositionOrderStrategyResolver {
    private final Set<PositionOrderStrategy> positionOrderStrategies;

    @Inject
    public PositionOrderStrategyResolverImp(Set<PositionOrderStrategy> positionOrderStrategies) {
        Preconditions.checkNotNull(positionOrderStrategies, "positionOrderStrategies cannot be null");
        this.positionOrderStrategies = positionOrderStrategies;
    }

    @Override
    public PositionOrderStrategy resolve(OrderBookModel orderBook, OrderMessage orderMessage) {
        PositionOrderStrategy strategy = null;
        Optional<PositionOrderStrategy> positionOrderStrategyOptional = positionOrderStrategies.stream().filter(positionOrderStrategy -> positionOrderStrategy.canProcess(orderBook, orderMessage)).findFirst();
        if (positionOrderStrategyOptional.isPresent()) {
            strategy = positionOrderStrategyOptional.get();
        }
        return strategy;
    }
}
