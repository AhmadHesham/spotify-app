package api.commands;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapChat = new ConcurrentHashMap<>();

    public static void initialize() throws Exception {
        Properties prop = new Properties();
        // Class<?> cls = Class.forName("api.commands.CommandsMap");
        // System.out.println(cls.getName());
        try {
            prop.load(CommandsMap.class.getClassLoader().getResourceAsStream("ChatCommands.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while (propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                cmdMapChat.put(current.toString(),
                        Class.forName("api.commands." + prop.get(current.toString()).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> queryClass(String cmd, String queue) {
        try {
            switch (queue) {
                case "chat":
                    return cmdMapChat.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }

    }

    public static void replace(String key, Class<?> cls) {
        System.out.println("BEFORE: " + cmdMapChat);
        if (cmdMapChat.containsKey(key)) {
            cmdMapChat.replace(key, cls);
        } else {
            cmdMapChat.put(key, cls);
        }
        System.out.println("AFTER : " + cmdMapChat);
        System.out.println("replaced");
    }
}
