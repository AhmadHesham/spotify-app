package netty.chat;

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

public class HandleChatNotification extends SimpleChannelInboundHandler<Object> {
    ExecutorService executorService = Executors.newCachedThreadPool();
    String responseBody;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        HashMap<String, String> data = (HashMap<String, String>) msg;
        RabbitMQConfig config = new RabbitMQConfig(data.get("queue") + "-IN");
        JSONObject request = new JSONObject(data);
        String correlationID = UUID.randomUUID().toString();
        Producer p = new Producer(config);
        p.send(request.toString(), correlationID, null);
        ChatNotifier notifier = new ChatNotifier(correlationID, data.get("queue"));
        Future future = executorService.submit(notifier);
        this.responseBody = (String) future.get();
        JSONObject response = new JSONObject(responseBody);
        System.out.println(response);
    }
}
