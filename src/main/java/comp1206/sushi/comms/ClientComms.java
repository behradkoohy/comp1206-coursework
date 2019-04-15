package comp1206.sushi.comms;

import comp1206.sushi.client.Client;
import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ClientComms {
    Client client;



    String serverSetupFileHash;

    Restaurant restaurant;
    List<Dish> dishes;
    List<Drone> drones;
    List<Ingredient> ingredients;
    List<Order> orders;
    List<Staff> staff;
    List<Supplier> suppliers;
    List<User> users;
    List<Postcode> postcodes;

    Server server;

    final String PATH_TO_FILE = "communication-files/serverstate";

    public ClientComms(Client client){
        this.client = client;



    }

    public void initalFileRead(){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            serverSetupFileHash = getFileChecksum(messageDigest, new File(PATH_TO_FILE));
            System.out.println(serverSetupFileHash);

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        List<List<? extends Model>> fileContents = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(PATH_TO_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            fileContents = (List<List<? extends Model>>) objectInputStream.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
        for (Object o : fileContents){
            if (((ArrayList) o).size() > 0){
                ArrayList<Object> dataColumn = (ArrayList) o;
                if (dataColumn.get(0) instanceof Postcode){
                    ArrayList<Postcode> newPostcodes = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newPostcodes.add((Postcode) obj);
                    }
                    client.postcodes = newPostcodes;
                } else if (dataColumn.get(0) instanceof Dish){
                    ArrayList<Dish> newDishes = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newDishes.add((Dish) obj);
                    }
                    client.dishes = newDishes;
                    client.notifyUpdate();
                } else if (dataColumn.get(0) instanceof Order){
                    ArrayList<Order> newOrders = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newOrders.add((Order) obj);
                    }
                    client.orders = newOrders;
                } else if (dataColumn.get(0) instanceof User){
                    ArrayList<User> newUsers = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newUsers.add((User) obj);
                    }
                    client.users = newUsers;
                }
            }
        }
        Thread UpdaterDaemon = new Thread(new ClientUpdaterDaemon(client, this));
//        ClientUpdaterDaemon clientUpdaterDaemon = new ClientUpdaterDaemon(client, this);
        UpdaterDaemon.start();

    }

    public void mainstreamDataRead(){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            serverSetupFileHash = getFileChecksum(messageDigest, new File(PATH_TO_FILE));

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        List<List<? extends Model>> fileContents = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(PATH_TO_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            fileContents = (List<List<? extends Model>>) objectInputStream.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
        for (Object o : fileContents){
            if (((ArrayList) o).size() > 0){
                ArrayList<Object> dataColumn = (ArrayList) o;
                if (dataColumn.get(0) instanceof Postcode){
                    ArrayList<Postcode> newPostcodes = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newPostcodes.add((Postcode) obj);
                    }
                    client.postcodes = newPostcodes;
                } else if (dataColumn.get(0) instanceof Dish){
                    ArrayList<Dish> newDishes = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newDishes.add((Dish) obj);
                    }
                    client.dishes = newDishes;
                } else if (dataColumn.get(0) instanceof Order){
                    ArrayList<Order> newOrders = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newOrders.add((Order) obj);
                    }
                    client.orders = newOrders;
                } else if (dataColumn.get(0) instanceof User){
                    ArrayList<User> newUsers = new ArrayList<>();
                    for (Object obj : dataColumn){
                        newUsers.add((User) obj);
                    }
                    client.users = newUsers;
                }

            }
        }
    }



    // TODO: REWRITE THIS
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }


    public String getServerSetupFileHash() {
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            serverSetupFileHash = getFileChecksum(messageDigest, new File(PATH_TO_FILE));

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return serverSetupFileHash;
    }

    public void setServerSetupFileHash(String serverSetupFileHash) {
        this.serverSetupFileHash = serverSetupFileHash;
    }

}
