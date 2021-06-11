package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentUpdateEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.BlockedUser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class UnblockUser extends Command {
    @Override
    public void execute()  {
        try{
            System.out.println("in unblock");
            JSONObject responseObject = new JSONObject();
//            responseObject.put("error","An exception has occurred");
            String checkKey = map.get("_key");
            String checkId = map.get("_id");
            String checkUserId = map.get("user_id");
            String checkBlockedUserId = map.get("blocked_id");
            if(checkKey!=null){
                BaseDocument myDocument = ArangoConfig.arangoDatabase.collection("BlockedUsers").getDocument(checkKey, BaseDocument.class);
                if(myDocument!=null){
                    System.out.println(myDocument.getProperties());
                }else{
                    System.out.println("There is no objects with _key: "+checkKey);
                }

            }else{
                if(checkId!=null){

                }else{
                    if(checkUserId!=null && checkBlockedUserId!=null){
//                        String query = "FOR t IN BlockedUsers FILTER t.UserID == @user_id RETURN t";
//                        Map<String, Object> bindVars = new MapBuilder().put("user_id", checkUserId).get();
//                        ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null,
//                                BaseDocument.class);
//                        List<BaseDocument> result = cursor.asListRemaining();

                        BaseDocument result = ArangoConfig.arangoDatabase.collection("BlockedUsers").getDocument(checkUserId, BaseDocument.class);
                        if(result==null){
//                            responseObject.put("status","error");
//                            responseObject.put("message","this user didn't block anyone");
                            ResponseHandler.handleError("this user didn't block anyone", STATUSCODES.ENTITYNOTFOUND,
                                    map.get("queue"), map.get("correlation_id"));
                            return;
//                            BaseDocument toBeInserted = new BaseDocument();
//                            toBeInserted.addAttribute("UserId",checkUserId);
//                            ArrayList<String> tempArray = new ArrayList<String>();
//                            tempArray.add(checkBlockedUserId);
//                            toBeInserted.addAttribute("BlockedUsers",tempArray);
//                            toBeInserted.setKey(checkUserId);
//                            DocumentCreateEntity newDocumentInsertion = ArangoConfig.arangoDatabase.collection("BlockedUsers").insertDocument(toBeInserted);
//                            System.out.println("Document inserted");
//                            responseObject.put("status","200 OK");
//                            responseObject.put("message","User blocked successfully !!");
                        }else{
                            ArrayList<Object> blockedUsersArray = (ArrayList<Object>) result.getAttribute("blocked_users");
                            boolean foundBlocked = false;
                            ArrayList<BlockedUser> newBlockedArray = new ArrayList<BlockedUser>();
                            for(Object singleBlockedObj:blockedUsersArray){
                                LinkedHashMap castedObject = (LinkedHashMap) singleBlockedObj;
                                if(!castedObject.get("user_id").equals(checkBlockedUserId)){
                                    BlockedUser newObj = new BlockedUser(castedObject.get("user_id").toString(),castedObject.get("user_name").toString());
                                    newBlockedArray.add(newObj);
                                }else{
                                    foundBlocked = true;
                                }
                            }
                            if(foundBlocked){
                                BaseDocument updateBody = new BaseDocument();
                                updateBody.addAttribute("blocked_users",newBlockedArray);
                                DocumentUpdateEntity<BaseDocument> newDocumentInsertion = ArangoConfig.arangoDatabase.collection("BlockedUsers").updateDocument(checkUserId, updateBody);
                                System.out.print(newDocumentInsertion.getNew());
//                                responseObject.put("status","200 OK");
                                responseObject.put("message","User unblocked successfully !!");
                            }else{
//                                responseObject.put("status","error");
//                                responseObject.put("message","blocked_id is not blocked");
                                ResponseHandler.handleError("blocked_id is not blocked", STATUSCODES.ENTITYNOTFOUND,
                                        map.get("queue"), map.get("correlation_id"));
                                return;

                            }


                        }
                    }
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