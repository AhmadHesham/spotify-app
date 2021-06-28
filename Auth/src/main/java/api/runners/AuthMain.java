package api.runners;

import api.commands.CommandsMap;
import controller.CommandsMapController;
import db.ArangoConfig;
import db.PostgresConfig;
import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;
import threading.Pool;

public class AuthMain {
    public static Pool pool = new Pool();
    public static RabbitMQConfig configIn = new RabbitMQConfig("auth-IN");
    public static RabbitMQConfig configInController = new RabbitMQConfig("auth-controller-IN");
    public static void main(String[] args) throws  Exception{

        Consumer consumer = new Consumer(configIn,true);
        Consumer consumerController = new Consumer(configInController, true);
        CommandsMap.initialize();
        CommandsMapController.initialize();
        try {
            PostgresConfig.readonfFile();
            PostgresConfig.initSource();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArangoConfig.initialize("demo");
    }
}
