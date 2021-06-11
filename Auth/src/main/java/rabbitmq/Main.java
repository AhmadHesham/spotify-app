package rabbitmq;

import netty.NettyHTTPServer;
import api.runners.AuthMain;

public class Main {




    public static void main(String[] args) throws  Exception{
        AuthMain.main(null);
        NettyHTTPServer.main(null);

    }
}