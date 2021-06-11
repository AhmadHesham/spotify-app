package netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ChatServerInitializer extends ChannelInitializer<Channel> {
//    private final ChannelGroup group;

//    public ChatServerInitializer(ChannelGroup group){
//        this.group = group;
//    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new ChatRequestHandler("/chat"));
    }
}
