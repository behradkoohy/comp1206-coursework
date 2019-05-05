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

}
