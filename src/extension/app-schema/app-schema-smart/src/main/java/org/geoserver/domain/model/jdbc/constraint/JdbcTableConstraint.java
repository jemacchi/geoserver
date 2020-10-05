package org.geoserver.domain.model.jdbc.constraint;

import com.google.common.collect.ComparisonChain;
import java.util.Objects;
import org.geoserver.domain.model.Constraint;
import org.geoserver.domain.model.DomainObject;
import org.geoserver.domain.model.jdbc.JdbcTable;

public class JdbcTableConstraint extends Constraint {
    private final JdbcTable table;

    public JdbcTableConstraint(JdbcTable table, String name) {
        super(name);
        this.table = table;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.table.toString());
        stringBuilder.append(" - ");
        stringBuilder.append(this.getName());
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
        return Objects.hash(this.table, this.getName());
    }

    public JdbcTable getTable() {
        return table;
    }

    @Override
    public int compareTo(DomainObject tableConstraint) {
        if (tableConstraint != null) {
            JdbcTableConstraint tc = (JdbcTableConstraint) tableConstraint;
            return ComparisonChain.start()
                    .compare(this.table, tc.getTable())
                    .compare(this.getName(), tc.getName())
                    .result();
        }
        return 1;
    }
}
