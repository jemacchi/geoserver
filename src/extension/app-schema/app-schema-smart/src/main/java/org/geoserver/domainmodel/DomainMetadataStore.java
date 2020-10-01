package org.geoserver.domainmodel;

import java.util.List;

public interface DomainMetadataStore {
	
	public List<Entity> getEntities();
	
	public List<Relation> getRelations(Entity entity);
	
	public List<Attribute> getAttributes(Entity entity);

}
