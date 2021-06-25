package rabbitmq;

import api.commands.CommandsMap;
import api.runners.AccountMain;
import db.RunningDBScript;
import netty.NettyHTTPServer;

public class Main {




    public static void main(String[] args) throws  Exception{
//        RunningDBScript.main(null);
        Thread.sleep(30000);
        CommandsMap.initialize();
        AccountMain.main(null);
        NettyHTTPServer.main(null);

    }
}