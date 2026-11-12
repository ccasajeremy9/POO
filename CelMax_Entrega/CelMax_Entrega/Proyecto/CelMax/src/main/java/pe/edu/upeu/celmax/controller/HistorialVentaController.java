package pe.edu.upeu.celmax.controller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.components.ColumnInfo;
import pe.edu.upeu.celmax.components.TableViewHelper;
import pe.edu.upeu.celmax.model.DetalleVenta;
import pe.edu.upeu.celmax.model.Venta;
import pe.edu.upeu.celmax.service.IVentaService;

import java.util.LinkedHashMap;

/** Consulta de ventas registradas y su detalle. */
@RequiredArgsConstructor
public class HistorialVentaController {

    private final IVentaService vs;

    @FXML TableView<Venta> tblVentas;
    @FXML TableView<DetalleVenta> tblDetalle;
    @FXML TextField txtFiltroDato;
    @FXML Label lblDetalle, lblResumen;

    private FilteredList<Venta> filtrados;

    @FXML
    public void initialize() {
        LinkedHashMap<String, ColumnInfo> colVentas = new LinkedHashMap<>();
        colVentas.put("Venta", new ColumnInfo("idVenta", 80.0));
        colVentas.put("Fecha", new ColumnInfo("fechaVenta", 95.0));
        colVentas.put("DNI", new ColumnInfo("idCliente.dni", 90.0));
        colVentas.put("Cliente", new ColumnInfo("idCliente.nombre", 190.0));
        colVentas.put("Vendedor", new ColumnInfo("idUsuario.nombreUsuario", 100.0));
        colVentas.put("Neto S/", new ColumnInfo("precioNeto", 90.0));
        colVentas.put("IGV S/", new ColumnInfo("igv", 80.0));
        colVentas.put("Total S/", new ColumnInfo("precioTotal", 90.0));
        new TableViewHelper<Venta>().addColumnsInOrderWithSize(tblVentas, colVentas);

        LinkedHashMap<String, ColumnInfo> colDet = new LinkedHashMap<>();
        colDet.put("Línea", new ColumnInfo("idDetalle", 80.0));
        colDet.put("Tipo", new ColumnInfo("tipo", 90.0));
        colDet.put("Producto", new ColumnInfo("descripcion", 240.0));
        colDet.put("Cant.", new ColumnInfo("cantidad", 60.0));
        colDet.put("P. Unit. S/", new ColumnInfo("precioUnitario", 100.0));
        colDet.put("Subtotal S/", new ColumnInfo("subtotal", 100.0));
        new TableViewHelper<DetalleVenta>().addColumnsInOrderWithSize(tblDetalle, colDet);

        tblVentas.getSelectionModel().selectedItemProperty().addListener((obs, o, v) -> mostrarDetalle(v));
        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        listar();
    }

    @FXML
    public void listar() {
        filtrados = new FilteredList<>(FXCollections.observableArrayList(vs.findAll()));
        tblVentas.setItems(filtrados);
        filtrar(txtFiltroDato.getText());
        tblDetalle.getItems().clear();
        lblDetalle.setText("Seleccione una venta para ver su detalle");
    }

    private void filtrar(String texto) {
        String t = texto == null ? "" : texto.toLowerCase().trim();
        filtrados.setPredicate(v -> t.isEmpty()
                || (v.getIdVenta() + " " + v.getIdCliente().getDni() + " " + v.getIdCliente().getNombre()
                + " " + v.getIdUsuario().getNombreUsuario()).toLowerCase().contains(t));
        double total = filtrados.stream().mapToDouble(Venta::getPrecioTotal).sum();
        lblResumen.setText(filtrados.size() + " ventas  ·  Total: " + String.format("S/ %,.2f", total));
    }

    private void mostrarDetalle(Venta v) {
        if (v == null) {
            tblDetalle.getItems().clear();
            return;
        }
        lblDetalle.setText("Detalle de " + v.getIdVenta() + " — " + v.getIdCliente().getNombre());
        tblDetalle.setItems(FXCollections.observableArrayList(vs.listarDetalles(v.getIdVenta())));
    }
}
