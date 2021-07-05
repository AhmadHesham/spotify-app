package controller;

import api.Command;
import api.shared.ResponseHandler;
import cache.Redis;
import config.STATUSCODES;

public class Continue extends Command {
    public void execute() throws Exception {
        try {
            Redis.put("auth-freeze", "false");
            ResponseHandler.handleResponse("Application Frozen", map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHandler.handleError("Something went wrong", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }

}
