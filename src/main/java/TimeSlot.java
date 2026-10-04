import java.util.*;
import java.io.*;

public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private String slotId;
    private String serviceName;
    private String therapistName;
    private String day;
    private boolean isAvailable = true;
     
    public TimeSlot(String slotId, String serviceName, String therapistName, String day) {
        if (!slotId.startsWith("SLOT-")) {
            this.slotId = "SLOT-" + slotId.trim();
        } else {
            this.slotId = slotId.trim();
        }
        this.serviceName = serviceName;
        this.therapistName = therapistName;
        this.day = day;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", getDay(), getServiceName(), getTherapistName());
    }

    public String getSlotId() {
        return slotId;
    }


    public String getServiceName() {
        return serviceName;
    }


    public String getTherapistName() {
        return therapistName;
    }


    public String getDay() {
        return day;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }


    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }


    String getTherapist() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}