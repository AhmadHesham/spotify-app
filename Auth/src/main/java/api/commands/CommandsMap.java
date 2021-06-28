package api.commands;

import cache.Redis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapAuth = new ConcurrentHashMap<>() ;
    private static Properties prop = new Properties();


//    public static void initialize() {
//
//        cmdMapAuth = new ConcurrentHashMap<>();
//
//        cmdMapAuth.put("sign-in", SignIn.class);
//        cmdMapAuth.put("create-account", CreateAccount.class);
//
//
//    }

    public static void initialize() throws Exception {
        try {
            prop.load(CommandsMap.class.getClassLoader().getResourceAsStream("AuthCommands.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while (propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                cmdMapAuth.put(current.toString(),
                        Class.forName("api.commands." + prop.get(current.toString()).toString()));
            }
            System.out.println("MAP: " + cmdMapAuth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Class<?> queryClass(String cmd, String queue) {
        try {
            System.out.println("here");
            System.out.println(cmd);
            System.out.println(queue);
            if(Redis.hasKey("auth-freeze") && Redis.get("auth-freeze").equals("true")){
                return FROZEN.class;
            }
            switch (queue) {
                case "auth":
                    return cmdMapAuth.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FOUROFOUR.class;
        }
    
    }
    public static void replace(String key, Class<?> cls, String fileName) {
        if (cmdMapAuth.containsKey(key)) {
            cmdMapAuth.replace(key, cls);
        } else {
            cmdMapAuth.put(key, cls);
            prop.setProperty(key, fileName);
            try {
                prop.store(new FileOutputStream("Auth/src/main/resources/AuthCommands.properties"), null);
            } catch (IOException e) {
                System.out.println("Error in updating .properties file");
            }
        }
    }


}
