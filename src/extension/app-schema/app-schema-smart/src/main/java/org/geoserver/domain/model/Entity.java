package org.geoserver.domain.model;

import java.util.List;

public abstract class Entity extends DomainObject {

    protected List<Attribute> attributes;

    public Entity(String name) {
        super(name);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
