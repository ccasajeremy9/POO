package pe.edu.upeu.celmax.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Configuración de la base de datos H2 (embebida, en archivo) con pool HikariCP.
 * Es la misma clase de SysVentas; lo único nuevo es {@code db.data.script}:
 * los datos de prueba se cargan solo cuando la base está vacía.
 */
public final class DatabaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/celmax";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_SCRIPT = "schema_celmax.sql";

    private static volatile HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    /** Inicializa el pool y ejecuta el DDL una sola vez. */
    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        Properties props = loadProperties();
        String url = props.getProperty("db.url", DEFAULT_URL);

        HikariDataSource ds = createDataSource(url, props);
        try {
            if (Boolean.parseBoolean(props.getProperty("db.ddl.auto", "true"))) {
                runScript(ds, props.getProperty("db.ddl.script", DEFAULT_SCRIPT));
                String data = props.getProperty("db.data.script");
                if (data != null && isEmpty(ds)) {
                    runScript(ds, data);
                }
            }
        } catch (RuntimeException e) {
            ds.close();
            throw e;
        }
        dataSource = ds;
        LOG.info("H2 listo: {}", url);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Cierra el pool al salir de la aplicación. */
    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static HikariDataSource createDataSource(String url, Properties props) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("CelMaxPool");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(url);
        config.setUsername(props.getProperty("db.username", DEFAULT_USER));
        config.setPassword(props.getProperty("db.password", ""));
        // App de escritorio con un solo usuario: un pool pequeño es suficiente.
        config.setMaximumPoolSize(5);
        try {
            return new HikariDataSource(config);
        } catch (RuntimeException e) {
            throw new IllegalStateException("No fue posible abrir la base de datos H2: " + url, e);
        }
    }

    private static Properties loadProperties() {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + PROPERTIES_FILE);
            }
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + PROPERTIES_FILE, e);
        }
    }

    /** true si todavía no hay perfiles registrados (base recién creada). */
    private static boolean isEmpty(DataSource ds) {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Perfil")) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error revisando la base de datos", e);
        }
    }

    /** Ejecuta un script SQL sentencia por sentencia. */
    private static void runScript(DataSource ds, String script) {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + script)) {
            if (in == null) {
                LOG.warn("No se encontró el script {}", script);
                return;
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .lines()
                    .filter(line -> !line.strip().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
                for (String statement : sql.split(";")) {
                    if (!statement.isBlank()) {
                        stmt.execute(statement);
                    }
                }
            }
            LOG.info("Script ejecutado: {}", script);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Error ejecutando el script " + script, e);
        }
    }
}
