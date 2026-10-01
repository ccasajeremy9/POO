package pe.edu.upeu.sysnacimientos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysnacimientos.config.AppContext;

import java.io.IOException;

public class SysNacimientos extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Screen screen=Screen.getPrimary();

        AppContext appContext=AppContext.getInstance();
        Rectangle2D dimen=screen.getBounds();

        FXMLLoader fxmlLoader = new FXMLLoader(SysNacimientos.class.getResource("/view/maingui.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load(), dimen.getWidth(), dimen.getHeight()-60);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("SysNacimientos");
        stage.setScene(scene);
        stage.show();
    }
}
