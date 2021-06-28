package controller;

import api.commands.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMapController {
    private static ConcurrentMap<String, Class<?>> cmdMapController = new ConcurrentHashMap<>();



    private static Properties prop = new Properties();

    public static void initialize() throws Exception {
        // Class<?> cls = Class.forName("api.commands.CommandsMap");
        // System.out.println(cls.getName());
        try {
            prop.load(CommandsMapController.class.getClassLoader().getResourceAsStream("AccountController.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while (propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                cmdMapController.put(current.toString(),
                        Class.forName("controller." + prop.get(current.toString()).toString()));

            }
            System.out.println(cmdMapController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> queryClass(String cmd, String queue) {
        try {
            switch (queue) {
                case "account-controller":
                    return cmdMapController.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }
    
    }
    public static void replace(String key, Class<?> cls, String fileName) {
        if (cmdMapController.containsKey(key)) {
            cmdMapController.replace(key, cls);
        } else {
            cmdMapController.put(key, cls);
            prop.setProperty(key, fileName);
            try {
                prop.store(new FileOutputStream("Account/src/main/resources/AccountController.properties"), null);
            } catch (IOException e) {
                System.out.println("Error in updating .properties file");
            }
        }
    }


}
