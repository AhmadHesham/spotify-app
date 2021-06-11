package api.shared;

import config.STATUSCODES;
import org.json.JSONObject;
import rabbitmq.Producer;
import rabbitmq.RabbitMQConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ResponseHandler {

    public static boolean isJSONValid(String jsonInString) {
        try {
            new JSONObject(jsonInString);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static void handleResponse(String response,  String queueName, String correlationId){
        System.out.println("responding from handle response");

        try {
            RabbitMQConfig config = new RabbitMQConfig(queueName+"-OUT");
            Producer p = new Producer(config);

            if(isJSONValid(response)) {
                JSONObject respBody = new JSONObject(response);
                JSONObject res = new JSONObject();
                res.put("data", respBody);
                res.put("statusCode", STATUSCODES.SUCCESS);
                res.put("easter_egg", "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764148/memes/ya_salam_howa_da_el_kalam_zzrvdy.jpg");
                p.send(res.toString(), correlationId,null);

            }else{
                JSONObject res = new JSONObject();
                res.put("data", response);
                res.put("statusCode", STATUSCODES.SUCCESS);
                res.put("easter_egg", "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764148/memes/ya_salam_howa_da_el_kalam_zzrvdy.jpg");
                p.send(res.toString(), correlationId,null);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void handleError(String error, String statusCode, String queueName, String correlationId){
        RabbitMQConfig config = new RabbitMQConfig(queueName+"-OUT");
        Producer p = new Producer(config);
        try {
            if(isJSONValid(error)) {
                JSONObject res = new JSONObject();
                JSONObject resp = new JSONObject(error);
                res.put("error", resp);
                res.put("statusCode", statusCode);

                p.send(res.toString(), correlationId, null);
            }
            else{
                JSONObject res = new JSONObject();
                res.put("error", error);
                res.put("statusCode", statusCode);

                p.send(res.toString(), correlationId, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
