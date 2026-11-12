package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaRepository extends AbstractJpaRepository<Venta, String> {

    private static final String SELECT_JOIN =
            "SELECT v.*, c.dni, c.nombre AS cli_nombre, u.nombreUsuario FROM Venta v " +
            "JOIN Cliente c ON v.idCliente = c.idCliente " +
            "JOIN Usuario u ON v.idUsuario = u.idUsuario ";

    @Override
    protected String getTableName() { return "Venta"; }

    @Override
    protected String getPkColumn() { return "idVenta"; }

    @Override
    public List<Venta> findAll() {
        return executeQuery(SELECT_JOIN + "ORDER BY v.idVenta DESC");
    }

    @Override
    public Optional<Venta> findById(String id) {
        Optional<Venta> opt = executeQueryOne(SELECT_JOIN + "WHERE v.idVenta = ?", id);
        opt.ifPresent(v -> v.setDetalles(findDetalles(id)));
        return opt;
    }

    /**
     * Guarda la cabecera, cada línea de detalle y descuenta el stock.
     * Se ejecuta dentro de la transacción que abre save() en AbstractJpaRepository:
     * si algo falla (por ejemplo, no hay stock) se hace rollback de todo.
     */
    @Override
    protected Venta insert(Connection conn, Venta e) throws SQLException {
        e.setIdVenta(nextCode(conn, "Venta", "idVenta", "VTA-", 3));
        executeUpdate(conn,
                "INSERT INTO Venta(idVenta, idCliente, fechaVenta, pNeto, igv, pTotal, idUsuario) VALUES(?,?,?,?,?,?,?)",
                e.getIdVenta(), e.getIdCliente().getIdCliente(), e.getFechaVenta(),
                e.getPrecioNeto(), e.getIgv(), e.getPrecioTotal(), e.getIdUsuario().getIdUsuario());

        for (DetalleVenta d : e.getDetalles()) {
            d.setIdDetalle(nextCode(conn, "DetalleVenta", "idDetalle", "DET-", 3));
            String idCelular = d.getIdCelular() != null ? d.getIdCelular().getIdCelular() : null;
            String idAccesorio = d.getIdAccesorio() != null ? d.getIdAccesorio().getIdAccesorio() : null;
            executeUpdate(conn,
                    "INSERT INTO DetalleVenta(idDetalle, idVenta, idCelular, idAccesorio, cantidad, precioUnitario, subtotal) VALUES(?,?,?,?,?,?,?)",
                    d.getIdDetalle(), e.getIdVenta(), idCelular, idAccesorio,
                    d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());

            // Descontar stock: celular -> fila de Inventario, accesorio -> tabla Accesorio
            String sqlStock = idCelular != null
                    ? "UPDATE Inventario SET stock = stock - ? WHERE idInventario = ? AND stock >= ?"
                    : "UPDATE Accesorio SET stock = stock - ? WHERE idAccesorio = ? AND stock >= ?";
            String idStock = idCelular != null ? d.getIdInventario() : idAccesorio;
            try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                ps.setInt(1, d.getCantidad());
                ps.setString(2, idStock);
                ps.setInt(3, d.getCantidad());
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Stock insuficiente para: " + d.getDescripcion());
                }
            }
        }
        return e;
    }

    @Override
    protected Venta updateRow(Connection conn, Venta e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Venta SET idCliente=?, fechaVenta=?, pNeto=?, igv=?, pTotal=?, idUsuario=? WHERE idVenta=?",
                e.getIdCliente().getIdCliente(), e.getFechaVenta(), e.getPrecioNeto(), e.getIgv(),
                e.getPrecioTotal(), e.getIdUsuario().getIdUsuario(), e.getIdVenta());
        return e;
    }

    @Override
    protected Venta mapRow(ResultSet rs) throws SQLException {
        return Venta.builder()
                .idVenta(rs.getString("idVenta"))
                .fechaVenta(rs.getDate("fechaVenta").toLocalDate())
                .precioNeto(rs.getDouble("pNeto"))
                .igv(rs.getDouble("igv"))
                .precioTotal(rs.getDouble("pTotal"))
                .idCliente(Cliente.builder()
                        .idCliente(rs.getString("idCliente"))
                        .dni(rs.getString("dni"))
                        .nombre(rs.getString("cli_nombre"))
                        .build())
                .idUsuario(Usuario.builder()
                        .idUsuario(rs.getString("idUsuario"))
                        .nombreUsuario(rs.getString("nombreUsuario"))
                        .build())
                .build();
    }

    /** Líneas de una venta, con el nombre del celular o accesorio. */
    public List<DetalleVenta> findDetalles(String idVenta) {
        String sql = "SELECT d.*, COALESCE(c.marca || ' ' || c.modelo, a.nombre) AS descripcion " +
                "FROM DetalleVenta d " +
                "LEFT JOIN Celular c ON d.idCelular = c.idCelular " +
                "LEFT JOIN Accesorio a ON d.idAccesorio = a.idAccesorio " +
                "WHERE d.idVenta = ? ORDER BY d.idDetalle";
        List<DetalleVenta> detalles = new ArrayList<>();
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String idCel = rs.getString("idCelular");
                    String idAcc = rs.getString("idAccesorio");
                    detalles.add(DetalleVenta.builder()
                            .idDetalle(rs.getString("idDetalle"))
                            .idCelular(idCel != null ? Celular.builder().idCelular(idCel).build() : null)
                            .idAccesorio(idAcc != null ? Accesorio.builder().idAccesorio(idAcc).build() : null)
                            .cantidad(rs.getInt("cantidad"))
                            .precioUnitario(rs.getDouble("precioUnitario"))
                            .subtotal(rs.getDouble("subtotal"))
                            .descripcion(rs.getString("descripcion"))
                            .build());
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error cargando el detalle de la venta: " + ex.getMessage(), ex);
        }
        return detalles;
    }
}
