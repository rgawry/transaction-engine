package com.gft.digitalbank.exchange.solution.models;

import com.google.common.base.Preconditions;

public class ModificationMessage {
    private int id;
    private long timestamp;
    private String broker;
    private int modifiedOrderId;
    private Details details;
    private String messageType;

    public ModificationMessage(int id, long timestamp, String broker, int modifiedOrderId, Details details, String messageType) {
        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(timestamp, "timestamp cannot be null");
        Preconditions.checkNotNull(broker, "broker cannot be null");
        Preconditions.checkNotNull(modifiedOrderId, "modifiedOrderId cannot be null");
        Preconditions.checkNotNull(details, "details cannot be null");
        Preconditions.checkNotNull(messageType, "messageType cannot be null");
        this.id = id;
        this.timestamp = timestamp;
        this.broker = broker;
        this.modifiedOrderId = modifiedOrderId;
        this.details = details;
        this.messageType = messageType;
    }

    public static ModificationMessage.ModificationMessageBuilder builder() {
        return new ModificationMessage.ModificationMessageBuilder();
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

    public void setModifiedOrderId(int modifiedOrderId) {
        this.modifiedOrderId = modifiedOrderId;
    }

    public void setDetails(Details details) {
        this.details = details;
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

    public int getModifiedOrderId() {
        return modifiedOrderId;
    }

    public Details getDetails() {
        return details;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "ModificationMessage{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", broker='" + broker + '\'' +
                ", modifiedOrderId=" + modifiedOrderId +
                ", details=" + details +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModificationMessage that = (ModificationMessage) o;

        if (id != that.id) return false;
        if (timestamp != that.timestamp) return false;
        if (modifiedOrderId != that.modifiedOrderId) return false;
        if (broker != null ? !broker.equals(that.broker) : that.broker != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        return messageType != null ? messageType.equals(that.messageType) : that.messageType == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (broker != null ? broker.hashCode() : 0);
        result = 31 * result + modifiedOrderId;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }

    public static class ModificationMessageBuilder {
        private int id;
        private long timestamp;
        private String broker;
        private int modifiedOrderId;
        private Details details;
        private String messageType;

        public ModificationMessageBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ModificationMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ModificationMessageBuilder broker(String broker) {
            this.broker = broker;
            return this;
        }

        public ModificationMessageBuilder modifiedOrderId(int modifiedOrderId) {
            this.modifiedOrderId = modifiedOrderId;
            return this;
        }

        public ModificationMessageBuilder details(Details details) {
            this.details = details;
            return this;
        }

        public ModificationMessageBuilder messageType(String messageType) {
            this.messageType = messageType;
            return this;
        }

        public ModificationMessage build() {
            return new ModificationMessage(id, timestamp, broker, modifiedOrderId, details, messageType);
        }
    }
}

