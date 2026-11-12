package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Perfil;
import pe.edu.upeu.celmax.model.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends AbstractJpaRepository<Usuario, String> {

    private static final String SELECT_JOIN =
            "SELECT u.*, p.nombrePerfil FROM Usuario u " +
            "JOIN Perfil p ON u.idPerfil = p.idPerfil ";

    @Override
    protected String getTableName() { return "Usuario"; }

    @Override
    protected String getPkColumn() { return "idUsuario"; }

    @Override
    public List<Usuario> findAll() {
        return executeQuery(SELECT_JOIN + "ORDER BY u.idUsuario");
    }

    @Override
    public Optional<Usuario> findById(String id) {
        return executeQueryOne(SELECT_JOIN + "WHERE u.idUsuario = ?", id);
    }

    public Optional<Usuario> findByUsuarioAndClave(String usuario, String clave) {
        return executeQueryOne(SELECT_JOIN + "WHERE u.nombreUsuario = ? AND u.contrasena = ?",
                usuario, clave);
    }

    @Override
    protected Usuario insert(Connection conn, Usuario e) throws SQLException {
        if (e.getIdUsuario() == null) {
            e.setIdUsuario(nextCode(conn, "Usuario", "idUsuario", "USR-", 2));
        }
        executeUpdate(conn,
                "INSERT INTO Usuario(idUsuario, nombreUsuario, contrasena, idPerfil) VALUES(?,?,?,?)",
                e.getIdUsuario(), e.getNombreUsuario(), e.getContrasena(), e.getIdPerfil().getIdPerfil());
        return e;
    }

    @Override
    protected Usuario updateRow(Connection conn, Usuario e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Usuario SET nombreUsuario=?, contrasena=?, idPerfil=? WHERE idUsuario=?",
                e.getNombreUsuario(), e.getContrasena(), e.getIdPerfil().getIdPerfil(), e.getIdUsuario());
        return e;
    }

    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        return Usuario.builder()
                .idUsuario(rs.getString("idUsuario"))
                .nombreUsuario(rs.getString("nombreUsuario"))
                .contrasena(rs.getString("contrasena"))
                .idPerfil(Perfil.builder()
                        .idPerfil(rs.getString("idPerfil"))
                        .nombrePerfil(rs.getString("nombrePerfil"))
                        .build())
                .build();
    }
}
