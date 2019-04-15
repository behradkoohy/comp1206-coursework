package comp1206.sushi.comms;

import comp1206.sushi.server.Server;

import java.io.File;

public class ServerMailboxDaemon implements Runnable{
    Server server;

    final String PATH_TO_FILE = "communication-files/serverstate/mailbox";
    File file;

    public ServerMailboxDaemon(Server server){
        this.server = server;
        file = new File(PATH_TO_FILE);
    }

    @Override
    public void run() {

    }
}
