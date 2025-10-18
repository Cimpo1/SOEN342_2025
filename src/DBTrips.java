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

    public void setTrips(HashSet<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "DBTrips{" +
                "trips=" + trips +
                '}';
    }
}
