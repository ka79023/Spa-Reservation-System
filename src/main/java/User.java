import java.util.*;
import java.io.*;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;

    private final List<TimeSlot> reservations = new LinkedList<>(); 

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    

    public void userReservations(TimeSlot targetSlot) {
        reservations.add(targetSlot);
    }

    public String getUsername() { 
        return username; 
    }

    public String getPassword() { 
        return password; 
    }

    public List<TimeSlot> getReservations() {
        return reservations;
    }

    public String toString() {
        StringBuilder reservationsStr = new StringBuilder();
        for (TimeSlot reservation : reservations) {
            reservationsStr.append(reservation.toString()).append("; ");
        }
        return "User{" + "username=" + username + ", Reservations=" + reservationsStr.toString() + '}';
    }
}

