package comp1206.sushi.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model implements Serializable {
	private long key;

	private String status;

	private Map<Dish, Number> orderDetails;
	private boolean isOrderComplete = false;
	private User user;

	public Order(String name, Map orderDetails, User user) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		this.name = name;
		this.orderDetails = orderDetails;
		this.status = "In Progress";
		this.user = user;
		key = System.currentTimeMillis();
	}

	public Order(){}

	public Number getDistance() {
		return user.getPostcode().getDistance();
	}
	@Override
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addDishToOrder(Dish d, Number n){
		this.orderDetails.put(d,n);
	}

	public Map<Dish, Number> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Map<Dish, Number> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public boolean isOrderComplete() {	return isOrderComplete;		}

	public void setOrderComplete(boolean orderComplete) {	isOrderComplete = orderComplete;	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		Order order = (Order) o;
//
////		for (Dish d : this.getOrderDetails().)
//
//
//		return order.getOrderDetails().equals(this.getOrderDetails());
//
//
//	}

	@Override
	public int hashCode() {
		return Objects.hash(status, orderDetails, isOrderComplete, user);
	}
}
