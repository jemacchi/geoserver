package org.geoserver.domain.model.jdbc;

import com.google.common.collect.ComparisonChain;
import java.sql.Connection;
import java.util.Objects;
import org.geoserver.domain.model.Attribute;
import org.geoserver.domain.model.DomainObject;

public class JdbcColumn extends Attribute implements JdbcConnectable {

    public JdbcColumn(JdbcTable table, String columnName, String columnType) {
        super(table, columnName, columnType);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getEntity().toString());
        stringBuilder.append(" -> ");
        stringBuilder.append(this.getName());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcColumn)) {
            return false;
        }
        JdbcColumn columnConstraint = (JdbcColumn) object;
        return this.compareTo(columnConstraint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEntity(), this.getName());
    }

    @Override
    public int compareTo(DomainObject tableColumn) {
        if (tableColumn != null) {
            JdbcColumn tc = (JdbcColumn) tableColumn;
            return ComparisonChain.start()
                    .compare(this.getEntity(), tc.getEntity())
                    .compare(this.getName(), tc.getName())
                    .result();
        }
        return 1;
    }

    @Override
    public Connection getConnection() {
        return ((JdbcTable) this.getEntity()).getConnection();
    }
}
