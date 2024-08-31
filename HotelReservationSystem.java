import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Room {
    private int roomNumber;
    private double pricePerDay;
    private boolean isReserved;

    public Room(int roomNumber, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.pricePerDay = pricePerDay;
        this.isReserved = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public String bookRoom() {
        if (!isReserved) {
            isReserved = true;
            return "Room " + roomNumber + " has been successfully booked.";
        } else {
            return "Room " + roomNumber + " is already booked.";
        }
    }

    public String cancelRoom() {
        if (isReserved) {
            isReserved = false;
            return "Room " + roomNumber + " has been successfully canceled.";
        } else {
            return "Room " + roomNumber + " is not currently booked.";
        }
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " - $" + pricePerDay + " per day";
    }
}

class SingleRoom extends Room {
    public SingleRoom(int roomNumber, double pricePerDay) {
        super(roomNumber, pricePerDay);
    }
}

class Reservation {
    private Room room;
    private String guestName;
    private int numberOfDays;

    public Reservation(Room room, String guestName, int numberOfDays) {
        this.room = room;
        this.guestName = guestName;
        this.numberOfDays = numberOfDays;
    }

    public double calculateTotalCost() {
        return room.getPricePerDay() * numberOfDays;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "Reservation for " + guestName + ": " + room + " for " + numberOfDays + " days.";
    }
}

class Hotel {
    private List<Room> rooms;
    private Map<Integer, Reservation> reservations;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new HashMap<>();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public String makeReservation(String guestName, int roomNumber, int numberOfDays, String phoneNumber) {
        Room room = findRoom(roomNumber);
        if (room != null) {
            String bookingStatus = room.bookRoom();
            if (bookingStatus.contains("successfully booked")) {
                Reservation reservation = new Reservation(room, guestName, numberOfDays);
                reservations.put(roomNumber, reservation);
                return reservation.toString() + "\nTotal cost: $" + reservation.calculateTotalCost();
            } else {
                return bookingStatus;
            }
        } else {
            return "Room " + roomNumber + " not found.";
        }
    }

    public String cancelReservation(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room != null) {
            if (reservations.containsKey(roomNumber)) {
                Reservation reservation = reservations.remove(roomNumber);
                room.cancelRoom();
                return "Reservation canceled for " + reservation.getRoom() + ".";
            } else {
                return "No reservation found for room " + roomNumber + ".";
            }
        } else {
            return "Room " + roomNumber + " not found.";
        }
    }

    public void listAvailableRooms() {
        System.out.println("Available Rooms:");
        for (Room room : rooms) {
            if (!room.isReserved()) {
                System.out.println(room);
            }
        }
    }

    public void listBookedRooms() {
        System.out.println("Booked Rooms:");
        for (Reservation reservation : reservations.values()) {
            System.out.println(reservation);
        }
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hotel hotel = new Hotel();
        hotel.addRoom(new SingleRoom(101, 100));
        hotel.addRoom(new SingleRoom(102, 100));
        hotel.addRoom(new SingleRoom(103, 100));
        hotel.addRoom(new SingleRoom(104, 100));
        hotel.addRoom(new SingleRoom(105, 100));

        boolean continueReservation = true;
        while (continueReservation) {
            System.out.println("\nWelcome to the Hotel Reservation System!");
            System.out.println("1. List available rooms");
            System.out.println("2. List booked rooms");
            System.out.println("3. Make a reservation");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. Exit");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    hotel.listAvailableRooms();
                    break;
                case 2:
                    hotel.listBookedRooms();
                    break;
                case 3:
                    System.out.print("Enter your name: ");
                    String guestName = scanner.nextLine();
                    System.out.print("Enter room number to reserve (101-105): ");
                    int roomNumber = scanner.nextInt();
                    System.out.print("Enter number of days: ");
                    int numberOfDays = scanner.nextInt();
                    System.out.print("Enter your phone number: ");
                    String phoneNumber = scanner.next();
                    String reservationDetails = hotel.makeReservation(guestName, roomNumber, numberOfDays, phoneNumber);
                    System.out.println(reservationDetails);
                    break;
                case 4:
                    System.out.print("Enter room number to cancel reservation (101-105): ");
                    int cancelRoomNumber = scanner.nextInt();
                    String cancelStatus = hotel.cancelReservation(cancelRoomNumber);
                    System.out.println(cancelStatus);
                    break;
                case 5:
                    continueReservation = false;
                    System.out.println("Thank you for using the Hotel Reservation System!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}
