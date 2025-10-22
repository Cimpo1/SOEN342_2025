import java.util.HashSet;

public class DBClient {
    private HashSet<Client> clients;

    public DBClient() {
        this.clients = new HashSet<>();
    }

    public HashSet<Client> getClients() {
        return clients;
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void setClients(HashSet<Client> clients) {
        this.clients = clients;
    }

    // create client
    public Client createClient(String firstName, String lastName, int age, int id) {
        Client newClient = new Client(firstName, lastName, age, id);
        this.clients.add(newClient);
        return newClient;
    }

    public Client getClientById(int id) {
        for (Client client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }
        return null; // or throw an exception if preferred
    }

    private Client find(Client client) {
        for (Client c : clients) {
            if (c.getId() == client.getId()) {
                return c;
            }
        }
        return null;
    }

    // add trip to client
    public void addTripToClient(Client client, Trip trip) {
        this.find(client).addTrip(trip);
    }

    @Override
    public String toString() {
        return "DBClient{" +
                "clients=" + clients +
                '}';
    }
}
