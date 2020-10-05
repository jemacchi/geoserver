package org.geoserver.appschema.smart.domain.entities;

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
