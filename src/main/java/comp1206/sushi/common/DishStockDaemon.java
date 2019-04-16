package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DishStockDaemon implements Runnable {
    Server server;

    Map<Dish, Number> dishStock;
    volatile ConcurrentLinkedQueue<Dish> dishRestockQueue = new ConcurrentLinkedQueue<>();

    public DishStockDaemon(Server server){
        this.server = server;
        dishStock = server.getDishStockLevels();

    }

    public boolean queueEmpty(){
        return dishRestockQueue.size() == 0 || dishRestockQueue.peek() == null;
    }

    public void resetSignal(){
        dishRestockQueue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void buildQueue(){
        CopyOnWriteArrayList<Dish> dishes = new CopyOnWriteArrayList<>(this.server.getDishes());
        if (!server.resetting){
            Iterator<Dish> dishesIterator = dishes.iterator();
            while (dishesIterator.hasNext()){
                Dish d = dishesIterator.next();
                int accountedFor = 0;
                if (this.server.getStaff() != null){
                    synchronized (this.server.getStaff()){
                        for (Staff s: this.server.getStaff()){
                            if (!(s.getBeingMadeDish() == null) && s.getBeingMadeDish().equals(d)){
                                accountedFor += 1;
                            }
                        }
                        for (Dish dish : dishRestockQueue){
                            if (dish.equals(d)){
                                accountedFor += 1;
                            }
                        }
                        int stockD = accountedFor + d.getStock().intValue();
                        if (stockD < d.getRestockThreshold().intValue()){
                            for (int x = 0; x < d.getRestockThreshold().intValue() - stockD; x++){
                                dishRestockQueue.add(d);
//                                System.out.println(d.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized Dish getTopOfQueue(){
        return dishRestockQueue.remove();
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
