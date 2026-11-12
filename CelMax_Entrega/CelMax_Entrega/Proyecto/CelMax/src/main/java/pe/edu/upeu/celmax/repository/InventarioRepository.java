package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Celular;
import pe.edu.upeu.celmax.model.Inventario;
import pe.edu.upeu.celmax.model.Sucursal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InventarioRepository extends AbstractJpaRepository<Inventario, String> {

    private static final String SELECT_JOIN =
            "SELECT i.*, c.modelo, c.marca, s.ciudad, s.numeroTienda, s.tipo AS suc_tipo " +
            "FROM Inventario i " +
            "JOIN Celular c ON i.idCelular = c.idCelular " +
            "JOIN Sucursal s ON i.idSucursal = s.idSucursal ";

    @Override
    protected String getTableName() { return "Inventario"; }

    @Override
    protected String getPkColumn() { return "idInventario"; }

    @Override
    public List<Inventario> findAll() {
        return executeQuery(SELECT_JOIN + "ORDER BY i.idInventario");
    }

    @Override
    public Optional<Inventario> findById(String id) {
        return executeQueryOne(SELECT_JOIN + "WHERE i.idInventario = ?", id);
    }

    @Override
    protected Inventario insert(Connection conn, Inventario e) throws SQLException {
        if (e.getIdInventario() == null) {
            e.setIdInventario(nextCode(conn, "Inventario", "idInventario", "INV-", 3));
        }
        executeUpdate(conn,
                "INSERT INTO Inventario(idInventario, idCelular, idSucursal, fechaIngreso, stock, precio) VALUES(?,?,?,?,?,?)",
                e.getIdInventario(), e.getIdCelular().getIdCelular(), e.getIdSucursal().getIdSucursal(),
                e.getFechaIngreso(), e.getStock(), e.getPrecio());
        return e;
    }

    @Override
    protected Inventario updateRow(Connection conn, Inventario e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Inventario SET idCelular=?, idSucursal=?, fechaIngreso=?, stock=?, precio=? WHERE idInventario=?",
                e.getIdCelular().getIdCelular(), e.getIdSucursal().getIdSucursal(),
                e.getFechaIngreso(), e.getStock(), e.getPrecio(), e.getIdInventario());
        return e;
    }

    @Override
    protected Inventario mapRow(ResultSet rs) throws SQLException {
        return Inventario.builder()
                .idInventario(rs.getString("idInventario"))
                .fechaIngreso(rs.getDate("fechaIngreso").toLocalDate())
                .stock(rs.getInt("stock"))
                .precio(rs.getDouble("precio"))
                .idCelular(Celular.builder()
                        .idCelular(rs.getString("idCelular"))
                        .modelo(rs.getString("modelo"))
                        .marca(rs.getString("marca"))
                        .build())
                .idSucursal(Sucursal.builder()
                        .idSucursal(rs.getString("idSucursal"))
                        .ciudad(rs.getString("ciudad"))
                        .numeroTienda(rs.getInt("numeroTienda"))
                        .tipo(rs.getString("suc_tipo"))
                        .build())
                .build();
    }
}
