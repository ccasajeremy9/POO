package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Accesorio;
import pe.edu.upeu.celmax.model.Categoria;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AccesorioRepository extends AbstractJpaRepository<Accesorio, String> {

    private static final String SELECT_JOIN =
            "SELECT a.*, cat.nombre AS cat_nombre FROM Accesorio a " +
            "JOIN Categoria cat ON a.idCategoria = cat.idCategoria ";

    @Override
    protected String getTableName() { return "Accesorio"; }

    @Override
    protected String getPkColumn() { return "idAccesorio"; }

    @Override
    public List<Accesorio> findAll() {
        return executeQuery(SELECT_JOIN + "ORDER BY a.idAccesorio");
    }

    @Override
    public Optional<Accesorio> findById(String id) {
        return executeQueryOne(SELECT_JOIN + "WHERE a.idAccesorio = ?", id);
    }

    @Override
    protected Accesorio insert(Connection conn, Accesorio e) throws SQLException {
        if (e.getIdAccesorio() == null) {
            e.setIdAccesorio(nextCode(conn, "Accesorio", "idAccesorio", "ACC-", 3));
        }
        executeUpdate(conn,
                "INSERT INTO Accesorio(idAccesorio, nombre, tipo, precio, stock, stockMinimo, idCategoria) VALUES(?,?,?,?,?,?,?)",
                e.getIdAccesorio(), e.getNombre(), e.getTipo(), e.getPrecio(), e.getStock(),
                e.getStockMinimo(), e.getIdCategoria().getIdCategoria());
        return e;
    }

    @Override
    protected Accesorio updateRow(Connection conn, Accesorio e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Accesorio SET nombre=?, tipo=?, precio=?, stock=?, stockMinimo=?, idCategoria=? WHERE idAccesorio=?",
                e.getNombre(), e.getTipo(), e.getPrecio(), e.getStock(), e.getStockMinimo(),
                e.getIdCategoria().getIdCategoria(), e.getIdAccesorio());
        return e;
    }

    @Override
    protected Accesorio mapRow(ResultSet rs) throws SQLException {
        return Accesorio.builder()
                .idAccesorio(rs.getString("idAccesorio"))
                .nombre(rs.getString("nombre"))
                .tipo(rs.getString("tipo"))
                .precio(rs.getDouble("precio"))
                .stock(rs.getInt("stock"))
                .stockMinimo(rs.getInt("stockMinimo"))
                .idCategoria(Categoria.builder()
                        .idCategoria(rs.getString("idCategoria"))
                        .nombre(rs.getString("cat_nombre"))
                        .build())
                .build();
    }
}
