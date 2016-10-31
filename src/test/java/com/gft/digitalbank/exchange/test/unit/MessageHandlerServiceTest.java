package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.solution.listener.CompletedListener;
import com.gft.digitalbank.exchange.solution.services.MessageHandlerServiceImp;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessageHandlerServiceTest {
    @Test
    public void unregister_WhenAllUnregistered_ShouldInvokeListenerOnCompleted() {
        CompletedListener completedListener = mock(CompletedListener.class);
        MessageHandlerServiceImp messageHandlerService = new MessageHandlerServiceImp();
        messageHandlerService.setCompletedListener(completedListener);

        messageHandlerService.register();
        messageHandlerService.unregister();

        verify(completedListener).onCompleted();
    }
}