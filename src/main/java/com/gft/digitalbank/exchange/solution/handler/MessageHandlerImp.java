package com.gft.digitalbank.exchange.solution.handler;

import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.processors.MessageProcessor;
import com.gft.digitalbank.exchange.solution.proxy.ObservableBrokerProxy;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

import java.util.Set;

public class MessageHandlerImp implements MessageHandler, Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(MessageHandlerImp.class);

    private final String productName;
    private final MessageHandlerService messageHandlerService;
    private final Set<MessageProcessor> messageProcessors;
    private Subscriber<StockMessage> subscriber;

    public MessageHandlerImp(String productName, Set<MessageProcessor> messageProcessors, MessageHandlerService messageHandlerService) {
        Preconditions.checkNotNull(productName, "productName cannot be null");
        Preconditions.checkNotNull(messageProcessors, "messageProcessors cannot be null");
        Preconditions.checkNotNull(messageHandlerService, "messageHandlerService cannot be null");
        this.productName = productName;
        this.messageProcessors = messageProcessors;
        this.messageHandlerService = messageHandlerService;
    }

    public void setObserver(Subscriber<StockMessage> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void initialize() {
        subscriber = new Subscriber<StockMessage>() {
            @Override
            public void onStart() {
                request(Integer.MAX_VALUE);
            }

            @Override
            public void onCompleted() {
                messageHandlerService.unregister();
            }

            @Override
            public void onError(Throwable e) {
                LOG.error(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(StockMessage message) {
                selectProcessor(message);
                request(Integer.MAX_VALUE);
            }
        };
    }

    @Override
    public void subscribe(ObservableBrokerProxy stream) {
        messageHandlerService.register();
        stream.getMessageStream()
                .filter(message -> message.getProduct().equals(productName))
                .subscribe(subscriber);
    }

    private void selectProcessor(StockMessage stockMessage) {
        messageProcessors
                .stream()
                .filter(messageProcessor -> stockMessage.getType().equals(messageProcessor.getProcessorType().name()))
                .forEach(messageProcessor -> messageProcessor.process(stockMessage));
    }
}
