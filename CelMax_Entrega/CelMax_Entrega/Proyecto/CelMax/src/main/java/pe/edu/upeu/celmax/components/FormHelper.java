package pe.edu.upeu.celmax.components;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;

/**
 * Métodos de apoyo que repiten todos los formularios CRUD
 * (validar, marcar errores, mostrar mensajes, convertir números).
 * En SysVentas este código estaba dentro de ProductoController;
 * aquí se separó para no copiarlo en cada controlador.
 */
public final class FormHelper {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ToltipCustom TTC = new ToltipCustom();

    private FormHelper() {
    }

    /** Valida el objeto con las anotaciones Jakarta y devuelve los errores ordenados por campo. */
    public static <T> List<ConstraintViolation<T>> validar(T objeto) {
        return VALIDATOR.validate(objeto).stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .toList();
    }

    /** Pinta en rojo los campos con error y muestra el primer mensaje en el label. */
    public static <T> void mostrarErrores(List<ConstraintViolation<T>> violaciones,
                                          LinkedHashMap<String, Control> campos, Label lbnMsg) {
        limpiarErrores(campos.values());
        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> campo : campos.entrySet()) {
            Optional<ConstraintViolation<T>> v = violaciones.stream()
                    .filter(x -> x.getPropertyPath().toString().equals(campo.getKey()))
                    .findFirst();
            if (v.isPresent()) {
                TTC.marcarError(campo.getValue(), v.get().getMessage().trim());
                if (primero == null) {
                    primero = campo.getValue();
                    primerMensaje = v.get().getMessage();
                }
            }
        }
        if (primerMensaje != null) {
            mensaje(lbnMsg, primerMensaje, false);
            Platform.runLater(primero::requestFocus);
        }
    }

    public static void limpiarErrores(Collection<Control> campos) {
        campos.forEach(TTC::limpiarCampo);
    }

    public static void mensaje(Label lbl, String texto, boolean ok) {
        lbl.setText(texto);
        lbl.getStyleClass().removeAll("msg-ok", "msg-error");
        lbl.getStyleClass().add(ok ? "msg-ok" : "msg-error");
    }

    public static void toastOk(Node nodo, String texto) {
        Stage stage = (Stage) nodo.getScene().getWindow();
        Toast.showToast(stage, texto, 2000, stage.getWidth() / 2 - 150, 90);
    }

    public static void toastError(Node nodo, String texto) {
        Stage stage = (Stage) nodo.getScene().getWindow();
        Toast.showError(stage, texto, 3500, stage.getWidth() / 2 - 150, 90);
    }

    /** Ventana de confirmación Sí / No. */
    public static boolean confirmar(String texto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Confirmar");
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    /** Mensaje amigable cuando la BD rechaza un DELETE por estar en uso. */
    public static String mensajeError(Exception e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        if (m.contains("Referential integrity") || m.contains("23503")) {
            return "No se puede eliminar: el registro está siendo usado en otra tabla.";
        }
        if (m.contains("Unique index") || m.contains("23505")) {
            return "Ya existe un registro con ese valor (dato duplicado).";
        }
        return m.length() > 160 ? m.substring(0, 160) + "..." : m;
    }

    public static Integer parseIntSafe(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value.trim()); } catch (NumberFormatException e) { return null; }
    }

    public static Double parseDoubleSafe(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Double.parseDouble(value.trim().replace(",", ".")); } catch (NumberFormatException e) { return null; }
    }

    /** Selecciona en el combo la opción cuya key coincide. */
    public static void seleccionar(ComboBox<pe.edu.upeu.celmax.dto.ComboBoxOption> cbx, String key) {
        cbx.getSelectionModel().select(cbx.getItems().stream()
                .filter(o -> o.getKey().equals(key)).findFirst().orElse(null));
    }

    public static String keySeleccionada(ComboBox<pe.edu.upeu.celmax.dto.ComboBoxOption> cbx) {
        return cbx.getSelectionModel().getSelectedItem() == null ? null
                : cbx.getSelectionModel().getSelectedItem().getKey();
    }
}
