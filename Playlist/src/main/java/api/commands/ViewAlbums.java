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
import java.util.Map;

public class ViewAlbums extends Command {

    public void execute() throws Exception {
        try {
            String query = "FOR a IN Albums FILTER a.userId == @userId Return a";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                JSONObject albums= new JSONObject(aDocument);
                String str= albums.toString();
                ResponseHandler.handleResponse(str,map.get("queue"),map.get("correlation_id"));
            });
        }
        catch(ArangoDBException e){
            ResponseHandler.handleError("Failed to view albums", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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
