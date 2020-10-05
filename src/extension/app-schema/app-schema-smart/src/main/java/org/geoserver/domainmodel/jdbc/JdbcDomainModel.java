package org.geoserver.domainmodel.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.geoserver.domainmodel.DomainModel;
import org.geoserver.domainmodel.Entity;
import org.geoserver.domainmodel.Relation;
import org.geoserver.domainmodel.Relation.Cardinality;
import org.geoserver.domainmodel.jdbc.constraint.JdbcForeignKeyConstraint;

public class JdbcDomainModel extends DomainModel {
	
	private JdbcDomainModelParameters parameters;

	public JdbcDomainModel(JdbcDomainModelParameters parameters) {
		super();
		this.parameters = parameters;
	}	
	
	@Override
	public void populateModel() throws Exception {
		entities = new ArrayList<Entity>();
		List<JdbcTable> tableList = JdbcUtilities.getInstance().getSchemaTables(parameters.getConnection().getMetaData(), parameters.getSchema());
		entities.addAll(tableList);
		relations = new ArrayList<Relation>();
		Iterator<JdbcTable> iTables = tableList.iterator();
		while (iTables.hasNext()) {
			JdbcTable jTable = iTables.next();
			SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap = JdbcUtilities.getInstance().getForeignKeysByTable(parameters.getConnection().getMetaData(), jTable);
			if (fkMap != null) {
				Iterator<JdbcForeignKeyConstraint> iFkConstraint = fkMap.keySet().iterator();
				while (iFkConstraint.hasNext()) {
					JdbcForeignKeyConstraint key = iFkConstraint.next();
					Collection<JdbcForeignKeyColumn> fkColumns = fkMap.get(key);
					Iterator<JdbcForeignKeyColumn> iFkColumns = fkColumns.iterator();
					while (iFkColumns.hasNext()) {
						JdbcForeignKeyColumn aFkColumn = iFkColumns.next();
						Cardinality card = JdbcUtilities.getInstance().getCardinality(jTable, key);
						JdbcRelation relation = new JdbcRelation(key.getName(),card, aFkColumn);
						relations.add(relation);	
					}
				}
			}
		}
	}

	public JdbcDomainModelParameters getParameters() {
		return parameters;
	}
}
