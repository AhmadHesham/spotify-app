package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class DeleteAccount extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(map.get("account_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                proc = dbConn.prepareCall("call delete_account(?)");
                proc.setPoolable(true);

                proc.setInt(1, Integer.parseInt(map.get("account_id")));

                proc.execute();
                String url = set.getString(4);
                if (!url.equals("https://res.cloudinary.com/spotify-scalable/raw/upload/v1619807370/photos/youssef.jpg"))
                    CloudinaryAPI.delete(url);
                result.put("msg", "account has been deleted successfully");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

            } else {
                result.put("error", "this account does not exist");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }



        } catch (Exception e) {
            e.printStackTrace();
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));

        }
        finally {
            try {
                 proc.close();
                 func.close();
                 set.close();
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
