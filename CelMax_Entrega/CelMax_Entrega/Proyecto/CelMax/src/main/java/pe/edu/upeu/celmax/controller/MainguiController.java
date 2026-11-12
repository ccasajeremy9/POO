package pe.edu.upeu.celmax.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.config.AppContext;
import pe.edu.upeu.celmax.dto.MenuMenuItenTO;
import pe.edu.upeu.celmax.dto.SessionManager;
import pe.edu.upeu.celmax.service.IMenuMenuItemDao;
import pe.edu.upeu.celmax.utils.UtilsX;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

@RequiredArgsConstructor
public class MainguiController {

    @FXML BorderPane bp;
    @FXML VBox topBox;
    @FXML HBox header;
    @FXML MenuBar menuBar;
    @FXML TabPane tabPane;
    @FXML Label lblUsuario, lblPerfil;

    private final IMenuMenuItemDao mmiDao;
    Preferences userPrefs = Preferences.userRoot();
    UtilsX util = new UtilsX();
    Properties myresources = new Properties();
    List<MenuMenuItenTO> lista;

    private final Menu menuEstilo = new Menu("Estilo");
    private final ComboBox<String> comboBoxEstilo = new ComboBox<>(
            FXCollections.observableArrayList("Claro", "Oscuro"));
    private final CustomMenuItem customItem = new CustomMenuItem(comboBoxEstilo);
    private final Menu menuIdioma = new Menu("Idioma");
    private final ComboBox<String> comboBoxIdioma = new ComboBox<>(
            FXCollections.observableArrayList("Español", "English"));
    private final CustomMenuItem customItemIdioma = new CustomMenuItem(comboBoxIdioma);

    @FXML
    public void initialize() {
        lblUsuario.setText(SessionManager.getInstance().getUserName());
        lblPerfil.setText(SessionManager.getInstance().getUserPerfil());
        tabPane.getTabs().clear();
        graficarMenus();
        abrirTabPaneFXML("/view/main_inicio.fxml", "Inicio");
        // aplicar el estilo guardado cuando la escena ya existe
        bp.sceneProperty().addListener((obs, o, scene) -> {
            if (scene != null) aplicarEstilo(userPrefs.get("ESTILOX", "Claro"));
        });
    }

    private List<MenuMenuItenTO> listaAccesos() {
        myresources = util.detectLanguage(userPrefs.get("IDIOMAX", "es"));
        return mmiDao.listaAccesos(SessionManager.getInstance().getUserPerfil(), myresources);
    }

    private void cambiarEstilo() {
        String estilo = comboBoxEstilo.getSelectionModel().getSelectedItem();
        if (estilo == null) return;
        userPrefs.put("ESTILOX", estilo);
        aplicarEstilo(estilo);
    }

    private void aplicarEstilo(String estilo) {
        Scene escena = bp.getScene();
        if (escena == null) return;
        escena.getStylesheets().clear();
        escena.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        if ("Oscuro".equals(estilo)) {
            escena.getStylesheets().add(getClass().getResource("/css/estilo-oscuro.css").toExternalForm());
        }
    }

    private void cambiarIdioma() {
        String idioma = comboBoxIdioma.getSelectionModel().getSelectedItem();
        userPrefs.put("IDIOMAX", "English".equals(idioma) ? "en" : "es");
        graficarMenus();
    }

    private void graficarMenus() {
        lista = listaAccesos();
        int[] mmi = mmiDao.contarMenuMunuItem(lista);
        Menu[] menu = new Menu[mmi[0]];
        MenuItem[] menuItem = new MenuItem[mmi[1]];
        menuBar = new MenuBar();
        MenuItemListener d = new MenuItemListener();
        String menuN = "";
        int menui = 0, menuitem = 0;
        char conti = 'N';
        for (MenuMenuItenTO mmix : lista) {
            if (!mmix.getMenunombre().equals(menuN)) {
                menu[menui] = new Menu(mmix.getMenunombre());
                menu[menui].setId("m" + mmix.getIdNombreObj());
                if (!mmix.getMenuitemnombre().equals("")) {
                    menuItem[menuitem] = new MenuItem(mmix.getMenuitemnombre());
                    menuItem[menuitem].setId("mi" + mmix.getIdNombreObj());
                    menuItem[menuitem].setOnAction(d::handle);
                    menu[menui].getItems().add(menuItem[menuitem]);
                    menuitem++;
                }
                menuBar.getMenus().add(menu[menui]);
                menuN = mmix.getMenunombre();
                conti = 'N';
                menui++;
            } else {
                conti = 'S';
            }
            if (!mmix.getMenuitemnombre().equals("") && mmix.getMenunombre().equals(menuN) && conti == 'S') {
                menuItem[menuitem] = new MenuItem(mmix.getMenuitemnombre());
                menuItem[menuitem].setId("mi" + mmix.getIdNombreObj());
                menuItem[menuitem].setOnAction(d::handle);
                menu[menui - 1].getItems().add(menuItem[menuitem]);
                menuitem++;
            }
        }
        menuEstilo.setText(myresources.getProperty("menu.nombre.estilo", "Estilo"));
        comboBoxEstilo.setOnAction(e -> cambiarEstilo());
        customItem.setHideOnClick(false);
        menuEstilo.getItems().setAll(customItem);
        menuBar.getMenus().add(menuEstilo);

        menuIdioma.setText(myresources.getProperty("menu.nombre.idioma", "Idioma"));
        comboBoxIdioma.setOnAction(e -> cambiarIdioma());
        customItemIdioma.setHideOnClick(false);
        menuIdioma.getItems().setAll(customItemIdioma);
        menuBar.getMenus().add(menuIdioma);

        topBox.getChildren().setAll(header, menuBar);
    }

    /** Abre una vista en una pestaña; si ya está abierta, solo la selecciona. */
    private void abrirTabPaneFXML(String fxmlPath, String titulo) {
        for (Tab t : tabPane.getTabs()) {
            if (t.getText().equals(titulo)) {
                tabPane.getSelectionModel().select(t);
                return;
            }
        }
        try {
            AppContext context = AppContext.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            Tab newTab = new Tab(titulo, scrollPane);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    class MenuItemListener {

        Map<String, String[]> menuConfig;

        MenuItemListener() {
            menuConfig = mmiDao.accesosAutorizados(lista);
        }

        /** Cierra sesión: vuelve a la ventana de login. */
        private void redireccionar(String fxmlPath) {
            try {
                Stage stage = (Stage) bp.getScene().getWindow();
                AppContext ctx = AppContext.getInstance();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                fxmlLoader.setControllerFactory(ctx::getBean);
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                SessionManager.getInstance().setUserId(null);
                SessionManager.getInstance().setUserName(null);
                SessionManager.getInstance().setUserPerfil(null);
                stage.setMaximized(false);
                stage.setScene(scene);
                stage.sizeToScene();
                stage.centerOnScreen();
                stage.setTitle("CelMax - Login");
                stage.setResizable(false);
                stage.show();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public void handle(ActionEvent e) {
            String id = ((MenuItem) e.getSource()).getId();
            if (menuConfig.containsKey(id)) {
                String[] items = menuConfig.get(id);
                if (items[2].equals("C")) {
                    redireccionar(items[0]);
                } else {
                    abrirTabPaneFXML(items[0], items[1]);
                }
            }
        }
    }
}
