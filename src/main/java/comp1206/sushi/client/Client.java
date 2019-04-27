package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;


import comp1206.sushi.comms.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");

	public Restaurant restaurant;
	public volatile ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Order> orders = new ArrayList<Order>();



	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	public User currentlyLoggedInUser = null;

	ClientCommunications clientComms;

	public Client() {

		clientComms = new ClientCommunications(this);

	}

	public void resetServerSignal(){
		dishes = new ArrayList<Dish>();
//		orders = new ArrayList<Order>();
		users = new ArrayList<User>();
		postcodes = new ArrayList<Postcode>();
		clientComms.sendMessage(new RequestDishes());
		clientComms.sendMessage(new RequestPostcodes());
		clientComms.sendMessage(new RequestUsers());
	}

	public void addDish(Dish dish){
		System.out.println("Adding dish : " + dish.getName());
		dishes.add(dish);
		System.out.println(dishes);
		this.notifyUpdate();
	}

	public void removeDish(Dish dish){
		Iterator<Dish> dishIterator = this.dishes.iterator();
		while (dishIterator.hasNext()){
			Dish d = dishIterator.next();
			if (d.getName().equals(dish.getName())){
				dishIterator.remove();
			}
		}
		this.notifyUpdate();
	}

	public synchronized void addPostcode(Postcode postcode){
		System.out.println("Adding postcode : " + postcode.getName());
		postcodes.add(postcode);
		this.notifyUpdate();
	}

	@Override
	public Restaurant getRestaurant() {
		return this.restaurant;
	}
	
	@Override
	public String getRestaurantName() {
		return this.restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return this.restaurant.getLocation();
	}
	
	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		User newUser = new User(username, password, address, postcode);
		users.add(newUser);
		currentlyLoggedInUser = newUser;
		clientComms.sendMessage(newUser);
		return newUser;
	}

	@Override
	public User login(String username, String password) {
		for (User u : this.getUsers()){
			if (u.getName().equals(username) && u.getPassword().equals(password)){
				currentlyLoggedInUser = u;
				return u;
			}


		}
		return new User();
	}

	@Override
	public List<Postcode> getPostcodes() {
		clientComms.sendMessage(new RequestPostcodes());
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		clientComms.sendMessage(new RequestDishes());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		return dish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		return dish.getPrice();
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
		return user.getBasket();
	}

	@Override
	public Number getBasketCost(User user) {
		Number cost = 0.0;
		Map<Dish, Number> userBasket = getBasket(user);
		for (Dish d: userBasket.keySet()){
			cost = cost.doubleValue() + (d.getPrice().doubleValue() * userBasket.get(d).doubleValue());
		}
		return cost;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		if (currentlyLoggedInUser != null){
			currentlyLoggedInUser.addToBasket(dish, quantity);
		} else {
			user.addToBasket(dish, quantity);
		}

	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		if (currentlyLoggedInUser != null){
			currentlyLoggedInUser.addToBasket(dish, quantity);
		} else {
			user.addToBasket(dish, quantity);
		}
	}

	@Override
	public Order checkoutBasket(User user) {
		// TODO Auto-generated method stub
		if (currentlyLoggedInUser != null){
			Order newOrder = new Order(currentlyLoggedInUser.getName() , currentlyLoggedInUser.getBasket(), currentlyLoggedInUser);
			orders.add(newOrder);
			clientComms.sendMessage(newOrder);
			return newOrder;
		} else {
			Order newOrder = new Order(user.getName() , user.getBasket(), user);
			orders.add(newOrder);
			clientComms.sendMessage(newOrder);
			return newOrder;
		}
	}

	@Override
	public void clearBasket(User user) {
		user.resetBasket();
	}

	@Override
	public List<Order> getOrders(User user) {
		List<Order> userOrders = new ArrayList<>();
		for (Order o : orders){
			if (o.getName().equals(user.getName())){
				userOrders.add(o);
			}
		}
		return userOrders;
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return order.isOrderComplete();
	}

	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}

	@Override
	public Number getOrderCost(Order order) {
		// TODO Auto-generated method stub
		Map<Dish, Number> orderContents = order.getOrderDetails();
		Number orderCost = 0;
		for (Dish d : orderContents.keySet()){
			orderCost = orderCost.doubleValue() + (d.getPrice().doubleValue() * orderContents.get(d).doubleValue());
		}
		return orderCost;
	}

	@Override
	public void cancelOrder(Order order) {
		clientComms.sendMessage(new CancelOrder(order));
		order.setStatus("Cancelled");


	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void notifyUpdate() {
		try{
			this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
		} catch (NullPointerException e){
			System.out.println("notifyUpdate NPE caught");
		}

	}

	public void setDishes(ArrayList<Dish> dishes) {
		System.out.println("setting dishes");


		this.dishes = dishes;
		this.notifyUpdate();
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

	public ArrayList<User> getUsers() {
		clientComms.sendMessage(new RequestUsers());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public void setPostcodes(ArrayList<Postcode> postcodes) {
		this.postcodes = postcodes;
	}


}
