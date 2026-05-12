import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class View {
    private VBox view;
    private Label heading;
    private Button seatSelectButton;
    private Button baggageCheckInButton;
    private Button createBoardingPassButton;
    private Button retrieveInformationButton;
    private Button listFlightsButton;
    private Button listBookingsButton;
    private Button extraPaymentsButton;
    private Button identityVerificationButton;
    private Button checkContrabandButton;
    private Button selfCheckInButton;
    private Button agentCheckInButton;

    private Controller controller;
    private Model model;
    private Stage primaryStage;

    public View(Controller controller, Model model, Stage primaryStage) {

        this.controller = controller;
        this.model = model;
        this.primaryStage = primaryStage;

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

    }

    private void updateControllerFromListeners() {

    }

    private void createAndLayoutControls() {
        heading = new Label("Airport Check In System");
        seatSelectButton = new Button("Select a Seat for The Customer");
        seatSelectButton.setOnAction(e -> {
            createSeatSelectionWindow();
        });
        baggageCheckInButton = new Button("Baggage Check In");
        baggageCheckInButton.setOnAction(e -> createBaggageCheckInWindow());
        createBoardingPassButton = new Button("Create Boarding Pass");
        createBoardingPassButton.setOnAction(e -> {
            createBoardingPassWindow();
        });
        retrieveInformationButton = new Button("Retrieve Information");
        retrieveInformationButton.setOnAction(e -> {
            createRetrieveInformationWindow();
        });
        listFlightsButton = new Button("List All Flights");
        listFlightsButton.setOnAction(e -> {
            createFlightsListWindow();
        });
        listBookingsButton = new Button("List All Bookings");
        listBookingsButton.setOnAction(e -> {
            createBookingsListWindow();
        });
        extraPaymentsButton = new Button("Extra Payments");
        extraPaymentsButton.setOnAction(e -> createExtraPaymentsWindow());
        identityVerificationButton = new Button("Identity Verification");
        identityVerificationButton.setOnAction(e -> createIdentityVerificationWindow());
        checkContrabandButton = new Button("Check Contraband");
        checkContrabandButton.setOnAction(e -> createCheckContrabandWindow());
        selfCheckInButton = new Button("Self Check In");
        selfCheckInButton.setOnAction(e -> createSelfCheckInWindow());
        agentCheckInButton = new Button("Agent Check In");
        agentCheckInButton.setOnAction(e -> createAgentCheckInWindow());

        HBox sampleRow = new HBox(5, heading);
        VBox columns = new VBox(
                5,
                sampleRow,
                seatSelectButton,
                baggageCheckInButton,
                createBoardingPassButton,
                retrieveInformationButton,
                listFlightsButton,
                listBookingsButton,
                extraPaymentsButton,
                identityVerificationButton,
                checkContrabandButton,
                selfCheckInButton,
                agentCheckInButton);
        sampleRow.setAlignment(Pos.BASELINE_LEFT);

        view.getChildren().addAll(columns);
    }

    private void createAndConfigurePane() {
        view = new VBox(5);
        view.setAlignment(Pos.CENTER);
    }

    private void createSeatSelectionWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField seatRowField = new TextField();
        HBox seatRowRow = new HBox(5, new Label("Enter Seat Row (1-30) :"), seatRowField);
        seatRowRow.setAlignment(Pos.CENTER);
        configTextFieldForInts(seatRowField);

        TextField seatColumnField = new TextField();
        HBox seatColumnRow = new HBox(5, new Label("Enter Seat Column (A-F) :"), seatColumnField);
        seatColumnRow.setAlignment(Pos.CENTER);

        Button addSeatButton = new Button("Add Seat");
        addSeatButton.setOnAction(e -> {
            controller.selectSeat(
                    bookingReferenceField.getText(),
                    seatRowField.getText(),
                    seatColumnField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.seatSelectionResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, seatRowRow, seatColumnRow, addSeatButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 320, 250);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createRetrieveInformationWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        Button retrieveButton = new Button("Retrieve");
        retrieveButton.setOnAction(e -> {
            controller.retrieveBookingByRef(bookingReferenceField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.retrieveInformationResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, retrieveButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 500, 400);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createFlightsListWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TableView<Flight> flightsTable = new TableView<>();
        flightsTable.setItems(FXCollections.observableArrayList(controller.getAllFlights()));

        TableColumn<Flight, String> flightNumCol = new TableColumn<>("Flight Number");
        flightNumCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().flightNumber));

        TableColumn<Flight, String> departureCol = new TableColumn<>("Departure");
        departureCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().departureLocation));

        TableColumn<Flight, String> arrivalCol = new TableColumn<>("Arrival");
        arrivalCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().arrivalLocation));

        TableColumn<Flight, String> durationCol = new TableColumn<>("Duration (min)");
        durationCol.setCellValueFactory(d -> new SimpleStringProperty("" + d.getValue().flightDuration));

        flightsTable.getColumns().add(flightNumCol);
        flightsTable.getColumns().add(departureCol);
        flightsTable.getColumns().add(arrivalCol);
        flightsTable.getColumns().add(durationCol);

        VBox root = new VBox(5, new Label("All Flights"), flightsTable);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 600, 400);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createBookingsListWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TableView<Booking> bookingsTable = new TableView<>();
        bookingsTable.setItems(FXCollections.observableArrayList(controller.getAllBookings()));

        TableColumn<Booking, String> refCol = new TableColumn<>("Booking Ref");
        refCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().bookingNum));

        TableColumn<Booking, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().bookingDate));

        TableColumn<Booking, String> passengerNameCol = new TableColumn<>("Passenger Name");
        passengerNameCol.setCellValueFactory(d -> {
            Passenger p = d.getValue().passenger;
            return new SimpleStringProperty(p == null ? "" : p.name);
        });

        TableColumn<Booking, String> passengerAgeCol = new TableColumn<>("Age");
        passengerAgeCol.setCellValueFactory(d -> {
            Passenger p = d.getValue().passenger;
            return new SimpleStringProperty(p == null ? "" : "" + p.age);
        });

        TableColumn<Booking, String> flightCol = new TableColumn<>("Flight");
        flightCol.setCellValueFactory(d -> {
            Flight f = d.getValue().associatedFlight;
            return new SimpleStringProperty(f == null ? "" : f.flightNumber);
        });

        TableColumn<Booking, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(d -> {
            Seat s = d.getValue().assignedSeatForDisplay();
            return new SimpleStringProperty(s == null ? "not assigned" : s.seatNumber);
        });

        TableColumn<Booking, String> baggageCheckedCol = new TableColumn<>("Baggage Checked");
        baggageCheckedCol.setCellValueFactory(d -> new SimpleStringProperty("" + d.getValue().baggageChecked));

        TableColumn<Booking, String> baggageIdCol = new TableColumn<>("Baggage ID");
        baggageIdCol.setCellValueFactory(d -> {
            Baggage b = d.getValue().baggage;
            return new SimpleStringProperty(b == null ? "" : b.baggageID);
        });

        TableColumn<Booking, String> baggageWeightCol = new TableColumn<>("Weight (kg)");
        baggageWeightCol.setCellValueFactory(d -> {
            Baggage b = d.getValue().baggage;
            return new SimpleStringProperty(b == null ? "" : "" + b.weight);
        });

        TableColumn<Booking, String> contrabandCol = new TableColumn<>("Contraband Scan");
        contrabandCol.setCellValueFactory(d -> {
            Baggage b = d.getValue().baggage;
            return new SimpleStringProperty(b == null ? "" : "" + b.contrabandFlag);
        });

        TableColumn<Booking, String> priorityCol = new TableColumn<>("Priority Boarding");
        priorityCol.setCellValueFactory(d -> new SimpleStringProperty("" + d.getValue().priorityBoarding));

        bookingsTable.getColumns().add(refCol);
        bookingsTable.getColumns().add(dateCol);
        bookingsTable.getColumns().add(passengerNameCol);
        bookingsTable.getColumns().add(passengerAgeCol);
        bookingsTable.getColumns().add(flightCol);
        bookingsTable.getColumns().add(seatCol);
        bookingsTable.getColumns().add(baggageCheckedCol);
        bookingsTable.getColumns().add(baggageIdCol);
        bookingsTable.getColumns().add(baggageWeightCol);
        bookingsTable.getColumns().add(contrabandCol);
        bookingsTable.getColumns().add(priorityCol);

        VBox root = new VBox(5, new Label("All Bookings"), bookingsTable);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 1100, 400);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createBoardingPassWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField seatRowField = new TextField();
        HBox seatRowRow = new HBox(5, new Label("Enter Seat Row (1-30) :"), seatRowField);
        seatRowRow.setAlignment(Pos.CENTER);
        configTextFieldForInts(seatRowField);

        TextField seatColumnField = new TextField();
        HBox seatColumnRow = new HBox(5, new Label("Enter Seat Column (A-F) :"), seatColumnField);
        seatColumnRow.setAlignment(Pos.CENTER);

        Button createPassButton = new Button("Create Boarding Pass");
        createPassButton.setOnAction(e -> {
            controller.createBoardingPass(
                    bookingReferenceField.getText(),
                    seatRowField.getText(),
                    seatColumnField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.boardingPassResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, seatRowRow, seatColumnRow, createPassButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 360, 360);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createAgentCheckInWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        HBox nameRow = new HBox(5,
                new Label("Passenger name at counter:"), nameField);
        nameRow.setAlignment(Pos.CENTER);

        TextField seatRowField = new TextField();
        configTextFieldForInts(seatRowField);
        HBox seatRowRow = new HBox(5, new Label("Enter Seat Row (1-30) :"), seatRowField);
        seatRowRow.setAlignment(Pos.CENTER);

        TextField seatColumnField = new TextField();
        HBox seatColumnRow = new HBox(5, new Label("Enter Seat Column (A-F) :"), seatColumnField);
        seatColumnRow.setAlignment(Pos.CENTER);

        TextField weightField = new TextField();
        HBox weightRow = new HBox(5, new Label("Baggage Weight (kg):"), weightField);
        weightRow.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Agent Check-In");
        startButton.setOnAction(e -> {
            controller.agentCheckIn(
                    bookingReferenceField.getText(),
                    nameField.getText(),
                    seatRowField.getText(),
                    seatColumnField.getText(),
                    weightField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.agentCheckInResultProperty());

        VBox root = new VBox(5,
                bookingReferenceRow, nameRow, seatRowRow, seatColumnRow, weightRow,
                startButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 460, 360);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createSelfCheckInWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        HBox nameRow = new HBox(5,
                new Label("Enter your full name for verification:"), nameField);
        nameRow.setAlignment(Pos.CENTER);

        TextField seatRowField = new TextField();
        configTextFieldForInts(seatRowField);
        HBox seatRowRow = new HBox(5, new Label("Enter Seat Row (1-30) :"), seatRowField);
        seatRowRow.setAlignment(Pos.CENTER);

        TextField seatColumnField = new TextField();
        HBox seatColumnRow = new HBox(5, new Label("Enter Seat Column (A-F) :"), seatColumnField);
        seatColumnRow.setAlignment(Pos.CENTER);

        TextField weightField = new TextField();
        HBox weightRow = new HBox(5, new Label("Baggage Weight (kg):"), weightField);
        weightRow.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Self Check-In");
        startButton.setOnAction(e -> {
            controller.selfCheckIn(
                    bookingReferenceField.getText(),
                    nameField.getText(),
                    seatRowField.getText(),
                    seatColumnField.getText(),
                    weightField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.selfCheckInResultProperty());

        VBox root = new VBox(5,
                bookingReferenceRow, nameRow, seatRowRow, seatColumnRow, weightRow,
                startButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 460, 360);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createCheckContrabandWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        Button scanButton = new Button("Scan Baggage");
        scanButton.setOnAction(e -> {
            controller.checkContraband(bookingReferenceField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.contrabandCheckResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, scanButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 400, 200);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createIdentityVerificationWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        HBox nameRow = new HBox(5,
                new Label("Enter passenger full name (as on booking):"), nameField);
        nameRow.setAlignment(Pos.CENTER);

        Button verifyButton = new Button("Verify Identity");
        verifyButton.setOnAction(e -> {
            controller.verifyIdentity(
                    bookingReferenceField.getText(),
                    nameField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.identityVerificationResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, nameRow, verifyButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 420, 220);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createExtraPaymentsWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        ListView<String> feeTypeList = new ListView<>(FXCollections.observableArrayList(
                "1. Extra baggage fee ($35)",
                "2. Preferred seat selection fee ($25)",
                "3. Priority boarding ($40)"));
        HBox feeTypeRow = new HBox(5, new Label("Fee Type:"), feeTypeList);
        feeTypeRow.setAlignment(Pos.CENTER);

        TextField weightField = new TextField();
        HBox weightRow = new HBox(5,
                new Label("Additional Baggage Weight (kg):"), weightField);
        weightRow.setAlignment(Pos.CENTER);

        Button payButton = new Button("Submit Payment");
        payButton.setOnAction(e -> {
            int selectedIdx = feeTypeList.getSelectionModel().getSelectedIndex();
            String feeTypeStr;
            if (selectedIdx >= 0) {
                feeTypeStr = "" + (selectedIdx + 1);
            } else {
                feeTypeStr = "";
            }
            controller.extraPayments(
                    bookingReferenceField.getText(),
                    feeTypeStr,
                    weightField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.extraPaymentsResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, feeTypeRow, weightRow, payButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 520, 600);

        stage.setScene(helloScene);
        stage.show();
    }

    private void createBaggageCheckInWindow() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField bookingReferenceField = new TextField();
        HBox bookingReferenceRow = new HBox(5, new Label("Booking Reference:"), bookingReferenceField);
        bookingReferenceRow.setAlignment(Pos.CENTER);

        TextField weightField = new TextField();
        configTextFieldForInts(weightField);
        HBox weightRow = new HBox(5, new Label("Baggage Weight (kg):"), weightField);
        weightRow.setAlignment(Pos.CENTER);

        Button checkInButton = new Button("Check In Baggage");
        checkInButton.setOnAction(e -> {
            controller.checkInBaggage(
                    bookingReferenceField.getText(),
                    weightField.getText());
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.baggageCheckInResultProperty());

        VBox root = new VBox(5, bookingReferenceRow, weightRow, checkInButton, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene helloScene = new Scene(root, 320, 220);

        stage.setScene(helloScene);
        stage.show();
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