package rabbitmq;

import api.runners.AccountMain;
import api.shared.RequestHandler;
import com.rabbitmq.client.*;
import threading.Pool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer extends RequestHandler {
    Logger lgr = Logger.getLogger(Consumer.class.getName());
    RabbitMQConfig config;
    Connection conn;
    com.rabbitmq.client.Consumer consumer;
    Channel channel;

//    Session session;

    public Consumer(RabbitMQConfig config, boolean callConsume) {
        this.config = config;
        try {
            conn = config.connect();
            channel = conn.createChannel();
            channel.queueDeclare(config.getQueueName(), false, false, false, null);
            if (callConsume)
                consume();
        } catch (IOException | TimeoutException e) {
            lgr.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    public void consume() {
//        String result;
//        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        try {
            System.out.println("heyyy inside consumeeee");

            channel.basicConsume(config.getQueueName(), true, new DefaultConsumer(channel) {  // eb2a shelha lamma tkhallas
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                    if (properties.getCorrelationId().equals(corrId)) {
//                        response.offer(new String(body, "UTF-8"));
//                    }
//                    try {
                    Pool pool = null;
                    switch (config.getQueueName()){
                        case "account-IN" :
                        case "account-controller-IN": pool = AccountMain.pool; break;
                    }
                    handleRequest(new String(body, StandardCharsets.UTF_8), pool, properties.getCorrelationId());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            });

        } catch (IOException e) {
            lgr.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    public String consumeOne(String corrId) {
//        String result;
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        try {


            channel.basicConsume(config.getQueueName(), false, new DefaultConsumer(channel) {  // eb2a shelha lamma tkhallas
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(corrId)) {
                        channel.basicAck(envelope.getDeliveryTag(), true);
                        response.offer(new String(body, StandardCharsets.UTF_8));
                        System.out.println("HMM WEIRD: " + new String(body, StandardCharsets.UTF_8));
                    }
                    try {
                        channel.close();
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            lgr.log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            return response.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String consumeTest(RabbitMQConfig config, String correlationId) throws IOException, TimeoutException {
        Connection connection = config.connect();

        Channel channel = connection.createChannel();
//        GetResponse response = channel.basicGet(config.getQueueName(),true);
//        channel.close();
//        connection.close();
//        return new String(response.getBody(), StandardCharsets.UTF_8);
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        try {


            channel.basicConsume(config.getQueueName(), false, new DefaultConsumer(channel) {  // eb2a shelha lamma tkhallas
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(correlationId)) {
                        response.offer(new String(body, StandardCharsets.UTF_8));

                    }
                    try {
                        channel.basicAck(envelope.getDeliveryTag(), true);
                        channel.close();
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
//            lgr.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            return response.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

}
