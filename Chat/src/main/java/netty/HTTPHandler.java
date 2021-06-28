package netty;

import auth.JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HTTPHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private String requestBody;
    private long correlationId;
    volatile String responseBody;
    ExecutorService executorService = Executors.newCachedThreadPool();
    private Claims claims;
    private String requestName;
    private String queueName;

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            Claims verifiedToken = null;
            requestName = request.uri().split("/")[2];
            queueName = request.uri().split("/")[1];
            System.out.println(requestName);
            if (!requestName.equals("sign-in") && !requestName.equals("create-account")) {
                try {
                    if (!request.headers().contains("token") || request.headers().get("token").equals("")) {
                        throw new JwtException("token not defined");
                    }
                    verifiedToken = JWT.decodeJWT(request.headers().get("token"));
                } catch (JwtException e) {
                    verifiedToken = new DefaultClaims();
                    verifiedToken.put("error", e.getMessage());
                    verifiedToken.put("queue", queueName);

                }

                this.claims = verifiedToken;
            }

            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();

            JSONObject json = new JSONObject(content.copy().toString(CharsetUtil.UTF_8));
            if (claims != null) {
                Map<String, Object> claimsMap = new HashMap<>(claims);
                Iterator it = claimsMap.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    json.put((String) pairs.getKey(), pairs.getValue());
                }
            }
            json.put("method", requestName);
            json.put("queue", queueName);

            ctx.fireChannelRead(json);
        }
        if (msg instanceof LastHttpContent) {
            // LastHttpContent trailer = (LastHttpContent) msg;
            HttpObject trailer = (HttpObject) msg;
            // writeresponse(trailer, ctx);
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        System.out.println("tab ehhhhhhhhh");
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
