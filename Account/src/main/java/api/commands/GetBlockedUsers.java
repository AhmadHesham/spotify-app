package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.BlockedUser;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetBlockedUsers extends Command {
    @Override
    public void execute()  {
        try{
            System.out.println("entered");
            JSONObject responseObject = new JSONObject();
//            responseObject.put("error","An exception has occurred");
            String checkKey = map.get("_key");
            String checkUserId = map.get("user_id");
            if(checkUserId!=null ){
//                        String query = "FOR t IN BlockedUsers FILTER t.UserID == @user_id RETURN t";
//                        Map<String, Object> bindVars = new MapBuilder().put("user_id", checkUserId).get();
//                        ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null,
//                                BaseDocument.class);
//                        List<BaseDocument> result = cursor.asListRemaining();

                BaseDocument result = ArangoConfig.arangoDatabase.collection("BlockedUsers").getDocument(checkUserId, BaseDocument.class);

                if(result==null){
//                    responseObject.put("status","error");
//                    responseObject.put("message","this user didn't block anyone");
                    ResponseHandler.handleError("this user didn't block anyone", STATUSCODES.ENTITYNOTFOUND,
                            map.get("queue"), map.get("correlation_id"));
                    return;
                }else{
                    BaseDocument foundDoc = result;
                    ArrayList<BlockedUser> blockedUsersArray = (ArrayList<BlockedUser>) foundDoc.getAttribute("blocked_users");
                    System.out.println(blockedUsersArray);

//                    responseObject.put("status","success");
                    responseObject.put("blocked_users",blockedUsersArray);
                    System.out.println(responseObject);

                }
            }
            ResponseHandler.handleResponse(responseObject.toString(),map.get("queue"),map.get("correlation_id"));
        }
        catch(Exception e){
            System.out.println(e.toString());
            System.out.println(e.getStackTrace()[0].getLineNumber());
//            JSONObject result = new JSONObject();
//            result.put("error","An exception has occurred");
//            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            ResponseHandler.handleError("An exception has occurred", STATUSCODES.UNKNOWN,
                    map.get("queue"), map.get("correlation_id"));
            return;
        }
    }
}