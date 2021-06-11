package api.commands;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapAuth;



    public static void initialize() {
        cmdMapAuth = new ConcurrentHashMap<>();
    
        cmdMapAuth.put("sign-in", SignIn.class);
        cmdMapAuth.put("create-account", CreateAccount.class);


    }
    
    public static Class<?> queryClass(String cmd, String queue) {
        try {
            switch (queue) {
                case "auth":
                    return cmdMapAuth.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }
    
    }


}
