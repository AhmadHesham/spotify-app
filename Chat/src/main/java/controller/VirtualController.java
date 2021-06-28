package controller;

import api.Command;
import api.shared.ResponseHandler;

public class VirtualController extends Command {
    @Override
    public void execute() throws Exception {
        ResponseHandler.handleResponse("Virtual Controller!", map.get("queue"), map.get("correlation_id"));
    }
}
