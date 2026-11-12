package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Cliente;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClienteRepository extends AbstractJpaRepository<Cliente, String> {

    @Override
    protected String getTableName() { return "Cliente"; }

    @Override
    protected String getPkColumn() { return "idCliente"; }

    @Override
    public List<Cliente> findAll() {
        return executeQuery("SELECT * FROM Cliente ORDER BY idCliente");
    }

    @Override
    protected Cliente insert(Connection conn, Cliente e) throws SQLException {
        if (e.getIdCliente() == null) {
            e.setIdCliente(nextCode(conn, "Cliente", "idCliente", "CLI-", 3));
        }
        executeUpdate(conn,
                "INSERT INTO Cliente(idCliente, dni, nombre, celular, correo) VALUES(?,?,?,?,?)",
                e.getIdCliente(), e.getDni(), e.getNombre(), e.getCelular(), e.getCorreo());
        return e;
    }

    @Override
    protected Cliente updateRow(Connection conn, Cliente e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Cliente SET dni=?, nombre=?, celular=?, correo=? WHERE idCliente=?",
                e.getDni(), e.getNombre(), e.getCelular(), e.getCorreo(), e.getIdCliente());
        return e;
    }

    @Override
    protected Cliente mapRow(ResultSet rs) throws SQLException {
        return Cliente.builder()
                .idCliente(rs.getString("idCliente"))
                .dni(rs.getString("dni"))
                .nombre(rs.getString("nombre"))
                .celular(rs.getString("celular"))
                .correo(rs.getString("correo"))
                .build();
    }
}
