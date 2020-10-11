package org.geoserver.appschema.smart.domain.entities;

/**
 * Class representing an attribute of an entity on the Smart AppSchema model.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
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
