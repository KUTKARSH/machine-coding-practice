import java.util.*;

// Enums
enum Direction {
    UP, DOWN, IDLE
}

enum ElevatorState {
    MOVING, STOPPED, DOORS_OPEN
}

enum RequestType {
    INTERNAL, EXTERNAL
}

// Request.java
class Request {
    private final int floor;
    private final Direction direction;
    private final RequestType type;

    Request(int floor, Direction direction, RequestType type) {
        this.floor = floor;
        this.direction = direction;
        this.type = type;
    }

    int getFloor() { return floor; }
    Direction getDirection() { return direction; }
    RequestType getType() { return type; }
}

// Elevator.java

class Elevator {
    private final int id;
    private int currentFloor = 0;
    private Direction direction = Direction.IDLE;
    private ElevatorState state = ElevatorState.STOPPED;
    private final TreeSet<Integer> destinations = new TreeSet<>();

    Elevator(int id) {
        this.id = id;
    }

    int getId() { return id; }
    int getCurrentFloor() { return currentFloor; }
    Direction getDirection() { return direction; }
    ElevatorState getState() { return state; }
    boolean isIdle() { return direction == Direction.IDLE && destinations.isEmpty(); }

    void addDestination(int floor) {
        destinations.add(floor);
        if (currentFloor < floor) direction = Direction.UP;
        else if (currentFloor > floor) direction = Direction.DOWN;
    }

    void step() {
        if (destinations.isEmpty()) {
            direction = Direction.IDLE;
            state = ElevatorState.STOPPED;
            return;
        }

        int target = direction == Direction.UP ? destinations.first() : destinations.last();

        if (currentFloor < target) {
            currentFloor++;
            direction = Direction.UP;
            state = ElevatorState.MOVING;
        } else if (currentFloor > target) {
            currentFloor--;
            direction = Direction.DOWN;
            state = ElevatorState.MOVING;
        } else {
            destinations.remove(currentFloor);
            state = ElevatorState.DOORS_OPEN;
            System.out.println("Elevator " + id + " opening doors at floor " + currentFloor);
        }
    }
}

// ElevatorController.java

class ElevatorController {
    private final List<Elevator> elevators;

    ElevatorController(int numElevators) {
        elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    void handleExternalRequest(int floor, Direction direction) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int distance = Math.abs(e.getCurrentFloor() - floor);
            if (e.isIdle() || e.getDirection() == direction) {
                if (distance < minDistance) {
                    minDistance = distance;
                    bestElevator = e;
                }
            }
        }

        if (bestElevator == null) {
            bestElevator = elevators.get(0); // fallback
        }

        System.out.println("Assigning Elevator " + bestElevator.getId() + " to floor " + floor);
        bestElevator.addDestination(floor);
    }

    void handleInternalRequest(int elevatorId, int floor) {
        Elevator e = elevators.get(elevatorId);
        e.addDestination(floor);
    }

    void stepAll() {
        for (Elevator e : elevators) {
            e.step();
        }
    }

    void status() {
        for (Elevator e : elevators) {
            System.out.println("Elevator " + e.getId() + " - Floor: " + e.getCurrentFloor() + ", Direction: " + e.getDirection() + ", State: " + e.getState());
        }
    }
}

// ElevatorSystemDemo.java
class ElevatorSystem {
    private final ElevatorController controller;

    ElevatorSystem(int numElevators) {
        controller = new ElevatorController(numElevators);
    }

    void externalRequest(int floor, Direction direction) {
        controller.handleExternalRequest(floor, direction);
    }

    void internalRequest(int elevatorId, int floor) {
        controller.handleInternalRequest(elevatorId, floor);
    }

    void step() {
        controller.stepAll();
        controller.status();
        System.out.println("-------------------------");
    }
}

// Main.java
public class ElevatorSystemDemo {
    public static void main(String[] args) throws InterruptedException {
        ElevatorSystem system = new ElevatorSystem(3);

        system.externalRequest(5, Direction.UP);
        system.externalRequest(2, Direction.DOWN);
        system.internalRequest(0, 7);

        for (int i = 0; i < 15; i++) {
            system.step();
            Thread.sleep(500); // simulate time step
        }
    }
}
