package org.geoserver.domain.model.jdbc;

import com.google.common.collect.ComparisonChain;
import java.util.Objects;
import org.geoserver.domain.model.DomainObject;

public class JdbcForeignKeyColumn extends JdbcColumn {
    private final JdbcColumn relatedColumn;

    public JdbcForeignKeyColumn(
            JdbcTable table, String columnName, String columnType, JdbcColumn relatedColumn) {
        super(table, columnName, columnType);
        this.relatedColumn = relatedColumn;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcForeignKeyColumn)) {
            return false;
        }
        JdbcForeignKeyColumn foreignKeyColumns = (JdbcForeignKeyColumn) object;
        return this.compareTo(foreignKeyColumns) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEntity(), this.getName(), this.getRelatedColumn());
    }

    @Override
    public int compareTo(DomainObject foreignKeyColumn) {
        JdbcForeignKeyColumn jdbcRelation = (JdbcForeignKeyColumn) foreignKeyColumn;
        if (foreignKeyColumn != null) {
            return ComparisonChain.start()
                    .compare(this.getName(), foreignKeyColumn.getName())
                    .compare(this.relatedColumn, jdbcRelation.getRelatedColumn())
                    .result();
        }
        return 1;
    }

    public JdbcColumn getRelatedColumn() {
        return this.relatedColumn;
    }
}
