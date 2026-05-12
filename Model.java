import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Model {

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

    private final SimpleIntegerProperty sampleVar = new SimpleIntegerProperty();
    private final SimpleStringProperty seatSelectionResult = new SimpleStringProperty("");
    private final SimpleStringProperty boardingPassResult = new SimpleStringProperty("");
    private final SimpleStringProperty retrieveInformationResult = new SimpleStringProperty("");
    private final SimpleStringProperty baggageCheckInResult = new SimpleStringProperty("");
    private final SimpleStringProperty extraPaymentsResult = new SimpleStringProperty("");
    private final SimpleStringProperty identityVerificationResult = new SimpleStringProperty("");
    private final SimpleStringProperty contrabandCheckResult = new SimpleStringProperty("");
    private final SimpleStringProperty selfCheckInResult = new SimpleStringProperty("");
    private final SimpleStringProperty agentCheckInResult = new SimpleStringProperty("");

    private final Random random = new Random();

    private final List<Flight> flights = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public Model() {
        flights.add(new Flight("D7 221", "Sydney", "Kuala Lumpur", 550));
        flights.add(new Flight("JQ 501", "Sydney", "Melbourne", 100));
        flights.add(new Flight("QF 500", "Sydney", "Brisbane", 95));

        bookings.add(new Booking("BK1001", "2026-04-19", new Passenger("Javier Oscar", 34), flights.get(0)));
        bookings.add(new Booking("BK2002", "2026-04-20", new Passenger("Fhilip Yanus", 52), flights.get(1)));
        bookings.add(new Booking("BK3003", "2026-04-21", new Passenger("Jeson Kayleen Dharmawan", 29), flights.get(2)));
    }

    public final void setSampleField(final int x) {
        this.sampleVarProperty().set(x);
    }

    public final SimpleIntegerProperty sampleVarProperty() {
        return this.sampleVar;
    }

    public final SimpleStringProperty seatSelectionResultProperty() {
        return this.seatSelectionResult;
    }

    public final SimpleStringProperty boardingPassResultProperty() {
        return this.boardingPassResult;
    }

    public final SimpleStringProperty retrieveInformationResultProperty() {
        return this.retrieveInformationResult;
    }

    public final SimpleStringProperty baggageCheckInResultProperty() {
        return this.baggageCheckInResult;
    }

    public final SimpleStringProperty extraPaymentsResultProperty() {
        return this.extraPaymentsResult;
    }

    public final SimpleStringProperty identityVerificationResultProperty() {
        return this.identityVerificationResult;
    }

    public final SimpleStringProperty contrabandCheckResultProperty() {
        return this.contrabandCheckResult;
    }

    public final SimpleStringProperty selfCheckInResultProperty() {
        return this.selfCheckInResult;
    }

    public final SimpleStringProperty agentCheckInResultProperty() {
        return this.agentCheckInResult;
    }

    Booking findBooking(String ref) {
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

    public void agentCheckIn(String bookingRef, String nameAtCounter, int row, String col, double weight) {
        String log = "\n";
        CheckInAgent agent = new CheckInAgent();
        agent.agentID = "AGT01";
        agent.name = "Fred";

        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            agentCheckInResult.set(log);
            return;
        }
        if (booking.passenger == null || booking.associatedFlight == null) {
            log = "Booking is missing passenger or flight. Cannot continue.";
            agentCheckInResult.set(log);
            return;
        }
        Passenger presented = new Passenger(nameAtCounter, booking.passenger.age);

        CounterCheckIn counter = new CounterCheckIn(agent, "C12", booking.associatedFlight);
        if (!counter.verifyDocuments(booking, presented)) {
            log = "Document verification failed. Agent check-in aborted.";
            agentCheckInResult.set(log);
            return;
        }
        counter.verifyIdentity();
        Flight flight = booking.associatedFlight;
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            log = "No such seat on this flight: " + seatNumber + "\n";
        } else {
            counter.selectSeat(seat);
        }
        counter.checkInBaggage(new Baggage(nextBaggageId(), weight));
        counter.handlePayment();
        counter.createBoardingPass();
        log = "Agent-assisted check-in sequence finished for booking " + booking.bookingNum + ".";
        agentCheckInResult.set(log);
    }

    public void selfCheckIn(String bookingRef, String name, int row, String col, double weight) {
        String log = "\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            selfCheckInResult.set(log);
            return;
        }
        if (booking.passenger == null || booking.associatedFlight == null) {
            log = "Booking is missing passenger or flight. Cannot continue.";
            selfCheckInResult.set(log);
            return;
        }
        if (!name.equals(booking.passenger.name)) {
            log = "Identity verification failed. Self check-in aborted.";
            selfCheckInResult.set(log);
            return;
        }
        log = "Identity verified.\n";
        Flight flight = booking.associatedFlight;
        SelfCheckIn kiosk = new SelfCheckIn(1, "SELF-01", flight);
        kiosk.verifyIdentity();
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            log = "No such seat on this flight: " + seatNumber + "\n";
        } else {
            kiosk.selectSeat(seat);
        }
        kiosk.checkInBaggage(new Baggage(nextBaggageId(), weight));
        kiosk.handlePayment();
        kiosk.createBoardingPass();
        log = "Self check-in sequence finished for booking " + booking.bookingNum + ".";
        selfCheckInResult.set(log);
    }

    public void checkContraband(String bookingRef) {
        String log = "-\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            contrabandCheckResult.set(log);
            return;
        }
        if (booking.baggage == null) {
            log = "No baggage checked in for this booking.";
            contrabandCheckResult.set(log);
            return;
        }
        booking.baggage.scanForContraband(random);
        log = "Baggage " + booking.baggage.baggageID + " status: " + booking.baggage.contrabandFlag;
        contrabandCheckResult.set(log);
    }

    public void verifyIdentity(String bookingRef, String enteredName) {
        String log = "\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            identityVerificationResult.set(log);
            return;
        }
        if (booking.passenger == null) {
            log = "No passenger on file for this booking.";
            identityVerificationResult.set(log);
            return;
        }
        if (enteredName.equals(booking.passenger.name)) {
            log = "Identity verified: name matches booking " + booking.bookingNum + ".";
        } else {
            log = "Verification failed: name does not match booking record.";
        }
        identityVerificationResult.set(log);
    }

    public void extraPayments(String bookingRef, int feeType, double extraBaggageWeight) {
        String log = "\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            extraPaymentsResult.set(log);
            return;
        }
        double amount;
        String paymentType;
        if (feeType == 1) {
            if (booking.baggage == null) {
                log = "No baggage on this booking. Check in baggage before paying extra baggage fees.";
                extraPaymentsResult.set(log);
                return;
            }
            amount = 35;
            paymentType = "EXTRA_BAGGAGE";
        } else if (feeType == 2) {
            amount = 25;
            paymentType = "SEAT_SELECTION";
        } else if (feeType == 3) {
            amount = 40;
            paymentType = "PRIORITY_BOARDING";
        } else {
            log = "Invalid choice.";
            extraPaymentsResult.set(log);
            return;
        }
        Payment p = new Payment(nextPaymentId(), amount, paymentType);
        if (p.processPayment()) {
            if (feeType == 1) {
                booking.baggage.updateWeight(extraBaggageWeight);
                log = "Baggage weight updated. Total weight: " + booking.baggage.weight + " kg\n";
            } else if (feeType == 3) {
                booking.priorityBoarding = true;
            }
            log = "Extra charge recorded for booking " + booking.bookingNum + ".";
        }
        extraPaymentsResult.set(log);
    }

    public void checkInBaggage(String bookingRef, double weight) {
        String log = "--- Baggage Check-In ---\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            baggageCheckInResult.set(log);
            return;
        }
        Baggage bag = new Baggage(nextBaggageId(), weight);
        bag.markedCheckIn();
        booking.baggage = bag;
        booking.baggageChecked = true;
        log = "Baggage checked in for booking " + booking.bookingNum + ".";
        baggageCheckInResult.set(log);
    }

    public void retrieveBookingByRef(String bookingRef) {
        String log = "\n";
        Booking b = findBooking(bookingRef);
        if (b == null) {
            log = "Booking not found.";
            retrieveInformationResult.set(log);
            return;
        }
        log = bookingDetails(b);
        retrieveInformationResult.set(log);
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    String bookingDetails(Booking b) {
        String result = "Booking reference: " + b.bookingNum + "\n";
        result += "Date: " + b.bookingDate + "\n";
        if (b.passenger != null) {
            result += "Passenger: " + b.passenger.name + " (age " + b.passenger.age + ")\n";
        }
        if (b.associatedFlight != null) {
            Flight f = b.associatedFlight;
            result += "Flight: " + f.flightNumber + " | " + f.departureLocation + " -> " + f.arrivalLocation
                    + " | duration " + f.flightDuration + " min\n";
        }
        Seat s = b.assignedSeatForDisplay();
        String seatLabel;
        if (s == null) {
            seatLabel = "not assigned";
        } else {
            seatLabel = s.seatNumber;
        }
        result += "Seat: " + seatLabel + "\n";
        result += "Baggage checked: " + b.baggageChecked + "\n";
        if (b.baggage != null) {
            result += "Baggage ID: " + b.baggage.baggageID + " | weight " + b.baggage.weight + " kg | "
                    + "contraband scan: " + b.baggage.contrabandFlag + "\n";
        }
        result += "Priority boarding: " + b.priorityBoarding + "\n";
        result += "n";
        return result;
    }

    public void createBoardingPass(String bookingRef, int row, String col) {
        String log = "\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            boardingPassResult.set(log);
            return;
        }
        Flight flight = booking.associatedFlight;
        if (flight == null) {
            log = "No flight on this booking.";
            boardingPassResult.set(log);
            return;
        }
        int passId = nextBoardingPassId();
        Seat seat = booking.assignedSeatForDisplay();
        if (seat == null) {
            log = "No seat on booking yet. ";
            seat = flight.findSeatByNumber("" + row + col);
            if (seat == null) {
                log = "Invalid seat.";
                boardingPassResult.set(log);
                return;
            }
        }
        booking.assignSeat(seat);
        BoardingPass pass = new BoardingPass(passId, seat, flight, booking.priorityBoarding);
        log = "" + pass;
        boardingPassResult.set(log);
    }

    public void selectSeat(String bookingRef, int row, String col) {
        String log = "\n";
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log = "Booking not found.";
            seatSelectionResult.set(log);
            return;
        }
        Flight flight = booking.associatedFlight;
        if (flight == null) {
            log = "No flight on this booking.";
            seatSelectionResult.set(log);
            return;
        }
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            log = "No such seat on this flight.";
            seatSelectionResult.set(log);
            return;
        }
        booking.assignSeat(seat);
        if (booking.assignedSeatForDisplay() == seat) {
            log = "Seat " + seat.seatNumber + " assigned to booking " + booking.bookingNum + ".";
        } else {
            log = "Seat could not be assigned (unavailable or already taken).";
        }
        seatSelectionResult.set(log);
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
        return "\n"
                + "Pass ID: " + boardingPassID + "\n"
                + "Flight: " + fn + "  |  " + route + "\n"
                + "Seat: " + seatNum + "\n"
                + "Priority boarding: " + priorityLabel + "\n";
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
        checkInBaggage(new Baggage(Model.nextBaggageId(), weight));
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
        int boardingPassID = Model.nextBoardingPassId();
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
        handlePayment(new Payment(Model.nextPaymentId(), amount, paymentType));
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
        handlePayment(new Payment(Model.nextPaymentId(), STANDARD_TICKET_PRICE, "STANDARD_TICKET"));
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
        int boardingPassID = Model.nextBoardingPassId();
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
        handlePayment(new Payment(Model.nextPaymentId(), STANDARD_TICKET_PRICE, "STANDARD_TICKET"));
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
        int boardingPassID = Model.nextBoardingPassId();
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
