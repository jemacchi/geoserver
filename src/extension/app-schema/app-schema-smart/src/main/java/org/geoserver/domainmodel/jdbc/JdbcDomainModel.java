package org.geoserver.domainmodel.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.geoserver.domainmodel.DomainModel;
import org.geoserver.domainmodel.Entity;
import org.geoserver.domainmodel.Relation;
import org.geoserver.domainmodel.jdbc.constraint.JdbcForeignKeyConstraint;

public class JdbcDomainModel extends DomainModel {
	
	private Connection connection;
	private String catalog;
	private String schema;

	public JdbcDomainModel(Connection connection, String catalog, String schema) {
		super();
		this.connection = connection;
		this.catalog = catalog;
		this.schema = schema;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	@Override
	public void populateModel() throws Exception {
		entities = new ArrayList<Entity>();
		List<JdbcTable> tableList = JdbcUtilities.getInstance().getSchemaTables(connection.getMetaData(), schema);
		entities.addAll(tableList);
		relations = new ArrayList<Relation>();
		Iterator<JdbcTable> iTables = tableList.iterator();
		while (iTables.hasNext()) {
			JdbcTable jTable = iTables.next();
			SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap = JdbcUtilities.getInstance().getForeignKeysByTable(connection.getMetaData(), jTable);
			if (fkMap != null) {
				Iterator<JdbcForeignKeyConstraint> iFkConstraint = fkMap.keySet().iterator();
				while (iFkConstraint.hasNext()) {
					JdbcForeignKeyConstraint key = iFkConstraint.next();
					Collection<JdbcForeignKeyColumn> fkColumns = fkMap.get(key);
					Iterator<JdbcForeignKeyColumn> iFkColumns = fkColumns.iterator();
					while (iFkColumns.hasNext()) {
						JdbcForeignKeyColumn aFkColumn = iFkColumns.next();
						JdbcRelation relation = new JdbcRelation(key.getName(),  aFkColumn);
						relations.add(relation);	
					}
				}
			}
		}
	}
}
