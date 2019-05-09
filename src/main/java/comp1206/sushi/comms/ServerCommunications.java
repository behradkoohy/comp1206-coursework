package comp1206.sushi.comms;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import comp1206.sushi.common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerCommunications {
    Server kryonetServer;
    comp1206.sushi.server.Server server;
    public ServerCommunications(comp1206.sushi.server.Server server){
        kryonetServer = new Server(16384,16384);
        kryonetServer.start();

        Kryo kryo = kryonetServer.getKryo();
        kryo.register(String.class);
        kryo.register(RequestUsers.class);
        kryo.register(ArrayList.class);
        kryo.register(User.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(Postcode.class);
        kryo.register(RequestPostcodes.class);
        kryo.register(Dish.class);
        kryo.register(Ingredient.class);
        kryo.register(Supplier.class);
        kryo.register(RequestDishes.class);
        kryo.register(Order.class);
        kryo.register(CancelOrder.class);
        kryo.register(RemoveDish.class);
        kryo.register(ResetServer.class);
        kryo.register(OrderedDelivered.class);
        kryo.register(RequestOrders.class);

        try {
            kryonetServer.bind(54555,54777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kryonetServer.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof RequestUsers) {
                    connection.sendTCP(server.getUsers());
                } else if (object instanceof User){
                    User newUser = (User) object;
                    server.addUser(newUser.getName(), newUser.getPassword(), newUser.getAddress(), newUser.getPostcode());
                } else if (object instanceof RequestPostcodes){
                    connection.sendTCP(server.getPostcodes());
                } else if (object instanceof RequestDishes){
                    connection.sendTCP(server.getDishes());
                } else if (object instanceof Order){
                    Order newOrder = (Order) object;
                    server.addOrder(newOrder);
                } else if (object instanceof CancelOrder){
                    server.cancelOrder((CancelOrder) object);
                } else if (object instanceof RequestOrders){
                    ArrayList<Order> userOrders = new ArrayList<>();
                    for (Order o : server.getOrders()){
                        if (o.getUser().getName().equals(((RequestOrders) object).getUserName())){
                            userOrders.add(o);
                        }
                    }
                    connection.sendTCP(userOrders);
                }
            }
        });
    }

    public void sendMessageToAll(Dish dish){
        System.out.println(dish);
        if (kryonetServer.getConnections() != null && dish != null){
            kryonetServer.sendToAllTCP(dish);
        }
    }

    public void sendMessageToAdd(RemoveDish removeDish){
        if (kryonetServer.getConnections() != null && removeDish != null){
            kryonetServer.sendToAllTCP(removeDish);
        }
    }

    public void sendMessageToAdd(ResetServer resetServer){
        if (kryonetServer.getConnections() != null && resetServer != null){
            kryonetServer.sendToAllTCP(resetServer);
        }
    }

    public void sendMessageToAll(OrderedDelivered orderedDelivered){
        if (kryonetServer.getConnections() != null && orderedDelivered != null){
            kryonetServer.sendToAllTCP(orderedDelivered);
        }
    }

}
