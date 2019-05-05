package comp1206.sushi.persistance;


import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Persistance implements Runnable {
    Server server;

    public List<Dish> dishes = new ArrayList<Dish>();
    public List<Ingredient> ingredients = new ArrayList<Ingredient>();
    public List<Order> orders = new ArrayList<Order>();
    public List<Supplier> suppliers = new ArrayList<Supplier>();
    public List<User> users = new ArrayList<User>();
    public List<Postcode> postcodes = new ArrayList<Postcode>();
    public List<SafeSendStaff> safeStaff = new ArrayList<>();
    public List<SafeSendDrone> safeDrone = new ArrayList<>();

    public final String persistanceFile = "persistance.server";
    File persistance;

    public volatile boolean running = false;

    public Persistance(Server server) {
        running = true;
        persistance = new File(persistanceFile);
        this.server = server;
        dishes = server.getDishes();
        ingredients = server.getIngredients();
        orders = server.getOrders();
        suppliers = server.getSuppliers();
        users = server.getUsers();
        postcodes = server.getPostcodes();

        for (Staff s : this.server.getStaff()){
            safeStaff.add(new SafeSendStaff(s));
        }
        for (Drone d : this.server.getDrones()){
            safeDrone.add(new SafeSendDrone(d));
        }

        if (persistance.exists()){
            System.out.println("FILE EXISTS");

            



        } else {
            ObjectOutputStream objectOutputStream;
            FileOutputStream fileOutputStream;
            try {
                persistance.createNewFile();
                fileOutputStream = new FileOutputStream(persistance);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);

                ArrayList<Object> serverContents = new ArrayList<>(Arrays.asList(dishes, ingredients, orders, suppliers, users, postcodes, safeDrone, safeStaff));
                objectOutputStream.writeObject(serverContents);
                System.out.println("Done");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void terminate(){

    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        try {
            FileOutputStream fileOutputStream;
            fileOutputStream = new FileOutputStream(persistance);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);


            System.out.println("Done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;
        while (!server.resetting && running) {
            // Sleep for 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // get the listd
            dishes = server.getDishes();
            ingredients = server.getIngredients();
            orders = server.getOrders();
            suppliers = server.getSuppliers();
            users = server.getUsers();
            postcodes = server.getPostcodes();
            // Rebuild the staff and drones
            safeStaff = new ArrayList<>();
            safeDrone = new ArrayList<>();
            for (Staff s : this.server.getStaff()){
                safeStaff.add(new SafeSendStaff(s));
            }
            for (Drone d : this.server.getDrones()){
                safeDrone.add(new SafeSendDrone(d));
            }
            ArrayList<Object> serverContents = new ArrayList<>(Arrays.asList(dishes, ingredients, orders, suppliers, users, postcodes, safeDrone, safeStaff));
            try {
                objectOutputStream.writeObject(serverContents);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
