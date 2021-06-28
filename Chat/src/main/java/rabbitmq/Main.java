package rabbitmq;

import api.runners.ChatMain;
import netty.NettyHTTPServer;

public class Main {

    public static void main(String[] args) throws Exception {
         Thread.sleep(30000);
        // CommandsMap.initialize();
        ChatMain.main(null);
        NettyHTTPServer.main(null);

    }
}