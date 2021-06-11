package netty.chat;

import auth.JWT;
import com.arangodb.entity.BaseDocument;
import db.ArangoConfig;
import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.ArrayList;

public class ChatRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;

    public ChatRequestHandler(String wsUri){
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String[] msgSplit = msg.uri().split("\\?");
        String uri = msgSplit[1];
//        System.out.println("URI: " + uri);
        if(uri.toLowerCase().contains(wsUri.toLowerCase().split("/")[1])){
            String token = msgSplit[2].split("=")[1];
            String registrationToken = msgSplit[3].split("=")[1];
            Claims claims = JWT.decodeJWT(token);
//            System.out.println(claims);

            try{
                BaseDocument chat = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(uri.split("_")[1], BaseDocument.class);
                ArrayList<String> participants = (ArrayList<String>) chat.getAttribute("participants");
                if(participants.contains(claims.get("token_user_id")) && claims.get("token_type").equals("user")) {
                    ChannelGroup group;
                    if(LinkMap.chatToGroupMap.containsKey(uri)) {
                        group = LinkMap.chatToGroupMap.get(uri);
                    }
                    else{
                        group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
                        LinkMap.chatToGroupMap.put(uri, group);
                        LinkMap.groupToChatMap.put(group, uri);
                    }

//                    if(!LinkMap.userToRegistrationToken.containsKey(claims.get("token_user_id"))){
//                        LinkMap.userToRegistrationToken.put((String)claims.get("token_user_id"), registrationToken);
//                        System.out.println(LinkMap.userToRegistrationToken.toString());
//                    }

                    ctx.pipeline().addLast(new WebSocketServerProtocolHandler(msg.uri()));
                    ctx.pipeline().addLast(new TextWebSocketFrameHandler(group, claims, token));
                    ctx.pipeline().addLast(new HandleChatLogging());
                    ctx.pipeline().addLast(new HandleChatNotification());
                    ctx.fireChannelRead(msg.retain());
                }
                else{
                    System.out.println("You are not authorized!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
