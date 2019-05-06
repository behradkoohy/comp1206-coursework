package comp1206.sushi.persistance;

import comp1206.sushi.common.Staff;
import comp1206.sushi.server.Server;

import java.io.Serializable;

public class SafeSendStaff implements Serializable {

    private String name;
    private String status;
    private Number fatigue;


    public SafeSendStaff(Staff staff){
        name = staff.getName();
        status = staff.getStatus();
        fatigue = staff.getFatigue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Number getFatigue() {
        return fatigue;
    }

    public void setFatigue(Number fatigue) {
        this.fatigue = fatigue;
    }

}
