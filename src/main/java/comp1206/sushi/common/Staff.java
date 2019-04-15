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

//	@Override
//	public void run() {
//		System.out.println("Running thread " + this.getName());
//		running = true;
//
//		while (running && !server.resetting){
//			setStatus("Idle");
//			int pos = 0;
//			List<Dish> dishNeedRestocking = server.checkDishStock();
//			while (pos < dishNeedRestocking.size()) {
//				if (dishNeedRestocking.size() >= pos) {
//					Dish d = dishNeedRestocking.get(pos);
//					server.makeDish(d);
//					beingMadeDish = d;
//					setStatus("Making dish " + beingMadeDish.getName());
//					try {
//						Thread.sleep(1000 * 5);
//					} catch (InterruptedException e){
//						System.out.println(e.getMessage());
//					}
//				}
//				pos++;
//			}
//		}
//		System.out.println("END RUN");
//	}

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
	public void run() {
		System.out.println("Running daemon thread " + name);
		running = true;
		while (running && !server.resetting){
			Dish topDish = null;
			synchronized (this.server.dishStockDaemon){
				try{
					topDish = this.server.dishStockDaemon.getTopOfQueue();
				} catch (NoSuchElementException e){
					setStatus("Idle");
				}
			}
			if (topDish != null){
				int numberToBe = this.server.getDishStockLevels().get(topDish).intValue();
				for (Staff s : this.server.getStaff()){
					if (s.getBeingMadeDish() != null && s.getBeingMadeDish().equals(topDish)){
						System.out.println(s.getBeingMadeDish().getName() + " being made");
						numberToBe += topDish.getRestockAmount().intValue();
					}
				}
				if (numberToBe < topDish.getRestockThreshold().intValue()){
					beingMadeDish = topDish;
					server.makeDish(topDish);
					setStatus("Making dish " + beingMadeDish.getName());
					try {
						Random random = new Random();
						int timeToMake = random.nextInt(41) + 20;
						Thread.sleep(1000 * (timeToMake));
					} catch (InterruptedException e){
						System.out.println(e.getMessage());
					}
					setBeingMadeDish(null);
				} else {
					synchronized (this.server.dishStockDaemon.dishRestockQueue){
						this.server.dishStockDaemon.dishRestockQueue.remove(topDish);
					}
				}
			}
		}
	}
}
