package org.geoserver.appschema.smart.metadata.jdbc;

import java.util.Objects;

import org.geoserver.appschema.smart.metadata.AttributeMetadata;

import com.google.common.collect.ComparisonChain;

/**
 * Class representing metadata for ForeignKeys in a JDBC DataStore.
 * ForeignKeys columns are a particular kind of Column, keeping a mapping with another related column (at same
 * entity or other)
 *  
 * @author Jose Macchi - Geosolutions
 *
 */
public class JdbcForeignKeyColumnMetadata extends JdbcColumnMetadata {
    private final JdbcColumnMetadata relatedColumn;

    public JdbcForeignKeyColumnMetadata(
            JdbcTableMetadata table, String columnName, String columnType, JdbcColumnMetadata relatedColumn) {
        super(table, columnName, columnType);
        this.relatedColumn = relatedColumn;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcForeignKeyColumnMetadata)) {
            return false;
        }
        JdbcForeignKeyColumnMetadata foreignKeyColumns = (JdbcForeignKeyColumnMetadata) object;
        return this.compareTo(foreignKeyColumns) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEntity(), this.name, this.getRelatedColumn());
    }

    @Override
    public int compareTo(AttributeMetadata foreignKeyColumn) {
    	JdbcForeignKeyColumnMetadata jfk = (JdbcForeignKeyColumnMetadata) foreignKeyColumn;
        if (foreignKeyColumn != null) {
            return ComparisonChain.start()
                    .compare(this.name, jfk.getName())
                    .compare(this.relatedColumn, jfk.getRelatedColumn())
                    .result();
        }
        return 1;
    }

    public JdbcColumnMetadata getRelatedColumn() {
        return this.relatedColumn;
    }
}
