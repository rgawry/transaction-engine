package com.gft.digitalbank.exchange.solution.resolvers;

import java.util.concurrent.ConcurrentHashMap;

public class TransactionIdResolverImp implements TransactionIdResolver {
    private ConcurrentHashMap<String, Integer> idsMap = new ConcurrentHashMap<>(8, 0.9f, 1);

    @Override
    public Integer getId(String productName) {
        Integer result = 1;
        if (idsMap.containsKey(productName)) {
            result = idsMap.get(productName);
        }
        idsMap.put(productName, result + 1);
        return result;
    }
}
