package api.shared;

import api.Command;
import api.commands.CommandsMap;
import controller.CommandsMapController;
import api.utils.JSONMapper;
import threading.Pool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RequestHandler {

    public static void handleRequest(String message, Pool pool, String correlationId) {
        HashMap<String, String> map = null;
        JSONMapper jsonMapper = new JSONMapper(message);
        System.out.println("inside handle request");

        try {
            map = jsonMapper.toHash();
            map.put("correlation_id", correlationId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<?> cmdClass = null;

        System.out.println(map.get("queue"));
        if (map.get("queue").equals("account-controller")) {
            System.out.println("hena walla fen?");
            cmdClass = CommandsMapController.queryClass(map.get("method"), map.get("queue"));
        } else {
            cmdClass = CommandsMap.queryClass(map.get("method"), map.get("queue"));
        }
        Command c = null;
        try {
            c = (Command) cmdClass.getDeclaredConstructor().newInstance();
            c.setMap(map);
            pool.execute(c);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
