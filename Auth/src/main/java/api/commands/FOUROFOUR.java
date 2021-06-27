package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import org.json.JSONObject;

public class FOUROFOUR extends Command {

    @Override
    public void execute() throws Exception {
        System.out.println("maaaaaaaP"+map);
        JSONObject response = new JSONObject();
        response.put("error", "Command not found");
        response.put("easter_egg", "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764323/memes/eh_el_kalam_da_w4jonr.jpg");
        ResponseHandler.handleError(response.toString(), STATUSCODES.NOTFOUND,
                map.get("queue"), map.get("correlation_id"));
    }
}
