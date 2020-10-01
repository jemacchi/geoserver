package org.geoserver.domainmodel.jdbc;

import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;

public class JdbcForeignKeyConstraint extends JdbcTableConstraint {
    private final JdbcTable relatedTable;

    public JdbcForeignKeyConstraint(JdbcTable table, String constraintName, JdbcTable relatedTable) {
        super(table, constraintName);
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
        return Objects.hash(this.getTable(), this.getConstraintName());
    }

    @Override
    public int compareTo(AbstractDomainObject tableConstraint) {
        JdbcForeignKeyConstraint foreignKeyConstraint = (JdbcForeignKeyConstraint) tableConstraint;
        return super.compareTo(foreignKeyConstraint);
    }

    public JdbcTable getRelatedTable() {
        return relatedTable;
    }
}
