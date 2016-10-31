package com.gft.digitalbank.exchange.solution.strategies;

import com.gft.digitalbank.exchange.model.orders.Side;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;

import java.util.Comparator;

public class BuyWhenSellSideIsEmptyPositionOrderStrategy implements PositionOrderStrategy {
    @Override
    public boolean canProcess(OrderBookModel orderBook, OrderMessage orderMessage) {
        boolean result = false;
        if (orderMessage.getSide().equals(Side.BUY.name()))
            if (orderBook.getSellEntries().isEmpty())
                result = true;

        return result;
    }

    @Override
    public void process(OrderBookModel orderBook, OrderMessage orderMessage) {
        orderBook.getBuyEntries().add(orderMessage);

        Comparator<OrderMessage> comparator = Comparator.comparing(om -> om.getDetails().getPrice());
        comparator = comparator.reversed().thenComparing(Comparator.comparing(OrderMessage::getTimestamp));

        orderBook.getBuyEntries().sort(comparator);
    }
}