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

    // get ticket by reservation
    public Ticket getTicketByReservation(Reservation reservation) {
        for (Ticket ticket : tickets) {
            if (ticket.getReservation().equals(reservation)) {
                return ticket;
            }
        }
        return null; // or throw an exception if not found
    }

    // get ticket by id
    public Ticket getTicketById(String id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(id)) {
                return ticket;
            }
        }
        return null; // or throw an exception if not found
    }

    // create ticket
    public Ticket createTicket(Reservation reservation) {
        Ticket newTicket = new Ticket(reservation);
        this.tickets.add(newTicket);
        return newTicket;
    }

    @Override
    public String toString() {
        return "DBTicket{" +
                "tickets=" + tickets +
                '}';
    }
}
