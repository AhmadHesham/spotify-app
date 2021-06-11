package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class DeleteAds extends Command {
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            proc = dbConn.prepareCall("call delete_all_ads()");
            proc.setPoolable(true);
            proc.execute();
            JSONObject result = new JSONObject();
            result.put("msg", "Ads deleted successfully");
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