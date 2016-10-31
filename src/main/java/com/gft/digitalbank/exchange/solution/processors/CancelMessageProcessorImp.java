package com.gft.digitalbank.exchange.solution.processors;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.CancelMessage;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class CancelMessageProcessorImp implements MessageProcessor {
    private final OrderBookService orderBookService;
    private final StockMessageMapper messageMapper;

    @Inject
    public CancelMessageProcessorImp(OrderBookService orderBookService, StockMessageMapper messageMapper) {
        Preconditions.checkNotNull(orderBookService, "orderBookService cannot be null");
        Preconditions.checkNotNull(messageMapper, "messageMapper cannot be null");
        this.orderBookService = orderBookService;
        this.messageMapper = messageMapper;
    }

    @Override
    public void process(StockMessage message) {
        CancelMessage cancelMessage = messageMapper.toCancel(message);
        orderBookService.removePositionOrder(cancelMessage.getCancelledOrderId());
    }

    @Override
    public MessageType getProcessorType() {
        return MessageType.CANCEL;
    }
}
