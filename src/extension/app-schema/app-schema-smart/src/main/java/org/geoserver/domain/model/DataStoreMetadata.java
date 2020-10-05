package org.geoserver.domain.model;

import java.util.List;

public interface DataStoreMetadata {

    public List<Entity> getEntities();

    public List<Relation> getRelations();

    public List<Relation> getEntityRelations(Entity entity);

    public void populateModel() throws Exception;
}
