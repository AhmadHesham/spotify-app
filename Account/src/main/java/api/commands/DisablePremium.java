package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class DisablePremium extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(map.get("account_id")));
            set = func.executeQuery();

            JSONObject result = new JSONObject();

            if (set.next()) {
                String t = set.getString(6);
                if (t.equalsIgnoreCase("user")) {
                    proc = dbConn.prepareCall("call edit_user(?,?,?)");
                    proc.setInt(1, Integer.parseInt(map.get("account_id")));
                    proc.setBoolean(2, false);
                    proc.setInt(3, -1);
                    proc.setPoolable(true);

                    proc.execute();
                } else {
                    result.put("error", "this is an artist account");
                    ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYTYPE, map.get("queue"), map.get("correlation_id"));
                }
                result.put("msg", "the user is no longer premium");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            } else {
                result.put("error", "this user does not exist");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }


        } catch (Exception e) {
            System.out.println(e.toString());
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));


        }
        finally {
            try {
                // proc.close();
                // func.close();
                // set.close();
                dbConn.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    @Override
    public String authorize() {
        if (map.get("token_user_id").equals(map.get("account_id"))) {
            return STATUSCODES.SUCCESS;
        }
        return STATUSCODES.AUTHORIZATION;
    }
}
