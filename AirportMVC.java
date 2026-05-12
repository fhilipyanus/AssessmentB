import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AirportMVC extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Airport MVC");
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller, model, primaryStage);

        Scene scene = new Scene(view.asParent(), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}