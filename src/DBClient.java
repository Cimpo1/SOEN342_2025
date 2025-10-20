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

    @Override
    public String toString() {
        return "DBClient{" +
                "clients=" + clients +
                '}';
    }
}
