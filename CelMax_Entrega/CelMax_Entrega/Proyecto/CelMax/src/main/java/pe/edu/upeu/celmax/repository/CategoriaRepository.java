package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Categoria;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, String> {

    @Override
    protected String getTableName() { return "Categoria"; }

    @Override
    protected String getPkColumn() { return "idCategoria"; }

    @Override
    protected Categoria insert(Connection conn, Categoria e) throws SQLException {
        if (e.getIdCategoria() == null) {
            e.setIdCategoria(nextCode(conn, "Categoria", "idCategoria", "CAT-", 2));
        }
        executeUpdate(conn, "INSERT INTO Categoria(idCategoria, nombre) VALUES(?,?)",
                e.getIdCategoria(), e.getNombre());
        return e;
    }

    @Override
    protected Categoria updateRow(Connection conn, Categoria e) throws SQLException {
        executeUpdate(conn, "UPDATE Categoria SET nombre=? WHERE idCategoria=?",
                e.getNombre(), e.getIdCategoria());
        return e;
    }

    @Override
    protected Categoria mapRow(ResultSet rs) throws SQLException {
        return Categoria.builder()
                .idCategoria(rs.getString("idCategoria"))
                .nombre(rs.getString("nombre"))
                .build();
    }
}
