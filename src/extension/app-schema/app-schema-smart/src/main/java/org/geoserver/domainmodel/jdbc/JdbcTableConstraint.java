package org.geoserver.domainmodel.jdbc;

import com.google.common.collect.ComparisonChain;
import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.Attribute;

public class JdbcTableConstraint extends Attribute {
    private final JdbcTable table;
    private final String constraintName;

    public JdbcTableConstraint(JdbcTable table, String constraintName) {
        this.table = table;
        this.constraintName = constraintName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.table.toString());
        stringBuilder.append(" - ");
        stringBuilder.append(this.constraintName);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcTableConstraint)) {
            return false;
        }
        JdbcTableConstraint tableConstaint = (JdbcTableConstraint) object;
        return this.compareTo(tableConstaint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.table, this.constraintName);
    }

    public JdbcTable getTable() {
        return table;
    }

    public String getConstraintName() {
        return constraintName;
    }

	@Override
	public int compareTo(AbstractDomainObject tableConstraint) {
		if (tableConstraint != null) {
			JdbcTableConstraint tc = (JdbcTableConstraint) tableConstraint;
            return ComparisonChain.start()
                    .compare(this.table, tc.getTable())
                    .compare(this.constraintName, tc.getConstraintName())
                    .result();
        }
        return 1;
	}
}
