package com.gft.digitalbank.exchange.solution.strategies;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.model.orders.Side;
import com.gft.digitalbank.exchange.solution.models.OrderBookModel;
import com.gft.digitalbank.exchange.solution.models.OrderMessage;
import com.gft.digitalbank.exchange.solution.services.TransactionService;
import com.google.inject.Inject;

import java.util.Comparator;
import java.util.stream.Collectors;

public class BuyWhenSellSideMatchPositionOrderStrategy implements PositionOrderStrategy {
    private final TransactionService transactionService;

    @Inject
    public BuyWhenSellSideMatchPositionOrderStrategy(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean canProcess(OrderBookModel orderBook, OrderMessage orderMessage) {
        boolean result = false;
        if (orderMessage.getSide().equals(Side.BUY.name()))
            if (orderBook.getSellEntries().stream().anyMatch(om -> om.getDetails().getPrice() <= orderMessage.getDetails().getPrice()))
                result = true;

        return result;
    }

    @Override
    public void process(OrderBookModel orderBook, OrderMessage buyOrderMessage) {
        int amountToBuy = buyOrderMessage.getDetails().getAmount();
        int transactionAmount;

        for (OrderMessage om : orderBook.getSellEntries()) {
            if (amountToBuy > 0 && om.getDetails().getPrice() <= buyOrderMessage.getDetails().getPrice()) {
                if (amountToBuy - om.getDetails().getAmount() > 0) {
                    transactionAmount = om.getDetails().getAmount();
                    amountToBuy -= om.getDetails().getAmount();
                    om.getDetails().setAmount(0);
                } else {
                    transactionAmount = amountToBuy;
                    om.getDetails().setAmount(om.getDetails().getAmount() - amountToBuy);
                    amountToBuy = 0;
                }

                Transaction transaction = Transaction.builder()
                        .product(orderBook.getProduct())
                        .brokerBuy(buyOrderMessage.getBroker())
                        .brokerSell(om.getBroker())
                        .clientBuy(buyOrderMessage.getClient())
                        .clientSell(om.getClient())
                        .amount(transactionAmount)
                        .price(om.getDetails().getPrice())
                        .id(Integer.MAX_VALUE)
                        .build();

                transactionService.add(transaction);
            }
        }

        orderBook.setSellEntries(orderBook.getSellEntries().stream().filter(om -> om.getDetails().getAmount() > 0).collect(Collectors.toList()));

        if (amountToBuy > 0) {
            buyOrderMessage.getDetails().setAmount(amountToBuy);
            orderBook.getBuyEntries().add(buyOrderMessage);

            Comparator<OrderMessage> comparator = Comparator.comparing(om -> om.getDetails().getPrice());
            comparator = comparator.reversed().thenComparing(Comparator.comparing(OrderMessage::getTimestamp));

            orderBook.getBuyEntries().sort(comparator);
        }
    }
}