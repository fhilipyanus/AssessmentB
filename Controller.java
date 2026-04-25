public class Controller {
    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void updateSampleField(String sampleField) {
        model.setSampleField(convertStringToInt(sampleField));
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