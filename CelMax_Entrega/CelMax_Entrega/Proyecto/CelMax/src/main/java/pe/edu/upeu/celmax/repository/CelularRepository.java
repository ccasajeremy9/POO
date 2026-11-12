package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Categoria;
import pe.edu.upeu.celmax.model.Celular;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CelularRepository extends AbstractJpaRepository<Celular, String> {

    private static final String SELECT_JOIN =
            "SELECT c.*, cat.nombre AS cat_nombre FROM Celular c " +
            "JOIN Categoria cat ON c.idCategoria = cat.idCategoria ";

    @Override
    protected String getTableName() { return "Celular"; }

    @Override
    protected String getPkColumn() { return "idCelular"; }

    @Override
    public List<Celular> findAll() {
        return executeQuery(SELECT_JOIN + "ORDER BY c.idCelular");
    }

    @Override
    public Optional<Celular> findById(String id) {
        return executeQueryOne(SELECT_JOIN + "WHERE c.idCelular = ?", id);
    }

    @Override
    protected Celular insert(Connection conn, Celular e) throws SQLException {
        if (e.getIdCelular() == null) {
            e.setIdCelular(nextCode(conn, "Celular", "idCelular", "CEL-", 3));
        }
        executeUpdate(conn,
                "INSERT INTO Celular(idCelular, modelo, marca, almacenamiento, ram, idCategoria) VALUES(?,?,?,?,?,?)",
                e.getIdCelular(), e.getModelo(), e.getMarca(), e.getAlmacenamiento(), e.getRam(),
                e.getIdCategoria().getIdCategoria());
        return e;
    }

    @Override
    protected Celular updateRow(Connection conn, Celular e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Celular SET modelo=?, marca=?, almacenamiento=?, ram=?, idCategoria=? WHERE idCelular=?",
                e.getModelo(), e.getMarca(), e.getAlmacenamiento(), e.getRam(),
                e.getIdCategoria().getIdCategoria(), e.getIdCelular());
        return e;
    }

    @Override
    protected Celular mapRow(ResultSet rs) throws SQLException {
        return Celular.builder()
                .idCelular(rs.getString("idCelular"))
                .modelo(rs.getString("modelo"))
                .marca(rs.getString("marca"))
                .almacenamiento(rs.getInt("almacenamiento"))
                .ram(rs.getInt("ram"))
                .idCategoria(Categoria.builder()
                        .idCategoria(rs.getString("idCategoria"))
                        .nombre(rs.getString("cat_nombre"))
                        .build())
                .build();
    }
}
