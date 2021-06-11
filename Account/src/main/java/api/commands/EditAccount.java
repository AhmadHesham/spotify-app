package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import auth.Password;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;

public class EditAccount extends Command {
    @Override
    public void execute() throws SQLException {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(map.get("account_id")));
            set = func.executeQuery();

            JSONObject result = new JSONObject();

            if (set.next()) {
                String t = set.getString(6);
                String photo = set.getString(4);
                if (map.get("username") != null) {
                    func = dbConn.prepareStatement("SELECT * FROM get_account_username(?);");
                    func.setString(1, map.get("username"));
                    set = func.executeQuery();
                    if (set.next()) {
                        result.put("error", "username already exists");
                        ResponseHandler.handleError(result.toString(), STATUSCODES.USERNAMEALREADYEXISTS, map.get("queue"), map.get("correlation_id"));
                         
                        return;
                    }


                }
                if (map.get("email") != null) {
                    func = dbConn.prepareStatement("SELECT * FROM get_account_email(?);");
                    System.out.println(map.get("email"));
                    func.setString(1, map.get("email"));
                    set = func.executeQuery();
                    if (set.next()) {
                        result.put("error", "email already exists");
                        ResponseHandler.handleError(result.toString(), STATUSCODES.EMAILALREADYEXISTS, map.get("queue"), map.get("correlation_id"));
                         
                        return;
                    }
                }
                proc = dbConn.prepareCall("call edit_account(?,?,?,?,?,?,?,?)");
                proc.setInt(1, Integer.parseInt(map.get("account_id")));
                proc.setString(2, map.get("username"));
                if (map.get("password") != null) {
                    Password p = new Password();
                    proc.setString(3, p.hash(map.get("password")));
                } else {
                    proc.setString(3, map.get("password"));
                }
                proc.setString(4, map.get("name"));
                if (map.get("photo_path") != null) {
                    String url = CloudinaryAPI.upload(map.get("photo_path"), "photos");
                    if (url.equals("Error")) {
                        url = photo;
                    }

                    proc.setString(5, url);
                } else {

                    proc.setString(5, map.get("photo_path"));
                }

                proc.setString(6, map.get("email"));
                if (map.get("num_of_followers") == null) {
                    proc.setInt(7, -1);
                } else {
                    proc.setInt(7, Integer.parseInt(map.get("num_of_followers")));
                }
                proc.setString(8, null);

                proc.setPoolable(true);

                proc.execute();

                if (t.equalsIgnoreCase("user")) {
                    proc = dbConn.prepareCall("call edit_user(?,?,?)");
                    proc.setInt(1, Integer.parseInt(map.get("account_id")));
                    proc.setBoolean(2, Boolean.parseBoolean(map.get("is_premium")));
                    if (map.get("bit_rate") == null) {
                        proc.setInt(3, -1);
                    } else {
                        proc.setInt(3, Integer.parseInt(map.get("bit_rate")));
                    }
                    proc.setPoolable(true);

                    proc.execute();
                } else {
                    proc = dbConn.prepareCall("call edit_artist(?,?,?)");
                    proc.setInt(1, Integer.parseInt(map.get("account_id")));
                    if (map.get("rating") == null) {
                        proc.setDouble(2, -1);
                    } else {

                        proc.setDouble(2, Double.parseDouble(map.get("rating")));
                    }
                    if (map.get("number_of_ratings") == null) {
                        proc.setInt(3, -1);
                    } else {

                        proc.setInt(3, Integer.parseInt(map.get("number_of_ratings")));
                    }
                    proc.setPoolable(true);

                    proc.execute();
                }
                result.put("msg", "account has been edited successfully");
                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

            } else {
                result.put("error", "this account does not exist");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }
             



        } catch (Exception e) {
            System.out.println(e.toString());
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred");
            ResponseHandler.handleError(result.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
             

        }
        finally {
            try {
                 proc.close();
                 func.close();
                 set.close();
                dbConn.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    @Override
    public String authorize() {
        if (map.get("token_user_id").equals(map.get("account_id"))) {
            return STATUSCODES.SUCCESS;
        }
        return STATUSCODES.AUTHORIZATION;
    }
}
