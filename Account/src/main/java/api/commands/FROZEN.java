package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import org.json.JSONObject;

public class FROZEN extends Command {

    @Override
    public void execute() throws Exception {
        JSONObject response = new JSONObject();
        response.put("error", "FROZEN");
        response.put("easter_egg", "https://res.cloudinary.com/spotify-scalable/image/upload/v1624643627/frozen-let-it-go_d2k7gp.jpg");
        ResponseHandler.handleError(response.toString(), STATUSCODES.NOTFOUND,
                map.get("queue"), map.get("correlation_id"));
    }
}
