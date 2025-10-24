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

    // create reservation
    public Reservation createReservation(Client client, Trip trip) {
        Reservation newReservation = new Reservation(client, trip);
        this.reservations.add(newReservation);
        return newReservation;
    }

    private Reservation find(Reservation reservation) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservation.getId())) {
                return r;
            }
        }
        return null;
    }

    // set ticket for reservation
    public void setTicketForReservation(Reservation reservation, Ticket ticket) {
        try {
            this.find(reservation).setTicket(ticket);
        } catch (Exception e) {
            System.out.println("Error setting ticket for reservation: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "DBReservation{" +
                "reservations=" + reservations +
                '}';
    }
}
