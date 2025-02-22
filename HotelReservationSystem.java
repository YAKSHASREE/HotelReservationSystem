import java.util.*;

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

    public synchronized boolean isReserved() {
        return isReserved;
    }

    public synchronized void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public synchronized String bookRoom() {
        if (!isReserved) {
            isReserved = true;
            return "Room " + roomNumber + " has been successfully booked.";
        } else {
            return "Room " + roomNumber + " is already booked.";
        }
    }

    public synchronized String cancelRoom() {
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
    private final List<Room> rooms;
    private final Map<Integer, Reservation> reservations;

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

    public synchronized void makeReservation(String guestName, int roomNumber, int numberOfDays) {
        Thread bookingThread = new Thread(() -> {
            try {
                Room room = findRoom(roomNumber);
                if (room == null) {
                    System.out.println("Room " + roomNumber + " not found.");
                    return;
                }

                synchronized (room) {
                    String bookingStatus = room.bookRoom();
                    if (bookingStatus.contains("successfully booked")) {
                        Reservation reservation = new Reservation(room, guestName, numberOfDays);
                        reservations.put(roomNumber, reservation);
                        System.out.println(reservation.toString() + "\nTotal cost: $" + reservation.calculateTotalCost());
                    } else {
                        System.out.println(bookingStatus);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while booking room: " + e.getMessage());
            }
        });
        bookingThread.start();
    }

    public synchronized void cancelReservation(int roomNumber) {
        Thread cancelThread = new Thread(() -> {
            try {
                Room room = findRoom(roomNumber);
                if (room == null) {
                    System.out.println("Room " + roomNumber + " not found.");
                    return;
                }

                synchronized (room) {
                    if (reservations.containsKey(roomNumber)) {
                        Reservation reservation = reservations.remove(roomNumber);
                        room.cancelRoom();
                        System.out.println("Reservation canceled for " + reservation.getRoom() + ".");
                    } else {
                        System.out.println("No reservation found for room " + roomNumber + ".");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while canceling reservation: " + e.getMessage());
            }
        });
        cancelThread.start();
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
            try {
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
                        hotel.makeReservation(guestName, roomNumber, numberOfDays);
                        break;
                    case 4:
                        System.out.print("Enter room number to cancel reservation (101-105): ");
                        int cancelRoomNumber = scanner.nextInt();
                        hotel.cancelReservation(cancelRoomNumber);
                        break;
                    case 5:
                        continueReservation = false;
                        System.out.println("Thank you for using the Hotel Reservation System!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
