package api.commands;

import api.Command;
import api.shared.ResponseHandler;

public class VirtualCommand extends Command{

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stub
        ResponseHandler.handleResponse("manga 111 Edited CCommand! bassem", map.get("queue"), map.get("correlation_id"));
    }
    
}
