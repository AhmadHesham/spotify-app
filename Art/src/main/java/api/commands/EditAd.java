package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class EditAd extends Command {
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_ad(?);");
            func.setInt(1, Integer.parseInt(map.get("ad_id")));
            set = func.executeQuery();
            JSONObject result = new JSONObject();
            if (set.next()) {
                result.put("ad_url", set.getString(1));
                result.put("ad_photo_url", set.getString(2));
            }
            else {
                result.put("error", " Ad does not exist");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            }
            set.close();
            func.close();
            if (!result.isEmpty()) {
                proc = dbConn.prepareCall("call edit_ad(?,?,?)");
                proc.setPoolable(true);
                proc.setInt(1, Integer.parseInt(map.get("ad_id")));
                if (map.get("ad_url") != null) proc.setString(2, map.get("ad_url"));
                else proc.setString(2, null);
                if (map.get("ad_image_link") != null){
                    String url = api.shared.helpers.CloudinaryAPI.upload(map.get("ad_image_link"), "ad");
                    if (url.equals("Error")) {
                        ResponseHandler.handleError("Ad failed to be uploaded", STATUSCODES.UPLOADERROR,
                                map.get("queue"), map.get("correlation_id"));
                        return;
                    }
                    proc.setString(3, url);
                }
                else proc.setString(3, null);
                System.out.println(proc.toString());
                proc.execute();

                ResponseHandler.handleResponse("zabtna el ad", map.get("queue"), map.get("correlation_id"));
            }
            else {
                ResponseHandler.handleResponse("Ad doesn't exist", map.get("queue"), map.get("correlation_id"));
            }
            proc.close();
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