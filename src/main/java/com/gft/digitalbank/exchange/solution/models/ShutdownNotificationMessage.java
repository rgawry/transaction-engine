package com.gft.digitalbank.exchange.solution.models;


import com.google.common.base.Preconditions;

public class ShutdownNotificationMessage {
    private int id;
    private long timestamp;
    private String broker;
    private String messageType;

    public ShutdownNotificationMessage(int id, long timestamp, String broker, String messageType) {
        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(timestamp, "timestamp cannot be null");
        Preconditions.checkNotNull(broker, "broker cannot be null");
        Preconditions.checkNotNull(messageType, "messageType cannot be null");
        this.id = id;
        this.timestamp = timestamp;
        this.broker = broker;
        this.messageType = messageType;
    }

    public static ShutdownNotificationMessage.ShutdownNotificationMessageBuilder builder() {
        return new ShutdownNotificationMessage.ShutdownNotificationMessageBuilder();
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

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "ShutdownNotificationMessage{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", broker='" + broker + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShutdownNotificationMessage that = (ShutdownNotificationMessage) o;

        if (id != that.id) return false;
        if (timestamp != that.timestamp) return false;
        if (broker != null ? !broker.equals(that.broker) : that.broker != null) return false;
        return messageType != null ? messageType.equals(that.messageType) : that.messageType == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (broker != null ? broker.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }

    public static class ShutdownNotificationMessageBuilder {
        private int id;
        private long timestamp;
        private String broker;
        private String messageType;

        public ShutdownNotificationMessage.ShutdownNotificationMessageBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ShutdownNotificationMessage.ShutdownNotificationMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ShutdownNotificationMessage.ShutdownNotificationMessageBuilder broker(String broker) {
            this.broker = broker;
            return this;
        }

        public ShutdownNotificationMessage.ShutdownNotificationMessageBuilder messageType(String messageType) {
            this.messageType = messageType;
            return this;
        }

        public ShutdownNotificationMessage build() {
            return new ShutdownNotificationMessage(id, timestamp, broker, messageType);
        }
    }
}
