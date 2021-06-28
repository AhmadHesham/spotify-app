package controller;

import api.Command;
import api.shared.ResponseHandler;

public class TestController extends Command {
    @Override
    public void execute() throws Exception {
        ResponseHandler.handleResponse("Test Controller!", map.get("queue"), map.get("correlation_id"));
    }
}
