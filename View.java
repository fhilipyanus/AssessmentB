import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class View {
    private VBox view;
    private TextField sampleField;
    private Label heading;

    private Controller controller;
    private Model model;

    public View(Controller controller, Model model) {

        this.controller = controller;
        this.model = model;

        createAndConfigurePane();
        createAndLayoutControls();
        updateControllerFromListeners();
        observeModelAndUpdateControls();
    }

    public Parent asParent() {
        return view;
    }

    // If the given field has changed, update its text value.
    private void updateIfNeeded(Number value, TextField field) {
        String s = value.toString();
        if (!field.getText().equals(s)) {
            field.setText(s);
        }
    }

    private void observeModelAndUpdateControls() {
        model.sampleVarProperty().addListener((obs, oldX, newX) -> updateIfNeeded(newX, sampleField));
    }

    private void updateControllerFromListeners() {
        sampleField.textProperty().addListener((obs, oldText, newText) -> controller.updateSampleField(newText));
    }

    private void createAndLayoutControls() {
        heading = new Label("Airport Check In System");
        sampleField = new TextField();
        configTextFieldForInts(sampleField);

        HBox sampleRow = new HBox(5, heading, sampleField);
        sampleRow.setAlignment(Pos.BASELINE_LEFT);

        view.getChildren().addAll(sampleRow);
    }

    private void createAndConfigurePane() {
        view = new VBox(5);
        view.setAlignment(Pos.CENTER);
    }

    // You may skip looking at this method. Its purpose is to ensure that
    // only integers can be entered into the X and Y text fields. You may find this
    // useful in your project B!
    private void configTextFieldForInts(TextField field) {
        field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            // "-?\\d*" is called a regular expression. For those who are curious:
            //
            // - The "-?" indicates that the minus sign is optionally present (we need to
            // allow for negative integers too)
            // - "\\d" is a digit character, which matches any digit from 0 to 9.
            // - The following "*" is a quantifier that means "zero or more occurrences".
            // - Therefore, \\d* matches a sequence of zero or more digits.
            if (c.getControlNewText().matches("-?\\d*")) {
                return c;
            }
            return null;
        }));
    }
}