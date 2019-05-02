package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class Stock {

    Server server;

    Map<Dish, Number> dishStock = new ConcurrentHashMap<>();
    Map<Ingredient, Number> ingredientStock = new ConcurrentHashMap<>();

    List<Dish> dishesBeingMade = new ArrayList<>();
    List<Ingredient> ingredientsBeingMade = new ArrayList<>();

    public Stock(Server server){
        this.server = server;

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

    public synchronized Dish getDishToGet(){
        System.out.println(dishStock.keySet().size());
        for (Dish d : dishStock.keySet()){
            if (dishStock.get(d).intValue() + (Collections.frequency(dishesBeingMade, d) * d.getRestockAmount().intValue()) < d.getRestockThreshold().intValue()){
                if (d.getRecipe() == null){
                    continue;
                }
                if (canDishBeMade(d)){
                    return d;
                }
            }
        }
        return null;
    }

    public void restockIngredient(Ingredient i){
        ingredientStock.put(i, ingredientStock.get(i).intValue() + i.getRestockAmount().intValue());
    }

    public void restockDish(Dish d){
        dishStock.put(d, dishStock.get(d).intValue() + d.getRestockAmount().intValue());
        for (Ingredient i : d.getRecipe().keySet()){
            ingredientStock.put(i, ingredientStock.get(i).intValue() - d.getRecipe().get(i).intValue());
        }
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
