package com.gft.digitalbank.exchange.solution.models;

import com.google.common.base.Preconditions;

public class Details {
    private int amount;
    private int price;

    public Details(int amount, int price) {
        Preconditions.checkNotNull(amount, "amount cannot be null");
        Preconditions.checkNotNull(price, "price cannot be null");
        this.amount = amount;
        this.price = price;
    }

    public static Details.DetailsBuilder builder() {
        return new Details.DetailsBuilder();
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Details{" +
                "amount=" + amount +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Details details = (Details) o;

        if (amount != details.amount) return false;
        return price == details.price;
    }

    @Override
    public int hashCode() {
        int result = amount;
        result = 31 * result + price;
        return result;
    }

    public static class DetailsBuilder {
        private int amount;
        private int price;

        public Details.DetailsBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Details.DetailsBuilder price(int price) {
            this.price = price;
            return this;
        }

        public Details build() {
            return new Details(amount, price);
        }
    }
}
