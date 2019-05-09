package comp1206.sushi.persistance;


import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataPersistance implements Runnable {
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
    File persistance = new File(persistanceFile);

    public volatile boolean running = false;
    public volatile boolean paused = false;

    public DataPersistance(Server server) {
        this.paused = true;
        this.server = server;
        if (persistance.exists()){
            System.out.println("FILE EXISTS");
            try {
                FileInputStream fileInputStream = new FileInputStream(persistance);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Object fileContents = objectInputStream.readObject();
                if (fileContents != null){
                    ArrayList<Object> persistantData = (ArrayList<Object>) fileContents;
                    this.server.resetServer();
                    this.server.postcodes = (ArrayList<Postcode>) persistantData.get(0);
                    this.server.ingredients = (ArrayList<Ingredient>) persistantData.get(1);
                    this.server.dishes = (ArrayList<Dish>) persistantData.get(2);
                    this.server.users = (ArrayList<User>) persistantData.get(3);
                    this.server.orders = (ArrayList<Order>) persistantData.get(4);
                    this.server.suppliers = (ArrayList<Supplier>) persistantData.get(5);
                    for (SafeSendDrone d : (ArrayList<SafeSendDrone>) persistantData.get(6)){
                        this.server.addDrone(d.getSpeed(), d.getCapacity(), d.getBattery());
                    }
                    for (SafeSendStaff d : (ArrayList<SafeSendStaff>) persistantData.get(7)){
                        this.server.addStaff(d.getName(), d.getStatus(), d.getFatigue());
                    }
                    SafeSendStock sentStock = (SafeSendStock) persistantData.get(8);
                    this.server.stock = new Stock(this.server, sentStock.dishStock, sentStock.ingredientStock, new ConcurrentLinkedQueue<>());

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        this.paused = false;
    }


    @Override
    public void run() {
        System.out.println("Starting thread");
        System.out.println(running);
        System.out.println(server.resetting);
        System.out.println(paused);
        running = true;
        while (running && !server.resetting){
            if (!paused) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<Object> serverContents = new ArrayList<>();
            serverContents.add(this.server.getPostcodes());
            serverContents.add(this.server.getIngredients());
            serverContents.add(this.server.getDishes());
            serverContents.add(this.server.getUsers());
            serverContents.add(this.server.getOrders());
            serverContents.add(this.server.getSuppliers());

            ArrayList<SafeSendDrone> safeDrones = new ArrayList<>();
            for (Drone d : this.server.getDrones()){
                safeDrones.add(new SafeSendDrone(d));
            }
            ArrayList<SafeSendStaff> safeStaff = new ArrayList<>();
            for (Staff d : this.server.getStaff()){
                safeStaff.add(new SafeSendStaff(d));
            }
            serverContents.add(safeDrones);
            serverContents.add(safeStaff);
            serverContents.add(new SafeSendStock(this.server.stock));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(persistance);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(serverContents);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
