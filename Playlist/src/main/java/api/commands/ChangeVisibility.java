package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;

import java.util.Collections;
import java.util.Map;

public class ChangeVisibility extends Command {
    @Override
    public void execute() throws Exception {
        try{
            String query = "FOR p IN Playlist FILTER p.id == @id Return p";
            Map<String, Object> bindVars = Collections.singletonMap("id", map.get("playlist_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myPlaylist=  ArangoConfig.arangoDatabase.collection("Playlist").getDocument(aDocument.getKey(),BaseDocument.class);
                myPlaylist.updateAttribute("visible",Boolean.valueOf(map.get("visibility")) );
                ArangoConfig.arangoDatabase.collection("Playlist").updateDocument(aDocument.getKey(),myPlaylist);

            });
            ResponseHandler.handleResponse("Success! "+ "Visibility status is: "+ map.get("visibility") ,map.get("queue"),map.get("correlation_id"));
        }catch(ArangoDBException e) {
            ResponseHandler.handleError("Couldn't change playlist visibility", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

