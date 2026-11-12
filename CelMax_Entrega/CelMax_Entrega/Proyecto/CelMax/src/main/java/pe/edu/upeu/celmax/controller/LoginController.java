package pe.edu.upeu.celmax.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.CelMax;
import pe.edu.upeu.celmax.components.StageManager;
import pe.edu.upeu.celmax.config.AppContext;
import pe.edu.upeu.celmax.dto.SessionManager;
import pe.edu.upeu.celmax.model.Usuario;
import pe.edu.upeu.celmax.service.IUsuarioService;

@RequiredArgsConstructor
public class LoginController {

    private final IUsuarioService us;

    @FXML TextField txtUsuario;
    @FXML PasswordField txtClave;
    @FXML Label lblError;

    @FXML
    public void initialize() {
        lblError.setText("");
        Platform.runLater(txtUsuario::requestFocus);
    }

    @FXML
    public void login(ActionEvent e) {
        if (txtUsuario.getText().isBlank() || txtClave.getText().isBlank()) {
            lblError.setText("Ingrese usuario y contraseña");
            return;
        }
        us.loginUsuario(txtUsuario.getText().trim(), txtClave.getText()).ifPresentOrElse(
                usuario -> abrirPantalla(e, usuario),
                () -> {
                    lblError.setText("Usuario o contraseña incorrectos");
                    txtClave.clear();
                    txtClave.requestFocus();
                }
        );
    }

    @FXML
    public void logout(ActionEvent e) {
        Platform.exit();
        System.exit(0);
    }

    public void abrirPantalla(ActionEvent e, Usuario u) {
        try {
            SessionManager.getInstance().setUserPerfil(u.getIdPerfil().getNombrePerfil());
            SessionManager.getInstance().setUserName(u.getNombreUsuario());
            SessionManager.getInstance().setUserId(u.getIdUsuario());

            Rectangle2D dimen = Screen.getPrimary().getVisualBounds();
            AppContext appContext = AppContext.getInstance();

            FXMLLoader fxmlLoader = new FXMLLoader(CelMax.class.getResource("/view/maingui.fxml"));
            fxmlLoader.setControllerFactory(appContext::getBean);
            Scene scene = new Scene(fxmlLoader.load(), dimen.getWidth(), dimen.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            StageManager.setPrimaryStage(stage);
            stage.setTitle("CelMax - Sistema de ventas");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setX(dimen.getMinX());
            stage.setY(dimen.getMinY());
            stage.setMaximized(true);
            stage.show();
            txtClave.clear();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
