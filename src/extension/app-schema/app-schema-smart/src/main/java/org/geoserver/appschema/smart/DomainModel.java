package org.geoserver.appschema.smart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geoserver.domainmodel.Attribute;
import org.geoserver.domainmodel.DomainMetadataStore;
import org.geoserver.domainmodel.Entity;
import org.geoserver.domainmodel.Relation;

public class DomainModel implements DomainMetadataStore {
	
	private List<Entity> entities;
	private List<Relation> relations;
	
	@Override
	public List<Entity> getEntities() {
		return entities;
	}

	@Override
	public List<Relation> getRelations(Entity entity) {
		List<Relation> output = new ArrayList<Relation>();
		Iterator<Relation> ir = this.relations.iterator();
		while (ir.hasNext()) {
			Relation relation = ir.next();
			if (relation.belongsToRelation(entity)) {
				output.add(relation);
			};
		}
		return output;
	}

	@Override
	public List<Attribute> getAttributes(Entity entity) {
		return entity.getAttributes();
	}

}
