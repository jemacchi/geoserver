package org.geoserver.domain.model.jdbc.result;

public class ResultIndex {
    private final String table;
    private final String fields;
    private final String constraintName;

    public ResultIndex(String table, String fields, String constraintName) {
        this.table = table;
        this.fields = fields;
        this.constraintName = constraintName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(table);
        stringBuilder.append("(");
        stringBuilder.append(fields);
        stringBuilder.append(") - ");
        stringBuilder.append(constraintName);
        return stringBuilder.toString();
    }

    public String getTable() {
        return table;
    }

    public String getFields() {
        return fields;
    }

    public String getConstraintName() {
        return constraintName;
    }
}
