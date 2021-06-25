package controller;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;

public class SetMaxDBConnections extends Command {
    @Override
    public void execute() throws Exception {
        try {
            PostgresConfig.setMaxDBConnections(Integer.parseInt(map.get("max_db_connections")));
            ResponseHandler.handleResponse("Threads updated", map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHandler.handleError("Something went wrong", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }
}
