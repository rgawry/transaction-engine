package com.gft.digitalbank.exchange.solution.models;

import com.google.common.base.Preconditions;

public class CancelMessage {
    private int id;
    private long timestamp;
    private String broker;
    private int cancelledOrderId;
    private String messageType;

    public CancelMessage(int id, long timestamp, String broker, int cancelledOrderId, String messageType) {
        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(timestamp, "timestamp cannot be null");
        Preconditions.checkNotNull(broker, "broker cannot be null");
        Preconditions.checkNotNull(cancelledOrderId, "cancelledOrderId cannot be null");
        Preconditions.checkNotNull(messageType, "messageType cannot be null");
        this.id = id;
        this.timestamp = timestamp;
        this.broker = broker;
        this.cancelledOrderId = cancelledOrderId;
        this.messageType = messageType;
    }

    public static CancelMessage.CancelMessageBuilder builder() {
        return new CancelMessage.CancelMessageBuilder();
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

    public void setCancelledOrderId(int cancelledOrderId) {
        this.cancelledOrderId = cancelledOrderId;
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

    public int getCancelledOrderId() {
        return cancelledOrderId;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "CancelMessage{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", broker='" + broker + '\'' +
                ", cancelledOrderId=" + cancelledOrderId +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CancelMessage that = (CancelMessage) o;

        if (id != that.id) return false;
        if (timestamp != that.timestamp) return false;
        if (cancelledOrderId != that.cancelledOrderId) return false;
        if (broker != null ? !broker.equals(that.broker) : that.broker != null) return false;
        return messageType != null ? messageType.equals(that.messageType) : that.messageType == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (broker != null ? broker.hashCode() : 0);
        result = 31 * result + cancelledOrderId;
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }

    public static class CancelMessageBuilder {
        private int id;
        private long timestamp;
        private String broker;
        private int cancelledOrderId;
        private String messageType;

        public CancelMessage.CancelMessageBuilder id(int id) {
            this.id = id;
            return this;
        }

        public CancelMessage.CancelMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CancelMessage.CancelMessageBuilder broker(String broker) {
            this.broker = broker;
            return this;
        }

        public CancelMessage.CancelMessageBuilder cancelledOrderId(int cancelledOrderId) {
            this.cancelledOrderId = cancelledOrderId;
            return this;
        }

        public CancelMessage.CancelMessageBuilder messageType(String messageType) {
            this.messageType = messageType;
            return this;
        }

        public CancelMessage build() {
            return new CancelMessage(id, timestamp, broker, cancelledOrderId, messageType);
        }
    }
}

