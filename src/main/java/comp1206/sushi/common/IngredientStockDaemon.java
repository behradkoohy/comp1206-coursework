package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class IngredientStockDaemon implements Runnable {

    Server server;
    ConcurrentLinkedQueue<Ingredient> ingredientRestockQueue = new ConcurrentLinkedQueue<>();


    public IngredientStockDaemon(Server server){
        this.server = server;

    }

    @Override
    public void run() {

    }
}
