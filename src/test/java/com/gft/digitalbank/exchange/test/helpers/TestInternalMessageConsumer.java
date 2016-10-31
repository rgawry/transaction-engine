package com.gft.digitalbank.exchange.test.helpers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class TestInternalMessageConsumer implements MessageConsumer {
    private MessageListener messageListener;

    @Override
    public String getMessageSelector() throws JMSException {
        return null;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return messageListener;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException {
        this.messageListener = messageListener;
    }

    @Override
    public Message receive() throws JMSException {
        return null;
    }

    @Override
    public Message receive(long l) throws JMSException {
        return null;
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        return null;
    }

    @Override
    public void close() throws JMSException {

    }
}
