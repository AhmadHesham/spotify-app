package rabbitmq;

import api.commands.CommandsMap;
import controller.CommandsMapController;
import db.RunningDBScript;
import netty.NettyHTTPServer;
import api.runners.AuthMain;

public class Main {




    public static void main(String[] args) throws  Exception{
        Thread.sleep(30000);
//        RunningDBScript.main(null);
        CommandsMap.initialize();
        CommandsMapController.initialize();
        AuthMain.main(null);
        NettyHTTPServer.main(null);

    }
}