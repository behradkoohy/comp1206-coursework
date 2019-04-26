package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class IngredientStockDaemon implements Runnable {

    Server server;
    Map<Ingredient, Number> ingredientStock;
    volatile ConcurrentLinkedQueue<Ingredient> ingredientRestockQueue = new ConcurrentLinkedQueue<>();


    public IngredientStockDaemon(Server server){
        this.server = server;
        ingredientStock = this.server.getIngredientStockLevels();

    }

    public boolean isQueueEmpty(){
        return ingredientRestockQueue.size() == 0 || ingredientRestockQueue.peek() == null;
    }

    public void resetSignal(){
        ingredientRestockQueue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void buildQueue(){
        CopyOnWriteArrayList<Ingredient> ingredients = new CopyOnWriteArrayList<>(this.server.getIngredients());
        if (!server.resetting){
            Iterator<Ingredient> ingredientIterator = ingredients.iterator();
            while (ingredientIterator.hasNext()){
                Ingredient i = ingredientIterator.next();
                int accountedFor = 0;
                if (this.server.getDrones() != null){
                    synchronized (this.server.getDrones()){
                        // TODO: work out if drones are getting the ingredients already
                        for (Drone d : this.server.getDrones()){
                            synchronized (d){
                                if (d.getIngredientBeingDelivered() != null && d.getIngredientBeingDelivered().equals(i)){
                                    accountedFor += 1;
                                }
                            }
                        }
                        for (Ingredient ingredient : ingredientRestockQueue){
                            if (ingredient.equals(i)){
                                accountedFor += 1;
                            }
                        }
                        int stockD = accountedFor + i.getStock().intValue();
                        if (stockD < i.getRestockThreshold().intValue()){
                            for (int x = 0; x < i.getRestockThreshold().intValue() - stockD; x++){
                                ingredientRestockQueue.add(i);
                            }
                        }
                    }
                }
            }
        }
    }

//    public synchronized void buildQueue(){
//        if (!server.resetting){
//            CopyOnWriteArrayList<Ingredient> ingredients = new CopyOnWriteArrayList<>(server.getIngredients());
//            Iterator<Ingredient> ingredientIterator = ingredients.iterator();
//            while (ingredientIterator.hasNext()){
//                Ingredient i = ingredientIterator.next();
//                int stockDifference = 0;
//                if (server.getDrones() != null) {
//                    for (Drone d : server.getDrones()) {
//                        if (d.getIngredientBeingDelivered() != null && d.getIngredientBeingDelivered().equals(i)) {
//                            stockDifference += i.getRestockAmount().intValue();
//                        }
//                        for (Ingredient ingredientInQueue : ingredientRestockQueue) {
//                            if (ingredientInQueue.equals(i)) {
//                                stockDifference += i.getRestockAmount().intValue();
//                            }
//                        }
//                        stockDifference += i.getStock().intValue();
//                        System.out.println(stockDifference);
//                        System.out.println(i.getRestockThreshold());
//                        for (int n = stockDifference; stockDifference < i.getRestockThreshold().intValue(); ++n) {
//                            ingredientRestockQueue.add(i);
//                            System.out.println(stockDifference);
//                        }
//                    }
//                }
//            }
//        }
//    }

    public synchronized Ingredient getTopOfQueue(){
        return ingredientRestockQueue.remove();
    }

    @Override
    public void run() {
        while (!false) {
            if (!server.resetting) {
                buildQueue();
            }
        }
    }
}
