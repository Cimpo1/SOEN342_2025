import java.util.HashSet;

public class DBTrips {
    private HashSet<Trip> trips;

    public DBTrips() {
        this.trips = new HashSet<>();
    }

    public HashSet<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        this.trips.add(trip);
    }

    public Trip createTrip() {
        Trip newTrip = new Trip();
        this.trips.add(newTrip);
        return newTrip;
    }

    public void setTrips(HashSet<Trip> trips) {
        this.trips = trips;
    }

    public HashSet<Trip> getTripsByClient(Client client) {
        HashSet<Trip> clientTrips = new HashSet<>();
        for (Trip trip : trips) {
            for (Reservation reservation : trip.getReservations()) {
                if (reservation.getClient().getId() == client.getId()) {
                    clientTrips.add(trip);
                    break;
                }
            }
        }
        return clientTrips;
    }

    @Override
    public String toString() {
        return "DBTrips{" +
                "trips=" + trips +
                '}';
    }
}
