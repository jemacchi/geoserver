package org.geoserver.domainmodel.jdbc;

import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.Attribute;

import com.google.common.collect.ComparisonChain;

public class JdbcColumn extends Attribute {

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
    public int compareTo(AbstractDomainObject tableColumn) {
    	if (tableColumn != null) {
			JdbcColumn tc = (JdbcColumn) tableColumn;
            return ComparisonChain.start()
                    .compare(this.getEntity(), tc.getEntity())
                    .compare(this.getName(), tc.getName())
                    .result();
        }
        return 1;
    }

}
