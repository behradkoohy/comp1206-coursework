package comp1206.sushi.comms;

import comp1206.sushi.client.Client;

public class ClientUpdaterDaemon implements Runnable {
    String serverFileCheckSum;
    Client client;
    ClientComms clientComms;

    public ClientUpdaterDaemon(Client client, ClientComms clientComms){
        this.client = client;
        this.clientComms = clientComms;
        serverFileCheckSum = clientComms.getServerSetupFileHash();
    }


    @Override
    public void run() {
        while (true){
            if (!serverFileCheckSum.equals(clientComms.getServerSetupFileHash())){
                serverFileCheckSum = clientComms.getServerSetupFileHash();
                System.out.println("RELOAD");
                clientComms.mainstreamDataRead();
            }
        }
    }
}
