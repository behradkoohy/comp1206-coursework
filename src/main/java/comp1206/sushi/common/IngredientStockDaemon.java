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
        CopyOnWriteArrayList<Ingredient> ingredients = new CopyOnWriteArrayList<Ingredient>(this.server.getIngredients());
        if (!server.resetting){
            Iterator<Ingredient> ingredientIterator = ingredients.iterator();
            while (ingredientIterator.hasNext()){
                Ingredient i = ingredientIterator.next();
                int accountedFor = 0;
                if (this.server.getStaff() != null){
                    synchronized (this.server.getStaff()){
                        // TODO: work out if drones are getting the ingredients already
//                        for (Staff s: this.server.getStaff()){
//                            if (!(s.getBeingMadeDish() == null) && s.getBeingMadeDish().equals(i)){
//                                accountedFor += 1;
//                            }
//                        }
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
    @Override
    public void run() {

    }
}
