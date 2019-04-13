package comp1206.sushi;

import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    File file;
    BufferedReader br;
    Server server;

    public Configuration(String filePath, Server s) throws IOException {
        file = new File(filePath);
        br = new BufferedReader(new FileReader(file));
        this.server = s;
    }

    public static void main(String[] args) {
        try {
            Configuration c = new Configuration("/Users/behradkoohy/Desktop/prog2-cwk2-files/sushi/src/main/java/comp1206/sushi/Configuration.txt", new Server());
            c.setConfigurations();
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    public void setConfigurations() throws IOException{
        server.resetServer();
        String st;
        while ((st = br.readLine()) != null){
            if (!(st.equals(""))){
                String[] splitString = st.split(":");
                switch (splitString[0]){
                    case "POSTCODE":
                        server.addPostcode(splitString[1]);
                        break;
                    case "RESTAURANT":
                        Postcode restaurantLocation = null;
                        for (Postcode p : server.getPostcodes()){
                            if (p.getName().equalsIgnoreCase(splitString[2])){
                                restaurantLocation = p;
                            }
                        }
                        if (restaurantLocation == null){
//                            throw new Exception("Unsuppored File Layout");
                        } else {
                            server.setRestaurant(new Restaurant(splitString[1], restaurantLocation));
                        }
                        break;
                    case "SUPPLIER":
                        Postcode supplierLocation = null;
                        for (Postcode p : server.getPostcodes()){
                            if (p.getName().equalsIgnoreCase(splitString[2])){
                                supplierLocation = p;
                            }
                        }
                        if (supplierLocation == null){
//                            throw new Exception("Unsupported File Layout");
                        } else {
                            server.addSupplier(splitString[1], supplierLocation);
                        }
                        break;
                    case "INGREDIENT":
                        Supplier ingredientSupplier = null;
                        for (Supplier s : server.getSuppliers()){
                            if (s.getName().equalsIgnoreCase(splitString[3])){
                                ingredientSupplier = s;
                            }
                        }
                        if (ingredientSupplier == null){
//                            throw new Exception("Unsupported File Layout");
                        } else {
                            server.addIngredient(splitString[1], splitString[2], ingredientSupplier, Double.parseDouble(splitString[4]),
                                    Double.parseDouble(splitString[5]), Double.parseDouble(splitString[6]));
                        }
                        break;
                    case "DISH":
                        Map<Ingredient,Number> ingredientMap = new HashMap<Ingredient,Number>();
                        String[] ingredientList = splitString[6].split(",");
                        for (String ingredientLine : ingredientList){
                            ingredientLine.replace(" ", "");
                            String ingredientSplit[] = ingredientLine.split("\\*");
                            Ingredient i = null;
                            for (Ingredient ingredient : server.getIngredients()){
                                ingredientSplit[1] = ingredientSplit[1].replace(" ", "");
//                                System.out.println(ingredient + "*" + ingredientSplit[1]);
                                if (ingredientSplit[1].equals(ingredient.getName()) || ingredient.getName().equals(ingredientSplit[1])){
                                    i = ingredient;
                                }
                            }
//                            System.out.println(ingredientSplit[0]);
//                            ingredientSplit[0].replace((i.getName()+" \\* "), "");
                            ingredientMap.put(i, Double.parseDouble(ingredientSplit[0]));
                        }
//                        System.out.println("DISH" + splitString[1] + splitString[2] + splitString[3] + splitString[4] +
//                                splitString[5] + ingredientMap);
                        server.addDish(splitString[1], splitString[2], Double.parseDouble(splitString[3]), Double.parseDouble(splitString[4]),
                                Double.parseDouble(splitString[5]), ingredientMap);
                        break;
                    case "USER":
                        Postcode userPostcode = null;
                        for (Postcode p : server.getPostcodes()){
                            if (p.getName().equalsIgnoreCase(splitString[4])){
                                userPostcode = p;
                            }
                        }
                        if (userPostcode == null){
//                            throw new Exception("Unsupported File Layout");
                        } else {
                            server.addUser(splitString[1], splitString[2], splitString[3], userPostcode);
                        }
                        break;
                    case "ORDER":
                        splitString[2].replace(" ", "");
                        String[] orderList = splitString[2].split(",");
                        HashMap<Dish, Number> orderBasket = new HashMap<>();
                        for (String order : orderList){
                            Dish orderedDish = null;
                            for (Dish d : server.getDishes()){
                                if (d.getName().equalsIgnoreCase(order.split("\\*")[0])){
                                    orderedDish = d;
                                }
                            }
                            if (orderedDish == null){
//                                throw new Exception("Unsupported File Layout");
                            }else {
                                orderBasket.put(orderedDish, Double.parseDouble(order.split("\\*")[1]));
                            }
                        }
                        server.addOrder(new Order(splitString[1], orderBasket));
                        break;
                    case "STOCK":
                        Dish dishStockModifer = null;
                        for (Dish d : server.getDishes()){
                            if (d.getName().equalsIgnoreCase(splitString[1])){
                                dishStockModifer = d;
                            }
                        }
                        if (dishStockModifer == null){
//                            throw new Exception("Unsupported file type");
                            Ingredient ingredientStockModifier = null;
                            for (Ingredient i : server.getIngredients()){
                                if (i.getName().equals(splitString[1])){
                                    ingredientStockModifier = i;
                                }
                            }
                            if (ingredientStockModifier == null){
                                // pass
                            } else {
                                ingredientStockModifier.setStock(Double.parseDouble(splitString[2]));
                            }
                        } else {
                            dishStockModifer.setStock(Double.parseDouble(splitString[2]));
                        }
                        break; // TODO: FIGURE THIS STUFF OUT
                    case "STAFF":
                        server.addStaff(splitString[1]);
                        break;
                    case "DRONE":
                        server.addDrone(Double.parseDouble(splitString[1]));
                        break;
                }
            }
        }

    }



}
