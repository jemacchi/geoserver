package org.geoserver.appschema.smart.domain.entities;

public final class DomainAttribute {

    private String name;
    private DomainAttributeType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DomainAttributeType getType() {
        return type;
    }

    public void setType(DomainAttributeType type) {
        this.type = type;
    }
}
