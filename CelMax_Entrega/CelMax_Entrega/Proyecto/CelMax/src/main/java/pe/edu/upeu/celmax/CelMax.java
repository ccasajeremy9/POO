package pe.edu.upeu.celmax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.celmax.components.StageManager;
import pe.edu.upeu.celmax.config.AppContext;
import pe.edu.upeu.celmax.config.DatabaseConfig;

import java.io.IOException;

public class CelMax extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppContext appContext = AppContext.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(CelMax.class.getResource("/view/login.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        StageManager.setPrimaryStage(stage);
        stage.setTitle("CelMax - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseConfig.shutdown();
    }
}
