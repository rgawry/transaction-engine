package com.gft.digitalbank.exchange.solution.strategies;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.model.orders.Side;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.google.inject.Inject;

import java.util.Comparator;
import java.util.stream.Collectors;

public class SellWhenBuySideMatchPositionOrderStrategy implements PositionOrderStrategy {
    private final TransactionService transactionService;

    @Inject
    public SellWhenBuySideMatchPositionOrderStrategy(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean canProcess(OrderBookModel orderBook, OrderMessage orderMessage) {
        boolean result = false;
        if (orderMessage.getSide().equals(Side.SELL.name()))
            if (orderBook.getBuyEntries().stream().anyMatch(om -> om.getDetails().getPrice() >= orderMessage.getDetails().getPrice()))
                result = true;

        return result;
    }

    @Override
    public void process(OrderBookModel orderBook, OrderMessage sellOrderMessage) {
        int amountToSell = sellOrderMessage.getDetails().getAmount();
        int transactionAmount;

        for (OrderMessage orderMessage : orderBook.getBuyEntries()) {
            if (amountToSell > 0 && orderMessage.getDetails().getPrice() >= sellOrderMessage.getDetails().getPrice()) {
                if (amountToSell - orderMessage.getDetails().getAmount() > 0) {
                    transactionAmount = orderMessage.getDetails().getAmount();
                    amountToSell -= orderMessage.getDetails().getAmount();
                    orderMessage.getDetails().setAmount(0);
                } else {
                    transactionAmount = amountToSell;
                    orderMessage.getDetails().setAmount(orderMessage.getDetails().getAmount() - amountToSell);
                    amountToSell = 0;
                }

                Transaction transaction = Transaction.builder()
                        .product(orderBook.getProduct())
                        .brokerBuy(orderMessage.getBroker())
                        .brokerSell(sellOrderMessage.getBroker())
                        .clientBuy(orderMessage.getClient())
                        .clientSell(sellOrderMessage.getClient())
                        .amount(transactionAmount)
                        .price(orderMessage.getDetails().getPrice())
                        .id(Integer.MAX_VALUE)
                        .build();
                transactionService.add(transaction);
            }
        }

        orderBook.setBuyEntries(orderBook.getBuyEntries().stream().filter(om -> om.getDetails().getAmount() > 0).collect(Collectors.toList()));

        if (amountToSell > 0) {
            sellOrderMessage.getDetails().setAmount(amountToSell);
            orderBook.getSellEntries().add(sellOrderMessage);

            Comparator<OrderMessage> comparator = Comparator.comparing(om -> om.getDetails().getPrice());
            comparator = comparator.thenComparing(Comparator.comparing(OrderMessage::getTimestamp));

            orderBook.getSellEntries().sort(comparator);
        }
    }
}