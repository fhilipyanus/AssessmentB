public class Controller {
    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void updateSampleField(String sampleField) {
        model.setSampleField(convertStringToInt(sampleField));
    }

    public void selectSeat(String bookingRef, String rowStr, String col) {
        model.selectSeat(bookingRef, convertStringToInt(rowStr), col);
    }

    public void createBoardingPass(String bookingRef, String rowStr, String col) {
        model.createBoardingPass(bookingRef, convertStringToInt(rowStr), col);
    }

    public void retrieveBookingByRef(String bookingRef) {
        model.retrieveBookingByRef(bookingRef);
    }

    public void checkInBaggage(String bookingRef, String weightStr) {
        model.checkInBaggage(bookingRef, convertStringToDouble(weightStr));
    }

    public void extraPayments(String bookingRef, String feeTypeStr, String weightStr) {
        model.extraPayments(bookingRef, convertStringToInt(feeTypeStr), convertStringToDouble(weightStr));
    }

    public void verifyIdentity(String bookingRef, String enteredName) {
        model.verifyIdentity(bookingRef, enteredName);
    }

    public void checkContraband(String bookingRef) {
        model.checkContraband(bookingRef);
    }

    public void selfCheckIn(String bookingRef, String name, String rowStr, String col, String weightStr) {
        model.selfCheckIn(bookingRef, name, convertStringToInt(rowStr), col, convertStringToDouble(weightStr));
    }

    public void agentCheckIn(String bookingRef, String nameAtCounter, String rowStr, String col, String weightStr) {
        model.agentCheckIn(bookingRef, nameAtCounter, convertStringToInt(rowStr), col, convertStringToDouble(weightStr));
    }

    public java.util.List<Model.Flight> getAllFlights() {
        return model.getAllFlights();
    }

    public java.util.List<Model.Booking> getAllBookings() {
        return model.getAllBookings();
    }

    private int convertStringToInt(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Integer.parseInt(s); // Convert string into integer
    }

    private double convertStringToDouble(String s) {
        if (s == null || s.isEmpty()) {
            return 0.0;
        }
        if ("-".equals(s) || ".".equals(s) || "-.".equals(s)) {
            return 0.0;
        }
        return Double.parseDouble(s);
    }
}