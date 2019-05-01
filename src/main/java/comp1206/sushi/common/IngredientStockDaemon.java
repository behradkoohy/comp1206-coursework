package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.ConcurrentModificationException;
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

    public int getQueueSize(){
        return ingredientRestockQueue.size();
    }

    public void resetSignal(){
        ingredientRestockQueue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void buildQueue(){
        CopyOnWriteArrayList<Ingredient> ingredients;
        synchronized (this.server.ingredients){
            ingredients = new CopyOnWriteArrayList<>(this.server.getIngredients());
        }
        if (!server.resetting){
            Iterator<Ingredient> ingredientIterator = ingredients.iterator();
            while (ingredientIterator.hasNext()){
                Ingredient nextIngredient = ingredientIterator.next();
                Number ingredientRT = nextIngredient.getRestockThreshold();
                Number ingredientRA = nextIngredient.getRestockAmount();
                Iterator<Drone> droneIterator = this.server.getDrones().iterator();
                int timesBeingDelivered = 0;
                while (droneIterator.hasNext()){
                    Drone nextDrone = droneIterator.next();
                    synchronized (nextDrone) {
                        if (nextDrone.getIngredientBeingDelivered() != null && nextDrone.getIngredientBeingDelivered().equals(nextIngredient)) {
                            timesBeingDelivered += 1;
                        }
                    }
                }
                if (!ingredientRestockQueue.contains(nextIngredient) && timesBeingDelivered == 0){
                    for (int x = 0; x < (Math.ceil(ingredientRT.doubleValue()/ingredientRA.doubleValue())); x++){
                        ingredientRestockQueue.add(nextIngredient);
                    }
                } else {
                    int timesInQueue = 0;
                    Iterator<Ingredient> ingredientsInQueue = ingredientRestockQueue.iterator();
                    while (ingredientsInQueue.hasNext()){
                        Ingredient nextQueueIngredient = ingredientsInQueue.next();
                        if (nextQueueIngredient.equals(nextIngredient)){
                            timesInQueue += 1;
                        }
                    }
                    double numberNeeded = (ingredientRA.doubleValue() - ((timesInQueue*ingredientRA.doubleValue()) + (timesBeingDelivered*ingredientRA.doubleValue())));
                    for (int x = 0; x < (Math.ceil(numberNeeded/ingredientRA.doubleValue())); x++){
                        ingredientRestockQueue.add(nextIngredient);
                    }
                }
            }

        }
    }


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
