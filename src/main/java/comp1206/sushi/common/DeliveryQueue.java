package comp1206.sushi.common;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DeliveryQueue {
    ConcurrentLinkedQueue<Order> deliveryQueue;

    public DeliveryQueue(){
        deliveryQueue = new ConcurrentLinkedQueue();
    }

    public void addToQueue(Order order){
        deliveryQueue.add(order);
    }

    public void cancelOrder(Order order){
        if (deliveryQueue.contains(order)){
            deliveryQueue.remove(order);
        }
    }

    public Order getDeliverable(){
        return deliveryQueue.poll();
    }

    public Order peekDeliverable(){
        return deliveryQueue.peek();
    }

}
