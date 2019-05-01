package comp1206.sushi.comms;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import comp1206.sushi.common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientCommunications {
    Client kryonetClient;
    comp1206.sushi.client.Client client;
    public ClientCommunications(comp1206.sushi.client.Client client){
        this.client = client;
        kryonetClient = new Client();
        kryonetClient.start();

        Kryo kryo = kryonetClient.getKryo();
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

        try {
            kryonetClient.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kryonetClient.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof List) {
                    if (((ArrayList) object).size() > 0){
                        ArrayList recievedList = (ArrayList) object;
                        if (recievedList.get(0) instanceof User){
                            client.users = recievedList;
                        } else if (recievedList.get(0) instanceof Postcode){
                            client.postcodes = recievedList;
                        } else if (recievedList.get(0) instanceof Dish){
                            client.dishes = recievedList;
                        }
                    }
                }
                if (object instanceof Dish) {
                    client.addDish((Dish)(object));
                }
                if (object instanceof RemoveDish){
                    client.removeDish(((RemoveDish) object).getDish());
                }
                if (object instanceof ResetServer){
                    client.resetServerSignal();
                }
            }
        });
    }

    public void sendMessage(Object object){
        kryonetClient.sendTCP(object);
    }

    public void recieveMessage(){
        kryonetClient.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof List) {
                    if (((ArrayList) object).size() > 0){
                        ArrayList recievedList = (ArrayList) object;
                        if (recievedList.get(0) instanceof User){
                            client.users = recievedList;
                        } else if (recievedList.get(0) instanceof Postcode){
                            client.postcodes = recievedList;
                        } else if (recievedList.get(0) instanceof Dish){
                            client.dishes = recievedList;
                        }
                    }
                }
            }
        });
    }
}
