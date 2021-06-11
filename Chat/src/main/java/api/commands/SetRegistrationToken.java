package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import netty.chat.LinkMap;

public class SetRegistrationToken extends Command {

    @Override
    public String authorize() throws Exception {
        if(map.get("token_type").equals("user"))
            return STATUSCODES.SUCCESS;
        return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        System.out.println("SET REGISTRATION CORR: " + map.get("correlation_id"));
        LinkMap.userToRegistrationToken.put(map.get("token_user_id"), map.get("registrationToken"));
        ResponseHandler.handleResponse("Token Set!", map.get("queue"), map.get("correlation_id"));
    }
}
