package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class GetAds  extends Command{
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_ads();");
            set = func.executeQuery();

            JSONObject result = new JSONObject();
            ResultSetMetaData metadata = set.getMetaData();
            int columnCount = metadata.getColumnCount();
            for (int i=1; i<=columnCount; i++)
            {
                String columnName = metadata.getColumnName(i);
                System.out.println(columnName);
            }

            while (set.next()) {

                JSONObject element = new JSONObject();
//                element.put("id", set.getInt(1));
                element.put("ad_photo_url", set.getString(1));
                element.put("ad_url", set.getString(2));
                result.append("data", element);
            }
            set.close();
            func.close();

            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e) {
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