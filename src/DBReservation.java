import java.util.HashSet;

public class DBReservation {
    private HashSet<Reservation> reservations;

    public DBReservation() {
        this.reservations = new HashSet<>();
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
        return "DBReservation{" +
                "reservations=" + reservations +
                '}';
    }
}
