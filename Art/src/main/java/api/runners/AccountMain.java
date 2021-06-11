package api.runners;

import api.commands.CommandsMap;
import db.ArangoConfig;
import db.PostgresConfig;
import rabbitmq.Consumer;
import rabbitmq.RabbitMQConfig;
import threading.Pool;

public class AccountMain {
    public static Pool pool = new Pool();
    public static RabbitMQConfig configIn = new RabbitMQConfig("account-IN");

    public static void main(String[] args) throws  Exception{

        CommandsMap.initialize();
        Consumer consumer = new Consumer(configIn,true);
        try {
            PostgresConfig.readonfFile();
            PostgresConfig.initSource();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArangoConfig.initialize("demo");
    }
}
