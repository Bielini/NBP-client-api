import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FxDemoApp extends Application {
    public static void main(String[] args) {
    launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        Scene scene = new Scene(pane,300,300);
        stage.setScene(scene);
        stage.show();
    }
}