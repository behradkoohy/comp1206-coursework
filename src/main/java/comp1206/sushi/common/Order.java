package comp1206.sushi.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model implements Serializable {

	private String status;
	private Map<Dish, Number> orderDetails;
	private boolean isOrderComplete = false;

	public Order(String name, Map orderDetails) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = name;
		this.orderDetails = orderDetails;
		this.status = "In Progress";
	}
	public Order(){}

	public Number getDistance() {
		return 1;
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

}
