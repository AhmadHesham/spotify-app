package rabbitmq;

import api.commands.CommandsMap;
import controller.CommandsMapController;
import api.runners.AccountMain;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception{
//        RunningDBScript.main(null);

        Thread.sleep(30000);
        CommandsMap.initialize();
        CommandsMapController.initialize();
        AccountMain.main(null);
        NettyHTTPServer.main(null);

    }
}