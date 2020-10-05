package org.geoserver.appschema.smart.metadata;

import java.util.ArrayList;
import java.util.List;

public final class EntityMetadata {

    private String name;
    private final List<AttributeMetadata> attributes = new ArrayList<>();
    private final List<RelationMetadata> relations = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AttributeMetadata> getAttributes() {
        return attributes;
    }

    public void addAttribute(AttributeMetadata attribute) {
        attributes.add(attribute);
    }

    public List<RelationMetadata> getRelations() {
        return relations;
    }

    public void addRelation(RelationMetadata relation) {
        relations.add(relation);
    }
}
