package api.runners;

import api.commands.CommandsMap;
import controller.CommandsMapController;
import db.ArangoConfig;
import db.PostgresConfig;
import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;
import threading.Pool;

public class ArtMain {
    public static Pool pool = new Pool();
    public static RabbitMQConfig configIn = new RabbitMQConfig("art-IN");
    public static RabbitMQConfig configInController = new RabbitMQConfig("art-controller-IN");

    public static void main(String[] args) throws  Exception{
        CommandsMap.initialize();
        CommandsMapController.initialize();
        Consumer consumer = new Consumer(configIn,true);
        Consumer ControllerConsumer = new Consumer(configInController, true);
        try {
            PostgresConfig.readonfFile();
            PostgresConfig.initSource();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArangoConfig.initialize("demo");
    }
}
