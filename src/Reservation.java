public class Reservation {
    private String id;
    private Client client;
    private Trip trip;
    private Ticket ticket;

    public Reservation(Client client, Trip trip) {
        this.id = java.util.UUID.randomUUID().toString();
        this.client = client;
        this.trip = trip;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Trip getTrip() {
        return trip;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", client=" + client +
                '}';
    }
}
