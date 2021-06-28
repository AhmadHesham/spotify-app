package rabbitmq;

import api.commands.CommandsMap;
import api.runners.*;
import controller.CommandsMapController;
import netty.NettyHTTPServer;

public class Main {

    static long start;

    public static long elapsed(){
        return start;
    }

    public static void main(String[] args) throws  Exception{
        start = System.currentTimeMillis();
//        AccountMain.main(null);
        Thread.sleep(30000);
        ArtMain.main(null);
        CommandsMap.initialize();
        CommandsMapController.initialize();
//        AuthMain.main(null);
//        PlaylistMain.main(null);
//        ChatMain.main(null);
        NettyHTTPServer.main(null);

    }
}