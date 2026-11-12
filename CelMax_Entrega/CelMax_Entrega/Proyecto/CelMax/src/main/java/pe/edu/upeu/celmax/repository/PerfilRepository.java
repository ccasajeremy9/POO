package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Perfil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PerfilRepository extends AbstractJpaRepository<Perfil, String> {

    @Override
    protected String getTableName() { return "Perfil"; }

    @Override
    protected String getPkColumn() { return "idPerfil"; }

    @Override
    protected Perfil insert(Connection conn, Perfil e) throws SQLException {
        if (e.getIdPerfil() == null) {
            e.setIdPerfil(nextCode(conn, "Perfil", "idPerfil", "PF-", 2));
        }
        executeUpdate(conn, "INSERT INTO Perfil(idPerfil, nombrePerfil) VALUES(?,?)",
                e.getIdPerfil(), e.getNombrePerfil());
        return e;
    }

    @Override
    protected Perfil updateRow(Connection conn, Perfil e) throws SQLException {
        executeUpdate(conn, "UPDATE Perfil SET nombrePerfil=? WHERE idPerfil=?",
                e.getNombrePerfil(), e.getIdPerfil());
        return e;
    }

    @Override
    protected Perfil mapRow(ResultSet rs) throws SQLException {
        return Perfil.builder()
                .idPerfil(rs.getString("idPerfil"))
                .nombrePerfil(rs.getString("nombrePerfil"))
                .build();
    }
}
