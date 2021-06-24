package rabbitmq;

import api.commands.CommandsMap;
import netty.NettyHTTPServer;
import api.runners.AuthMain;

public class Main {




    public static void main(String[] args) throws  Exception{
        Thread.sleep(30000);
        CommandsMap.initialize();
        AuthMain.main(null);
        NettyHTTPServer.main(null);

    }
}