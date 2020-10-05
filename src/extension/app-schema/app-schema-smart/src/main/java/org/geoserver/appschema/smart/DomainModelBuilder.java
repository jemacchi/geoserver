package org.geoserver.appschema.smart;

import java.util.Iterator;
import java.util.List;
import org.geoserver.domain.model.Attribute;
import org.geoserver.domain.model.DataStoreMetadata;
import org.geoserver.domain.model.Entity;
import org.geoserver.domain.model.Relation;

public class DomainModelBuilder {

    private DataStoreMetadata dataStoreMetadata;

    public DomainModelBuilder(DataStoreMetadata ds) {
        this.dataStoreMetadata = ds;
    }

    public DataStoreMetadata getDataStoreMetadata() {
        return dataStoreMetadata;
    }

    public Entity getEntity(String name) {
        Iterator<Entity> iEntities = dataStoreMetadata.getEntities().iterator();
        while (iEntities.hasNext()) {
            Entity e = iEntities.next();
            if (e.getName().equals(name)) return e;
        }
        return null;
    }

    public Relation getRelation(String name) {
        Iterator<Relation> iRelations = dataStoreMetadata.getRelations().iterator();
        while (iRelations.hasNext()) {
            Relation r = iRelations.next();
            if (r.getName().equals(name)) return r;
        }
        return null;
    }

    public List<Attribute> getAttributes(String entityName) {
        Entity e = this.getEntity(entityName);
        return e.getAttributes();
    }
}
