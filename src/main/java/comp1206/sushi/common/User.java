package comp1206.sushi.common;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User extends Model implements Serializable {
	
	private String name;

	private String password;
	private String address;
	private Postcode postcode;
	private Map<Dish, Number> basket;



	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		if (postcode != null){
			this.postcode = postcode;
		} else {
			this.postcode = new Postcode("SO17 1BJ");
		}
		this.basket = new HashMap<>();
		//TODO: fix this if postcode is null
	}
	public User(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getDistance() {
		return postcode.getDistance();
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public String getPassword() {	return password;	}

	public void setPassword(String password) {	this.password = password;	}

	public String getAddress() {	return address;		}

	public Map<Dish, Number> getBasket() {	return basket;	}

	public void addToBasket(Dish d, Number n){
		if (this.basket.containsKey(d)){
			this.basket.put(d, this.basket.get(d).doubleValue() + n.doubleValue());
		} else {
			this.basket.put(d, n);
		}
	}

	public void resetBasket(){
		this.basket = new HashMap<>();
	}
}
