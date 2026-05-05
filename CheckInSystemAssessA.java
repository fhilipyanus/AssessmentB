import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class CheckInSystemAssessA {

    private static int baggageIdSequence = 1;
    private static int paymentIdSequence = 1;
    private static int boardingPassIdSequence = 1;

    public static String nextBaggageId() {
        return "" + baggageIdSequence++;
    }

    public static String nextPaymentId() {
        return "" + paymentIdSequence++;
    }

    public static int nextBoardingPassId() {
        return boardingPassIdSequence++;
    }

    public static void main(String[] args) {
        Random random = new Random();
        List<Flight> flights = new ArrayList<>();
        flights.add(new Flight("D7 221", "Sydney", "Kuala Lumpur", 550));
        flights.add(new Flight("JQ 501", "Sydney", "Melbourne", 100));
        flights.add(new Flight("QF 500", "Sydney", "Brisbane", 95));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("BK1001", "2026-04-19", new Passenger("Javier Oscar", 34), flights.get(0)));
        bookings.add(new Booking("BK2002", "2026-04-20", new Passenger("Fhilip Yanus", 52), flights.get(1)));
        bookings.add(new Booking("BK3003", "2026-04-21", new Passenger("Jeson Kayleen Dharmawan", 29), flights.get(2)));

        System.out.println("=== Airport Check-In System ===\n");
        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Choose an option: ");
            int choice = In.nextInt();
            if (choice == 1) {
                menuSeatSelection(bookings);
            } else if (choice == 2) {
                menuBaggageCheckIn(bookings);
            } else if (choice == 3) {
                menuCreatePrintBoardingPass(bookings);
            } else if (choice == 4) {
                menuRetrieveFlightBookingInfo(flights, bookings);
            } else if (choice == 5) {
                menuExtraPayments(bookings);
            } else if (choice == 6) {
                menuIdentityVerification(bookings);
            } else if (choice == 7) {
                menuContrabandScreening(random, bookings);
            } else if (choice == 8) {
                menuSelfCheckIn(flights, bookings);
            } else if (choice == 9) {
                menuAgentCheckIn(flights, bookings);
            } else if (choice == 0) {
                running = false;
                System.out.println("Thank you. Goodbye.");
            } else {
                System.out.println("Invalid option. Try again.");
            }
            System.out.println();
        }
    }

    static void printMainMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println(" 1. Seat Selection");
        System.out.println(" 2. Baggage Check-In");
        System.out.println(" 3. Create and Print Boarding Pass");
        System.out.println(" 4. Retrieve Flight and Booking Information");
        System.out.println(" 5. Extra Payments (baggage / seat / priority boarding)");
        System.out.println(" 6. Identity Verification");
        System.out.println(" 7. Check Baggage for Contraband");
        System.out.println(" 8. Self Check-In (full flow)");
        System.out.println(" 9. Agent-Assisted Check-In");
        System.out.println(" 0. Exit");
    }

    static Booking promptBooking(List<Booking> bookings) {
        System.out.print("Enter booking reference: ");
        String ref = In.nextLine();
        return findBooking(bookings, ref);
    }

    static Booking findBooking(List<Booking> bookings, String ref) {
        if (ref == null) {
            return null;
        }
        for (Booking b : bookings) {
            if (b.bookingNum.equals(ref)) {
                return b;
            }
        }
        return null;
    }

    static void menuSeatSelection(List<Booking> bookings) {
        System.out.println("--- Seat Selection ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        Flight flight = booking.associatedFlight;
        if (flight == null) {
            System.out.println("No flight on this booking.");
            return;
        }
        System.out.print("Enter seat row (1-30): ");
        int row = In.nextInt();
        System.out.print("Enter seat column (A-F): ");
        String col = In.nextLine();
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            System.out.println("No such seat on this flight.");
            return;
        }
        booking.assignSeat(seat);
        if (booking.assignedSeatForDisplay() == seat) {
            System.out.println("Seat " + seat.seatNumber + " assigned to booking " + booking.bookingNum + ".");
        } else {
            System.out.println("Seat could not be assigned (unavailable or already taken).");
        }
    }

    static void menuBaggageCheckIn(List<Booking> bookings) {
        System.out.println("--- Baggage Check-In ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        System.out.print("Enter baggage weight (kg): ");
        double weight = In.nextDouble();
        Baggage bag = new Baggage(nextBaggageId(), weight);
        bag.markedCheckIn();
        booking.baggage = bag;
        booking.baggageChecked = true;
        System.out.println("Baggage checked in for booking " + booking.bookingNum + ".");
    }

    static void menuCreatePrintBoardingPass(List<Booking> bookings) {
        System.out.println("--- Create and Print Boarding Pass ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        Flight flight = booking.associatedFlight;
        if (flight == null) {
            System.out.println("No flight on this booking.");
            return;
        }
        int passId = nextBoardingPassId();
        Seat seat = booking.assignedSeatForDisplay();
        if (seat == null) {
            System.out.print("No seat on booking yet. Enter seat row (1-30): ");
            int row = In.nextInt();
            System.out.print("Enter seat column (A-F): ");
            String col = In.nextLine();
            seat = flight.findSeatByNumber("" + row + col);
            if (seat == null) {
                System.out.println("Invalid seat.");
                return;
            }
        }
        booking.assignSeat(seat);
        BoardingPass pass = new BoardingPass(passId, seat, flight, booking.priorityBoarding);
        System.out.println(pass);
    }

    static void menuRetrieveFlightBookingInfo(List<Flight> flights, List<Booking> bookings) {
        System.out.println("--- Flight and Booking Information ---");
        System.out.println(" 1. Look up by booking reference");
        System.out.println(" 2. List all flights");
        System.out.println(" 3. List all bookings");
        System.out.print("Choice: ");
        int sub = In.nextInt();
        if (sub == 1) {
            Booking b = promptBooking(bookings);
            if (b == null) {
                System.out.println("Booking not found.");
                return;
            }
            printBookingDetails(b);
        } else if (sub == 2) {
            for (Flight f : flights) {
                System.out.println(f);
            }
        } else if (sub == 3) {
            for (Booking bk : bookings) {
                printBookingDetails(bk);
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    static void printBookingDetails(Booking b) {
        System.out.println("Booking reference: " + b.bookingNum);
        System.out.println("Date: " + b.bookingDate);
        if (b.passenger != null) {
            System.out.println("Passenger: " + b.passenger.name + " (age " + b.passenger.age + ")");
        }
        if (b.associatedFlight != null) {
            Flight f = b.associatedFlight;
            System.out.println("Flight: " + f.flightNumber + " | " + f.departureLocation + " -> " + f.arrivalLocation
                    + " | duration " + f.flightDuration + " min");
        }
        Seat s = b.assignedSeatForDisplay();
        String seatLabel;
        if (s == null) {
            seatLabel = "not assigned";
        } else {
            seatLabel = s.seatNumber;
        }
        System.out.println("Seat: " + seatLabel);
        System.out.println("Baggage checked: " + b.baggageChecked);
        if (b.baggage != null) {
            System.out.println("Baggage ID: " + b.baggage.baggageID + " | weight " + b.baggage.weight + " kg | "
                    + "contraband scan: " + b.baggage.contrabandFlag);
        }
        System.out.println("Priority boarding purchased: " + b.priorityBoarding);
        System.out.println("---");
    }

    static void menuExtraPayments(List<Booking> bookings) {
        System.out.println("--- Extra Payments ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        System.out.println(" 1. Extra baggage fee ($35)");
        System.out.println(" 2. Preferred seat selection fee ($25)");
        System.out.println(" 3. Priority boarding ($40)");
        System.out.print("Choose fee type: ");
        int t = In.nextInt();
        double amount;
        String paymentType;
        if (t == 1) {
            if (booking.baggage == null) {
                System.out.println("No baggage on this booking. Check in baggage before paying extra baggage fees.");
                return;
            }
            amount = 35;
            paymentType = "EXTRA_BAGGAGE";
        } else if (t == 2) {
            amount = 25;
            paymentType = "SEAT_SELECTION";
        } else if (t == 3) {
            amount = 40;
            paymentType = "PRIORITY_BOARDING";
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        Payment p = new Payment(nextPaymentId(), amount, paymentType);
        if (p.processPayment()) {
            if (t == 1) {
                System.out.print("Enter additional baggage weight (kg) to add: ");
                double weightToAdd = In.nextDouble();
                booking.baggage.updateWeight(weightToAdd);
                System.out.println("Baggage weight updated. Total weight: " + booking.baggage.weight + " kg");
            } else if (t == 3) {
                booking.priorityBoarding = true;
            }
            System.out.println("Extra charge recorded for booking " + booking.bookingNum + ".");
        }
    }

    static void menuIdentityVerification(List<Booking> bookings) {
        System.out.println("--- Identity Verification ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (booking.passenger == null) {
            System.out.println("No passenger on file for this booking.");
            return;
        }
        System.out.print("Enter passenger full name (as on booking): ");
        String entered = In.nextLine();
        if (entered.equals(booking.passenger.name)) {
            System.out.println("Identity verified: name matches booking " + booking.bookingNum + ".");
        } else {
            System.out.println("Verification failed: name does not match booking record.");
        }
    }

    static void menuContrabandScreening(Random random, List<Booking> bookings) {
        System.out.println("--- Contraband Screening (simulated) ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (booking.baggage == null) {
            System.out.println("No baggage checked in for this booking.");
            return;
        }
        booking.baggage.scanForContraband(random);
        System.out.println("Baggage " + booking.baggage.baggageID + " status: " + booking.baggage.contrabandFlag);
    }

    static void menuSelfCheckIn(List<Flight> flights, List<Booking> bookings) {
        System.out.println("--- Self Check-In ---");
        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (booking.passenger == null || booking.associatedFlight == null) {
            System.out.println("Booking is missing passenger or flight. Cannot continue.");
            return;
        }
        System.out.print("Enter your full name for verification: ");
        String name = In.nextLine();
        if (!name.equals(booking.passenger.name)) {
            System.out.println("Identity verification failed. Self check-in aborted.");
            return;
        }
        System.out.println("Identity verified.");
        Flight flight = booking.associatedFlight;
        SelfCheckIn kiosk = new SelfCheckIn(1, "SELF-01", flight);
        System.out.println("(Follow prompts for seat, baggage, payment, boarding pass.)");
        kiosk.verifyIdentity();
        kiosk.selectSeat();
        kiosk.checkInBaggage();
        kiosk.handlePayment();
        kiosk.createBoardingPass();
        System.out.println("Self check-in sequence finished for booking " + booking.bookingNum + ".");
    }

    static void menuAgentCheckIn(List<Flight> flights, List<Booking> bookings) {
        System.out.println("--- Agent-Assisted Check-In ---");
        CheckInAgent agent = new CheckInAgent();
        agent.agentID = "AGT01";
        agent.name = "Fred";

        Booking booking = promptBooking(bookings);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (booking.passenger == null || booking.associatedFlight == null) {
            System.out.println("Booking is missing passenger or flight. Cannot continue.");
            return;
        }
        System.out.print("Passenger name at counter: ");
        String atCounter = In.nextLine();
        Passenger presented = new Passenger(atCounter, booking.passenger.age);

        CounterCheckIn counter = new CounterCheckIn(agent, "C12", booking.associatedFlight);
        if (!counter.verifyDocuments(booking, presented)) {
            System.out.println("Document verification failed. Agent check-in aborted.");
            return;
        }
        System.out.println("Documents verified.");
        System.out.println("(Agent-assisted flow: seat, baggage, payment, boarding pass.)");
        counter.verifyIdentity();
        counter.selectSeat();
        counter.checkInBaggage();
        counter.handlePayment();
        counter.createBoardingPass();
        System.out.println("Agent-assisted check-in sequence finished for booking " + booking.bookingNum + ".");
    }
}

enum ContrabandFlag {
    CLEAR,
    FLAGGED
}

enum SeatSelectionStatus {
    NOT_SELECTED,
    SELECTED
}

enum PaymentStatus {
    PAID,
    PENDING,
    UNPAID
}

class Flight {
    String flightNumber;
    String departureLocation;
    String arrivalLocation;
    float flightDuration;
    List<Seat> seats = new ArrayList<>();

    Flight(String flightNumber, String depatureLocation, String arrivalLocation, float flightDuration) {
        this.flightNumber = flightNumber;
        this.departureLocation = depatureLocation;
        this.arrivalLocation = arrivalLocation;
        this.flightDuration = flightDuration;

        String[] cols = { "A", "B", "C", "D", "E", "F" };
        for (int row = 1; row <= 30; row++) {
            for (String col : cols) {
                seats.add(new Seat("" + row + col, "Economy"));
            }
        }
    }

    public void addSeat(Seat seat) {
        if (seat != null) {
            seats.add(seat);
        }
    }

    public Seat findSeatByNumber(String seatNumber) {
        if (seatNumber == null) {
            return null;
        }
        for (Seat s : seats) {
            if (s.seatNumber.equals(seatNumber)) {
                return s;
            }
        }
        return null;
    }

    public String toString() {
        return "Flight{" +
                "flightNumber=" + flightNumber +
                ", departureLocation=" + departureLocation +
                ", arrivalLocation=" + arrivalLocation +
                ", flightDuration=" + flightDuration +
                ", seats=" + seats +
                '}';
    }

}

class Booking {
    String bookingNum;
    boolean baggageChecked;
    String bookingDate;

    Passenger passenger;
    Flight associatedFlight;
    Baggage baggage;
    boolean priorityBoarding;

    private Seat assignedSeat;
    private boolean isConfirmed;
    private boolean isCancelled;

    Booking(String bookingNum, String bookingDate, Passenger passenger, Flight associatedFlight) {
        this.bookingNum = bookingNum;
        this.bookingDate = bookingDate;
        this.passenger = passenger;
        this.associatedFlight = associatedFlight;
        this.baggageChecked = false;
        this.isConfirmed = false;
        this.isCancelled = false;
        this.priorityBoarding = false;
    }

    Seat assignedSeatForDisplay() {
        return assignedSeat;
    }

    void confirmBooking() {
        if (isCancelled) {
            return;
        }
        isConfirmed = true;
    }

    void cancelBooking() {
        isCancelled = true;
        isConfirmed = false;
        if (assignedSeat != null) {
            assignedSeat.releaseSeat();
            assignedSeat = null;
        }
    }

    void assignSeat(Seat seat) {
        if (seat == null || isCancelled) {
            return;
        }
        if (!seat.checkAvailability()) {
            return;
        }

        if (assignedSeat != null) {
            assignedSeat.releaseSeat();
        }

        seat.assignSeat();
        assignedSeat = seat;
    }

    @Override
    public String toString() {
        String seatNum;
        if (assignedSeat == null) {
            seatNum = "None";
        } else {
            seatNum = assignedSeat.seatNumber;
        }
        return "Booking{bookingNum='" + bookingNum + "', baggageChecked=" + baggageChecked
                + ", bookingDate='" + bookingDate + "', confirmed=" + isConfirmed
                + ", cancelled=" + isCancelled + ", seat=" + seatNum + "}";
    }
}

class Passenger {
    String name;
    int age;

    Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + " (age " + age + ")";
    }
}

class CheckedInBooking extends Booking {
    String checkInTime;

    CheckedInBooking(String bookingNum, String bookingDate, String checkInTime) {
        super(bookingNum, bookingDate, null, null);
        this.checkInTime = checkInTime;
    }
}

class ConfirmedBooking extends Booking {

    SeatSelectionStatus seatSelectionStatus = SeatSelectionStatus.NOT_SELECTED;
    String bookingDate;

    ConfirmedBooking(String bookingNum, String bookingDate) {
        super(bookingNum, bookingDate, null, null);
        this.bookingDate = bookingDate;
    }

    void confirmSeatSelection() {
        seatSelectionStatus = SeatSelectionStatus.SELECTED;
    }
}

class Baggage {
    String baggageID;
    double weight;
    boolean checkedIn;
    ContrabandFlag contrabandFlag;

    public Baggage(String baggageID, double weight) {
        this.baggageID = baggageID;
        this.weight = weight;
        checkedIn = false;
        contrabandFlag = ContrabandFlag.CLEAR;
    }

    void updateWeight(double weight) {
        this.weight += weight;
    }

    void markedCheckIn() {
        checkedIn = true;
    }

    void flagContraband() {
        contrabandFlag = ContrabandFlag.FLAGGED;
    }

    void scanForContraband(Random random) {
        double roll = random.nextDouble();
        if (roll < 0.12) {
            flagContraband();
            System.out.println("Alert: screening flagged potential contraband (simulated).");
        } else {
            System.out.println("Screening clear: no contraband detected (simulated).");
        }
    }

}

class BoardingPass {
    int boardingPassID;
    Seat seat;
    Flight flight;
    boolean priorityBoarding;

    public BoardingPass(int boardingPassID, Seat seat, Flight flight) {
        this(boardingPassID, seat, flight, false);
    }

    public BoardingPass(int boardingPassID, Seat seat, Flight flight, boolean priorityBoarding) {
        this.boardingPassID = boardingPassID;
        this.seat = seat;
        this.flight = flight;
        this.priorityBoarding = priorityBoarding;
    }

    @Override
    public String toString() {
        String seatNum;
        if (seat == null) {
            seatNum = "TBD";
        } else {
            seatNum = seat.seatNumber;
        }
        String fn;
        if (flight == null) {
            fn = "N/A";
        } else {
            fn = flight.flightNumber;
        }
        String route;
        if (flight == null) {
            route = "";
        } else {
            route = flight.departureLocation + " -> " + flight.arrivalLocation;
        }
        String priorityLabel;
        if (priorityBoarding) {
            priorityLabel = "YES";
        } else {
            priorityLabel = "no";
        }
        return "========== BOARDING PASS ==========\n"
                + "Pass ID: " + boardingPassID + "\n"
                + "Flight: " + fn + "  |  " + route + "\n"
                + "Seat: " + seatNum + "\n"
                + "Priority boarding: " + priorityLabel + "\n"
                + "====================================";
    }

}

interface CheckInService {
    void selectSeat();

    void checkInBaggage();

    void createBoardingPass();

    void verifyIdentity();

    void handlePayment();
}

class CheckIn implements CheckInService {

    protected static final double STANDARD_TICKET_PRICE = 390.0;

    int checkInID;
    Flight flight;
    Seat seatSelectedThisSession;

    public CheckIn(int checkInID) {
        this.checkInID = checkInID;
    }

    public CheckIn(int checkInID, Flight flight) {
        this.checkInID = checkInID;
        this.flight = flight;
    }

    @Override
    public void selectSeat() {
        if (flight == null) {
            System.out.println("No flight assigned; cannot select seat.");
            return;
        }
        System.out.print("Enter seat row (1-30): ");
        int row = In.nextInt();
        System.out.print("Enter seat column (A-F): ");
        String col = In.nextLine();
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            System.out.println("No such seat on this flight: " + seatNumber);
            return;
        }
        selectSeat(seat);
    }

    public void selectSeat(Seat seat) {
        if ((seat != null) && (seat.checkAvailability())) {
            seat.assignSeat();
            seatSelectedThisSession = seat;
            System.out.println("Seat selected successfully");
        } else {
            System.out.println("Seat is not available");
        }
    }

    @Override
    public void checkInBaggage() {
        System.out.print("Enter baggage weight (kg): ");
        double weight = In.nextDouble();
        checkInBaggage(new Baggage(CheckInSystemAssessA.nextBaggageId(), weight));
    }

    public void checkInBaggage(Baggage baggage) {
        if (baggage != null) {
            baggage.markedCheckIn();
            System.out.println("Baggage checked in");
        } else {
            System.out.println("No baggage");
        }
    }

    @Override
    public void createBoardingPass() {
        if (flight == null) {
            System.out.println("No flight assigned; cannot create boarding pass.");
            return;
        }
        int boardingPassID = CheckInSystemAssessA.nextBoardingPassId();
        System.out.print("Enter seat row (1-30) for boarding pass: ");
        int row = In.nextInt();
        System.out.print("Enter seat column (A-F): ");
        String col = In.nextLine();
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            System.out.println("No such seat on this flight: " + seatNumber);
            return;
        }
        createBoardingPass(boardingPassID, seat, flight);
    }

    public void createBoardingPass(int boardingPassID, Seat seat, Flight flight) {
        BoardingPass boardingPass = new BoardingPass(boardingPassID, seat, flight);
    }

    @Override
    public void verifyIdentity() {
        System.out.println("Identity verified");
    }

    @Override
    public void handlePayment() {
        System.out.print("Enter amount: ");
        double amount = In.nextDouble();
        System.out.print("Enter payment type: ");
        String paymentType = In.nextLine();
        handlePayment(new Payment(CheckInSystemAssessA.nextPaymentId(), amount, paymentType));
    }

    public void handlePayment(Payment payment) {
        if (payment != null) {
            boolean success = payment.processPayment();
            if (success) {
                System.out.println("Payment completed successfully");
            } else {
                System.out.println("Payment failed. Please try again");
            }
        } else {
            System.out.println("No payment required");
        }
    }
}

class SelfCheckIn extends CheckIn {

    String machineID;

    public SelfCheckIn(int checkInID, String machineID, Flight flight) {
        super(checkInID, flight);
        this.machineID = machineID;
    }

    @Override
    public void handlePayment() {
        handlePayment(new Payment(CheckInSystemAssessA.nextPaymentId(), STANDARD_TICKET_PRICE, "STANDARD_TICKET"));
    }

    @Override
    public void createBoardingPass() {
        if (flight == null) {
            System.out.println("No flight assigned; cannot create boarding pass.");
            return;
        }
        if (seatSelectedThisSession == null) {
            System.out.println("No seat selected; cannot create boarding pass.");
            return;
        }
        int boardingPassID = CheckInSystemAssessA.nextBoardingPassId();
        createBoardingPass(boardingPassID, seatSelectedThisSession, flight);
    }

    void startSelfCheckIn() {
        System.out.println("Starting self cehck-in at machine: " + machineID);

        verifyIdentity();
        selectSeat();
        checkInBaggage();
        handlePayment();
        createBoardingPass();

        System.out.println("Self check-in completed.");
    }
}

class CounterCheckIn extends CheckIn {
    CheckInAgent agent;
    String counterID;

    public CounterCheckIn(CheckInAgent agent, String counterID, Flight flight) {
        super(0, flight);
        this.agent = agent;
        this.counterID = counterID;
    }

    @Override
    public void handlePayment() {
        handlePayment(new Payment(CheckInSystemAssessA.nextPaymentId(), STANDARD_TICKET_PRICE, "STANDARD_TICKET"));
    }

    @Override
    public void createBoardingPass() {
        if (flight == null) {
            System.out.println("No flight assigned; cannot create boarding pass.");
            return;
        }
        if (seatSelectedThisSession == null) {
            System.out.println("No seat selected; cannot create boarding pass.");
            return;
        }
        int boardingPassID = CheckInSystemAssessA.nextBoardingPassId();
        createBoardingPass(boardingPassID, seatSelectedThisSession, flight);
    }

    boolean verifyDocuments(Booking booking, Passenger passenger) {
        System.out.println("Check-in at counter: " + counterID);
        return agent != null && agent.verifyDocuments(booking, passenger);
    }

    void assistCheckIn(Booking booking, Passenger passenger) {
        System.out.println("Check-in at counter: " + counterID);

        if (verifyDocuments(booking, passenger)) {
            System.out.println("Documents verified");
            System.out.println("Processing booking...");
            System.out.println("Check-in successfull");
        } else {
            System.out.println("Verification failed");
        }
    }

    void assistPassenger(Passenger passenger) {
        if (agent != null) {
            agent.assistCheckIn(passenger);
        }
    }
}

class CheckInAgent {
    String agentID;
    String name;

    boolean verifyDocuments(Booking booking, Passenger passenger) {
        System.out.println("Verifying documents for " + passenger);
        if (booking == null || booking.passenger == null || passenger == null) {
            return false;
        }
        if (booking.passenger.name == null || passenger.name == null) {
            return false;
        }
        if (booking.passenger.name.equals(passenger.name)) {
            return true;
        }
        return false;
    }

    void assistCheckIn(Passenger passenger) {
        System.out.println("Assisting passenger : " + passenger);
    }
}

class Payment {
    String paymentID;
    double amount;
    String paymentType;
    PaymentStatus status;

    public Payment(String paymentID, double amount, String paymentType) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.paymentType = paymentType;
        this.status = PaymentStatus.UNPAID;
    }

    public boolean processPayment() {
        if (amount <= 0) {
            System.out.println("Payment " + paymentID + " failed: invalid amount.");
            status = PaymentStatus.PENDING;
            return false;
        }
        status = PaymentStatus.PAID;
        System.out.println("Payment " + paymentID + " processed successfully. Amount: $" + amount);
        return true;
    }

    void refundPayment() {
        if (status == PaymentStatus.PAID) {
            status = PaymentStatus.UNPAID;
            System.out.println("Payment " + paymentID + " has been refunded.");
        } else {
            System.out.println("Payment " + paymentID + " cannot be refunded (current status: " + status + ").");
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID='" + paymentID + '\'' +
                ", amount=" + amount +
                ", paymentType='" + paymentType + '\'' +
                ", status=" + status +
                '}';
    }
}

class Seat {
    String seatNumber;
    String seatClass;
    boolean isAvailable;

    Seat(String seatNumber, String seatClass) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.isAvailable = true;
    }

    void releaseSeat() {
        this.isAvailable = true;
    }

    void assignSeat() {
        this.isAvailable = false;
    }

    boolean checkAvailability() {
        return this.isAvailable;
    }

    public String toString() {
        return "Seat{" +
                "seatNumber=" + seatNumber +
                ", seatClass=" + seatClass +
                ", isAvailable=" + isAvailable +
                '}';
    }

}
