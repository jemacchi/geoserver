package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.domain.DomainModelVisitor;

import java.util.ArrayList;
import java.util.List;

public final class DomainEntity {

    private String name;
    private final List<DomainAttribute> attributes = new ArrayList<>();
    private final List<DomainRelation> relations = new ArrayList<>();

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    List<DomainAttribute> getAttributes() {
        return attributes;
    }

    List<DomainRelation> getRelations() {
        return relations;
    }

    void add(DomainAttribute attribute) {
        attributes.add(attribute);
    }

    void add(DomainRelation relation) {
        relations.add(relation);
    }

    void accept(DomainModelVisitor visitor) {
        attributes.forEach(visitor::visit);
        relations.forEach(visitor::visit);
    }
}
