package rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class RabbitMQConfig {

    private Connection connection;
    private String queueName;

    public RabbitMQConfig(String queueName) {
        this.queueName = queueName;
    }

    public Connection connect() throws IOException, TimeoutException {
        if (connection == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
//            connectionFactory.setHost("localhost");
//            connectionFactory.setHost("rabbitmq");
//            connectionFactory.setPort(5672);
            connection = connectionFactory.newConnection();
//            connection.openChannel();
        }
        return connection;
    }

//    public void disconnect(Connection connection) throws IOException {
//        connection.close();
//    }

    public String getQueueName() {
        return this.queueName;
    }
}
