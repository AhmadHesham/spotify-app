package controller;

import api.Command;
import api.commands.CommandsMap;
import api.shared.ResponseHandler;

public class UpdateClass extends Command {
    @Override
    public void execute() throws Exception {
        ClassLoader classLoader = UpdateClass.class.getClassLoader();

        try {
            Class aClass = classLoader.loadClass("api.commands.GetAccount");
            System.out.println("aClass.getName() = " + aClass);

            CommandsMap.replace("get-account", aClass);
            ResponseHandler.handleResponse("eh yaba tamam", map.get("queue"), map.get("correlation_id"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
