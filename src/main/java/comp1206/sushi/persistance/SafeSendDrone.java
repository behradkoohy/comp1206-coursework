package comp1206.sushi.persistance;

import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Postcode;

import java.io.Serializable;

public class SafeSendDrone implements Serializable {

    private Number speed;
    private Number capacity;
    private Number battery;
    private String status;

     public SafeSendDrone(Drone drone){
        speed = drone.getSpeed();
        capacity = drone.getCapacity();
        battery = drone.getBattery();
        status = drone.getStatus();
    }
}
