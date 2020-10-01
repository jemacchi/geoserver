package org.geoserver.domainmodel.jdbc.utils;

import java.util.List;

import org.geoserver.domainmodel.jdbc.JdbcTable;

public class QueryData {
    private final JdbcTable table;
    private final String[] columnNames;
    private final List<String[]> valueList;

    public QueryData(JdbcTable table, String[] columnNames, List<String[]> valueList) {
        this.table = table;
        this.columnNames = columnNames;
        this.valueList = valueList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getTable().toString());
        stringBuilder.append("(");
        if (this.columnNames != null) {
            for (String columnName : columnNames) {
                stringBuilder.append(columnName);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append(")");
            for (String[] values : valueList) {
                stringBuilder.append("[");
                for (String value : values) {
                    stringBuilder.append(value);
                    stringBuilder.append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }

    public JdbcTable getTable() {
        return table;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<String[]> getValueList() {
        return valueList;
    }
}
