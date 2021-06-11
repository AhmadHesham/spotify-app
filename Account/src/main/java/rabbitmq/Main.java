package rabbitmq;

import api.runners.AccountMain;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception{
        AccountMain.main(null);
        NettyHTTPServer.main(null);

    }
}