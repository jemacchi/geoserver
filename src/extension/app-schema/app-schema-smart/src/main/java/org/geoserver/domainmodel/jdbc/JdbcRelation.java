package org.geoserver.domainmodel.jdbc;

import java.sql.Connection;

import org.geoserver.domainmodel.AbstractDomainObject;
import org.geoserver.domainmodel.Attribute;
import org.geoserver.domainmodel.Relation;

public class JdbcRelation extends Relation implements JdbcConnectable {

	private final JdbcForeignKeyColumn jfkc;
	
	public JdbcRelation(String name, Cardinality cardinality, JdbcForeignKeyColumn fkColumn) {
		super(name, cardinality);
		this.jfkc = fkColumn;
	}

	@Override
	public int compareTo(AbstractDomainObject o) {
		return jfkc.compareTo(o);
	}

	@Override
	public Connection getConnection() {
		return this.jfkc.getConnection();
	}
	
	@Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getName());
        stringBuilder.append(" - ");
        stringBuilder.append(this.getSourceAttribute());
        stringBuilder.append(" <-");
        stringBuilder.append(this.getCardinality());
        stringBuilder.append("-> ");
        stringBuilder.append(this.getDestinationAttribute());
        return stringBuilder.toString();
    }
	
	@Override
	public Attribute getSourceAttribute() {
		return this.jfkc;
	}

	@Override
	public Attribute getDestinationAttribute() {
		return this.jfkc.getRelatedColumn();
	}

	@Override
	public Cardinality getCardinality( ) {
		return this.cardinality;
	}
}
