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

public class DeletePlaylist extends Command {


    @Override
    public void execute() throws Exception {

        try{
            String query = "FOR t IN Playlist FILTER t.userId == @id RETURN t";
            Map<String, Object> bindVars = Collections.singletonMap("id", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj=  ArangoConfig.arangoDatabase.collection("Playlist").getDocument(aDocument.getKey(),BaseDocument.class);
                if(aDocument.getAttribute("id").equals(map.get("playlist_id"))) {
                    ArangoConfig.arangoDatabase.collection("Playlist").deleteDocument(aDocument.getKey());
                    ResponseHandler.handleResponse("Playlist removed successfully ",map.get("queue"),map.get("correlation_id"));
                }
            });
        }
        catch(ArangoDBException e){
            ResponseHandler.handleError("Couldn't delete the playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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
