package org.geoserver.appschema.smart.domain.entities;

/**
 * Class representing a relation between two entities on the Smart AppSchema model.
 *  
 * @author Jose Macchi - Geosolutions
 *
 */
public final class DomainRelation {

    private DomainEntity source;
    private DomainEntity destination;

    public DomainEntity getSource() {
        return source;
    }

    public void setSource(DomainEntity source) {
        this.source = source;
    }

    public DomainEntity getDestination() {
        return destination;
    }

    public void setDestination(DomainEntity destination) {
        this.destination = destination;
    }
}
