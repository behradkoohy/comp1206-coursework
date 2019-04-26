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
        kryonetServer = new Server();
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
                }
            }
        });
    }

    public void sendMessageToAll(Dish dish){
        System.out.println(dish);
        System.out.println("SENDING DISHES");
        if (kryonetServer.getConnections() != null && dish != null){
            kryonetServer.sendToAllTCP(dish);
        }
    }

}
