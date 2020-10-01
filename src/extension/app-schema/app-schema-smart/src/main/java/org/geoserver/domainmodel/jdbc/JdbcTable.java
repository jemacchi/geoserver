package org.geoserver.domainmodel.jdbc;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import java.util.Objects;

import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.Entity;

public class JdbcTable extends Entity {
    private final String catalog;
    private final String schema;

    public JdbcTable(String catalog, String schema, String name) {
    	super(name);
    	this.catalog = catalog;
        this.schema = schema;
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
        stringBuilder.append(this.getName());
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
        return Objects.hash(this.catalog, this.schema, this.getName());
    }

    @Override
    public int compareTo(AbstractDomainObject table) {
        if (table != null) {
        	JdbcTable t = (JdbcTable) table;
        	if (this.catalog != null && t.getCatalog() != null) {
	            return ComparisonChain.start()
	                    .compare(this.catalog, t.getCatalog())
	                    .compare(this.schema, t.getSchema())
	                    .compare(this.getName(), t.getName())
	                    .result();
        	} else if (this.catalog == null && t.getCatalog() == null) {
        		return ComparisonChain.start()
	                    .compare(this.schema, t.getSchema())
	                    .compare(this.getName(), t.getName())
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

}
