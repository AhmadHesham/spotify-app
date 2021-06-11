package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import db.PostgresConfig;
import models.nosql.BlockedUser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BlockUser extends Command {
    @Override
    public void execute()  {
        try{
            boolean checkIfCollectionExists = ArangoConfig.arangoDatabase.collection("BlockedUsers").exists();
            if(!checkIfCollectionExists){
                CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("BlockedUsers");
                System.out.println("New collection created: " + myArangoCollection.getName());
            }
            JSONObject responseObject = new JSONObject();
            String checkKey = map.get("_key");
            String checkId = map.get("_id");
            String checkUserId = map.get("user_id");
            String checkBlockedUserId = map.get("blocked_id");

             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(checkBlockedUserId));
            set = func.executeQuery();
            JSONObject retrievedUser = new JSONObject();
            boolean flag = false;
            while(set.next()) {
                retrievedUser.put("user_name", set.getString(1));
                retrievedUser.put("user_id", checkBlockedUserId);
                flag = true;
            }
            if(!flag){
//                retrievedUser.add("error","no accounts found");
//                throw new Exception("No accounts found");
                ResponseHandler.handleError("No accounts found", STATUSCODES.ENTITYNOTFOUND,
                        map.get("queue"), map.get("correlation_id"));
                return;
            }
            set.close();
            func.close();

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
                            BaseDocument toBeInserted = new BaseDocument();
                            toBeInserted.addAttribute("user_id",checkUserId);
                            ArrayList<BlockedUser> tempArray = new ArrayList<BlockedUser>();
                            tempArray.add(new BlockedUser(retrievedUser.getString("user_id"),retrievedUser.getString("user_name")));
                            toBeInserted.addAttribute("blocked_users",tempArray);
                            toBeInserted.setKey(checkUserId);
                            DocumentCreateEntity newDocumentInsertion = ArangoConfig.arangoDatabase.collection("BlockedUsers").insertDocument(toBeInserted);
                            System.out.println("Document inserted");
//                            responseObject.put("status","200 OK");
                            responseObject.put("message","User blocked successfully !!");
                        }else{
                            ArrayList<Object> blockedUsersArray = (ArrayList<Object>) result.getAttribute("blocked_users");
                            boolean alreadyBlockedFlag = false;
                            ArrayList<BlockedUser> newBlockedArray = new ArrayList<BlockedUser>();
                            for(Object singleBlockedObj:blockedUsersArray){
                                LinkedHashMap castedObject = (LinkedHashMap) singleBlockedObj;
                                if(castedObject.get("user_id").equals(checkBlockedUserId)){
                                    alreadyBlockedFlag =true;
                                    break;
                                }else{
                                    BlockedUser newObj = new BlockedUser(castedObject.get("user_id").toString(),castedObject.get("user_name").toString());
                                    newBlockedArray.add(newObj);
                                }
                            }
                            if(alreadyBlockedFlag){
//                                responseObject.put("status","error");
//                                responseObject.put("message","blocked_id is already blocked");
                                ResponseHandler.handleError("blocked_id is already blocked", STATUSCODES.ENTITYNOTFOUND,
                                        map.get("queue"), map.get("correlation_id"));
                                return;
                            }else{
                                newBlockedArray.add(new BlockedUser(retrievedUser.getString("user_id"),retrievedUser.getString("user_name")));
                                BaseDocument updateBody = new BaseDocument();
                                updateBody.addAttribute("blocked_users",newBlockedArray);
                                DocumentUpdateEntity<BaseDocument> newDocumentInsertion = ArangoConfig.arangoDatabase.collection("BlockedUsers").updateDocument(result.getKey(), updateBody);
                                System.out.print(newDocumentInsertion.getNew());
//                                responseObject.put("status","200 OK");
                                responseObject.put("message","User blocked successfully !!");
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
            JSONObject result = new JSONObject();
//            result.put("error","An exception has occurred");
//            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            ResponseHandler.handleError("An exception has occurred", STATUSCODES.UNKNOWN,
                    map.get("queue"), map.get("correlation_id"));
            return;

        }
    }
}