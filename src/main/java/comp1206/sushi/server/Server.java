package comp1206.sushi.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import comp1206.sushi.Configuration;
import comp1206.sushi.common.*;


import comp1206.sushi.comms.CancelOrder;
import comp1206.sushi.comms.ServerCommunications;
import comp1206.sushi.exceptions.NegativeStockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements ServerInterface {

    private static final Logger logger = LogManager.getLogger("Server");



	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Staff> staff = new ArrayList<Staff>();
	public ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	public ArrayList<Thread> staffThreads = new ArrayList<>();
	public ArrayList<Thread> droneThreads = new ArrayList<>();

	public DishStockDaemon dishStockDaemon = new DishStockDaemon(this);
	Thread dishDaemon;

	public IngredientStockDaemon ingredientStockDaemon = new IngredientStockDaemon(this);
	Thread ingredientDaemon;

	List<Dish> dishesBeingMade = new ArrayList<>();
	public volatile boolean resetting = false;

	ServerCommunications serverComms;


	public Server() {


		logger.info("Starting up server...");

		Postcode restaurantPostcode = new Postcode("SO17 1BJ");
		restaurant = new Restaurant("Mock Restaurant",restaurantPostcode);


		dishDaemon = new Thread(dishStockDaemon);
		dishDaemon.start();

		ingredientDaemon = new Thread(ingredientStockDaemon);
		ingredientDaemon.start();

		serverComms = new ServerCommunications(this);

//		commsServer.start();

//		serverInput = new Thread(serverComIn);
//		serverInput.start();


		Postcode postcode1 = addPostcode("SO17 1TJ", this.restaurant);
		Postcode postcode2 = addPostcode("SO17 1BX", this.restaurant);
		Postcode postcode3 = addPostcode("SO17 2NJ", this.restaurant);
		Postcode postcode4 = addPostcode("SO17 1TW", this.restaurant);
		Postcode postcode5 = addPostcode("SO17 2LB", this.restaurant);
////
		Supplier supplier1 = addSupplier("Supplier 1",postcode1);
		Supplier supplier2 = addSupplier("Supplier 2",postcode2);
		Supplier supplier3 = addSupplier("Supplier 3",postcode3);

		Ingredient ingredient1 = addIngredient("Ingredient 1","grams",supplier1,1,5,1);
		Ingredient ingredient2 = addIngredient("Ingredient 2","grams",supplier2,1,5,1);
		Ingredient ingredient3 = addIngredient("Ingredient 3","grams",supplier3,1,5,1);

		Dish dish1 = addDish("Dish 1","Dish 1",1,1,10);
		Dish dish2 = addDish("Dish 2","Dish 2",2,1,10);
		Dish dish3 = addDish("Dish 3","Dish 3",3,1,10);
		User user = addUser("a", "a", "a", postcode1);
//
//
		addIngredientToDish(dish1,ingredient1,1);
		addIngredientToDish(dish1,ingredient2,2);
		addIngredientToDish(dish2,ingredient2,3);
		addIngredientToDish(dish2,ingredient3,1);
		addIngredientToDish(dish3,ingredient1,2);
		addIngredientToDish(dish3,ingredient3,1);
//
		addStaff("Staff 1");
		addStaff("Staff 2");
		addStaff("Staff 3");
//
		addDrone(1);
		addDrone(2);
		addDrone(3);


	}

	public void addToDishesBeingMade(Dish dish){
		dishesBeingMade.add(dish);
	}

	public void removeFromDishesBeingMade(Dish dish){
		dishesBeingMade.remove(dish);
	}



	public synchronized void resetServer(){
		resetting = true;
		for (Staff s : staff){
			s.terminate();
		}
		dishStockDaemon.resetSignal();
		dishes = new ArrayList<Dish>();
		drones = new ArrayList<Drone>();
		ingredients = new ArrayList<Ingredient>();
		orders = new ArrayList<Order>();
		staff = new ArrayList<Staff>();
		suppliers = new ArrayList<Supplier>();
		users = new ArrayList<User>();
		postcodes = new ArrayList<Postcode>();
		listeners = new ArrayList<UpdateListener>();
		staffThreads = new ArrayList<Thread>();
		resetting = false;
	}

	public synchronized Map<Ingredient, Number> getDishIngredientStock(Dish dish){
		Map<Ingredient, Number> oldStock = new HashMap<>();
		for (Ingredient i : dish.getRecipe().keySet()){
			if (i != null){
				System.out.println(i);
				oldStock.put(i, i.getStock());
			}
		}
		return oldStock;
	}

	public void cancelOrder(CancelOrder cancelOrder){
		for (Order o : this.getOrders()){
			if (o.getName().equals(cancelOrder.getOrder().getName())){
				o.setStatus("Cancelled");
			}
		}
		notifyUpdate();
	}

	public boolean makeDish(Dish dish){
		// returns true if dish is successful
		// else false
		Map<Ingredient,Number> previousStock = this.getDishIngredientStock(dish);
		try{
			for (Ingredient i: this.getRecipe(dish).keySet()){
				reduceIngredientStock(i, this.getRecipe(dish).get(i));
			}
			dish.setStock(dish.getStock().doubleValue() + dish.getRestockAmount().intValue());
			this.notifyUpdate();
			return true;
		} catch (Exception e){
			for (Ingredient i: previousStock.keySet()){
				this.setStock(i, previousStock.get(i));
			}
			return false;
		}
	}

	public synchronized void deliverIngredients(Ingredient ingredient){
		System.out.println("Call to deliver ingredients");
		synchronized (ingredient){
			int newStock = ingredient.getRestockAmount().intValue();
			int oldStock = ingredient.getStock().intValue();
			ingredient.setStock(newStock + oldStock);
//			ingredient.setStock(this.getIngredientStockLevels().get(ingredient).intValue() + (1));
		}

	}

	public Order addOrder(Order order){
		this.orders.add(order);
		return order;
	}

	public User addUser(String username, String password, String address, Postcode postcode){
		User newUser = new User(username, password, address, postcode);
		this.users.add(newUser);
		this.notifyUpdate();
		return newUser;
	}


	@Override
	public synchronized List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		serverComms.sendMessageToAll(newDish);
		this.notifyUpdate();
		return newDish;
	}

	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount, Map<Ingredient, Number> recipie) {
		Dish newDish = new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(newDish);
		this.notifyUpdate();
		newDish.setRecipe(recipie);
		this.notifyUpdate();
		return newDish;
	}
	
	@Override
	public void removeDish(Dish dish) {
		this.dishes.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		Random random = new Random();
		List<Dish> dishes = getDishes();
		HashMap<Dish, Number> levels = new HashMap<Dish, Number>();
		for(Dish dish : dishes) {
			levels.put(dish,random.nextInt(50));
		}
		return levels;
	}
	
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		/// TODO Implement
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		/// TODO Implement
	}
	
	@Override
	public void setStock(Dish dish, Number stock) {
	
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
			Number restockThreshold, Number restockAmount, Number weight) {
		Ingredient mockIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		this.ingredients.add(mockIngredient);
		this.notifyUpdate();
		return mockIngredient;
	}

	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	public void reduceIngredientStock(Ingredient ingredient, Number number) throws NegativeStockException {
		System.out.println(ingredient.getStock());
		System.out.println(number.doubleValue());
		Number postStock = ingredient.getStock().doubleValue() - number.doubleValue();
		if (postStock.intValue() >= 0){
			ingredient.setStock(postStock);
		} else {
			throw new NegativeStockException("Negative number of stock");
		}

	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Supplier mock = new Supplier(name,postcode);
		this.suppliers.add(mock);
		return mock;
	}


	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed, this);
		synchronized (this.drones){
			this.drones.add(mock);
			Thread t = new Thread(mock);
			try{
				Thread.sleep(50);
			} catch (InterruptedException e){
				System.out.println("addDrone Interrupt");
			}
			droneThreads.add(t);
			t.start();
		}
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	@Override
	public Staff addStaff(String name) {
		Staff mock = new Staff(name, this);
		synchronized (this.staff){
			this.staff.add(mock);
			Thread t = new Thread(mock);
			try{
				Thread.sleep(50);
			} catch (InterruptedException e){
				System.out.println("addStaff Interrupt");
			}
			staffThreads.add(t);
			t.start();
		}
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		this.notifyUpdate();
	}
	
	@Override
	public Number getOrderCost(Order order) {
		Number totalCost = new Double(0);
		for (Dish d : order.getOrderDetails().keySet()){
			totalCost = totalCost.doubleValue() + (d.getPrice().doubleValue() * order.getOrderDetails().get(d).doubleValue());
		}
		return totalCost;
	}

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		Random random = new Random();
		List<Ingredient> dishes = getIngredients();
		HashMap<Ingredient, Number> levels = new HashMap<Ingredient, Number>();
		for(Ingredient ingredient : ingredients) {
			levels.put(ingredient,random.nextInt(50));
		}
		return levels;
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		Order mock = (Order)order;
		return mock.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code, this.restaurant);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}


	public Postcode addPostcode(String code, Restaurant restaurant) {
		Postcode mock = new Postcode(code, restaurant);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return this.users;
	}
	
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}

	@Override
	public void loadConfiguration(String filename) {
		try {
			Configuration c = new Configuration(filename, this);
			c.setConfigurations();
			System.out.println("Loaded configuration: " + filename);
		} catch (IOException e){
			System.out.println(e.getCause());
		}
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for(Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish,recipeItem.getKey(),recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return true;
	}

	@Override
	public String getOrderStatus(Order order) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Complete";
		} else {
			return "Pending";
		}
	}
	
	@Override
	public String getDroneStatus(Drone drone) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Flying";
		}
	}
	
	@Override
	public String getStaffStatus(Staff staff) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Working";
		}
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
	}

	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}
	
	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }


	/**
	 *
	 * **/
	public static void main(String[] args) {
		Server s = new Server();

	}

}
