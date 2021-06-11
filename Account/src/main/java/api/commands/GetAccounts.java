package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetAccounts extends Command {
    @Override
    public void execute(){
        try{
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_accounts();");
            set = func.executeQuery();


            JSONObject result = new JSONObject();
            boolean flag = false;
            while(set.next()) {
                JSONObject element = new JSONObject();
                element.put("username", set.getString(1));
                element.put("password", set.getString(2));
                element.put("name", set.getString(3));
                element.put("profile_photo", set.getString(4));
                element.put("email", set.getString(5));
                element.put("type", set.getString(6));
                element.put("number_of_followers", set.getString(7));
                result.append("data", element);
                flag = true;
            }
            if(!flag){
                result.put("error","no accounts found");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
                return;
            }
             

            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));


        }
        catch(Exception e){
            JSONObject result = new JSONObject();
            result.put("error","An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN ,map.get("queue"), map.get("correlation_id"));


        }
        finally {
            try {
//                 proc.close();
                 func.close();
                 set.close();
                dbConn.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
