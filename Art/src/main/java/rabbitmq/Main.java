package rabbitmq;

import api.runners.*;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception{
        AccountMain.main(null);
        ArtMain.main(null);
        AuthMain.main(null);
        PlaylistMain.main(null);
        ChatMain.main(null);
        NettyHTTPServer.main(null);

    }
}