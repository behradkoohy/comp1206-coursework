package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IngredientStockChecker {
    Server server;
    Map<Ingredient, Number> ingredientStock;



    List<Ingredient> ingredientsToBeRestocked;

    public IngredientStockChecker(Server server){
        this.server = server;
        ingredientStock = server.getIngredientStockLevels();
    }

    public synchronized List<Ingredient> checkStockLevels(){
        ingredientsToBeRestocked = new ArrayList<>();
        ingredientStock = server.getIngredientStockLevels();

        for (Ingredient i: ingredientStock.keySet()){
            if (ingredientStock.get(i).doubleValue() < i.getRestockThreshold().doubleValue()){
                ingredientsToBeRestocked.add(i);
            } else {
                System.out.println(i.getName() + " is fully restocked");
            }
        }
        return getIngredientsToBeRestocked();
    }

    public List<Ingredient> getIngredientsToBeRestocked() { return ingredientsToBeRestocked; }


}
