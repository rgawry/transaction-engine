package com.gft.digitalbank.exchange.solution.processors;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.ModificationMessage;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.services.OrderBookService;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class ModificationMessageProcessorImp implements MessageProcessor {
    private final OrderBookService orderBookService;
    private final MessageProcessor orderMessageProcessor;
    private final StockMessageMapper messageMapper;

    @Inject
    public ModificationMessageProcessorImp(OrderBookService orderBookService, MessageProcessor orderMessageProcessor, StockMessageMapper messageMapper) {
        Preconditions.checkNotNull(orderBookService, "orderBookService cannot be null");
        Preconditions.checkNotNull(orderMessageProcessor, "orderMessageProcessor cannot be null");
        Preconditions.checkNotNull(messageMapper, "messageMapper cannot be null");
        this.orderBookService = orderBookService;
        this.orderMessageProcessor = orderMessageProcessor;
        this.messageMapper = messageMapper;
    }

    @Override
    public void process(StockMessage message) {
        ModificationMessage modificationMessage = messageMapper.toModification(message);

        OrderMessage newOrderMessage = orderBookService.getOrderMessage(modificationMessage.getModifiedOrderId());
        newOrderMessage.getDetails().setAmount(modificationMessage.getDetails().getAmount());
        newOrderMessage.getDetails().setPrice(modificationMessage.getDetails().getPrice());
        newOrderMessage.setTimestamp(modificationMessage.getTimestamp());

        orderBookService.removePositionOrder(modificationMessage.getModifiedOrderId());
        orderMessageProcessor.process(messageMapper.toStockMessage(newOrderMessage));
    }

    @Override
    public MessageType getProcessorType() {
        return MessageType.MODIFICATION;
    }
}
