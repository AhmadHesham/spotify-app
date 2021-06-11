package api.runners;

import api.commands.CommandsMap;
import api.shared.FirebaseConfig;
import db.ArangoConfig;
import db.PostgresConfig;
import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;
import threading.Pool;

public class ChatMain {
    public static Pool pool = new Pool();
    public static RabbitMQConfig configIn = new RabbitMQConfig("chat-IN");

    public static void main(String[] args) throws Exception {
        FirebaseConfig.initialize();
        CommandsMap.initialize();
        ArangoConfig.initialize("demo");

        Consumer consumer = new Consumer(configIn,true);
        try {
            PostgresConfig.readonfFile();
            PostgresConfig.initSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
