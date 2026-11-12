package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ProductoVenta;
import pe.edu.upeu.celmax.enums.TipoProducto;
import pe.edu.upeu.celmax.model.*;
import pe.edu.upeu.celmax.repository.AccesorioRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.repository.InventarioRepository;
import pe.edu.upeu.celmax.repository.VentaRepository;
import pe.edu.upeu.celmax.service.IVentaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaServiceImp extends CrudGenericoServiceImp<Venta, String> implements IVentaService {

    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;
    private final AccesorioRepository accesorioRepository;

    public VentaServiceImp(VentaRepository ventaRepository,
                           InventarioRepository inventarioRepository,
                           AccesorioRepository accesorioRepository) {
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.accesorioRepository = accesorioRepository;
    }

    @Override
    protected ICrudGenericoRepository<Venta, String> getRepo() {
        return ventaRepository;
    }

    @Override
    public List<ProductoVenta> listarProductosVenta() {
        List<ProductoVenta> lista = new ArrayList<>();
        for (Inventario i : inventarioRepository.findAll()) {
            if (i.getStock() > 0) {
                lista.add(ProductoVenta.builder()
                        .tipo(TipoProducto.CELULAR)
                        .idProducto(i.getIdCelular().getIdCelular())
                        .idInventario(i.getIdInventario())
                        .descripcion(i.getIdCelular().getMarca() + " " + i.getIdCelular().getModelo()
                                + " (" + i.getIdSucursal().getCiudad() + ")")
                        .precio(i.getPrecio())
                        .stock(i.getStock())
                        .build());
            }
        }
        for (Accesorio a : accesorioRepository.findAll()) {
            if (a.getStock() > 0) {
                lista.add(ProductoVenta.builder()
                        .tipo(TipoProducto.ACCESORIO)
                        .idProducto(a.getIdAccesorio())
                        .descripcion(a.getNombre())
                        .precio(a.getPrecio())
                        .stock(a.getStock())
                        .build());
            }
        }
        return lista;
    }

    @Override
    public List<DetalleVenta> listarDetalles(String idVenta) {
        return ventaRepository.findDetalles(idVenta);
    }

    @Override
    public double[] calcularTotales(List<DetalleVenta> detalles) {
        BigDecimal total = detalles.stream()
                .map(d -> BigDecimal.valueOf(d.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal neto = total.divide(BigDecimal.valueOf(1 + TASA_IGV), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(neto);
        return new double[]{neto.doubleValue(), igv.doubleValue(), total.doubleValue()};
    }

    @Override
    public Venta registrarVenta(Cliente cliente, Usuario vendedor, List<DetalleVenta> detalles) {
        if (cliente == null) throw new IllegalArgumentException("Seleccione un cliente");
        if (detalles == null || detalles.isEmpty()) throw new IllegalArgumentException("Agregue al menos un producto");

        double[] t = calcularTotales(detalles);
        Venta venta = Venta.builder()
                .idCliente(cliente)
                .idUsuario(vendedor)
                .fechaVenta(LocalDate.now())
                .precioNeto(t[0])
                .igv(t[1])
                .precioTotal(t[2])
                .detalles(new ArrayList<>(detalles))
                .build();
        return ventaRepository.save(venta);
    }
}
