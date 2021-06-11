package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViewPlaylist extends Command {
    @Override
    public void execute() throws Exception {

        try {
            String query = "FOR p IN Playlist FILTER p.id==@id Return p";
            Map<String, Object> bindVars = Collections.singletonMap("id", map.get("playlist_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            List<BaseDocument> getPlaylist = cursor.asListRemaining();

            if (getPlaylist.isEmpty()) {
                ResponseHandler.handleError(getPlaylist.toString(), STATUSCODES.NOTFOUND, map.get("queue"), map.get("correlation_id"));
            }
            else {
                for(BaseDocument aDocument : getPlaylist){
                    BaseDocument myobj = ArangoConfig.arangoDatabase.collection("Playlist").getDocument(aDocument.getKey(), BaseDocument.class);
                    JSONObject temp = new JSONObject(aDocument);
                    String str = temp.toString();

                    if (myobj.getAttribute("userId").equals(map.get("user_id"))) {
                        ResponseHandler.handleResponse(str, map.get("queue"), map.get("correlation_id"));
                    }
                    else {
                        String songVisibility = String.valueOf(myobj.getAttribute("visible"));
                        if (Boolean.valueOf(songVisibility)) {
                            ResponseHandler.handleResponse(str, map.get("queue"), map.get("correlation_id"));
                        } else {
                            ResponseHandler.handleError("No playlist found!", STATUSCODES.NOTFOUND, map.get("queue"), map.get("correlation_id"));
                        }
                    }
                }
            }
        }
        catch (ArangoDBException e) {
            ResponseHandler.handleError("Failed to view the playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }

    @Override
    public String authorize() {

        if (map.get("token_user_id").equals(map.get("user_id"))) {
            return STATUSCODES.SUCCESS;
        }

        return STATUSCODES.AUTHORIZATION;
    }
}
