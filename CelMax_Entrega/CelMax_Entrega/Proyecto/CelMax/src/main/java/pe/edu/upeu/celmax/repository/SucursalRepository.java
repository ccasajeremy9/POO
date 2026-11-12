package pe.edu.upeu.celmax.repository;

import pe.edu.upeu.celmax.model.Sucursal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SucursalRepository extends AbstractJpaRepository<Sucursal, String> {

    @Override
    protected String getTableName() { return "Sucursal"; }

    @Override
    protected String getPkColumn() { return "idSucursal"; }

    @Override
    public List<Sucursal> findAll() {
        return executeQuery("SELECT * FROM Sucursal ORDER BY idSucursal");
    }

    @Override
    protected Sucursal insert(Connection conn, Sucursal e) throws SQLException {
        if (e.getIdSucursal() == null) {
            e.setIdSucursal(nextCode(conn, "Sucursal", "idSucursal", "SUC-", 2));
        }
        executeUpdate(conn,
                "INSERT INTO Sucursal(idSucursal, numeroTienda, ciudad, tipo) VALUES(?,?,?,?)",
                e.getIdSucursal(), e.getNumeroTienda(), e.getCiudad(), e.getTipo());
        return e;
    }

    @Override
    protected Sucursal updateRow(Connection conn, Sucursal e) throws SQLException {
        executeUpdate(conn,
                "UPDATE Sucursal SET numeroTienda=?, ciudad=?, tipo=? WHERE idSucursal=?",
                e.getNumeroTienda(), e.getCiudad(), e.getTipo(), e.getIdSucursal());
        return e;
    }

    @Override
    protected Sucursal mapRow(ResultSet rs) throws SQLException {
        return Sucursal.builder()
                .idSucursal(rs.getString("idSucursal"))
                .numeroTienda(rs.getInt("numeroTienda"))
                .ciudad(rs.getString("ciudad"))
                .tipo(rs.getString("tipo"))
                .build();
    }
}
