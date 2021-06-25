package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import auth.JWT;
import auth.Password;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignIn extends Command {
    @Override
    public void execute() throws Exception {

        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_account_username(?);");
            func.setString(1, map.get("username"));
            set = func.executeQuery();
            JSONObject result = new JSONObject();
            if (set.next()) {
                Password p = new Password();
                if (p.verifyHash(map.get("password"), set.getString(2))) {

                    result.put("username", set.getString(1));
                    result.put("password", set.getString(2));
                    result.put("name", set.getString(3));
                    result.put("profile_photo", set.getString(4));
                    result.put("email", set.getString(5));
                    result.put("type", set.getString(6));
                    result.put("number_of_followers", set.getString(7));
                    result.put("id", set.getString(8));

                    Map<String, Object> claims = new HashMap<>();
                    claims.put("token_user_id", result.get("id"));
                    claims.put("token_username", result.get("username"));
                    claims.put("token_email", result.get("email"));
                    claims.put("token_name", result.get("name"));
                    claims.put("token_type", result.get("type"));
                    Date d = new Date();
                    d.setTime(d.getTime() + TimeUnit.HOURS.toMillis(10));
                    claims.put("expiration", d);

                    if (set.getString(6).equalsIgnoreCase("user")) {
                        func = dbConn.prepareStatement("SELECT * FROM get_user(?);");
                        func.setInt(1, Integer.parseInt(set.getString(8)));
                        set = func.executeQuery();
                        if (set.next()) {
                            claims.put("token_is_premium", set.getString(1));
                            claims.put("token_bit_rate", set.getString(2));
                        }
                    } else {
                        func = dbConn.prepareStatement("SELECT * FROM get_artist(?);");
                        func.setInt(1, Integer.parseInt(set.getString(8)));
                        set = func.executeQuery();
                        if (set.next()) {
                            claims.put("token_rating", set.getString(1));
                        }

                    }

                    String token = JWT.signToken(claims);
                    result.put("token", token);

                    ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
                    return;
                } else {

                    result.put("error", "wrong credentials");

//                    proc.close();
                    ResponseHandler.handleError(result.toString(), STATUSCODES.INVALIDCREDENTIALS, map.get("queue"), map.get("correlation_id"));

                    return;
                }


            } else {
                result.put("error", "username does not exist");
                ResponseHandler.handleError(result.toString(), STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
                return;
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
