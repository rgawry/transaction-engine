package com.gft.digitalbank.exchange.solution.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class MessageConsumerFactoryImp implements MessageConsumerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerFactoryImp.class);

    @Override
    public MessageConsumer create(String name, Connection connection) {
        Session session;
        Destination destination;
        MessageConsumer consumer = null;
        try {
            session = getSession(connection);
            destination = session.createQueue(name);
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return consumer;
    }

    private Session getSession(Connection connection) {
        Session session = null;
        try {
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return session;
    }
}
