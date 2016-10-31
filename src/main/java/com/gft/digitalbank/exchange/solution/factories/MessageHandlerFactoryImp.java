package com.gft.digitalbank.exchange.solution.factories;

import com.gft.digitalbank.exchange.solution.handler.MessageHandler;
import com.gft.digitalbank.exchange.solution.handler.MessageHandlerImp;
import com.gft.digitalbank.exchange.solution.processors.MessageProcessor;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageHandlerFactoryImp implements MessageHandlerFactory {
    private final MessageHandlerService messageHandlerService;
    private final Set<MessageProcessor> messageProcessors;

    private final ConcurrentLinkedDeque<MessageHandler> messageHandlers = new ConcurrentLinkedDeque<>();

    @Inject
    public MessageHandlerFactoryImp(Set<MessageProcessor> messageProcessors, MessageHandlerService messageHandlerService) {
        Preconditions.checkNotNull(messageProcessors, "messageProcessors cannot be null");
        Preconditions.checkNotNull(messageHandlerService, "messageHandlerService cannot be null");
        this.messageProcessors = messageProcessors;
        this.messageHandlerService = messageHandlerService;
    }

    @Override
    public MessageHandler create(String productName) {
        MessageHandlerImp messageHandler = new MessageHandlerImp(productName, messageProcessors, messageHandlerService);
        messageHandler.initialize();
        messageHandlers.add(messageHandler);
        return messageHandler;
    }
}
