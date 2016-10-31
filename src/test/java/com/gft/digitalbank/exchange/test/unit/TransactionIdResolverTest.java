package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.solution.resolvers.TransactionIdResolverImp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionIdResolverTest {
    private final TransactionIdResolverImp transactionIdResolver = new TransactionIdResolverImp();

    @Test
    public void getId_WhenRequestingFirstIdForProducts_ShouldReturnOne() {
        String productName1 = "product-name-1";
        String productName2 = "product-name-2";
        int expectedId = 1;

        int actual1 = transactionIdResolver.getId(productName1);
        int actual2 = transactionIdResolver.getId(productName2);

        assertEquals(expectedId, actual1);
        assertEquals(expectedId, actual2);
    }

    @Test
    public void getId_WhenRequestingSecondIdForSameProduct_ShouldReturnNextId() {
        String productName1 = "product-name-1";
        int expectedId1 = 1;
        int expectedId2 = 2;

        int actual1 = transactionIdResolver.getId(productName1);
        int actual2 = transactionIdResolver.getId(productName1);

        assertEquals(expectedId1, actual1);
        assertEquals(expectedId2, actual2);
    }
}