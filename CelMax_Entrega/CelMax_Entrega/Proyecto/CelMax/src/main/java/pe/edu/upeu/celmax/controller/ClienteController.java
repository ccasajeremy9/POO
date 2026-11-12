package pe.edu.upeu.celmax.controller;

import jakarta.validation.ConstraintViolation;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.components.ColumnInfo;
import pe.edu.upeu.celmax.components.FormHelper;
import pe.edu.upeu.celmax.components.TableViewHelper;
import pe.edu.upeu.celmax.model.Cliente;
import pe.edu.upeu.celmax.service.IClienteService;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService cs;

    @FXML TextField txtDni, txtNombre, txtCelular, txtCorreo, txtFiltroDato;
    @FXML TableView<Cliente> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private FilteredList<Cliente> filtrados;
    private Cliente formulario;
    private String idClienteCE = null;

    @FXML
    public void initialize() {
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idCliente", 90.0));
        columns.put("DNI", new ColumnInfo("dni", 100.0));
        columns.put("Nombre", new ColumnInfo("nombre", 220.0));
        columns.put("Celular", new ColumnInfo("celular", 110.0));
        columns.put("Correo", new ColumnInfo("correo", 230.0));
        new TableViewHelper<Cliente>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);

        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        idClienteCE = null;
        listar();
    }

    public void listar() {
        filtrados = new FilteredList<>(FXCollections.observableArrayList(cs.findAll()));
        tableView.setItems(filtrados);
        filtrar(txtFiltroDato.getText());
    }

    private void filtrar(String texto) {
        String t = texto == null ? "" : texto.toLowerCase().trim();
        filtrados.setPredicate(c -> t.isEmpty()
                || (c.getDni() + " " + c.getNombre() + " " + c.getCorreo()).toLowerCase().contains(t));
    }

    @FXML
    public void validarFormulario() {
        formulario = Cliente.builder()
                .dni(txtDni.getText().trim())
                .nombre(txtNombre.getText().trim())
                .celular(txtCelular.getText().trim())
                .correo(txtCorreo.getText().trim())
                .build();

        List<ConstraintViolation<Cliente>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idClienteCE != null) {
                formulario.setIdCliente(idClienteCE);
                cs.update(idClienteCE, formulario);
                FormHelper.toastOk(miContenedor, "Cliente actualizado correctamente");
            } else {
                cs.save(formulario);
                FormHelper.toastOk(miContenedor, "Cliente registrado: " + formulario.getIdCliente());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Cliente c) {
        if (!FormHelper.confirmar("¿Eliminar al cliente " + c.getNombre() + "?")) return;
        try {
            cs.delete(c.getIdCliente());
            FormHelper.toastOk(miContenedor, "Cliente eliminado");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Cliente c) {
        txtDni.setText(c.getDni());
        txtNombre.setText(c.getNombre());
        txtCelular.setText(c.getCelular());
        txtCorreo.setText(c.getCorreo());
        idClienteCE = c.getIdCliente();
        lblTitulo.setText("Editando " + c.getIdCliente());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        txtDni.clear();
        txtNombre.clear();
        txtCelular.clear();
        txtCorreo.clear();
        idClienteCE = null;
        lblTitulo.setText("Nuevo cliente");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("dni", txtDni);
        campos.put("nombre", txtNombre);
        campos.put("celular", txtCelular);
        campos.put("correo", txtCorreo);
        return campos;
    }
}
