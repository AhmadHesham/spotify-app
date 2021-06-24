package api.commands;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapChat;



    public static void initialize() {
        //Start of Chat CommandsMap
        cmdMapChat = new ConcurrentHashMap<>();
        cmdMapChat.put("create-chat", CreateChat.class);
        cmdMapChat.put("log-text", LogText.class);
        cmdMapChat.put("get-chat-logs", ChatLog.class);
        cmdMapChat.put("add-participant", AddParticipant.class);
        cmdMapChat.put("send-chat-notification", ChatNotification.class);
        cmdMapChat.put("get-chat-notification", GetNotifications.class);
        cmdMapChat.put("set-registration-token", SetRegistrationToken.class);
        //End of Chat CommandsMap

        ConcurrentMap<String, Class<?>> testMap  = new ConcurrentHashMap<>();
        Properties prop = new Properties();
        try{
            prop.load(CommandsMap.class.getClassLoader().getResourceAsStream("ChatCommands.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while(propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                testMap.put(current.toString(), Class.forName(prop.get(current.toString()).toString()));
            }
            System.out.println(testMap);
        }
        catch(Exception e){
            System.out.println(testMap);
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


}
