package pe.edu.upeu.celmax.controller;

import jakarta.validation.ConstraintViolation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.components.ColumnInfo;
import pe.edu.upeu.celmax.components.FormHelper;
import pe.edu.upeu.celmax.components.TableViewHelper;
import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.dto.SessionManager;
import pe.edu.upeu.celmax.model.Usuario;
import pe.edu.upeu.celmax.service.IPerfilService;
import pe.edu.upeu.celmax.service.IUsuarioService;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService us;
    private final IPerfilService ps;

    @FXML TextField txtUsuario;
    @FXML PasswordField txtClave;
    @FXML ComboBox<ComboBoxOption> cbxPerfil;
    @FXML TableView<Usuario> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private Usuario formulario;
    private String idUsuarioCE = null;

    @FXML
    public void initialize() {
        cbxPerfil.getItems().setAll(ps.listarCombobox());

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idUsuario", 90.0));
        columns.put("Usuario", new ColumnInfo("nombreUsuario", 160.0));
        columns.put("Perfil", new ColumnInfo("idPerfil.nombrePerfil", 150.0));
        new TableViewHelper<Usuario>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);
        idUsuarioCE = null;
        listar();
    }

    public void listar() {
        tableView.setItems(FXCollections.observableArrayList(us.findAll()));
    }

    @FXML
    public void validarFormulario() {
        String idPerfil = FormHelper.keySeleccionada(cbxPerfil);
        formulario = Usuario.builder()
                .nombreUsuario(txtUsuario.getText().trim())
                .contrasena(txtClave.getText())
                .idPerfil(idPerfil == null ? null : ps.findById(idPerfil))
                .build();

        List<ConstraintViolation<Usuario>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idUsuarioCE != null) {
                formulario.setIdUsuario(idUsuarioCE);
                us.update(idUsuarioCE, formulario);
                FormHelper.toastOk(miContenedor, "Usuario actualizado correctamente");
            } else {
                us.save(formulario);
                FormHelper.toastOk(miContenedor, "Usuario registrado: " + formulario.getIdUsuario());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Usuario u) {
        if (u.getIdUsuario().equals(SessionManager.getInstance().getUserId())) {
            FormHelper.toastError(miContenedor, "No puede eliminar su propio usuario");
            return;
        }
        if (!FormHelper.confirmar("¿Eliminar al usuario " + u.getNombreUsuario() + "?")) return;
        try {
            us.delete(u.getIdUsuario());
            FormHelper.toastOk(miContenedor, "Usuario eliminado");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Usuario u) {
        txtUsuario.setText(u.getNombreUsuario());
        txtClave.setText(u.getContrasena());
        FormHelper.seleccionar(cbxPerfil, u.getIdPerfil().getIdPerfil());
        idUsuarioCE = u.getIdUsuario();
        lblTitulo.setText("Editando " + u.getIdUsuario());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        txtUsuario.clear();
        txtClave.clear();
        cbxPerfil.getSelectionModel().clearSelection();
        idUsuarioCE = null;
        lblTitulo.setText("Nuevo usuario");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreUsuario", txtUsuario);
        campos.put("contrasena", txtClave);
        campos.put("idPerfil", cbxPerfil);
        return campos;
    }
}
