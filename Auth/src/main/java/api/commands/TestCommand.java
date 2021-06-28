package api.commands;

import api.Command;
import api.shared.ResponseHandler;

public class TestCommand extends Command {
    @Override
    public void execute() throws Exception {
        ResponseHandler.handleResponse("New Auth Command", map.get("queue"), map.get("correlation_id"));
    }
}
