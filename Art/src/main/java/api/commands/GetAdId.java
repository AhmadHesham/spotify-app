package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Random;

public class GetAdId extends Command {
    @Override
    public void execute() throws Exception {
        try {

            dbConn = db.PostgresConfig.getDataSource().getConnection();
            boolean addExist = false;
            Random rand = new Random();
            int randNum = 1;
            int max_id = 1;
            JSONObject result = new  JSONObject();
            func = dbConn.prepareStatement("SELECT * FROM get_max_ad_id ();");
            set = func.executeQuery();
            if(set.next()) max_id = Integer.parseInt(set.getString(1));

            result.put("id", set.getString(1));
            set.close();
            func.close();
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

        }catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        } finally {
            try {
                dbConn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
