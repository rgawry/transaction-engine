package com.gft.digitalbank.exchange.solution.services;

import javax.jms.MessageConsumer;

public interface MessageConsumerService {
    MessageConsumer create(String name);

    void close(String name);
}
