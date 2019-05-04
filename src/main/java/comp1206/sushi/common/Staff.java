package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Staff extends Model implements Runnable {

	private String name;
	private String status;
	private Number fatigue;
	private Server server;
	private volatile boolean running = false;



	private Dish beingMadeDish = null;
	
	public Staff(String name, Server server) {
		this.setName(name);
		this.setFatigue(0);
		this.server = server;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	public void terminate(){
		running = false;
	}

	public Dish getBeingMadeDish() {
		return beingMadeDish;
	}

	public void setBeingMadeDish(Dish beingMadeDish) {
		this.beingMadeDish = beingMadeDish;
	}

	@Override
	public void run(){
		System.out.println("Running daemon thread " + name);
		running = true;
		while (running && !this.server.resetting){
			Dish dishToMake = this.server.stock.getDishToGet();
			if (dishToMake != null){
				setBeingMadeDish(dishToMake);
				Random random = new Random();
				int timeToMake = random.nextInt(40) + 20;
//				this.server.stock.dishesBeingMade.add(dishToMake);
				setStatus("Making dish " + dishToMake.getName());
				this.server.stock.beginRestock(dishToMake);
				try {
					Thread.sleep(timeToMake * 1000);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				setStatus("Idle");
				setBeingMadeDish(null);
				this.server.stock.dishesBeingMade.remove(dishToMake);
				this.server.stock.restockDish(dishToMake);

			}


		}

	}


}
