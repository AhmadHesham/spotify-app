package controller;

import api.commands.FOUROFOUR;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMapController {
    private static ConcurrentMap<String, Class<?>> cmdMapController;



    public static void initialize() {

        cmdMapController = new ConcurrentHashMap<>();

        cmdMapController.put("freeze", Freeze.class);
        cmdMapController.put("set-max-threads", SetMaxThreads.class);
        cmdMapController.put("set-max-db-conns", SetMaxDBConnections.class);
        cmdMapController.put("continue", Continue.class);
        cmdMapController.put("set-error-reporting-level", SetErrorReportingLevel.class);


    }
    
    public static Class<?> queryClass(String cmd, String queue) {
        try {
            switch (queue) {
                case "controller":
                    return cmdMapController.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }
    
    }


}
