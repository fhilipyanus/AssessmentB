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

    private int convertStringToInt(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Integer.parseInt(s); // Convert string into integer
    }
}