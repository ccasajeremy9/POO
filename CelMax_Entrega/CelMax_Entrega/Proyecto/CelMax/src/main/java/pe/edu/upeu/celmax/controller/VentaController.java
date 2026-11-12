package pe.edu.upeu.celmax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.components.ColumnInfo;
import pe.edu.upeu.celmax.components.FormHelper;
import pe.edu.upeu.celmax.components.TableViewHelper;
import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.dto.ProductoVenta;
import pe.edu.upeu.celmax.dto.SessionManager;
import pe.edu.upeu.celmax.enums.TipoProducto;
import pe.edu.upeu.celmax.model.*;
import pe.edu.upeu.celmax.service.IClienteService;
import pe.edu.upeu.celmax.service.IVentaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Registrar venta: se arma un carrito en memoria (lista de DetalleVenta)
 * y al confirmar se guarda Venta + DetalleVenta y se descuenta el stock.
 */
@RequiredArgsConstructor
public class VentaController {

    private final IVentaService vs;
    private final IClienteService cs;

    @FXML ComboBox<ComboBoxOption> cbxCliente;
    @FXML ComboBox<ProductoVenta> cbxProducto;
    @FXML TextField txtCantidad;
    @FXML Label lblVendedor, lblFecha, lblInfoProducto, lblNeto, lblIgv, lblTotal, lbnMsg;
    @FXML TableView<DetalleVenta> tableView;
    @FXML AnchorPane miContenedor;

    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblVendedor.setText(SessionManager.getInstance().getUserName());
        lblFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cargarCombos();

        cbxProducto.valueProperty().addListener((obs, o, p) -> lblInfoProducto.setText(p == null ? ""
                : "Precio: S/ " + String.format("%.2f", p.getPrecio()) + "   ·   Disponible: " + p.getStock()));

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Tipo", new ColumnInfo("tipo", 90.0));
        columns.put("Producto", new ColumnInfo("descripcion", 260.0));
        columns.put("Cant.", new ColumnInfo("cantidad", 60.0));
        columns.put("P. Unit. S/", new ColumnInfo("precioUnitario", 100.0));
        columns.put("Subtotal S/", new ColumnInfo("subtotal", 100.0));
        new TableViewHelper<DetalleVenta>().addColumnsInOrderWithSize(tableView, columns, null, this::quitar);

        carrito.clear();
        tableView.setItems(carrito);
        txtCantidad.setText("1");
        actualizarTotales();
    }

    private void cargarCombos() {
        cbxCliente.getItems().setAll(cs.listarCombobox());
        cbxProducto.getItems().setAll(vs.listarProductosVenta());
    }

    @FXML
    public void agregar() {
        ProductoVenta p = cbxProducto.getValue();
        Integer cantidad = FormHelper.parseIntSafe(txtCantidad.getText());
        if (p == null) {
            FormHelper.mensaje(lbnMsg, "Seleccione un producto", false);
            return;
        }
        if (cantidad == null || cantidad <= 0) {
            FormHelper.mensaje(lbnMsg, "La cantidad debe ser un número mayor a 0", false);
            return;
        }

        // Si el producto ya está en el carrito, se suma la cantidad
        Optional<DetalleVenta> existente = carrito.stream().filter(d -> esMismoProducto(d, p)).findFirst();
        int total = cantidad + existente.map(DetalleVenta::getCantidad).orElse(0);
        if (total > p.getStock()) {
            FormHelper.mensaje(lbnMsg, "Stock insuficiente. Disponible: " + p.getStock(), false);
            return;
        }

        if (existente.isPresent()) {
            DetalleVenta d = existente.get();
            d.setCantidad(total);
            d.setSubtotal(redondear(total * d.getPrecioUnitario()));
            tableView.refresh();
        } else {
            DetalleVenta d = DetalleVenta.builder()
                    .cantidad(cantidad)
                    .precioUnitario(p.getPrecio())
                    .subtotal(redondear(cantidad * p.getPrecio()))
                    .descripcion(p.getDescripcion())
                    .build();
            if (p.getTipo() == TipoProducto.CELULAR) {
                d.setIdCelular(Celular.builder().idCelular(p.getIdProducto()).build());
                d.setIdInventario(p.getIdInventario());
            } else {
                d.setIdAccesorio(Accesorio.builder().idAccesorio(p.getIdProducto()).build());
            }
            carrito.add(d);
        }
        FormHelper.mensaje(lbnMsg, "", true);
        cbxProducto.getSelectionModel().clearSelection();
        txtCantidad.setText("1");
        actualizarTotales();
    }

    private boolean esMismoProducto(DetalleVenta d, ProductoVenta p) {
        if (p.getTipo() == TipoProducto.CELULAR) {
            return d.getIdCelular() != null && p.getIdInventario().equals(d.getIdInventario());
        }
        return d.getIdAccesorio() != null && p.getIdProducto().equals(d.getIdAccesorio().getIdAccesorio());
    }

    private void quitar(DetalleVenta d) {
        carrito.remove(d);
        actualizarTotales();
    }

    private void actualizarTotales() {
        double[] t = vs.calcularTotales(carrito);
        lblNeto.setText(String.format("S/ %,.2f", t[0]));
        lblIgv.setText(String.format("S/ %,.2f", t[1]));
        lblTotal.setText(String.format("S/ %,.2f", t[2]));
    }

    @FXML
    public void registrarVenta() {
        String idCliente = FormHelper.keySeleccionada(cbxCliente);
        if (idCliente == null) {
            FormHelper.mensaje(lbnMsg, "Seleccione un cliente", false);
            return;
        }
        if (carrito.isEmpty()) {
            FormHelper.mensaje(lbnMsg, "Agregue al menos un producto", false);
            return;
        }
        try {
            Cliente cliente = cs.findById(idCliente);
            Usuario vendedor = Usuario.builder().idUsuario(SessionManager.getInstance().getUserId()).build();
            Venta venta = vs.registrarVenta(cliente, vendedor, carrito);
            FormHelper.toastOk(miContenedor, "Venta " + venta.getIdVenta() + " registrada. Total: S/ "
                    + String.format("%,.2f", venta.getPrecioTotal()));
            limpiar();
        } catch (Exception e) {
            FormHelper.mensaje(lbnMsg, FormHelper.mensajeError(e), false);
            cargarCombos(); // el stock pudo cambiar
        }
    }

    @FXML
    public void limpiar() {
        carrito.clear();
        cbxCliente.getSelectionModel().clearSelection();
        cbxProducto.getSelectionModel().clearSelection();
        txtCantidad.setText("1");
        FormHelper.mensaje(lbnMsg, "", true);
        cargarCombos();
        actualizarTotales();
    }

    private double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
