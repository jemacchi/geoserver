package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.Connection;
import java.util.Objects;
import org.geoserver.appschema.smart.metadata.AttributeMetadata;

/**
 * Class representing metadata for a column (entity's attribute) in a JDBC DataStore.
 *
 * @author Jose Macchi - GeoSolutions
 */
public class JdbcColumnMetadata extends AttributeMetadata implements JdbcConnectable {

    public JdbcColumnMetadata(JdbcTableMetadata table, String columnName, String columnType, boolean externalReference) {
        super(table, columnName, columnType, externalReference);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getEntity().toString());
        stringBuilder.append(" -> ");
        stringBuilder.append(this.name);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcColumnMetadata)) {
            return false;
        }
        JdbcColumnMetadata columnConstraint = (JdbcColumnMetadata) object;
        return this.compareTo(columnConstraint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEntity(), this.name);
    }

    @Override
    public Connection getConnection() {
        return ((JdbcTableMetadata) this.getEntity()).getConnection();
    }
}
