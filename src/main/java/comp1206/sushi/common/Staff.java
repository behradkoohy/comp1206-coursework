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
	public void run(){
		System.out.println("Running daemon thread " + name);
		running = true;
		while (running && !this.server.resetting){
			Dish dishToMake = this.server.stock.getDishToGet();
			if (dishToMake != null){
				System.out.println(dishToMake);
				setBeingMadeDish(dishToMake);
				Random random = new Random();
				int timeToMake = random.nextInt(40) + 20;
				this.server.stock.dishesBeingMade.add(dishToMake);
				System.out.println(timeToMake);
				setStatus("Making dish " + dishToMake.getName());
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






















































	//		while (running && !server.resetting){
//			Dish dishToMake = null;
//			synchronized (this.server.dishStockDaemon){
////                System.out.println(this.server.dishStockDaemon.dishesToBeMade.size());
//				if (!this.server.dishStockDaemon.isQueueEmpty()){
//					dishToMake = this.server.dishStockDaemon.getTopOfQueue();
//                    System.out.println("got dish " + dishToMake);
//				}
//			}
//			if (dishToMake != null){
//                System.out.println(dishToMake);
//				setStatus("Making dish");
//                try {
//                    Random random = new Random();
//                    int timeToMake = random.nextInt(41) + 20;
//                    System.out.println("sleeping for " + timeToMake);
////                    Thread.sleep(1000 * (timeToMake));
//                    Thread.sleep(5000);
//                } catch (InterruptedException e){
//                    System.out.println(e.getMessage());
//                }
//                server.makeDish(dishToMake);
//                setBeingMadeDish(null);
//            }
//
//		}


//    @Override
//	public void run() {
//		System.out.println("Running daemon thread " + name);
//		running = true;
//		while (running && !server.resetting){
//			Dish dishToMake = null;
//			synchronized (this.server.dishStockDaemon){
//				try{
//					dishToMake = this.server.dishStockDaemon.getTopOfQueue();
//				} catch (NoSuchElementException e){
//					setStatus("Idle");
//				}
//			}
//			if (dishToMake != null){
//				int numberToBe;
//				try {
//					numberToBe = this.server.getDishStockLevels().get(dishToMake).intValue();
//				} catch (NullPointerException e){
//					continue;
//				}
//				for (Staff s : this.server.getStaff()){
//					if (s.getBeingMadeDish() != null && s.getBeingMadeDish().equals(dishToMake)){
//						numberToBe += dishToMake.getRestockAmount().intValue();
//					}
//				}
//				if (numberToBe < dishToMake.getRestockThreshold().intValue()){
//					beingMadeDish = dishToMake;
//					server.makeDish(dishToMake);
//					setStatus("Making dish " + beingMadeDish.getName());
//					try {
//						Random random = new Random();
//						int timeToMake = random.nextInt(41) + 20;
//						Thread.sleep(1000 * (timeToMake));
//					} catch (InterruptedException e){
//						System.out.println(e.getMessage());
//					}
//					setBeingMadeDish(null);
//				} else {
//					synchronized (this.server.dishStockDaemon.dishesToBeMade){
//						this.server.dishStockDaemon.dishesToBeMade.remove(dishToMake);
//					}
//				}
//			}
//		}
//	}




}
