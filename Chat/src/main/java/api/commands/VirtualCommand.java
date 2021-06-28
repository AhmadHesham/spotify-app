package api.commands;

import api.Command;
import api.shared.ResponseHandler;

public class VirtualCommand extends Command{

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stub
        ResponseHandler.handleResponse("Edited CCommand! 123123123", map.get("queue"), map.get("correlation_id"));
    }
    
}
