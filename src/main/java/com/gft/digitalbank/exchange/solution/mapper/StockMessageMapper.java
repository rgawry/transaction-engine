package com.gft.digitalbank.exchange.solution.mapper;

import com.gft.digitalbank.exchange.solution.models.*;

import javax.jms.Message;

public interface StockMessageMapper {
    CancelMessage toCancel(StockMessage message);

    ShutdownNotificationMessage toShutdown(StockMessage message);

    OrderMessage toOrder(StockMessage message);

    ModificationMessage toModification(StockMessage message);

    StockMessage toStockMessage(OrderMessage orderMessage);

    StockMessage toStockMessage(Message message);
}
