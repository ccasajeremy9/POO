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
import pe.edu.upeu.celmax.model.Sucursal;
import pe.edu.upeu.celmax.service.ISucursalService;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class SucursalController {

    private final ISucursalService ss;

    @FXML TextField txtNumeroTienda, txtCiudad;
    @FXML ComboBox<String> cbxTipo;
    @FXML TableView<Sucursal> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private Sucursal formulario;
    private String idSucursalCE = null;

    @FXML
    public void initialize() {
        cbxTipo.getItems().setAll("Flagship", "Mall", "Quiosco");

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idSucursal", 90.0));
        columns.put("N° Tienda", new ColumnInfo("numeroTienda", 90.0));
        columns.put("Ciudad", new ColumnInfo("ciudad", 180.0));
        columns.put("Tipo", new ColumnInfo("tipo", 120.0));
        new TableViewHelper<Sucursal>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);
        idSucursalCE = null;
        listar();
    }

    public void listar() {
        tableView.setItems(FXCollections.observableArrayList(ss.findAll()));
    }

    @FXML
    public void validarFormulario() {
        formulario = Sucursal.builder()
                .numeroTienda(FormHelper.parseIntSafe(txtNumeroTienda.getText()))
                .ciudad(txtCiudad.getText().trim())
                .tipo(cbxTipo.getValue())
                .build();

        List<ConstraintViolation<Sucursal>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idSucursalCE != null) {
                formulario.setIdSucursal(idSucursalCE);
                ss.update(idSucursalCE, formulario);
                FormHelper.toastOk(miContenedor, "Sucursal actualizada correctamente");
            } else {
                ss.save(formulario);
                FormHelper.toastOk(miContenedor, "Sucursal registrada: " + formulario.getIdSucursal());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Sucursal s) {
        if (!FormHelper.confirmar("¿Eliminar la sucursal de " + s.getCiudad() + "?")) return;
        try {
            ss.delete(s.getIdSucursal());
            FormHelper.toastOk(miContenedor, "Sucursal eliminada");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Sucursal s) {
        txtNumeroTienda.setText(String.valueOf(s.getNumeroTienda()));
        txtCiudad.setText(s.getCiudad());
        cbxTipo.setValue(s.getTipo());
        idSucursalCE = s.getIdSucursal();
        lblTitulo.setText("Editando " + s.getIdSucursal());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        txtNumeroTienda.clear();
        txtCiudad.clear();
        cbxTipo.getSelectionModel().clearSelection();
        idSucursalCE = null;
        lblTitulo.setText("Nueva sucursal");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("numeroTienda", txtNumeroTienda);
        campos.put("ciudad", txtCiudad);
        campos.put("tipo", cbxTipo);
        return campos;
    }
}
