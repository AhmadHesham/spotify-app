package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class EditReport extends Command {
    @Override
    public void execute() {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_report(?);");

            func.setInt(1, Integer.parseInt(map.get("report_id")));
            set = func.executeQuery();

            JSONObject result = new JSONObject();

            if (set.next()) {
                proc = dbConn.prepareCall("call edit_report(?,?)");
                proc.setInt(1, Integer.parseInt(map.get("report_id")));
                proc.setString(2, map.get("reason"));

                proc.setPoolable(true);

                proc.execute();
                result.put("msg", "report has been edited successfully");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            } else {
                result.put("error", "report not found");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }


            set.close();
            func.close();
            proc.close();


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

    @Override
    public String authorize() throws SQLException {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_report(?);");
            func.setInt(1, Integer.parseInt(map.get("report_id")));
            set = func.executeQuery();
            if (set.next()) {
                if (map.get("token_user_id").equals(set.getString(2))) {
                    set.close();
                    func.close();
                    return STATUSCODES.SUCCESS;
                }
            }
            return STATUSCODES.AUTHORIZATION;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUSCODES.AUTHORIZATION;
        }
        finally {
            dbConn.close();
        }
    }
}
