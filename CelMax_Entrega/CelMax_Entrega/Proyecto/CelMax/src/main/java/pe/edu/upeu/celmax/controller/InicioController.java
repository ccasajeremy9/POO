package pe.edu.upeu.celmax.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.celmax.components.ColumnInfo;
import pe.edu.upeu.celmax.components.TableViewHelper;
import pe.edu.upeu.celmax.dto.SessionManager;
import pe.edu.upeu.celmax.model.Accesorio;
import pe.edu.upeu.celmax.model.Inventario;
import pe.edu.upeu.celmax.model.Venta;
import pe.edu.upeu.celmax.service.IAccesorioService;
import pe.edu.upeu.celmax.service.IClienteService;
import pe.edu.upeu.celmax.service.IInventarioService;
import pe.edu.upeu.celmax.service.IVentaService;

import java.util.LinkedHashMap;
import java.util.List;

/** Pantalla de bienvenida con un resumen del negocio. */
@RequiredArgsConstructor
public class InicioController {

    private final IVentaService vs;
    private final IClienteService cs;
    private final IInventarioService is;
    private final IAccesorioService as;

    @FXML Label lblBienvenida, lblVentas, lblTotal, lblClientes, lblCelulares;
    @FXML TableView<Venta> tblUltimas;
    @FXML TableView<Accesorio> tblStockBajo;

    @FXML
    public void initialize() {
        lblBienvenida.setText("¡Hola, " + SessionManager.getInstance().getUserName() + "!");

        LinkedHashMap<String, ColumnInfo> colVentas = new LinkedHashMap<>();
        colVentas.put("Venta", new ColumnInfo("idVenta", 80.0));
        colVentas.put("Fecha", new ColumnInfo("fechaVenta", 100.0));
        colVentas.put("Cliente", new ColumnInfo("idCliente.nombre", 200.0));
        colVentas.put("Total S/", new ColumnInfo("precioTotal", 100.0));
        new TableViewHelper<Venta>().addColumnsInOrderWithSize(tblUltimas, colVentas);

        LinkedHashMap<String, ColumnInfo> colAcc = new LinkedHashMap<>();
        colAcc.put("Accesorio", new ColumnInfo("nombre", 220.0));
        colAcc.put("Stock", new ColumnInfo("stock", 70.0));
        colAcc.put("Mínimo", new ColumnInfo("stockMinimo", 70.0));
        new TableViewHelper<Accesorio>().addColumnsInOrderWithSize(tblStockBajo, colAcc);

        actualizar();
    }

    @FXML
    public void actualizar() {
        List<Venta> ventas = vs.findAll();
        double total = ventas.stream().mapToDouble(Venta::getPrecioTotal).sum();
        int celulares = is.findAll().stream().mapToInt(Inventario::getStock).sum();

        lblVentas.setText(String.valueOf(ventas.size()));
        lblTotal.setText(String.format("S/ %,.2f", total));
        lblClientes.setText(String.valueOf(cs.findAll().size()));
        lblCelulares.setText(String.valueOf(celulares));

        tblUltimas.setItems(FXCollections.observableArrayList(ventas.stream().limit(8).toList()));
        tblStockBajo.setItems(FXCollections.observableArrayList(
                as.findAll().stream().filter(Accesorio::isStockBajo).toList()));
    }
}
