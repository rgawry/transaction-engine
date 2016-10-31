package com.gft.digitalbank.exchange.solution.strategies;

import com.gft.digitalbank.exchange.model.orders.Side;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;

import java.util.Comparator;

public class SellWhenBuySideDoesNotMatchPositionOrderStrategy implements PositionOrderStrategy {
    @Override
    public boolean canProcess(OrderBookModel orderBook, OrderMessage orderMessage) {
        boolean result = false;
        if (orderMessage.getSide().equals(Side.SELL.name()))
            if (orderBook.getBuyEntries().stream().noneMatch(om -> om.getDetails().getPrice() >= orderMessage.getDetails().getPrice()))
                result = true;

        return result;
    }

    @Override
    public void process(OrderBookModel orderBook, OrderMessage orderMessage) {
        orderBook.getSellEntries().add(orderMessage);

        Comparator<OrderMessage> comparator = Comparator.comparing(om -> om.getDetails().getPrice());
        comparator = comparator.thenComparing(Comparator.comparing(OrderMessage::getTimestamp));

        orderBook.getSellEntries().sort(comparator);
    }
}