package org.geoserver.domainmodel;

import java.util.List;

public interface DomainMetadataStore {
	
	public List<Entity> getEntities();
	
	public List<Relation> getRelations();
	
	public List<Relation> getEntityRelations(Entity entity);
	
	public List<Attribute> getAttributes();
	
	public void populateModel() throws Exception;

}
