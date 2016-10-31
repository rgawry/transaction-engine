package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;

import java.util.Set;

public interface OrderBookService {
    Set<OrderBookModel> getOrderBooks();

    OrderBookModel getOrderBook(String productName);

    OrderBookModel createOrderBook(String productName);

    OrderMessage getOrderMessage(int id);

    void removePositionOrder(int id);
}
