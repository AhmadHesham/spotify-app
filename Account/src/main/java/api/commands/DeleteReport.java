package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class DeleteReport extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_report(?);");

            func.setInt(1, Integer.parseInt(map.get("report_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                proc = dbConn.prepareCall("call delete_report(?)");
                proc.setPoolable(true);

                proc.setInt(1, Integer.parseInt(map.get("report_id")));

                proc.execute();
                result.put("msg", "report has been deleted successfully");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            } else {
                result.put("error", "this report does not exist");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));

            }


            set.close();
            func.close();
            proc.close();


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

    @Override
    public String authorize() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_report(?);");

            func.setInt(1, Integer.parseInt(map.get("report_id")));
            set = func.executeQuery();
            if (set.next()) {
                if (map.get("token_user_id").equals(set.getString(2))) {
                    return STATUSCODES.SUCCESS;
                }
            }
            return STATUSCODES.AUTHORIZATION;
        } catch (Exception e) {
            return STATUSCODES.AUTHORIZATION;
        }
        finally {
            try {
                func.close();
                set.close();
                dbConn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
}
