package netty.chat;

import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.json.JSONObject;

import java.util.HashMap;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;
    private Claims claims;
    private String token;

    public TextWebSocketFrameHandler(ChannelGroup group, Claims claims, String token) {
        this.group = group;
        this.claims = claims;
        this.token = token;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            ctx.pipeline().remove(ChatRequestHandler.class);
//            group.writeAndFlush(new TextWebSocketFrame("Client " +
//                    ctx.channel() + " joined"));
//            group.writeAndFlush(new TextWebSocketFrame(claims.get("token_name") + " is online"));
            group.add(ctx.channel());
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        String senderName = (String)claims.get("token_name");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("text", text);
        jsonResponse.put("senderName", senderName);
        TextWebSocketFrame response = new TextWebSocketFrame(jsonResponse.toString());
//        System.out.println(response);
        group.writeAndFlush(response.retain());
        HashMap<String, String> logRequestInfo = new HashMap<>();
        logRequestInfo.put("chatID", LinkMap.groupToChatMap.get(group));
        logRequestInfo.put("text", msg.text());
        logRequestInfo.put("senderID", (String)claims.get("token_user_id"));
        logRequestInfo.put("senderName", senderName);
        logRequestInfo.put("token", this.token);
        logRequestInfo.put("token_type", (String)claims.get("token_type"));
//        ctx.fireChannelRead(msg.text());
        ctx.fireChannelRead(logRequestInfo);

    }
}
