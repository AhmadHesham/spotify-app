package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import auth.Password;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class CreateAccount extends Command {
    @Override
    public void execute() {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            dbConn.setAutoCommit(false);
            func = dbConn.prepareStatement("SELECT * FROM get_account_username(?);");
            func.setString(1, map.get("username"));
            set = func.executeQuery();
            JSONObject result = new JSONObject();
            if (set.next()) {
                result.put("error", "username already exists");
                ResponseHandler.handleError(result.toString(), STATUSCODES.USERNAMEALREADYEXISTS, map.get("queue"), map.get("correlation_id"));
                return;
            }
            func = dbConn.prepareStatement("SELECT * FROM get_account_email(?);");
            func.setString(1, map.get("email"));
            set = func.executeQuery();
            if (set.next()) {
                result.put("error", "email already exists");
                ResponseHandler.handleError(result.toString(), STATUSCODES.EMAILALREADYEXISTS, map.get("queue"), map.get("correlation_id"));

                return;
            }
            proc = dbConn.prepareCall("call create_account(?,?,?,?,?,?,?);");
            proc.setPoolable(true);


            proc.setString(1, map.get("username"));
            Password p = new Password();
            proc.setString(2, p.hash(map.get("password")));
            proc.setString(3, map.get("name"));
            if (map.get("photo_path") == null) {
                proc.setString(4, "https://res.cloudinary.com/spotify-scalable/raw/upload/v1619807370/photos/youssef.jpg");
            } else {
                String url = CloudinaryAPI.upload(map.get("photo_path"), "photos");
                if (url.equals("Error")) {
                    url = "https://res.cloudinary.com/spotify-scalable/raw/upload/v1619807370/photos/youssef.jpg";
                }
                proc.setString(4, url);
            }
            proc.setString(5, map.get("email"));
            proc.setString(6, map.get("type"));
            proc.setInt(7, Integer.parseInt(map.get("number_of_followers")));

            proc.execute();
            if (map.get("type").equalsIgnoreCase("user")) {
                try {
                    func = dbConn.prepareStatement("SELECT * FROM get_account_username(?);");

                    func.setString(1, map.get("username"));
                    set = func.executeQuery();
                    if (set.next()) {
                        proc = dbConn.prepareCall("call create_user(?,?,?);");
                        proc.setPoolable(true);
                        proc.setInt(1, Integer.parseInt(set.getString(8)));
                        proc.setBoolean(2, Boolean.parseBoolean(map.get("is_premium")));
                        proc.setInt(3, Integer.parseInt(map.get("bit_rate")));

                        proc.execute();
                        result.put("msg", "user created successfully");
                        ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
                        dbConn.commit();

                    }
                } catch (Exception e) {
                    dbConn.rollback();
                    System.out.println(e.toString());
                    result = new JSONObject();
                    result.put("error", "An exception has occurred");
                    ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));

                }
            } else {
                try {
                    func = dbConn.prepareStatement("SELECT * FROM get_account_username(?);");

                    func.setString(1, map.get("username"));
                    set = func.executeQuery();
                    if (set.next()) {
                        proc = dbConn.prepareCall("call create_artist(?,?,?);");
                        proc.setPoolable(true);
                        proc.setInt(1, Integer.parseInt(set.getString(8).toString()));
                        proc.setDouble(2, Double.parseDouble(map.get("rating")));
                        proc.setInt(3, Integer.parseInt(map.get("number_of_ratings")));

                        proc.execute();
                        result.put("msg", "artist created successfully");
                        ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
                        dbConn.commit();

                    }
                    proc.close();
                } catch (Exception e) {
                    dbConn.rollback();

                    e.printStackTrace();
                    result = new JSONObject();
                    result.put("error", "An exception has occurred");
                    ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        } finally {
            try {
                // proc.close();
                // func.close();
                // set.close();
                dbConn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
