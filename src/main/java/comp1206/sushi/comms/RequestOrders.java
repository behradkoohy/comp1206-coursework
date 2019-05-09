package comp1206.sushi.comms;

public class RequestOrders {
    String userName;

    public RequestOrders(){}

    public RequestOrders(String name){
        this.userName = name;
    }

    public String getUserName() {
        return userName;
    }
}
