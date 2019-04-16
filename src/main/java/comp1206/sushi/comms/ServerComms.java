package comp1206.sushi.comms;

import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerComms {
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

    public ServerComms(Server server){
        restaurant = server.getRestaurant();
        dishes = server.getDishes();
        orders = server.getOrders();
        users = server.getUsers();
        postcodes = server.getPostcodes();
        System.out.println(postcodes);

        this.server = server;

        List<List<? extends Model>> serverDetails = Arrays.asList(dishes, orders, users, postcodes);


        try {
            File file = new File(PATH_TO_FILE);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serverDetails);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("Finished Writing");
        } catch (IOException e){
            e.printStackTrace();
        }

        ServerMailboxDaemon serverMailboxDaemon = new ServerMailboxDaemon(this.server);
        Thread serverMailboxReaderDaemon = new Thread(serverMailboxDaemon);
        serverMailboxReaderDaemon.start();
    }

    public synchronized void writeToFile() {
        if (!server.resetting) {
            restaurant = server.getRestaurant();
            dishes = server.getDishes();
            orders = server.getOrders();
            users = server.getUsers();
            postcodes = server.getPostcodes();
            System.out.println(postcodes);

            this.server = server;

            List<List<? extends Model>> serverDetails = Arrays.asList(dishes, orders, users, postcodes);


            try {
                File file = new File(PATH_TO_FILE);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(serverDetails);
                objectOutputStream.flush();
                objectOutputStream.close();
                System.out.println("Finished Writing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
