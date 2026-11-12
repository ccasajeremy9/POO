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
import pe.edu.upeu.celmax.model.Accesorio;
import pe.edu.upeu.celmax.model.Categoria;
import pe.edu.upeu.celmax.service.IAccesorioService;
import pe.edu.upeu.celmax.service.ICategoriaService;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class AccesorioController {

    private final IAccesorioService as;
    private final ICategoriaService cats;

    @FXML TextField txtNombre, txtPrecio, txtStock, txtStockMinimo, txtFiltroDato;
    @FXML ComboBox<String> cbxTipo;
    @FXML TableView<Accesorio> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private FilteredList<Accesorio> filtrados;
    private Accesorio formulario;
    private String idAccesorioCE = null;

    @FXML
    public void initialize() {
        cbxTipo.getItems().setAll("Protección", "Carga", "Audio", "Otros");

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idAccesorio", 90.0));
        columns.put("Nombre", new ColumnInfo("nombre", 220.0));
        columns.put("Tipo", new ColumnInfo("tipo", 100.0));
        columns.put("Precio S/", new ColumnInfo("precio", 90.0));
        columns.put("Stock", new ColumnInfo("stock", 70.0));
        columns.put("Stock mín.", new ColumnInfo("stockMinimo", 80.0));
        new TableViewHelper<Accesorio>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);

        // Resaltar en rojo los accesorios con stock bajo
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Accesorio a, boolean empty) {
                super.updateItem(a, empty);
                getStyleClass().remove("fila-alerta");
                if (!empty && a != null && a.isStockBajo()) getStyleClass().add("fila-alerta");
            }
        });

        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        idAccesorioCE = null;
        listar();
    }

    public void listar() {
        filtrados = new FilteredList<>(FXCollections.observableArrayList(as.findAll()));
        tableView.setItems(filtrados);
        filtrar(txtFiltroDato.getText());
    }

    private void filtrar(String texto) {
        String t = texto == null ? "" : texto.toLowerCase().trim();
        filtrados.setPredicate(a -> t.isEmpty()
                || (a.getNombre() + " " + a.getTipo()).toLowerCase().contains(t));
    }

    @FXML
    public void validarFormulario() {
        formulario = Accesorio.builder()
                .nombre(txtNombre.getText().trim())
                .tipo(cbxTipo.getValue())
                .precio(FormHelper.parseDoubleSafe(txtPrecio.getText()))
                .stock(FormHelper.parseIntSafe(txtStock.getText()))
                .stockMinimo(FormHelper.parseIntSafe(txtStockMinimo.getText()))
                // Todo accesorio pertenece a la categoría "Accesorios"
                .idCategoria(cats.findById(Categoria.ACCESORIOS))
                .build();

        List<ConstraintViolation<Accesorio>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idAccesorioCE != null) {
                formulario.setIdAccesorio(idAccesorioCE);
                as.update(idAccesorioCE, formulario);
                FormHelper.toastOk(miContenedor, "Accesorio actualizado correctamente");
            } else {
                as.save(formulario);
                FormHelper.toastOk(miContenedor, "Accesorio registrado: " + formulario.getIdAccesorio());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Accesorio a) {
        if (!FormHelper.confirmar("¿Eliminar el accesorio " + a.getNombre() + "?")) return;
        try {
            as.delete(a.getIdAccesorio());
            FormHelper.toastOk(miContenedor, "Accesorio eliminado");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Accesorio a) {
        txtNombre.setText(a.getNombre());
        cbxTipo.setValue(a.getTipo());
        txtPrecio.setText(String.valueOf(a.getPrecio()));
        txtStock.setText(String.valueOf(a.getStock()));
        txtStockMinimo.setText(String.valueOf(a.getStockMinimo()));
        idAccesorioCE = a.getIdAccesorio();
        lblTitulo.setText("Editando " + a.getIdAccesorio());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        txtNombre.clear();
        cbxTipo.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtStock.clear();
        txtStockMinimo.clear();
        idAccesorioCE = null;
        lblTitulo.setText("Nuevo accesorio");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("tipo", cbxTipo);
        campos.put("precio", txtPrecio);
        campos.put("stock", txtStock);
        campos.put("stockMinimo", txtStockMinimo);
        return campos;
    }
}
