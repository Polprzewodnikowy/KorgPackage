package polprzewodnikowy.korgpkgedit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class PkgEditor extends Application {

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setup(primaryStage);
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setTitle("KORG pkg editor");
        primaryStage.show();
    }

}
