package org.geoserver.domainmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class DomainModel implements DomainMetadataStore {
	
	protected List<Entity> entities;
	protected List<Relation> relations;
	
	public DomainModel() {
		this.entities = new ArrayList<Entity>();
		this.relations = new ArrayList<Relation>();
	}
	
	@Override
	public List<Entity> getEntities() {
		return entities;
	}

	@Override
	public List<Relation> getEntityRelations(Entity entity) {
		List<Relation> output = new ArrayList<Relation>();
		Iterator<Relation> ir = this.relations.iterator();
		while (ir.hasNext()) {
			Relation relation = ir.next();
			if (relation.participatesIn(entity)) {
				output.add(relation);
			};
		}
		return output;
	}

	@Override
	public List<Relation> getRelations() {
		return this.relations;
	}

	@Override
	public List<Attribute> getAttributes() {
		List<Attribute> output = new ArrayList<Attribute>();
		Iterator<Entity> ie = this.entities.iterator();
		while (ie.hasNext()) {
			Entity entity = ie.next();
			output.addAll(entity.getAttributes());
		}
		return output;
	}

}
