package rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Producer {
    RabbitMQConfig config;

    public Producer(RabbitMQConfig config) {
        this.config = config;
    }

    public void send(String msg, String correlationId, Logger logger) throws IOException, TimeoutException {
        Connection connection = config.connect();
        Channel channel = connection.createChannel();
        channel.queueDeclare(config.getQueueName(), false,false,false, null);
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(config.getQueueName())
                .build();
        channel.basicPublish("",config.getQueueName(),false, props, msg.getBytes(StandardCharsets.UTF_8));
        System.out.println(channel.messageCount(config.getQueueName()));
        connection.close();
    }
}
