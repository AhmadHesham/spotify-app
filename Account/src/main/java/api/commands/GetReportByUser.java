package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetReportByUser extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * from get_report_user(?);");

            func.setInt(1, Integer.parseInt(map.get("user_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                result.put("reported_id", set.getString(1));
                result.put("user_id", set.getString(2));
                result.put("reason", set.getString(3));
                result.put("report_id", set.getString(4));
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            } else {
                result.put("error", "report not found");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }


            set.close();
            func.close();


        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            try {
                dbConn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
