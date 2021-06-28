package api.shared;

import api.Command;
import api.commands.CommandsMap;
import api.utils.JSONMapper;
import controller.CommandsMapController;
import threading.Pool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RequestHandler {

    public static void handleRequest(String message, Pool pool, String correlationId){
        HashMap<String, String> map = null;
        JSONMapper jsonMapper = new JSONMapper(message);

        try {
            map = jsonMapper.toHash();
            map.put("correlation_id", correlationId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<?> cmdClass;
        if (map.get("queue").contains("controller")) {
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
