package comp1206.sushi.comms;

import comp1206.sushi.common.Order;

import java.io.Serializable;

public class CancelOrder implements Serializable {
    Order order;

    public CancelOrder(Order order){
        this.order = order;
    }

    public CancelOrder(){}

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
