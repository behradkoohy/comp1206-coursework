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
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    public void setConfigurations() throws Exception{
        server.resetServer();
        String st;
        while ((st = br.readLine()) != null){
            if (!(st.equals(""))){
                System.out.println("Line:" + st);
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
                            throw new Exception("Unsuppored File Layout");
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
                            throw new Exception("Unsupported File Layout");
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
                            throw new Exception("Unsupported File Layout");
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
                                if (ingredientSplit[1].equalsIgnoreCase(ingredient.getName())){
                                    i = ingredient;
                                }
                            }


                        }
                        break;
                    case "USER":
                        break;
                    case "ORDER":
                        break;
                    case "STOCK":
                        break;
                    case "STAFF":
                        break;
                    case "DRONE":
                        break;
                }
            }
        }

    }



}
