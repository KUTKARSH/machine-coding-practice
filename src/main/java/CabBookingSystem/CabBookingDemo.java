package CabBookingSystem;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
class Passenger {
    private String id;
    private List<Trip> trips;
    private String lastTripId;
    private Integer totalRides;

    public Passenger(String id) {
        this.id = id;
        this.totalRides = 0;
        this.lastTripId = null;
    }
}

@Getter
@Setter
class Trip {
    private String tripId;
    int cabId;
    private String passengerId;
    private int startPoint;
    private int endPoint;
    private double fare;
    private Boolean completed;

    public Trip(String tripId, int cabId, String passengerId, int startPoint, int endPoint) {
        this.tripId = tripId;
        this.cabId = cabId;
        this.passengerId = passengerId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.fare = 0;
        this.completed = false;
    }
}

@Getter
@Setter
class Cab {
    private int id;
    private int position;
    private double totalEarnings;
    private String lastTripId;
    private Integer totalRides;

    public Cab(int id, int position) {
        this.id = id;
        this.position = position;
        this.totalEarnings = 0;
        this.totalRides = 0;
        this.lastTripId = null;

    }
}

@Getter
@Setter
class CabBookingSystem {
    private int k;
    private int n;
    private int r;
    private Map<Integer, Cab> cabs;
    private Map<String, Passenger> passengers;
    private Map<String, Trip> trips;
    private ReadWriteLock lock;
    private AtomicInteger tripIdCounter;
    private TreeSet<Cab> availableCabs;
    private Comparator<Cab> cabComparator = Comparator.comparingInt((Cab c) -> c.getPosition())
            .thenComparingInt(c -> c.getId());

    public CabBookingSystem(int k, int n, int r) {
        this.k = k;
        this.n = n;
        this.r = r;
        this.cabs = new HashMap<>();
        this.passengers = new HashMap<>();
        this.trips = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.tripIdCounter = new AtomicInteger(0);
        this.availableCabs = new TreeSet<>(cabComparator);
        Random rand = new Random();
        for (int i = 1; i <= n; i++) {
            int pos = rand.nextInt(k) + 1;
            Cab cab = new Cab(i ,pos);
            cabs.put(i, cab);
            availableCabs.add(cab);
        }
    }

    public Trip bookCab(String passengerId, int pickup, int drop) {
        lock.writeLock().lock();
        try {
            Passenger passenger = passengers.get(passengerId);
            if (Objects.isNull(passenger)) {
                passenger = new Passenger(passengerId);
                passengers.put(passengerId, passenger);
            }

            Cab lowerBound = new Cab(Integer.MIN_VALUE, pickup - r);
            Cab upperBound = new Cab(Integer.MAX_VALUE, pickup + r);
            NavigableSet<Cab> inRange = availableCabs.subSet(lowerBound, true, upperBound, true);

            // inRange (logN)

            Cab bestCab = null;
            int minDist = Integer.MAX_VALUE;

            for (Cab cab: inRange) {
                int dist = Math.abs(cab.getPosition() - pickup);
                if (dist > r) {
                    continue;
                }
                if (Objects.isNull(bestCab) || dist < minDist || (dist == minDist && cab.getId() < bestCab.getId())) {
                    bestCab = cab;
                    minDist = dist;
                }
            }
            if (Objects.isNull(bestCab)) {
                System.out.println("No can available within radius for passenger: " + passengerId);
                return null;
            }
            String tripId = "TRIP" + tripIdCounter.incrementAndGet();
            Trip trip = new Trip(tripId, bestCab.getId(), passengerId, pickup, drop);
            trips.put(tripId, trip);

            availableCabs.remove(bestCab);
            System.out.println("Cab booked. TripId : " + tripId + " , cabId : " + bestCab.getId());
            return trip;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void endRide(String tripId) {
        lock.writeLock().lock();
        try {
            Trip trip = trips.get(tripId);
            if (Objects.isNull(trip)) {
                System.out.println("Invalid trip id: " + tripId);
                return;
            }
            if (trip.getCompleted()) {
                System.out.println("Trip already ended " + tripId);
                return;
            }
            double fare = 10 * Math.abs(trip.getEndPoint() - trip.getStartPoint());
            trip.setFare(fare);
            trip.setCompleted(true);

            Cab cab = cabs.get(trip.getCabId());
            cab.setPosition(trip.getEndPoint());
            cab.setTotalEarnings(cab.getTotalEarnings() + fare);
            cab.setTotalRides(cab.getTotalRides() + 1);
            cab.setLastTripId(tripId);
            availableCabs.add(cab);

            Passenger passenger =  passengers.get(trip.getPassengerId());
            passenger.setTotalRides(passenger.getTotalRides() + 1);
            passenger.setLastTripId(tripId);

            System.out.println("Ride ended for driver id " + cab.getId() + " pickup point " +
                    trip.getStartPoint() + " drop point " + trip.getEndPoint() + "Total fare = " + fare);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void printDetails(String id) {
        lock.readLock().lock();
        try {
            if (trips.containsKey(id)) {
                Trip trip = trips.get(id);
                System.out.println("Trip = " + trip.getTripId() +
                "Driver - " + trip.cabId +
                        "Passenger - " + trip.getPassengerId()
                        + " from " + trip.getStartPoint()
                        + " to " + trip.getEndPoint()
                        + " Fare = " + trip.getFare()
                );
            } else if (passengers.containsKey(id)) {
                Passenger passenger = passengers.get(id);
                System.out.println("Passenger = " + passenger.getId()
                + " Total rides = " + passenger.getTotalRides());
                if (passenger.getLastTripId() != null) {
                    Trip lastTrip = trips.get(passenger.getLastTripId());
                    System.out.println(" LastRide = " + lastTrip.getStartPoint() +
                            " to " + lastTrip.getEndPoint()
                    + " Fare : " + lastTrip.getFare());
                }
            } else {
                System.out.println("Nort found");
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}

public class CabBookingDemo {

     public static void main(String[] args) {
         CabBookingSystem system = new CabBookingSystem(20, 3, 10);
         for (Cab cab: system.getAvailableCabs()) {
             System.out.print(cab.getPosition() + " ");
         }
         System.out.println();
         Trip trip1 = system.bookCab("P1", 5, 15);
         Trip trip2 = system.bookCab("P2", 20, 15);
         system.endRide(trip1.getTripId());
         if (Objects.nonNull(trip1)) {
             system.endRide(trip1.getTripId());
         }
         if (Objects.nonNull(trip2)) {
             system.endRide(trip2.getTripId());
         }
         system.printDetails(trip1.getTripId());
         system.printDetails(trip2.getTripId());


     }


}
