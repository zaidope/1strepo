public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private List<Booking> bookings;

    public void register() {}

    public boolean login(String email, String password) {
        return true;
    }

    public List<Booking> getBookingHistory() {
        return bookings;
    }
}

public class Event {
    private String eventId;
    private String eventName;
    private String description;
    private Venue venue;
    private List<Show> shows;

    public List<Show> getUpcomingShows() {
        return shows;
    }
}

public class Show {
    private String showId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Event event;
    private Venue venue;
    private List<Seat> seats;

    public List<Seat> getAvailableSeats() {
        return seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .toList();
    }
}

public class Venue {
    private String venueId;
    private String name;
    private String location;
    private List<Seat> seats;

    public List<Seat> getAllSeats() {
        return seats;
    }
}

public class Seat {
    private String seatId;
    private int row;
    private int number;
    private SeatType type;
    private SeatStatus status;

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public SeatType getType() {
        return type;
    }
}

enum SeatType {
    REGULAR, PREMIUM, VIP
}

enum SeatStatus {
    AVAILABLE, BOOKED
}

public class Booking {
    private String bookingId;
    private User user;
    private Show show;
    private List<Seat> seats;
    private BookingStatus status;
    private LocalDateTime bookingTime;

    public boolean cancelBooking() {
        this.status = BookingStatus.CANCELLED;
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }
        return true;
    }

    public double calculateTotalAmount() {
        return seats.stream().mapToDouble(seat -> {
            switch (seat.getType()) {
                case REGULAR -> { return 200.0; }
                case PREMIUM -> { return 300.0; }
                case VIP -> { return 500.0; }
            }
            return 0;
        }).sum();
    }
}

enum BookingStatus {
    CONFIRMED, CANCELLED
}

public class Payment {
    private String paymentId;
    private Booking booking;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentTime;

    public boolean processPayment() {
        this.status = PaymentStatus.SUCCESS;
        return true;
    }
}

enum PaymentStatus {
    SUCCESS, FAILED, PENDING
}

public class BookingService {
    public Booking createBooking(User user, Show show, List<Seat> selectedSeats) {
        for (Seat seat : selectedSeats) {
            if (!seat.isAvailable()) {
                throw new RuntimeException("Seat already booked");
            }
        }

        selectedSeats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));

        Booking booking = new Booking();
        booking.setBookingId(UUID.randomUUID().toString());
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(selectedSeats);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        return booking;
    }

    public boolean cancelBooking(Booking booking) {
        return booking.cancelBooking();
    }
}

public class BookingRequest {
    private User user;
    private Show show;
    private List<Seat> seats;
}

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request.getUser(), request.getShow(), request.getSeats());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Boolean> cancelBooking(@RequestParam String bookingId) {
        Booking booking = mockGetBooking(bookingId);
        return ResponseEntity.ok(bookingService.cancelBooking(booking));
    }

    private Booking mockGetBooking(String id) {
        return new Booking(); 
    }
}
