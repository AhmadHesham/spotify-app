package api.commands;



import api.Command;
import api.shared.ResponseHandler;

public class TestCommand extends Command{

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stu
        
        ResponseHandler.handleResponse("New Version Compiled File!", map.get("queue"), map.get("correlation_id"));
    }
    
}
