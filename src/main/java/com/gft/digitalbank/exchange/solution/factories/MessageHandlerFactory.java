package com.gft.digitalbank.exchange.solution.factories;

import com.gft.digitalbank.exchange.solution.handler.MessageHandler;

public interface MessageHandlerFactory {
    MessageHandler create(String productName);
}
