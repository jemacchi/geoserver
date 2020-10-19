package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.geoserver.appschema.smart.domain.metadata.jdbc.utils.JdbcUrlSplitter;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;

/**
 * Configuration class that keeps specific information related to DataStoreMetadata for JDBCs
 * connections.
 *
 * @author Jose Macchi - Geosolutions
 */
public class JdbcDataStoreMetadataConfig extends DataStoreMetadataConfig {

    public static String TYPE = "JDBC";

    private Connection connection;
    private String password;
    private String name;
    private String catalog;
    private String schema;

    public JdbcDataStoreMetadataConfig(
            String name,
            String driver,
            String url,
            String user,
            String pass,
            String catalog,
            String schema) {
        super();
        this.name = name;
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, pass);
            this.connection = conn;
        } catch (Exception e) {
            throw new RuntimeException(
                    "JdbcDataStoreMetadataConfig: Cannot get connection to DataStoreMetadata.");
        }
        // it's not possible to get password from JDBC API connection (it's private info)
        // so, it's necessary to keep it in configuration object
        this.password = pass;
        this.catalog = catalog;
        this.schema = schema;
    }

    public JdbcDataStoreMetadataConfig(
            String name, Connection connection, String catalog, String schema) {
        super();
        this.name = name;
        this.connection = connection;
        this.catalog = catalog;
        this.schema = schema;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String getType() {
        return JdbcDataStoreMetadataConfig.TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Type: ");
        stringBuilder.append(this.getType());
        stringBuilder.append(" - Connection: ");
        try {
            stringBuilder.append(this.connection.getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException("JdbcDataStoreMetadataConfig: Cannot get connection URL.");
        }
        stringBuilder.append(" - Catalog: ");
        stringBuilder.append(this.getCatalog());
        stringBuilder.append(" - Schema: ");
        stringBuilder.append(this.getSchema());
        return stringBuilder.toString();
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> out = new HashMap<String, String>();
        try {
            JdbcUrlSplitter urlFields = new JdbcUrlSplitter(connection.getMetaData().getURL());
            String dbtype = urlFields.driverName;
            String host = urlFields.host;
            String port = urlFields.port;
            String database = urlFields.database;
            String username = connection.getMetaData().getUserName();
            String password = this.getPassword();
            // in case dbtype = postgresql, then translate it to postgis (since geoserver would not
            // understand it in
            // appschema context
            if (dbtype.equals("postgresql")) dbtype = "postgis";

            out.put("dbtype", dbtype);
            out.put("host", host);
            out.put("port", port);
            out.put("database", database);
            out.put("schema", schema);
            out.put("user", username);
            out.put("passwd", password);
            out.put("Expose primary keys", "true");

        } catch (SQLException e) {
            throw new RuntimeException("Error gettings URL parameters from JDBC connection.");
        }
        return out;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return password;
    }
}
