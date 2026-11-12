package pe.edu.upeu.celmax.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {

    private static final String OK = "#10b981";
    private static final String ERROR = "#ef4444";

    public static void showToast(Stage ownerStage, String message, int durationInMillis, double x, double y) {
        show(ownerStage, message, durationInMillis, x, y, OK);
    }

    public static void showError(Stage ownerStage, String message, int durationInMillis, double x, double y) {
        show(ownerStage, message, durationInMillis, x, y, ERROR);
    }

    private static void show(Stage ownerStage, String message, int durationInMillis, double x, double y, String color) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(380);
        label.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px; "
                + "-fx-font-weight: bold; -fx-padding: 12 18 12 18; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);");
        label.setOpacity(0);

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        StackPane pane = new StackPane(label);
        pane.setPadding(new Insets(10));
        pane.setAlignment(Pos.CENTER);
        popup.getContent().add(pane);
        popup.show(ownerStage, ownerStage.getX() + x, ownerStage.getY() + y);

        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(label.opacityProperty(), 1)));
        Timeline fadeOut = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(label.opacityProperty(), 0)));
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(durationInMillis)));
        delay.setOnFinished(event -> fadeOut.play());
        fadeIn.play();
        fadeIn.setOnFinished(event -> delay.play());
        fadeOut.setOnFinished(event -> popup.hide());
    }
}
