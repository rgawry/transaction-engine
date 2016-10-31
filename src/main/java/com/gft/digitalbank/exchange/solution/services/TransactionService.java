package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.model.Transaction;

import java.util.Collection;

public interface TransactionService {
    void add(Transaction transaction);

    Collection<Transaction> getTransactions();
}
