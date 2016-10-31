package com.gft.digitalbank.exchange.solution.services;

import com.gft.digitalbank.exchange.solution.listener.CompletedListener;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageHandlerServiceImp implements MessageHandlerService {
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private CompletedListener messageHandlersCompletedListener;

    @Override
    public void register() {
        atomicInteger.getAndIncrement();
    }

    @Override
    public void unregister() {
        if (atomicInteger.decrementAndGet() == 0) {
            messageHandlersCompletedListener.onCompleted();
        }
    }

    @Override
    public void setCompletedListener(CompletedListener completedListener) {
        this.messageHandlersCompletedListener = completedListener;
    }
}
