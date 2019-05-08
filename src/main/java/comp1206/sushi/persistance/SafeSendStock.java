package comp1206.sushi.persistance;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Stock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SafeSendStock implements Serializable {

    Map<Dish, Number> dishStock = new ConcurrentHashMap<>();
    Map<Ingredient, Number> ingredientStock = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<Order> orderQueue;
    List<Dish> dishesBeingMade = new ArrayList<>();
    List<Ingredient> ingredientsBeingMade = new ArrayList<>();

    public SafeSendStock(Stock stock){
        dishStock = stock.getDishStock();
        ingredientStock = stock.getIngredientStock();
        orderQueue = stock.getOrderQueue();
        dishesBeingMade = new ArrayList<>();
        ingredientsBeingMade = new ArrayList<>();
    }

}
