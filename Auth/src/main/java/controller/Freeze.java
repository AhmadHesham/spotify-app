package controller;

import api.Command;
import api.shared.ResponseHandler;
import cache.Redis;
import config.STATUSCODES;

public class Freeze extends Command {
    @Override
    public void execute() throws Exception {
        try {
            System.out.println("geh hena");
            Redis.put("auth-freeze", "true");
            System.out.println("here??");
            ResponseHandler.handleResponse("Application Frozen", map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHandler.handleError("Something went wrong", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }
}
