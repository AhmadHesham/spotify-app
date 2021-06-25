package api.commands;

import cache.Redis;

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
    }
    
    public static Class<?> queryClass(String cmd, String queue) {
        try {
            if(Redis.hasKey("freeze") && Redis.get("freeze").equals("true")){
                return FROZEN.class;
            }
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
