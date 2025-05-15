import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@AllArgsConstructor
class Attendee {

    private static Integer userSequence = 1;

    private Integer userId;
    private String name;

    public Attendee(String name) {
        this.name = name;
        this.userId = userSequence++;
    }
}

@Setter
@Getter
class Room {

    private static Integer roomSequence = 1;

    private String roomName;
    private Integer roomId;
    private Boolean isBooked;
    private Integer capacity;
    private List<Meeting> meetings;
    public Room(String roomName, Integer roomId, Integer capacity) {
        this.roomName = roomName;
        this.roomId = roomSequence++;
        this.isBooked = false;
        this.capacity = capacity;
        this.meetings = new ArrayList<>();
    }
}

@Getter
@Setter
class Meeting {

    private static Integer bookingSequence = 1;

    private Integer bookingId;
    private Date startTime;
    private Date endTime;
    private List<Attendee> users;
    public Meeting(Date startTime, Date endTime, List<Attendee> users) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
        this.bookingId = bookingSequence++;
    }
}

@Getter
@Setter
class RoomManager {
    private List<Room> rooms;
    private List<Meeting> meetings;
    private HashMap<String, ReentrantLock> roomLocks;

    private RoomManager() {
        this.rooms = new ArrayList<>();
        this.meetings = new ArrayList<>();
        this.roomLocks = new HashMap<>();
    }

    private Room findVacantRoom(Date startDate, Date endDate) {
        boolean isConflict = false;
        Room vacantRoom = null;
        for (Room room: this.rooms) {
            List<Meeting> meetings = room.getMeetings();
            for (Meeting meeting : meetings) {
                if (meeting.getStartTime().compareTo(endDate) < 0 || meeting.getEndTime().compareTo(startDate) > 0) {
                    isConflict = true;
                    break;
                }
            }
            if (!isConflict) {
                vacantRoom = room;
                break;
            }
        }
        return vacantRoom;
    }

    public Meeting bookRoom(Date startTime, Date endTime, List<Attendee> users) {
        Room vacantRoom = findVacantRoom(startTime, endTime);
        if (Objects.nonNull(vacantRoom)) {
            Meeting meeting = new Meeting(startTime, endTime, users);
            meetings.add(meeting);
            return meeting;
        } else {
            return null;
        }
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

}



public class MeetingRoomBooking {

    public static void main(String[] args) {

    }

}
