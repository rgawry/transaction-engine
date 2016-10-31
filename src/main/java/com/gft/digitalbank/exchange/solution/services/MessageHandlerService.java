package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.solution.listener.CompletedListener;

public interface MessageHandlerService {
    void register();

    void unregister();

    void setCompletedListener(CompletedListener completedListener);
}
