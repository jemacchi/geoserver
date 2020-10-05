package org.geoserver.domain.model.jdbc.constraint;

import java.util.Objects;
import org.geoserver.domain.model.DomainObject;
import org.geoserver.domain.model.jdbc.JdbcTable;

public class JdbcIndexConstraint extends JdbcTableConstraint {

    public JdbcIndexConstraint(JdbcTable table, String constraintName) {
        super(table, constraintName);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcIndexConstraint)) {
            return false;
        }
        JdbcIndexConstraint indexConstraint = (JdbcIndexConstraint) object;
        return this.compareTo(indexConstraint) == 0;
    }

    @Override
    public int compareTo(DomainObject tableConstraint) {
        JdbcIndexConstraint indexConstraint = (JdbcIndexConstraint) tableConstraint;
        return super.compareTo(indexConstraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTable(), this.getName());
    }
}
