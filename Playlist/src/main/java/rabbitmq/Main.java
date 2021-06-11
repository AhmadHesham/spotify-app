package rabbitmq;

import api.runners.PlaylistMain;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception{
        PlaylistMain.main(null);
        NettyHTTPServer.main(null);

    }
}