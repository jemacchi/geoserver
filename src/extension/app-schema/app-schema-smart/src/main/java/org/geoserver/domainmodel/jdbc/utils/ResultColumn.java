package org.geoserver.domainmodel.jdbc.utils;

import org.geoserver.domainmodel.jdbc.JdbcColumn;

public class ResultColumn {
    private final JdbcColumn jdbcColumn;

    public ResultColumn(JdbcColumn aColumn) {
        this.jdbcColumn = aColumn;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(jdbcColumn.getEntity().getName());
        stringBuilder.append(" - ");
        stringBuilder.append(jdbcColumn.getName());
        stringBuilder.append(" (");
        stringBuilder.append(jdbcColumn.getType());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

}
