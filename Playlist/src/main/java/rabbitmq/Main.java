package rabbitmq;

import api.commands.CommandsMap;
import api.runners.PlaylistMain;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception {
        Thread.sleep(30000);
        PlaylistMain.main(null);
//        CommandsMap.initialize();
        NettyHTTPServer.main(null);
    }
}