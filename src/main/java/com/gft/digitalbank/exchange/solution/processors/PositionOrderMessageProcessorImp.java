package com.gft.digitalbank.exchange.solution.processors;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.resolvers.PositionOrderStrategyResolver;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.gft.digitalbank.exchange.solution.strategies.PositionOrderStrategy;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class PositionOrderMessageProcessorImp implements MessageProcessor {
    private final OrderBookService orderBookService;
    private final PositionOrderStrategyResolver positionOrderStrategyResolver;
    private final StockMessageMapper messageMapper;

    @Inject
    public PositionOrderMessageProcessorImp(OrderBookService orderBookService, PositionOrderStrategyResolver positionOrderStrategyResolver, StockMessageMapper messageMapper) {
        Preconditions.checkNotNull(orderBookService, "orderBookService cannot be null");
        Preconditions.checkNotNull(positionOrderStrategyResolver, "positionOrderStrategyResolver cannot be null");
        Preconditions.checkNotNull(messageMapper, "messageMapper cannot be null");
        this.orderBookService = orderBookService;
        this.positionOrderStrategyResolver = positionOrderStrategyResolver;
        this.messageMapper = messageMapper;
    }

    @Override
    public void process(StockMessage message) {
        OrderMessage orderMessage = messageMapper.toOrder(message);
        OrderBookModel orderBook = orderBookService.getOrderBook(orderMessage.getProduct());
        PositionOrderStrategy positionOrderStrategy = positionOrderStrategyResolver.resolve(orderBook, orderMessage);
        positionOrderStrategy.process(orderBook, orderMessage);
    }

    @Override
    public MessageType getProcessorType() {
        return MessageType.ORDER;
    }
}
