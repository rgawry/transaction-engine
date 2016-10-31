package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.model.Transaction;
import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolver;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TransactionServiceImp implements TransactionService {
    private final TransactionIdResolver transactionIdResolver;
    private final ConcurrentLinkedDeque<Transaction> transactions = new ConcurrentLinkedDeque<>();
    private static final Object lock = new Object();

    @Inject
    public TransactionServiceImp(TransactionIdResolver transactionIdResolver) {
        Preconditions.checkNotNull(transactionIdResolver, "transactionIdResolver cannot be null");
        this.transactionIdResolver = transactionIdResolver;
    }

    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public void add(Transaction transaction) {
        synchronized (lock) {
            Transaction t = Transaction.builder()
                    .id(transactionIdResolver.getId(transaction.getProduct()))
                    .amount(transaction.getAmount())
                    .price(transaction.getPrice())
                    .product(transaction.getProduct())
                    .brokerBuy(transaction.getBrokerBuy())
                    .brokerSell(transaction.getBrokerSell())
                    .clientBuy(transaction.getClientBuy())
                    .clientSell(transaction.getClientSell())
                    .build();
            transactions.add(t);
        }
    }
}
