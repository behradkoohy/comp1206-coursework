package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.*;

@Deprecated
public class DishStockChecker {
    Server server;
    Map<Dish, Number> dishStock;
    List<Dish> dishesToBeRestocked;

    public DishStockChecker(Server server){
        this.server = server;
        dishStock = server.getDishStockLevels();

    }

    public synchronized List<Dish> checkStockLevels(){
        dishesToBeRestocked = new ArrayList<>();
        dishStock = server.getDishStockLevels();
        for (Dish d : dishStock.keySet()){
//            System.out.println(d.getName() + " " + dishStock.get(d).doubleValue() + " " + d.getRestockThreshold().doubleValue());
            if (dishStock.get(d).doubleValue() < d.getRestockThreshold().doubleValue()){
                dishesToBeRestocked.add(d);
            } else {
//                System.out.println(d.getName() + " is fully stocked");
            }
        }
        return getDishesToBeRestocked();
    }

    public List<Dish> getDishesToBeRestocked() {
        return dishesToBeRestocked;
    }

}
