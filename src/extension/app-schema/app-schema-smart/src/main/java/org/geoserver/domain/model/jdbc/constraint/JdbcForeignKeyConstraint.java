package org.geoserver.domain.model.jdbc.constraint;

import java.util.Objects;
import org.geoserver.domain.model.DomainObject;
import org.geoserver.domain.model.jdbc.JdbcTable;

public class JdbcForeignKeyConstraint extends JdbcTableConstraint {
    private final JdbcTable relatedTable;

    public JdbcForeignKeyConstraint(JdbcTable table, String name, JdbcTable relatedTable) {
        super(table, name);
        this.relatedTable = relatedTable;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getTable().toString());
        stringBuilder.append(" -> ");
        stringBuilder.append(this.relatedTable);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcForeignKeyConstraint)) {
            return false;
        }
        JdbcForeignKeyConstraint foreignKeyConstraint = (JdbcForeignKeyConstraint) object;
        return this.compareTo(foreignKeyConstraint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTable(), this.getName());
    }

    @Override
    public int compareTo(DomainObject tableConstraint) {
        JdbcForeignKeyConstraint foreignKeyConstraint = (JdbcForeignKeyConstraint) tableConstraint;
        return super.compareTo(foreignKeyConstraint);
    }

    public JdbcTable getRelatedTable() {
        return relatedTable;
    }
}
