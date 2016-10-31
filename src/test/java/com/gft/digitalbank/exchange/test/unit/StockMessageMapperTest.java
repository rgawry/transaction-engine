package com.gft.digitalbank.exchange.test.unit;

import com.gft.digitalbank.exchange.model.orders.MessageType;
import com.gft.digitalbank.exchange.model.orders.Side;
import com.gft.digitalbank.exchange.solution.mapper.StockMessageMapperImp;
import com.gft.digitalbank.exchange.solution.models.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StockMessageMapperTest {
    private StockMessageMapperImp mapper = new StockMessageMapperImp();
    private StockMessage stockMessageWithWrongMessageType = StockMessage.builder().type("message-type").body("test-body").build();

    @Before
    public void setUp() throws Exception {
        mapper.initialize();
    }

    @Test
    public void toCancel_WhenStockMessageIsCancel_ShouldReturnCancelMessage() {
        int id = 1;
        long timestamp = 11;
        String broker = "test-broker";
        int cancelledOrderId = 1;
        String messageType = MessageType.CANCEL.name();
        CancelMessage expected = CancelMessage.builder().id(id).timestamp(timestamp).broker(broker).cancelledOrderId(cancelledOrderId).messageType(messageType).build();
        String bodyJson = "{id: " + id + ", timestamp: " + timestamp + ", broker: " + broker + ", cancelledOrderId: " + cancelledOrderId + ", messageType: " + messageType + "}";
        StockMessage stockMessage = new StockMessage(messageType, bodyJson);

        CancelMessage actual = mapper.toCancel(stockMessage);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toCancel_WhenStockMessageIsNotCancel_ShouldThrowException() {
        mapper.toCancel(stockMessageWithWrongMessageType);
    }

    @Test
    public void toShutdown_WhenStockMessageIsShutdownNotification_ShouldReturnShutdownNotificationMessage() {
        int id = 1;
        long timestamp = 11;
        String broker = "test-broker";
        String messageType = MessageType.SHUTDOWN_NOTIFICATION.name();
        ShutdownNotificationMessage expected = ShutdownNotificationMessage.builder().timestamp(timestamp).id(id).broker(broker).messageType(messageType).build();
        String bodyJson = "{id: " + id + ", timestamp: " + timestamp + ", broker: " + broker + ", messageType: " + messageType + "}";
        StockMessage stockMessage = new StockMessage(messageType, bodyJson);

        ShutdownNotificationMessage actual = mapper.toShutdown(stockMessage);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toShutdown_WhenStockMessageIsNotShutdownNotification_ShouldThrowException() {
        mapper.toShutdown(stockMessageWithWrongMessageType);
    }

    @Test
    public void toOrder_WhenStockMessageIsOrder_ShouldReturnOrderMessage() {
        String side = Side.BUY.name();
        int id = 1;
        long timestamp = 11;
        String broker = "test-broker";
        String client = "test-client";
        String product = "test-product";
        String messageType = MessageType.ORDER.name();
        int amount = 11;
        int price = 11;
        Details orderDetails = Details.builder().amount(amount).price(price).build();
        OrderMessage expected = OrderMessage.builder().side(side).id(id).timestamp(timestamp).broker(broker).client(client).product(product).details(orderDetails).messageType(messageType).build();
        String detailsJson = "{amount: " + amount + ", price: " + price + "}";
        String bodyJson = "{side: " + side + " , id: " + id + ", timestamp: " + timestamp + ", broker: " + broker + ", client: " + client + ", product: " + product + ", messageType: " + messageType + ", details: " + detailsJson + "}";
        StockMessage stockMessage = new StockMessage(messageType, bodyJson);

        OrderMessage actual = mapper.toOrder(stockMessage);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toOrder_WhenStockMessageIsNotOrder_ShouldThrowException() {
        mapper.toOrder(stockMessageWithWrongMessageType);
    }

    @Test
    public void toModification_WhenStockMessageIsModification_ShouldReturnModificationMessage() {
        int id = 1;
        long timestamp = 11;
        String broker = "test-broker";
        int modifiedOrderId = 1;
        String messageType = MessageType.MODIFICATION.name();
        int amount = 11;
        int price = 11;
        Details details = Details.builder().amount(amount).price(price).build();
        ModificationMessage expected = ModificationMessage.builder().id(id).timestamp(timestamp).broker(broker).modifiedOrderId(modifiedOrderId).details(details).messageType(messageType).build();
        String detailsJson = "{amount: " + amount + ", price: " + price + "}";
        String bodyJson = "{id: " + id + ", timestamp: " + timestamp + ", broker: " + broker + ", modifiedOrderId: " + modifiedOrderId + ", messageType: " + messageType + ", details: " + detailsJson + "}";
        StockMessage stockMessage = new StockMessage(messageType, bodyJson);

        ModificationMessage actual = mapper.toModification(stockMessage);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toModification_WhenStockMessageIsNotModification_ShouldThrowException() {
        mapper.toModification(stockMessageWithWrongMessageType);
    }

    @Test
    public void toStockMessage_WhenGivenOrderMessage_ShouldConvertToStockMessage() {
        String side = Side.BUY.name();
        int id = 1;
        long timestamp = 11;
        String broker = "test-broker";
        String client = "test-client";
        String product = "test-product";
        String messageType = MessageType.ORDER.name();
        int amount = 11;
        int price = 11;
        Details orderDetails = Details.builder().amount(amount).price(price).build();
        OrderMessage orderMessage = OrderMessage.builder().side(side).id(id).timestamp(timestamp).broker(broker).client(client).product(product).details(orderDetails).messageType(messageType).build();
        String detailsJson = "{\"amount\":" + amount + ",\"price\":" + price + "}";
        String bodyJson = "{\"side\":\"" + side + "\",\"id\":" + id + ",\"timestamp\":" + timestamp + ",\"broker\":\"" + broker + "\",\"client\":\"" + client + "\",\"product\":\"" + product + "\",\"details\":" + detailsJson + ",\"messageType\":\"" + messageType + "\"}";
        StockMessage expected = new StockMessage(messageType, bodyJson);

        StockMessage actual = mapper.toStockMessage(orderMessage);

        assertEquals(expected, actual);
    }
}
