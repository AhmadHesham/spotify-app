package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class DeleteAd extends Command {
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_ad(?);");
            func.setInt(1, Integer.parseInt(map.get("ad_id")));
            set = func.executeQuery();
            String url = "";
            JSONObject result = new JSONObject();
            if (set.next()) {
                url = set.getString(2);
            }
            else {
                result.put("error", " Ad does not exist");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            }
            set.close();
            func.close();
            System.out.println(url);
            CloudinaryAPI.delete(url);
            proc = dbConn.prepareCall("call delete_ad_by_id(?)");
            proc.setPoolable(true);

            proc.setInt(1, Integer.parseInt(map.get("ad_id")));

            proc.execute();
            result = new JSONObject();
            result.put("msg", "Ad deleted successfully");
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            proc.close();
        } catch (Exception e) {
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