package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;

import java.util.*;

public class OrderBookServiceImp implements OrderBookService {
    private Set<OrderBookModel> orderBooks = new HashSet<>();
    private static final Object lock = new Object();

    @Override
    public Set<OrderBookModel> getOrderBooks() {
        return orderBooks;
    }

    @Override
    public OrderBookModel getOrderBook(String productName) {
        synchronized (lock) {
            Optional<OrderBookModel> result = orderBooks.stream().filter(orderBook -> orderBook.getProduct().equals(productName)).findFirst();
            if (result.isPresent())
                return result.get();
            return null;
        }
    }

    @Override
    public OrderBookModel createOrderBook(String productName) {
        synchronized (lock) {
            OrderBookModel orderBook = OrderBookModel.builder().product(productName).sellEntries(new ArrayList<>()).buyEntries(new ArrayList<>()).build();
            orderBooks.add(orderBook);
            return orderBook;
        }
    }

    @Override
    public OrderMessage getOrderMessage(int id) {
        synchronized (lock) {
            OrderMessage positionOrderResult;
            for (OrderBookModel orderBook : orderBooks) {
                positionOrderResult = internalGetPositionOrder(id, orderBook.getBuyEntries());
                if (positionOrderResult != null) {
                    return positionOrderResult;
                }
                positionOrderResult = internalGetPositionOrder(id, orderBook.getSellEntries());
                if (positionOrderResult != null) {
                    return positionOrderResult;
                }
            }
            return null;
        }
    }

    private OrderMessage internalGetPositionOrder(int id, List<OrderMessage> positionOrderEntries) {
        synchronized (lock) {
            OrderMessage positionOrderResult = null;
            if (positionOrderEntries.stream().anyMatch(positionOrder -> positionOrder.getId() == id)) {
                Optional<OrderMessage> positionOrderOptional = positionOrderEntries.stream().filter(positionOrder -> positionOrder.getId() == id).findFirst();
                if (positionOrderOptional.isPresent()) {
                    positionOrderResult = positionOrderOptional.get();
                }
            }
            return positionOrderResult;
        }
    }

    @Override
    public void removePositionOrder(int id) {
        synchronized (lock) {
            for (OrderBookModel orderBook : orderBooks) {
                if (!orderBook.getBuyEntries().removeIf(positionOrder -> positionOrder.getId() == id)) {
                    orderBook.getSellEntries().removeIf(positionOrder -> positionOrder.getId() == id);
                }
            }
        }
    }
}
