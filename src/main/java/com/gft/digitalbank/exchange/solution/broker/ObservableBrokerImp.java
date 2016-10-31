package com.gft.digitalbank.exchange.solution.broker;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapper;
import com.gft.digitalbank.exchange.solution.models.StockMessage;
import com.gft.digitalbank.exchange.solution.services.MessageConsumerService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class ObservableBrokerImp implements ObservableBroker, Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableBrokerImp.class);

    private final StockMessageMapper stockMessageMapper;
    private final MessageConsumerService messageConsumerService;
    private final String name;
    private MessageConsumer messageConsumer;

    public ObservableBrokerImp(StockMessageMapper stockMessageMapper, MessageConsumerService messageConsumerService, String name) {
        Preconditions.checkNotNull(stockMessageMapper, "stockMessageMapper cannot be null");
        Preconditions.checkNotNull(messageConsumerService, "messageConsumerService cannot be null");
        Preconditions.checkNotNull(name, "name cannot be null");
        this.stockMessageMapper = stockMessageMapper;
        this.messageConsumerService = messageConsumerService;
        this.name = name;
    }

    @Override
    public void initialize() {
        messageConsumer = messageConsumerService.create(name);
    }

    @Override
    public Observable<StockMessage> getMessageStream() {
        return Observable.create(observer -> {
            try {
                MessageListener listener = message -> {
                    try {
                        if (message.getStringProperty("messageType").equals(MessageType.SHUTDOWN_NOTIFICATION.name())) {
                            observer.onCompleted();
                            messageConsumerService.close(name);
                        } else {
                            observer.onNext(stockMessageMapper.toStockMessage(message));
                        }
                    } catch (JMSException e) {
                        observer.onError(e);
                        LOG.error(e.getMessage());
                        e.printStackTrace();
                    }
                };
                messageConsumer.setMessageListener(listener);
            } catch (JMSException e) {
                observer.onError(e);
                e.printStackTrace();
            }
        });
    }
}
