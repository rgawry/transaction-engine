package com.gft.digitalbank.exchange.solution.factories;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

public interface MessageConsumerFactory {
    MessageConsumer create(String name, Connection connection);
}
