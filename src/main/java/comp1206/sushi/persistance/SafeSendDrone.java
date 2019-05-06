package comp1206.sushi.persistance;

import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Postcode;

import java.io.Serializable;

public class SafeSendDrone implements Serializable {


    private Number speed;
    private Number capacity;
    private Number battery;
    private String status;

    public SafeSendDrone(){}

     public SafeSendDrone(Drone drone){
        speed = drone.getSpeed();
        capacity = drone.getCapacity();
        battery = drone.getBattery();
        status = drone.getStatus();

    }

    public Number getSpeed() {
        return speed;
    }

    public void setSpeed(Number speed) {
        this.speed = speed;
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
        this.status = status;
    }
}
