package org.geoserver.appschema.smart.domain.entities;

import java.util.ArrayList;
import java.util.List;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;

/**
 * Class representing an entity on the Smart AppSchema model.
 *
 * @author Jose Macchi - Geosolutions
 */
public final class DomainEntity {

    private String name;
    private final List<DomainAttribute> attributes = new ArrayList<>();
    private final List<DomainRelation> relations = new ArrayList<>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DomainAttribute> getAttributes() {
        return attributes;
    }

    public List<DomainRelation> getRelations() {
        return relations;
    }

    public void add(DomainAttribute attribute) {
        attributes.add(attribute);
    }

    public void add(DomainRelation relation) {
        relations.add(relation);
    }

    public void accept(DomainModelVisitor visitor) {
        attributes.forEach(visitor::visit);
        relations.forEach(visitor::visit);
    }
}
