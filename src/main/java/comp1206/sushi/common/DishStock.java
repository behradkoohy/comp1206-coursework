package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DishStock {

    ConcurrentLinkedQueue<Dish> dishesToBeMade = new ConcurrentLinkedQueue();
    Server server;

    public DishStock(Server server){
        this.server = server;
    }

    private synchronized void addToQueue(Dish d){
        dishesToBeMade.add(d);
    }

    public void addNewDish(Dish d){
        System.out.println("adding dish " + d.getName());
        System.out.println(d.getRestockThreshold().intValue() + " " + d.getRestockAmount().intValue());
        for (int x = 0; x < (d.getRestockThreshold().intValue() % d.getRestockAmount().intValue())+1;x++){
            addToQueue(d);
        }
    }

    public void dishOrdered(Order o){
        int stock = 0;
        for (Dish dish : o.getOrderDetails().keySet()){
            for (Dish dishInQueue : dishesToBeMade){
                if (dishInQueue.equals(dish)){
                    stock += dish.getRestockAmount().intValue();
                }
            }
            stock += dish.getStock().intValue();
            if (stock < dish.getRestockThreshold().intValue()){
                addToQueue(dish);
            }
        }
    }

    public void resetSignal() {
        synchronized (this.dishesToBeMade) {
            dishesToBeMade = new ConcurrentLinkedQueue();
        }
    }

    public Dish getTopOfQueue(){
        return dishesToBeMade.poll();
    }

    public void removeDish(Dish d){
        this.dishesToBeMade.remove(d);
    }

    public synchronized boolean isQueueEmpty(){
        return (this.dishesToBeMade.size() == 0);
    }

}
