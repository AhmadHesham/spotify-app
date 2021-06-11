package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetAccount extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(map.get("account_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                result.put("username", set.getString(1));
                result.put("password", set.getString(2));
                result.put("name", set.getString(3));
                result.put("profile_photo", set.getString(4));
                result.put("email", set.getString(5));
                result.put("type", set.getString(6));
                result.put("number_of_followers", set.getString(7));
                if (result.getString("type").equals("user")) {
                    func = dbConn.prepareStatement("SELECT * FROM get_user(?);");

                    func.setInt(1, Integer.parseInt(map.get("account_id")));
                    set = func.executeQuery();
                    if (set.next()) {
                        result.put("is_premium", set.getString(1));
                        result.put("bit_rate", set.getString(2));
                    }
                } else {
                    func = dbConn.prepareStatement("SELECT * FROM get_artist(?);");

                    func.setInt(1, Integer.parseInt(map.get("account_id")));
                    set = func.executeQuery();
                    if (set.next()) {
                        result.put("rating", set.getString(1));
                        result.put("number_of_ratings", set.getString(2));
                    }
                }

                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            } else {
                result.put("error", "account not found");

                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }



        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));


        }
        finally {
            try {
//                 proc.close();
//                 func.close();
//                 set.close();
                dbConn.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

    }
}
