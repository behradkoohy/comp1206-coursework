package comp1206.sushi.comms;

import comp1206.sushi.common.Order;

public class CancelOrder {

    public Order order;

    public CancelOrder(){}

    public CancelOrder(Order order){
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
