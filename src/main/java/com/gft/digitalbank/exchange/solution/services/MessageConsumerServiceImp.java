package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.solution.factories.MessageConsumerFactory;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import java.util.concurrent.ConcurrentHashMap;

public class MessageConsumerServiceImp implements MessageConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerServiceImp.class);

    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>(8, 0.9f, 1);
    private final ConnectionFactory connectionFactory;
    private final MessageConsumerFactory messageConsumerFactory;

    @Inject
    public MessageConsumerServiceImp(ConnectionFactory connectionFactory, MessageConsumerFactory messageConsumerFactory) {
        Preconditions.checkNotNull(connectionFactory, "connectionFactory cannot be null");
        Preconditions.checkNotNull(messageConsumerFactory, "messageConsumerFactory cannot be null");
        this.connectionFactory = connectionFactory;
        this.messageConsumerFactory = messageConsumerFactory;
    }

    @Override
    public MessageConsumer create(String name) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connections.put(name, connection);
        } catch (JMSException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return messageConsumerFactory.create(name, connection);
    }

    @Override
    public void close(String name) {
        try {
            Connection c = connections.get(name);
            if (c != null) {
                c.close();
                connections.remove(name);
            }
        } catch (JMSException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
