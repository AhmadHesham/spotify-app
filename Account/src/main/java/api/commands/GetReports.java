package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetReports extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_report();");
            set = func.executeQuery();


            JSONObject result = new JSONObject();
            boolean flag = false;
            while (set.next()) {
                JSONObject element = new JSONObject();
                element.put("reported_id", set.getString(1));
                element.put("user_id", set.getString(2));
                element.put("reason", set.getString(3));
                element.put("id", set.getString(4));
                result.append("data", element);
                flag = true;
            }

            if (!flag) {
                result.put("error", "no reports found");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
                return;
            }
            set.close();
            func.close();


            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
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
