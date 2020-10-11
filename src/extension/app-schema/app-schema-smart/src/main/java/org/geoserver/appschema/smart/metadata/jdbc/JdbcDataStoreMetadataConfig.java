package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.Connection;

import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;

/**
 * Configuration class that keeps specific information related to DataStoreMetadata for JDBCs connections.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public class JdbcDataStoreMetadataConfig extends DataStoreMetadataConfig {

    public static String TYPE = "JDBC";

    private Connection connection;
    private String catalog;
    private String schema;

    public JdbcDataStoreMetadataConfig(Connection connection, String catalog, String schema) {
        super();
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
}
