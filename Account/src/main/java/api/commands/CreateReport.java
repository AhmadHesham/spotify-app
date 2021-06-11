package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class CreateReport extends Command {
    @Override
    public void execute() {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();


            proc = dbConn.prepareCall("call create_report(?,?,?)");
            proc.setPoolable(true);

            proc.setInt(1, Integer.parseInt(map.get("reported_id")));
            proc.setInt(2, Integer.parseInt(map.get("user_id")));
            proc.setString(3, map.get("reason"));

            proc.execute();
            JSONObject result = new JSONObject();
            result.put("msg", "report created successfully");
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

    @Override
    public String authorize() {

        if (map.get("token_user_id").equals(map.get("user_id"))) {
            return STATUSCODES.SUCCESS;
        }

        return STATUSCODES.AUTHORIZATION;
    }

}
