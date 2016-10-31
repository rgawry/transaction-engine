package com.gft.digitalbank.exchange.solution.mapper;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.Json;
import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.gft.digitalbank.exchange.solution.models.*;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class StockMessageMapperImp implements StockMessageMapper, Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(StockMessageMapperImp.class);

    private Gson gson;

    @Override
    public void initialize() {
        gson = new GsonBuilder().create();
    }

    @Override
    public CancelMessage toCancel(StockMessage message) {
        checkMessageType(message, MessageType.CANCEL);
        return gson.fromJson(message.getBody(), CancelMessage.class);
    }

    @Override
    public ShutdownNotificationMessage toShutdown(StockMessage message) {
        checkMessageType(message, MessageType.SHUTDOWN_NOTIFICATION);
        return gson.fromJson(message.getBody(), ShutdownNotificationMessage.class);
    }

    @Override
    public OrderMessage toOrder(StockMessage message) {
        checkMessageType(message, MessageType.ORDER);
        return gson.fromJson(message.getBody(), OrderMessage.class);
    }

    @Override
    public ModificationMessage toModification(StockMessage message) {
        checkMessageType(message, MessageType.MODIFICATION);
        return gson.fromJson(message.getBody(), ModificationMessage.class);
    }

    @Override
    public StockMessage toStockMessage(OrderMessage orderMessage) {
        return new StockMessage(orderMessage.getMessageType(), gson.toJson(orderMessage));
    }

    @Override
    public StockMessage toStockMessage(Message message) {
        StockMessage stockMessage = null;
        try {
            String jsonBody = ((TextMessage) message).getText();
            stockMessage = StockMessage.builder().type(message.getStringProperty("messageType")).body(jsonBody).build();
            stockMessage.setTimestamp(Long.parseLong(Json.getValue("timestamp", jsonBody)));
        } catch (JMSException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return stockMessage;
    }

    private static void checkMessageType(StockMessage actual, MessageType expected) {
        String expectedAsString = expected.name();
        Preconditions.checkArgument(actual.getType().equals(expectedAsString), String.format("message type must be %s", expectedAsString));
    }
}