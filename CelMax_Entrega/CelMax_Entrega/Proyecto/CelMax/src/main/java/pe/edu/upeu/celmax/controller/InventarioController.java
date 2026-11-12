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
import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Inventario;
import pe.edu.upeu.celmax.service.ICelularService;
import pe.edu.upeu.celmax.service.IInventarioService;
import pe.edu.upeu.celmax.service.ISucursalService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class InventarioController {

    private final IInventarioService is;
    private final ICelularService cs;
    private final ISucursalService ss;

    @FXML ComboBox<ComboBoxOption> cbxCelular, cbxSucursal;
    @FXML DatePicker dpFechaIngreso;
    @FXML TextField txtStock, txtPrecio, txtFiltroDato;
    @FXML TableView<Inventario> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private FilteredList<Inventario> filtrados;
    private Inventario formulario;
    private String idInventarioCE = null;

    @FXML
    public void initialize() {
        cbxCelular.getItems().setAll(cs.listarCombobox());
        cbxSucursal.getItems().setAll(ss.listarCombobox());
        dpFechaIngreso.setValue(LocalDate.now());

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idInventario", 85.0));
        columns.put("Marca", new ColumnInfo("idCelular.marca", 100.0));
        columns.put("Celular", new ColumnInfo("idCelular.modelo", 160.0));
        columns.put("Sucursal", new ColumnInfo("idSucursal.ciudad", 110.0));
        columns.put("Ingreso", new ColumnInfo("fechaIngreso", 100.0));
        columns.put("Stock", new ColumnInfo("stock", 70.0));
        columns.put("Precio S/", new ColumnInfo("precio", 90.0));
        new TableViewHelper<Inventario>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);

        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        idInventarioCE = null;
        listar();
    }

    public void listar() {
        filtrados = new FilteredList<>(FXCollections.observableArrayList(is.findAll()));
        tableView.setItems(filtrados);
        filtrar(txtFiltroDato.getText());
    }

    private void filtrar(String texto) {
        String t = texto == null ? "" : texto.toLowerCase().trim();
        filtrados.setPredicate(i -> t.isEmpty()
                || (i.getIdCelular().getMarca() + " " + i.getIdCelular().getModelo() + " "
                + i.getIdSucursal().getCiudad()).toLowerCase().contains(t));
    }

    @FXML
    public void validarFormulario() {
        String idCel = FormHelper.keySeleccionada(cbxCelular);
        String idSuc = FormHelper.keySeleccionada(cbxSucursal);
        formulario = Inventario.builder()
                .idCelular(idCel == null ? null : cs.findById(idCel))
                .idSucursal(idSuc == null ? null : ss.findById(idSuc))
                .fechaIngreso(dpFechaIngreso.getValue())
                .stock(FormHelper.parseIntSafe(txtStock.getText()))
                .precio(FormHelper.parseDoubleSafe(txtPrecio.getText()))
                .build();

        List<ConstraintViolation<Inventario>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idInventarioCE != null) {
                formulario.setIdInventario(idInventarioCE);
                is.update(idInventarioCE, formulario);
                FormHelper.toastOk(miContenedor, "Inventario actualizado correctamente");
            } else {
                is.save(formulario);
                FormHelper.toastOk(miContenedor, "Ingreso registrado: " + formulario.getIdInventario());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Inventario i) {
        if (!FormHelper.confirmar("¿Eliminar el registro " + i.getIdInventario() + "?")) return;
        try {
            is.delete(i.getIdInventario());
            FormHelper.toastOk(miContenedor, "Registro eliminado");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Inventario i) {
        FormHelper.seleccionar(cbxCelular, i.getIdCelular().getIdCelular());
        FormHelper.seleccionar(cbxSucursal, i.getIdSucursal().getIdSucursal());
        dpFechaIngreso.setValue(i.getFechaIngreso());
        txtStock.setText(String.valueOf(i.getStock()));
        txtPrecio.setText(String.valueOf(i.getPrecio()));
        idInventarioCE = i.getIdInventario();
        lblTitulo.setText("Editando " + i.getIdInventario());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        cbxCelular.getSelectionModel().clearSelection();
        cbxSucursal.getSelectionModel().clearSelection();
        dpFechaIngreso.setValue(LocalDate.now());
        txtStock.clear();
        txtPrecio.clear();
        idInventarioCE = null;
        lblTitulo.setText("Nuevo ingreso");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("idCelular", cbxCelular);
        campos.put("idSucursal", cbxSucursal);
        campos.put("fechaIngreso", dpFechaIngreso);
        campos.put("stock", txtStock);
        campos.put("precio", txtPrecio);
        return campos;
    }
}
