package com.gft.digitalbank.exchange.solution.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderBookModel {
    private String product;
    private List<OrderMessage> buyEntries = new ArrayList<>();
    private List<OrderMessage> sellEntries = new ArrayList<>();

    public OrderBookModel() {
    }

    public OrderBookModel(String product, List<OrderMessage> buyEntries, List<OrderMessage> sellEntries) {
        this.product = product;
        this.buyEntries = buyEntries;
        this.sellEntries = sellEntries;
    }

    public static OrderBookModel.OrderBookBuilder builder() {
        return new OrderBookModel.OrderBookBuilder();
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<OrderMessage> getBuyEntries() {
        return buyEntries;
    }

    public void setBuyEntries(List<OrderMessage> buyEntries) {
        this.buyEntries = buyEntries;
    }

    public List<OrderMessage> getSellEntries() {
        return sellEntries;
    }

    public void setSellEntries(List<OrderMessage> sellEntries) {
        this.sellEntries = sellEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBookModel orderBook = (OrderBookModel) o;

        if (product != null ? !product.equals(orderBook.product) : orderBook.product != null)
            return false;
        if (buyEntries != null ? !buyEntries.equals(orderBook.buyEntries) : orderBook.buyEntries != null) return false;
        return sellEntries != null ? sellEntries.equals(orderBook.sellEntries) : orderBook.sellEntries == null;

    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (buyEntries != null ? buyEntries.hashCode() : 0);
        result = 31 * result + (sellEntries != null ? sellEntries.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderBookModel{" +
                "product='" + product + '\'' +
                ", buyEntries=" + buyEntries +
                ", sellEntries=" + sellEntries +
                '}';
    }

    public static class OrderBookBuilder {
        private String product;
        private ArrayList<OrderMessage> buyEntries;
        private ArrayList<OrderMessage> sellEntries;

        public OrderBookModel.OrderBookBuilder product(String product) {
            this.product = product;
            return this;
        }

        public OrderBookModel.OrderBookBuilder buyEntry(OrderMessage buyEntry) {
            if (this.buyEntries == null) {
                this.buyEntries = new ArrayList<>();
            }

            this.buyEntries.add(buyEntry);
            return this;
        }

        public OrderBookModel.OrderBookBuilder buyEntries(Collection<? extends OrderMessage> buyEntries) {
            if (this.buyEntries == null) {
                this.buyEntries = new ArrayList<>();
            }

            this.buyEntries.addAll(buyEntries);
            return this;
        }

        public OrderBookModel.OrderBookBuilder clearBuyEntries() {
            if (this.buyEntries != null) {
                this.buyEntries.clear();
            }

            return this;
        }

        public OrderBookModel.OrderBookBuilder sellEntry(OrderMessage sellEntry) {
            if (this.sellEntries == null) {
                this.sellEntries = new ArrayList<>();
            }

            this.sellEntries.add(sellEntry);
            return this;
        }

        public OrderBookModel.OrderBookBuilder sellEntries(Collection<? extends OrderMessage> sellEntries) {
            if (this.sellEntries == null) {
                this.sellEntries = new ArrayList<>();
            }

            this.sellEntries.addAll(sellEntries);
            return this;
        }

        public OrderBookModel.OrderBookBuilder clearSellEntries() {
            if (this.sellEntries != null) {
                this.sellEntries.clear();
            }

            return this;
        }

        public OrderBookModel build() {
            return new OrderBookModel(this.product, buyEntries, sellEntries);
        }
    }
}
