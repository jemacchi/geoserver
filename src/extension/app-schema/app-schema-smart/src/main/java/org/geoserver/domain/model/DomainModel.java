package org.geoserver.domain.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class DomainModel implements DataStoreMetadata {

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
            }
            ;
        }
        return output;
    }

    @Override
    public List<Relation> getRelations() {
        return this.relations;
    }
}
