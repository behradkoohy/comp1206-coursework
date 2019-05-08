package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Stock {

    Server server;

    Map<Dish, Number> dishStock = new ConcurrentHashMap<>();
    Map<Ingredient, Number> ingredientStock = new ConcurrentHashMap<>();

    ConcurrentLinkedQueue<Order> orderQueue;

    List<Dish> dishesBeingMade = new ArrayList<>();

    List<Ingredient> ingredientsBeingMade = new ArrayList<>();
    public Stock(Server server){
        this.server = server;
        synchronized (this.server.orders) {
            this.orderQueue = new ConcurrentLinkedQueue<>(this.server.orders);
        }
    }
    public Stock(Server server,  Map<Dish, Number> dishStock, Map<Ingredient, Number> ingredientStock, ConcurrentLinkedQueue<Order> orderQueue){
        this.server = server;
        this.dishStock = dishStock;
        this.ingredientStock = ingredientStock;
        this.orderQueue = orderQueue;
        this.dishesBeingMade = dishesBeingMade;
    }

    public Order getOrderToDeliver(){
        if (orderQueue.size() > 0){
            Order potentialToBeDelivered = orderQueue.poll();
            boolean canBeMade = true;
            synchronized (this.dishStock) {
                for (Dish d : potentialToBeDelivered.getOrderDetails().keySet()) {
                    Dish dInServer = d;
                    for (Dish serverDishes : this.server.getDishes()){
                        if (serverDishes.getName().equals(d.getName())){
                            dInServer = serverDishes;
                            System.out.println(dInServer);
                        }
                    }

//                    if (dishStock.get(d) == null){
//                        System.out.println(d);
//                        canBeMade = false;
//                        break;
//                    }
                    if (dishStock.get(dInServer).intValue() < potentialToBeDelivered.getOrderDetails().get(d).intValue()) {
                        canBeMade = false;
                        break;
                    }
                }
                if (canBeMade){
                    for (Dish d : potentialToBeDelivered.getOrderDetails().keySet()){
                        Dish serverEquivelantDish = null;
                        for (Dish dInServer : this.server.getDishes()){
                            if (d.getName().equals(dInServer.getName())){
                                serverEquivelantDish = dInServer;
                            }
                        }
                        dishStock.put(serverEquivelantDish, dishStock.get(serverEquivelantDish).intValue() - potentialToBeDelivered.getOrderDetails().get(d).intValue());
                    }
                    System.out.println(potentialToBeDelivered);
                    return potentialToBeDelivered;
                } else {
                    orderQueue.add(potentialToBeDelivered);
                    return null;
                }
            }

        } else {
            return null;
        }
    }

    public void addOrderToQueue(Order order){
        this.orderQueue.add(order);
    }

    public synchronized Ingredient getIngredientToGet(){
        for (Ingredient i : ingredientStock.keySet()){
            if (i.getRestockThreshold().intValue() > (ingredientStock.get(i).intValue() + (Collections.frequency(ingredientsBeingMade, i) * i.getRestockAmount().intValue()))){
                ingredientsBeingMade.add(i);
                return i;
            }
        }
        return null;
    }

    public synchronized boolean canDishBeMade(Dish dish){
        for (Ingredient i : dish.getRecipe().keySet()){
            if (ingredientStock.get(i).intValue() < dish.getRecipe().get(i).intValue()){
                return false;
            }
        }
        return true;
    }

    /**
     * @param dish  this is very powerful, will remove the dish
     *
     * */
    public synchronized void removeDishFromStock(Dish dish){
        synchronized (this.dishStock){
            this.dishStock.remove(dish);
        }
    }

    public synchronized Dish getDishToGet(){
        for (Dish d : dishStock.keySet()){
            try{
                if (dishStock.get(d).intValue() + (Collections.frequency(dishesBeingMade, d) * d.getRestockAmount().intValue()) < d.getRestockThreshold().intValue()){
                    if (d.getRecipe() == null){
                        continue;
                    }
                    if (canDishBeMade(d)){
                        dishesBeingMade.add(d);
                        return d;
                    }
                }
            } catch (ConcurrentModificationException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void restockIngredient(Ingredient i){
        ingredientStock.put(i, ingredientStock.get(i).intValue() + i.getRestockAmount().intValue());
    }

    public ConcurrentLinkedQueue<Order> getOrderQueue() {
        return orderQueue;
    }

    public void beginRestock(Dish d){
        for (Ingredient i : d.getRecipe().keySet()){
            ingredientStock.put(i, ingredientStock.get(i).intValue() - d.getRecipe().get(i).intValue());
        }
    }

    public void restockDish(Dish d){
        dishStock.put(d, dishStock.get(d).intValue() + d.getRestockAmount().intValue());

    }

    public void addIngredient(Ingredient i){
        ingredientStock.put(i,0);
    }

    public void setStock(Ingredient i, Number n){
        ingredientStock.put(i,n);
    }

    public void addDish(Dish d){
        System.out.println("Adding dish " + d);
        dishStock.put(d,0);
    }

    public void setStock(Dish d, Number n){
        dishStock.put(d,n);
    }

    public void addStock(Dish d, Number n){

    }
    public Map<Dish, Number> getDishStock() {
        return dishStock;
    }

    public void setDishStock(Map<Dish, Number> dishStock) {
        this.dishStock = dishStock;
    }

    public Map<Ingredient, Number> getIngredientStock() {
        return ingredientStock;
    }

    public void setIngredientStock(Map<Ingredient, Number> ingredientStock) {
        this.ingredientStock = ingredientStock;
    }

    public List<Dish> getDishesBeingMade() {
        return dishesBeingMade;
    }

    public void setDishesBeingMade(List<Dish> dishesBeingMade) {
        this.dishesBeingMade = dishesBeingMade;
    }

    public List<Ingredient> getIngredientsBeingMade() {
        return ingredientsBeingMade;
    }

    public void setIngredientsBeingMade(List<Ingredient> ingredientsBeingMade) {
        this.ingredientsBeingMade = ingredientsBeingMade;
    }
}
