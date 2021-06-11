package netty.chat;

import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;

import java.util.concurrent.Callable;

public class ChatNotifier implements Callable<String> {
    private String correlationID;
    private String queueName;

    public ChatNotifier(String correlationID, String queueName){
        this.correlationID = correlationID;
        this.queueName = queueName;
    }

    @Override
    public String call() throws Exception {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig(this.queueName+"-OUT");
        Consumer consumer = new Consumer(rabbitMQConfig, false);
        System.out.println("ana fel chat notifier");
        return  consumer.consumeOne(this.correlationID);
    }
}
