package org.geoserver.domainmodel.jdbc;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.Entity;

public class JdbcTable extends Entity {
    private final String catalog;
    private final String schema;
    private final String tableName;

    public JdbcTable(String catalog, String schema, String tableName) {
        this.catalog = catalog;
        this.schema = schema;
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(catalog)) {
            stringBuilder.append(catalog);
            stringBuilder.append(".");
        }
        if (!Strings.isNullOrEmpty(schema)) {
            stringBuilder.append(schema);
            stringBuilder.append(".");
        }
        stringBuilder.append(tableName);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof JdbcTable)) {
            return false;
        }
        JdbcTable table = (JdbcTable) object;
        return this.compareTo(table) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.catalog, this.schema, this.tableName);
    }

    @Override
    public int compareTo(AbstractDomainObject table) {
        if (table != null) {
        	JdbcTable t = (JdbcTable) table;
        	if (this.catalog != null && t.getCatalog() != null) {
	            return ComparisonChain.start()
	                    .compare(this.catalog, t.getCatalog())
	                    .compare(this.schema, t.getSchema())
	                    .compare(this.tableName, t.getTableName())
	                    .result();
        	} else if (this.catalog == null && t.getCatalog() == null) {
        		return ComparisonChain.start()
	                    .compare(this.schema, t.getSchema())
	                    .compare(this.tableName, t.getTableName())
	                    .result();
        	}
        }
        return 1;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }
}
