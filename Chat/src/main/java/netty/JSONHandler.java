package netty;

import config.CONSTANTS;
import config.STATUSCODES;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import rabbitmq.Producer;
import rabbitmq.RabbitMQConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class JSONHandler extends SimpleChannelInboundHandler<Object> {

    ExecutorService executorService = Executors.newCachedThreadPool();
    String responseBody;
    private String correlationId;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        try {
           JSONObject jsonObject = (JSONObject) o;
            String queueName = jsonObject.getString("queue");

            if (!Arrays.asList(CONSTANTS.QUEUES).contains(queueName)) {
                JSONObject error404 = new JSONObject();
                error404.put("error", "bad request");
                error404.put("easter_egg", "https://res.cloudinary.com/spotify-scalable/image/upload/v1620764323/memes/eh_el_kalam_da_w4jonr.jpg");
                error404.put("statusCode", "404");
                this.responseBody = error404.toString();
            } else if (jsonObject.has("error")) {
                JSONObject error = new JSONObject();
                error.put("error", "Not authenticated");
                error.put("statusCode", STATUSCODES.AUTHENTICATION);

                this.responseBody = error.toString();
            } else {
                correlationId = UUID.randomUUID().toString();
//        System.out.println(jsonObject.toString());

                QueueNotifier notifier = new QueueNotifier(this, queueName);

                sendToQueue(jsonObject.toString(), queueName);


                Future future = executorService.submit(notifier);

                this.responseBody = (String) future.get();
                System.out.println(responseBody);


            }

            JSONObject json = new JSONObject(getResponseBody());
            HttpResponseStatus status = null;

            status = new HttpResponseStatus(Integer.parseInt((String) json
                    .get("statusCode")),
                    !json.has("error") ? "Ok" : json.get("error").toString());
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8)));
            response.headers().set("Content-Type", "application/json");
//            response.headers().set("Content-Length", response.content().readableBytes());

            channelHandlerContext.write(response);
        } catch (Exception e) {
            e.printStackTrace();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    new HttpResponseStatus(500, "Something went wrong"), Unpooled.wrappedBuffer(Unpooled.copiedBuffer("SOMETHING WENT WRONG!", CharsetUtil.UTF_8)));
            response.headers().set("Content-Type", "application/json");
//            response.headers().set("Content-Length", response.content().readableBytes());

            channelHandlerContext.write(response);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE); // Empty the buffer and flush the buffer then close the channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendToQueue(String jsonBody, String queue) {
        Producer p = new Producer(new RabbitMQConfig(queue + "-IN"));
        try {

            p.send(jsonBody, correlationId, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getCorrelationId() {
        return correlationId;
    }


}