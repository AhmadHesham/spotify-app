package netty.chat;

import config.STATUSCODES;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;
import rabbitmq.Producer;
import rabbitmq.RabbitMQConfig;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HandleChatLogging extends SimpleChannelInboundHandler<Object> {
    ExecutorService executorService = Executors.newCachedThreadPool();
    String responseBody;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("hoa f eh?");
        HashMap<String, String> msgMap = (HashMap<String ,String>) msg;
        HashMap<String, String> request = new HashMap<String, String>();
        String queueName = "chat";
        String correlationID = UUID.randomUUID().toString();
        request.put("queue", queueName);
        request.put("method", "log-text");
        request.put("senderID", msgMap.get("senderID"));
        request.put("senderName", msgMap.get("senderName"));
        request.put("chatID", msgMap.get("chatID").split("=")[1]);
        request.put("text", msgMap.get("text"));
        request.put("logToken", msgMap.get("token"));
        request.put("correlation_id", correlationID);
        request.put("token_type", msgMap.get("token_type"));

        JSONObject requestJson = new JSONObject(request);
        RabbitMQConfig config = new RabbitMQConfig(queueName + "-IN");
        Producer p = new Producer(config);
        ChatNotifier notifier = new ChatNotifier(correlationID, queueName);
        p.send(requestJson.toString(), correlationID, null);

        Future future = executorService.submit(notifier);
        this.responseBody = (String) future.get();
        JSONObject response = new JSONObject(responseBody);

        HashMap<String, String> notification = new HashMap<>();
        notification.put("senderID", msgMap.get("senderID"));
        notification.put("chatID", msgMap.get("chatID"));
        notification.put("text", msgMap.get("text"));
        notification.put("senderName", msgMap.get("senderName"));
        notification.put("queue", queueName);
        notification.put("method", "send-chat-notification");
        notification.put("logToken", msgMap.get("token"));
        notification.put("token_type", msgMap.get("token_type"));
//        System.out.println("Chat Response: " + response);

        if(response.get("statusCode").equals(STATUSCODES.SUCCESS)) {
//            System.out.println("what is dis brother");
            ctx.fireChannelRead(notification);
        }
    }
}
