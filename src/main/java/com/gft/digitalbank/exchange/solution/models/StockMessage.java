package com.gft.digitalbank.exchange.solution.models;

import com.google.common.base.Preconditions;

public class StockMessage implements Comparable {
    private String type;
    private String body;
    private String product;
    private long timestamp;

    public StockMessage(String type, String body) {
        Preconditions.checkNotNull(type, "type cannot be null");
        Preconditions.checkNotNull(body, "body cannot be null");
        this.type = type;
        this.body = body;
    }

    public static StockMessage.StockMessageBuilder builder() {
        return new StockMessage.StockMessageBuilder();
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "StockMessage{" +
                "type='" + type + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockMessage that = (StockMessage) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return body != null ? body.equals(that.body) : that.body == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        StockMessage other = (StockMessage) o;
        if (timestamp > other.timestamp) return 1;
        if (timestamp < other.timestamp) return -1;
        return 0;
    }

    public static class StockMessageBuilder {
        private String type;
        private String body;

        public StockMessage.StockMessageBuilder type(String type) {
            this.type = type;
            return this;
        }

        public StockMessage.StockMessageBuilder body(String body) {
            this.body = body;
            return this;
        }

        public StockMessage build() {
            return new StockMessage(type, body);
        }
    }
}

