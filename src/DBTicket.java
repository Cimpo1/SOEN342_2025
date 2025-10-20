import java.util.HashSet;

public class DBTicket {
    private HashSet<Ticket> tickets;

    public DBTicket() {
        this.tickets = new HashSet<>();
    }

    public HashSet<Ticket> getTickets() {
        return tickets;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void setTickets(HashSet<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "DBTicket{" +
                "tickets=" + tickets +
                '}';
    }
}
