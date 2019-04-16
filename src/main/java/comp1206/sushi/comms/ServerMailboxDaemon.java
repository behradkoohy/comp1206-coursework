package comp1206.sushi.comms;

import comp1206.sushi.common.Model;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.User;
import comp1206.sushi.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerMailboxDaemon implements Runnable{
    Server server;

    final String PATH_TO_FILE = "communication-files/mailbox/";
    File file;

    public ServerMailboxDaemon(Server server){
        System.out.println("Start of mailbox daemon");
        this.server = server;
        file = new File(PATH_TO_FILE);
        if (file.listFiles() != null){
            for (File f : file.listFiles()){
                f.delete();
            }
        }
    }

    @Override
    public void run() {
        while (true){
            if (!server.resetting){
                readMessages();
            }
        }

    }

    public synchronized void readMessages(){
        File mailbox = new File(PATH_TO_FILE);
        if (mailbox.listFiles() != null && mailbox.listFiles().length > 0) {
            boolean successful = false;
            while (!successful) {
                System.out.println(mailbox.listFiles().length);
                File toBeRead = mailbox.listFiles()[0];
                Object readFile = null;
                try {
                    FileInputStream fileInputStream = new FileInputStream(toBeRead);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    readFile = objectInputStream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("interrupted");
                }
                try {
                    Object fromFile = ((ArrayList) readFile).get(0);
                    // TODO: this throws an NPE sometimes
//                 TODO: Find out why and produce a fix for it

                    if (fromFile instanceof User) {
                        User newUser = (User) fromFile;
                        server.addUser(newUser.getName(), newUser.getPassword(), newUser.getAddress(), newUser.getPostcode());
                        successful = true;
                    } else if (fromFile instanceof Order) {
                        Order newOrder = (Order) fromFile;
                        server.addOrder(newOrder);
                        successful = true;
                    } else if (fromFile instanceof CancelOrder) {
                        System.out.println("Order cancelled");
                        CancelOrder cancelOrder = (CancelOrder) fromFile;
                        server.cancelOrder(cancelOrder);
                        successful = true;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (successful){
                    toBeRead.delete();
                }
            }
        }

    }
}
