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
import pe.edu.upeu.celmax.model.Categoria;
import pe.edu.upeu.celmax.model.Celular;
import pe.edu.upeu.celmax.service.ICategoriaService;
import pe.edu.upeu.celmax.service.ICelularService;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class CelularController {

    private final ICelularService cs;
    private final ICategoriaService cats;

    @FXML TextField txtModelo, txtMarca, txtAlmacenamiento, txtRam, txtFiltroDato;
    @FXML TableView<Celular> tableView;
    @FXML Label lbnMsg, lblTitulo;
    @FXML AnchorPane miContenedor;

    private FilteredList<Celular> filtrados;
    private Celular formulario;
    private String idCelularCE = null;

    @FXML
    public void initialize() {
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Código", new ColumnInfo("idCelular", 90.0));
        columns.put("Marca", new ColumnInfo("marca", 120.0));
        columns.put("Modelo", new ColumnInfo("modelo", 180.0));
        columns.put("Almac. (GB)", new ColumnInfo("almacenamiento", 100.0));
        columns.put("RAM (GB)", new ColumnInfo("ram", 90.0));
        columns.put("Categoría", new ColumnInfo("idCategoria.nombre", 110.0));
        new TableViewHelper<Celular>().addColumnsInOrderWithSize(tableView, columns, this::editForm, this::eliminar);

        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        idCelularCE = null;
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
                || (c.getMarca() + " " + c.getModelo()).toLowerCase().contains(t));
    }

    @FXML
    public void validarFormulario() {
        formulario = Celular.builder()
                .modelo(txtModelo.getText().trim())
                .marca(txtMarca.getText().trim())
                .almacenamiento(FormHelper.parseIntSafe(txtAlmacenamiento.getText()))
                .ram(FormHelper.parseIntSafe(txtRam.getText()))
                // Todo celular pertenece a la categoría "Celulares"
                .idCategoria(cats.findById(Categoria.CELULARES))
                .build();

        List<ConstraintViolation<Celular>> violaciones = FormHelper.validar(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            FormHelper.mostrarErrores(violaciones, campos(), lbnMsg);
        }
    }

    private void procesarFormulario() {
        try {
            if (idCelularCE != null) {
                formulario.setIdCelular(idCelularCE);
                cs.update(idCelularCE, formulario);
                FormHelper.toastOk(miContenedor, "Celular actualizado correctamente");
            } else {
                cs.save(formulario);
                FormHelper.toastOk(miContenedor, "Celular registrado: " + formulario.getIdCelular());
            }
            clearForm();
            listar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
        }
    }

    private void eliminar(Celular c) {
        if (!FormHelper.confirmar("¿Eliminar el celular " + c.getMarca() + " " + c.getModelo() + "?")) return;
        try {
            cs.delete(c.getIdCelular());
            FormHelper.toastOk(miContenedor, "Celular eliminado");
            listar();
        } catch (Exception e) {
            FormHelper.toastError(miContenedor, FormHelper.mensajeError(e));
        }
    }

    public void editForm(Celular c) {
        txtModelo.setText(c.getModelo());
        txtMarca.setText(c.getMarca());
        txtAlmacenamiento.setText(String.valueOf(c.getAlmacenamiento()));
        txtRam.setText(String.valueOf(c.getRam()));
        idCelularCE = c.getIdCelular();
        lblTitulo.setText("Editando " + c.getIdCelular());
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    @FXML
    public void clearForm() {
        txtModelo.clear();
        txtMarca.clear();
        txtAlmacenamiento.clear();
        txtRam.clear();
        idCelularCE = null;
        lblTitulo.setText("Nuevo celular");
        FormHelper.limpiarErrores(campos().values());
        FormHelper.mensaje(lbnMsg, "", true);
    }

    private LinkedHashMap<String, Control> campos() {
        LinkedHashMap<String, Control> campos = new LinkedHashMap<>();
        campos.put("marca", txtMarca);
        campos.put("modelo", txtModelo);
        campos.put("almacenamiento", txtAlmacenamiento);
        campos.put("ram", txtRam);
        return campos;
    }
}
