package comp1206.sushi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");




	public Client() {
        logger.info("Starting up client...");

	}

	public Restaurant restaurant;
	public ArrayList<Dish> dishes = new ArrayList<Dish>();
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();

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

		System.out.println("Sending message");
		return newUser;
	}

	@Override
	public User login(String username, String password) {
		for (User u : users){
			if (u.getName().equals(username) && u.getPassword().equals(password)){
				return u;
			}
		}
		return null;
	}

	@Override
	public List<Postcode> getPostcodes() {
		System.out.println(postcodes);
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
//		for (Dish d : dishes){
//			if (d.equals(dish)){
//				return d.getDescription();
//			}
//		}
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
		return null;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		user.addToBasket(dish, quantity);

	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		user.addToBasket(dish, quantity);

	}

	@Override
	public Order checkoutBasket(User user) {
		// TODO Auto-generated method stub
		Order newOrder = new Order(user.getName() , user.getBasket());
		orders.add(newOrder);
		return newOrder;
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
		order.setStatus("Cancelled");

	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);

	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

}
