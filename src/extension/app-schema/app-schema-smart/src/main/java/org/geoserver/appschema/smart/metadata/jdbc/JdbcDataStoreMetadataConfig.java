package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.Connection;
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Type: ");
        stringBuilder.append(this.getType());
        stringBuilder.append(" - Connection: ");
        stringBuilder.append(this.connection.toString());
        stringBuilder.append(" - Catalog: ");
        stringBuilder.append(this.getCatalog());
        stringBuilder.append(" - Schema: ");
        stringBuilder.append(this.getSchema());
        return stringBuilder.toString();
    }

	@Override
	public Map<String, String> getParameters() {
		Map<String,String> out = new HashMap<String, String>();
		try {
			// Parse URL to get values
			JdbcUrlSplitter urlFields = new JdbcUrlSplitter(connection.getMetaData().getURL());
			String dbtype = urlFields.driverName;
			String host = urlFields.host;
			String port = urlFields.port;
			String database = urlFields.database;
			String username = connection.getMetaData().getUserName();
			// TODO: it's not possible to get it from JDBC API connection (makes sense)
			String password = "CHANGE ME";
			
			out.put("dbtype", dbtype);
			out.put("host", host);
			out.put("port", port);
			out.put("database", database);
			out.put("schema", schema);
			out.put("user", username);
			out.put("passwd", password);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
}
