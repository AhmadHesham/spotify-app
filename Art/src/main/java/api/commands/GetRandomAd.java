package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Random;

public class GetRandomAd extends Command {
    @Override
    public void execute() throws Exception {
        try {

            dbConn = PostgresConfig.getDataSource().getConnection();
            boolean addExist = false;
            Random rand = new Random();
            int randNum = 1;
            int max_id = 1;
            JSONObject result = new  JSONObject();
            func = dbConn.prepareStatement("SELECT * FROM get_max_ad_id ();");
            set = func.executeQuery();
            if(set.next()) max_id = Integer.parseInt(set.getString(1));

            func = dbConn.prepareStatement("SELECT * FROM get_ad(?);");

            while(!addExist) {
                randNum = rand.nextInt(max_id+1);
                func.setInt(1, randNum);
                set = func.executeQuery();
                if (set.next()) {
                    addExist = true;
                    result.put("ad_url", set.getString(1));
                    result.put("ad_photo_url", set.getString(2));
                }
            }

            set.close();
            func.close();
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

        }catch (Exception e) {
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