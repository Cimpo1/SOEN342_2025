import java.util.HashSet;
import java.util.UUID;
import java.time.LocalTime;

public class Trip {
    private String id;
    private HashSet<Reservation> reservations;
    private LocalTime departureTime;

    public Trip() {
        this.id = UUID.randomUUID().toString();
        this.reservations = new HashSet<>();
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

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", reservations=" + reservations +
                '}';
    }

}
