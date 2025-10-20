import java.util.HashSet;
import java.util.UUID;

public class Trip {
    private String id;
    private HashSet<Reservation> reservations;

    public Trip() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public HashSet<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public void setReservations(HashSet<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", reservations=" + reservations +
                '}';
    }
    
}
