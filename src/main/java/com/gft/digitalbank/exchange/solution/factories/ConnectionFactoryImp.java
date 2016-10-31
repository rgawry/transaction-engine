package com.gft.digitalbank.exchange.solution.factories;

import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectionFactoryImp implements ConnectionFactory, Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactoryImp.class);

    private ConnectionFactory connectionFactory;

    @Override
    public Connection createConnection() throws JMSException {
        return connectionFactory.createConnection();
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
        return null;
    }

    @Override
    public void initialize() {
        Context context;
        try {
            context = new InitialContext();
            connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
