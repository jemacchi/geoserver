package org.geoserver.domainmodel.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;

public class JdbcPrimaryKey extends JdbcTableConstraint {
    private final List<String> columnNames;
    private final Map<String, Integer> columnOrderMap;

    public JdbcPrimaryKey(JdbcTable table, String constraintName, List<String> columnNames) {
        super(table, constraintName);
        this.columnNames = columnNames;
        this.columnOrderMap = new HashMap<String, Integer>();
        for (int i = 0; i < columnNames.size(); i++) {
            columnOrderMap.put(columnNames.get(i), i);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getTable().toString());
        stringBuilder.append("(");
        for (String columnName : columnNames) {
            stringBuilder.append(columnName);
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcPrimaryKey)) {
            return false;
        }
        JdbcPrimaryKey primaryKey = (JdbcPrimaryKey) object;
        return this.compareTo(primaryKey) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTable(), this.getConstraintName());
    }

    @Override
    public int compareTo(AbstractDomainObject tableConstraint) {
        JdbcPrimaryKey indexConstraint = (JdbcPrimaryKey) tableConstraint;
        return super.compareTo(indexConstraint);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public Map<String, Integer> getColumnOrderMap() {
        return columnOrderMap;
    }
}
