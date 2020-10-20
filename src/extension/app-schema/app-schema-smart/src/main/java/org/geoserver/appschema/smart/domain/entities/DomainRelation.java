package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.domain.DomainModelVisitorImpl;

/**
 * Class representing a relation between two entities on the Smart AppSchema model.
 *
 * @author Jose Macchi - Geosolutions
 */
public final class DomainRelation {

    private DomainEntityAttribute sourceAttribute;
    private DomainEntityAttribute destinationAttribute;

    private DomainRelationType relationType;

    public DomainEntity getSourceEntity() {
        return sourceAttribute.getEntity();
    }

    public DomainEntity getDestinationEntity() {
        return destinationAttribute.getEntity();
    }

    public DomainRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(DomainRelationType relationType) {
        this.relationType = relationType;
    }

    public void accept(DomainModelVisitorImpl visitor) {
        visitor.visit(this.getSourceEntity());
        visitor.visit(this.getDestinationEntity());
    }

    public DomainEntityAttribute getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(DomainEntityAttribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public DomainEntityAttribute getDestinationAttribute() {
        return destinationAttribute;
    }

    public void setDestinationAttribute(DomainEntityAttribute destinationAttribute) {
        this.destinationAttribute = destinationAttribute;
    }
}
