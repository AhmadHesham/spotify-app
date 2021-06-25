package controller;

import api.Command;
import api.runners.PlaylistMain;
import api.shared.ResponseHandler;
import config.STATUSCODES;

public class SetMaxThreads extends Command {
    @Override
    public void execute() throws Exception {
        try {
            PlaylistMain.pool.setMaxThreads(Integer.parseInt(map.get("max_threads")));
            ResponseHandler.handleResponse("Threads updated", map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHandler.handleError("Something went wrong", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }
}
