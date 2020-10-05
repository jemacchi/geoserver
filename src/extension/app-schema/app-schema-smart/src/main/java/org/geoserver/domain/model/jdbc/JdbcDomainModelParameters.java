package org.geoserver.domain.model.jdbc;

import java.sql.Connection;
import org.geoserver.domain.model.DomainModelParameters;

public class JdbcDomainModelParameters extends DomainModelParameters {

    public static String TYPE = "JDBC";

    private Connection connection;
    private String catalog;
    private String schema;

    public JdbcDomainModelParameters(Connection connection, String catalog, String schema) {
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
        return JdbcDomainModelParameters.TYPE;
    }
}
