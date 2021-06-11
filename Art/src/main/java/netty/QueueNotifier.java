package netty;

import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;

import java.util.concurrent.Callable;

public class QueueNotifier implements Callable<String> {

    private JSONHandler serverHandler;

    private String responseBody;
    private String queueName;

    public QueueNotifier(JSONHandler serverHandler,
                         String queueName) {
        this.serverHandler = serverHandler;
        this.setQueueName(queueName);
    }

    @Override
    public String call() {

        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig(getQueueName()+"-OUT");
        Consumer consumer = new Consumer(rabbitMQConfig, false);

        // wait until the response sent from the micro-services

        return  consumer.consumeOne(serverHandler.getCorrelationId());

    }

    public JSONHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(JSONHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

}
