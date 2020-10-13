package org.geoserver.appschema.smart.metadata.jdbc.constraint;

import java.util.Objects;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcTableMetadata;

/**
 * Class representing metadata for a constraint type foreignkey in a JDBC DataStore.
 *
 * @author Jose Macchi - Geosolutions
 */
public class JdbcForeignKeyConstraintMetadata extends JdbcTableConstraintMetadata {
    private final JdbcTableMetadata relatedTable;

    public JdbcForeignKeyConstraintMetadata(
            JdbcTableMetadata table, String name, JdbcTableMetadata relatedTable) {
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
        if (object == null || !(object instanceof JdbcForeignKeyConstraintMetadata)) {
            return false;
        }
        JdbcForeignKeyConstraintMetadata foreignKeyConstraint =
                (JdbcForeignKeyConstraintMetadata) object;
        return this.compareTo(foreignKeyConstraint) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTable(), this.getName());
    }

    public JdbcTableMetadata getRelatedTable() {
        return relatedTable;
    }
}
