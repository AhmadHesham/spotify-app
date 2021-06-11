package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;


public class CreateAd extends Command {
    @Override
    public void execute() {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();

            proc = dbConn.prepareCall("call create_ad(?,?)");
            proc.setPoolable(true);
            String url ="";
            proc.setString(1, map.get("ad_link"));
            if (map.get("ad_image_link") == null) {
                proc.setString(2, "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764323/memes/eh_el_kalam_da_w4jonr.jpg");
            } else {
                url = CloudinaryAPI.upload(map.get("ad_image_link"), "ad");
                if (url.equals("Error")) {
                    url = "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764323/memes/eh_el_kalam_da_w4jonr.jpg";
                }
            }
            proc.setString(2,url);

            proc.execute();
            JSONObject result = new JSONObject();
            result.put("msg", "Ad created successfully");
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


