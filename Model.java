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
    private final SimpleStringProperty statusMessage = new SimpleStringProperty("");

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

    public final SimpleStringProperty statusMessageProperty() {
        return this.statusMessage;
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

    public void selectSeat(String bookingRef, int row, String col) {
        StringBuilder log = new StringBuilder();
        log.append("--- Seat Selection ---\n");
        Booking booking = findBooking(bookingRef);
        if (booking == null) {
            log.append("Booking not found.");
            statusMessage.set(log.toString());
            return;
        }
        Flight flight = booking.associatedFlight;
        if (flight == null) {
            log.append("No flight on this booking.");
            statusMessage.set(log.toString());
            return;
        }
        String seatNumber = "" + row + col;
        Seat seat = flight.findSeatByNumber(seatNumber);
        if (seat == null) {
            log.append("No such seat on this flight.");
            statusMessage.set(log.toString());
            return;
        }
        booking.assignSeat(seat);
        if (booking.assignedSeatForDisplay() == seat) {
            log.append("Seat ").append(seat.seatNumber)
                    .append(" assigned to booking ").append(booking.bookingNum).append(".");
        } else {
            log.append("Seat could not be assigned (unavailable or already taken).");
        }
        statusMessage.set(log.toString());
    }

    // ===== Classes copied from CheckInSystemAssessA (now nested to avoid name clashes) =====

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

    static class Flight {
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

    static class Booking {
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

    static class Passenger {
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

    static class CheckedInBooking extends Booking {
        String checkInTime;

        CheckedInBooking(String bookingNum, String bookingDate, String checkInTime) {
            super(bookingNum, bookingDate, null, null);
            this.checkInTime = checkInTime;
        }
    }

    static class ConfirmedBooking extends Booking {

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

    static class Baggage {
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

    static class BoardingPass {
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

    static class CheckIn implements CheckInService {

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

    static class SelfCheckIn extends CheckIn {

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

    static class CounterCheckIn extends CheckIn {
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

    static class CheckInAgent {
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

    static class Payment {
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

    static class Seat {
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
}
