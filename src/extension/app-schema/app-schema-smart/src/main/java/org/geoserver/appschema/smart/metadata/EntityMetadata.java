package org.geoserver.appschema.smart.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents metadata for entities on the underlying DataStore model.
 *
 * @author Jose Macchi - GeoSolutions
 */
public abstract class EntityMetadata implements Comparable<EntityMetadata> {

    protected String name;

    protected final List<AttributeMetadata> attributes = new ArrayList<>();
    protected final List<RelationMetadata> relations = new ArrayList<>();

    public EntityMetadata(String name) {
        this.name = name;
    }

    public void addAttribute(AttributeMetadata attribute) {
        if (!this.attributes.contains(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public void addRelation(RelationMetadata relation) {
        if (!this.relations.contains(relation)) {
            relations.add(relation);
        }
    }

    public String getName() {
        return name;
    }

    public abstract List<AttributeMetadata> getAttributes();

    public abstract List<RelationMetadata> getRelations();
}
