package com.gft.digitalbank.exchange.solution.models;

import com.google.common.base.Preconditions;

public class OrderMessage {
    private String side;
    private int id;
    private long timestamp;
    private String broker;
    private String client;
    private String product;
    private Details details;
    private String messageType;

    public OrderMessage(String side, int id, long timestamp, String broker, String client, String product, Details details, String messageType) {
        Preconditions.checkNotNull(side, "side cannot be null");
        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(timestamp, "timestamp cannot be null");
        Preconditions.checkNotNull(broker, "broker cannot be null");
        Preconditions.checkNotNull(client, "client cannot be null");
        Preconditions.checkNotNull(product, "product cannot be null");
        Preconditions.checkNotNull(details, "details cannot be null");
        Preconditions.checkNotNull(messageType, "messageType cannot be null");
        this.side = side;
        this.id = id;
        this.timestamp = timestamp;
        this.broker = broker;
        this.client = client;
        this.product = product;
        this.details = details;
        this.messageType = messageType;
    }

    public static OrderMessage.OrderMessageBuilder builder() {
        return new OrderMessage.OrderMessageBuilder();
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSide() {
        return side;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getBroker() {
        return broker;
    }

    public String getClient() {
        return client;
    }

    public String getProduct() {
        return product;
    }

    public Details getDetails() {
        return details;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "side='" + side + '\'' +
                ", id=" + id +
                ", timestamp=" + timestamp +
                ", broker='" + broker + '\'' +
                ", client='" + client + '\'' +
                ", product='" + product + '\'' +
                ", details=" + details +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderMessage that = (OrderMessage) o;

        if (id != that.id) return false;
        if (timestamp != that.timestamp) return false;
        if (side != null ? !side.equals(that.side) : that.side != null) return false;
        if (broker != null ? !broker.equals(that.broker) : that.broker != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        return messageType != null ? messageType.equals(that.messageType) : that.messageType == null;
    }

    @Override
    public int hashCode() {
        int result = side != null ? side.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (broker != null ? broker.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }

    public static class OrderMessageBuilder {
        private String side;
        private int id;
        private long timestamp;
        private String broker;
        private String client;
        private String product;
        private Details details;
        private String messageType;

        public OrderMessage.OrderMessageBuilder side(String side) {
            this.side = side;
            return this;
        }

        public OrderMessage.OrderMessageBuilder id(int id) {
            this.id = id;
            return this;
        }

        public OrderMessage.OrderMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public OrderMessage.OrderMessageBuilder broker(String broker) {
            this.broker = broker;
            return this;
        }

        public OrderMessage.OrderMessageBuilder client(String client) {
            this.client = client;
            return this;
        }

        public OrderMessage.OrderMessageBuilder product(String product) {
            this.product = product;
            return this;
        }

        public OrderMessage.OrderMessageBuilder details(Details details) {
            this.details = details;
            return this;
        }

        public OrderMessage.OrderMessageBuilder messageType(String messageType) {
            this.messageType = messageType;
            return this;
        }

        public OrderMessage build() {
            return new OrderMessage(side, id, timestamp, broker, client, product, details, messageType);
        }
    }
}
