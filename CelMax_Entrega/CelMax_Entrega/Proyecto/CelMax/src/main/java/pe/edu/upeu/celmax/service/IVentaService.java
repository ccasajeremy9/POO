package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ProductoVenta;
import pe.edu.upeu.celmax.model.Cliente;
import pe.edu.upeu.celmax.model.DetalleVenta;
import pe.edu.upeu.celmax.model.Usuario;
import pe.edu.upeu.celmax.model.Venta;

import java.util.List;

public interface IVentaService extends ICrudGenericoService<Venta, String> {

    double TASA_IGV = 0.18;

    /** Celulares (por sucursal, desde Inventario) y accesorios con stock disponible. */
    List<ProductoVenta> listarProductosVenta();

    List<DetalleVenta> listarDetalles(String idVenta);

    /** Calcula {neto, igv, total} a partir de los subtotales (los precios incluyen IGV). */
    double[] calcularTotales(List<DetalleVenta> detalles);

    /** Arma la venta, calcula los totales y la guarda junto con el detalle. */
    Venta registrarVenta(Cliente cliente, Usuario vendedor, List<DetalleVenta> detalles);
}
