public class Reservation {
    private String id;
    private Client client;
    private Connection connection;
    private Ticket ticket;

    public Reservation(Client client, Connection connection) {
        this.id = java.util.UUID.randomUUID().toString();
        this.client = client;
        this.connection = connection;
        this.ticket = new Ticket(this);
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
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
                ", connection=" + connection +
                '}';
    }
}
