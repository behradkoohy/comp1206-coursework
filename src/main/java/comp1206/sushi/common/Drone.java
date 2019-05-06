package comp1206.sushi.common;

import comp1206.sushi.server.Server;

public class Drone extends Model implements Runnable{

	private Number speed;
	private Number progress;
	
	private Number capacity;
	private Number battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	private volatile boolean running = false;
	Server server;



	private volatile Ingredient ingredientBeingDelivered = null;
	private volatile Order orderBeingDelivered = null;

	public Drone(Number speed, Server server) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
		this.server = server;
	}

	public Drone(Number speed, Server server, Number capacity, Number battery) {
		this.setSpeed(speed);
		this.setCapacity(capacity);
		this.setBattery(battery);
		this.server = server;
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		return progress;
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
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

	public synchronized Ingredient getIngredientBeingDelivered() {
		return ingredientBeingDelivered;
	}

	public synchronized void setIngredientBeingDelivered(Ingredient ingredientBeingDelivered) {
		this.ingredientBeingDelivered = ingredientBeingDelivered;
	}

	public Order getOrderBeingDelivered() {
		return orderBeingDelivered;
	}

	public void setOrderBeingDelivered(Order orderBeingDelivered) {
		this.orderBeingDelivered = orderBeingDelivered;
	}



	@Override
	public void run() {
		System.out.println("Running thread for drone with speed " + getSpeed());
		running = true;
		while (running && !server.resetting){
			Ingredient ingredientToGet = this.server.stock.getIngredientToGet();
			if (ingredientToGet != null){
				Number distanceToTravel = ingredientToGet.getSupplier().getDistance().doubleValue() * 1000;
				Number timeToTravel = distanceToTravel.doubleValue() / getSpeed().doubleValue();
				setIngredientBeingDelivered(ingredientToGet);
				setDestination(ingredientToGet.getSupplier().getPostcode());
				boolean loopCompleted = true;
				for (int t = 0; t < timeToTravel.doubleValue(); t++) {
					try {
						Thread.sleep(1000);
						if (!this.server.getIngredients().contains(ingredientToGet)){
							loopCompleted = false;
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					double distanceGone = getSpeed().doubleValue() * t;
					setProgress((distanceGone * 100) / distanceToTravel.doubleValue());
				}
				setProgress(0);
				setIngredientBeingDelivered(null);
				setDestination(null);
				if (loopCompleted) {
					synchronized (this.server.stock.ingredientsBeingMade) {
						this.server.stock.ingredientsBeingMade.remove(ingredientToGet);
						this.server.stock.restockIngredient(ingredientToGet);
					}
				}
				ingredientToGet = null;
			} else {
				Order orderToDeliver = null;
				synchronized (this.server.stock.orderQueue){
					orderToDeliver = this.server.stock.getOrderToDeliver();
//					System.out.println(this.server.stock.orderQueue.size());

				}
				if (orderToDeliver != null){

					Postcode destination = orderToDeliver.getUser().getPostcode();
					Number distanceToTravel = destination.getDistance().doubleValue() * 1000;
					Number timeToTravel = distanceToTravel.doubleValue() / getSpeed().doubleValue();

					setOrderBeingDelivered(orderToDeliver);
					setDestination(destination);
					setProgress(0);
					boolean loopCompleted = true;
					for (int t = 0; t < timeToTravel.doubleValue(); t++) {
						System.out.println("loop");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						double distanceGone = getSpeed().doubleValue() * t;
						setProgress((distanceGone * 100) / distanceToTravel.doubleValue());
					}
					setProgress(0);
					setOrderBeingDelivered(null);
					setDestination(null);
					if (loopCompleted) {
						synchronized (orderToDeliver) {
							orderToDeliver.setOrderComplete(true);
							orderToDeliver.setStatus("Delivered");
						}
					} else {
						synchronized (orderToDeliver){
							orderToDeliver.setOrderComplete(false);
							orderToDeliver.setStatus("In Progress");
						}
					}
				}
			}
			if (ingredientToGet != null){
				System.out.println("CHECKING FOR ORDERS");
			}


		}






	}
//		System.out.println(server.resetting);
//		while (running && !server.resetting){
//
//			Ingredient ingredientToGet = null;
//			synchronized (this.server.ingredientStockDaemon){
//				try {
//					ingredientToGet = this.server.ingredientStockDaemon.getTopOfQueue();
//				} catch (Exception e){
//					ingredientToGet = null;
//					ingredientBeingDelivered = null;
//				}
//			}
//			if (ingredientToGet != null){
//				int numberToBe;
//				try {
//					numberToBe = this.server.getIngredientStockLevels().get(ingredientToGet).intValue();
//				} catch (NullPointerException e){
//					continue;
//				}
//				synchronized (this.server.drones){
//					for (Drone d : this.server.getDrones()){
//						if (d.getIngredientBeingDelivered() != null && d.getIngredientBeingDelivered().equals(ingredientToGet)) {
//							numberToBe += ingredientToGet.getRestockAmount().intValue();
//						}
//					}
//				}
//				if (numberToBe < ingredientToGet.getRestockThreshold().intValue()){
//					ingredientBeingDelivered = ingredientToGet;
////					System.out.println(ingredientToGet.getName() + " being made");
//					try{
//						if (this.server.getIngredients().contains(ingredientBeingDelivered)){
//							Number distanceToTravel = ingredientToGet.getSupplier().getDistance().doubleValue() * 1000;
//							Number timeToTravel = distanceToTravel.doubleValue() / getSpeed().doubleValue();
//							boolean loopCompleted = true;
//							for (int t = 0; t < timeToTravel.doubleValue(); t++) {
//								Thread.sleep(1000);
//								double distanceGone = getSpeed().doubleValue() * t;
//
//								setProgress((distanceGone * 100) / distanceToTravel.doubleValue());
//								if (!this.server.getIngredients().contains(ingredientBeingDelivered)){
//									loopCompleted = false;
//									break;
//								}
//								if (this.server.getIngredientStockLevels().get(ingredientBeingDelivered).intValue() >= ingredientBeingDelivered.getRestockThreshold().intValue()){
//									loopCompleted = false;
//									break;
//								}
//							}
//							if (loopCompleted){
//								server.deliverIngredients(ingredientToGet);
//							}
//						}
//					} catch (Exception e){
//						e.printStackTrace();
//					}
//					setIngredientBeingDelivered(null);
//					setDestination(null);
//					setProgress(0);
//				} else {
//					synchronized (this.server.ingredientStockDaemon.ingredientRestockQueue){
//						this.server.ingredientStockDaemon.ingredientRestockQueue.remove(ingredientToGet);
//					}
//				}
//			}
//		}
//		if (this.server.ingredientStockDaemon.getQueueSize() == 0 && this.server.deliveryQueue.peekDeliverable() != null && this.getIngredientBeingDelivered() == null && this.getOrderBeingDelivered() == null){
//			Order toDeliver = this.server.deliveryQueue.getDeliverable();
//			try{
//				Number distanceToTravel = toDeliver.getUser().getPostcode().getDistance().doubleValue() * 1000;
//				Number timeToTravel = distanceToTravel.doubleValue() / getSpeed().doubleValue();
//				for (int t = 0; t < timeToTravel.doubleValue(); t++){
//					Thread.sleep(1000);
//					double distanceGone = getSpeed().doubleValue() * t;
//					setProgress((distanceGone*100)/distanceToTravel.doubleValue());
//				}
//
//				server.deliverOrder(toDeliver);
//			} catch (Exception e){
//				e.printStackTrace();
//			}
//			setIngredientBeingDelivered(null);
//			setDestination(null);
//			setProgress(0);
//		}

}
