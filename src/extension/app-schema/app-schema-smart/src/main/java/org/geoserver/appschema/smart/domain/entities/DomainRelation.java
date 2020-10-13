package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.domain.DomainModelVisitor;

/**
 * Class representing a relation between two entities on the Smart AppSchema model.
 *
 * @author Jose Macchi - Geosolutions
 */
public final class DomainRelation {

    private DomainAttribute sourceAttribute;
    private DomainAttribute destinationAttribute;

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

    public void accept(DomainModelVisitor visitor) {
        visitor.visit(this.getSourceEntity());
        visitor.visit(this.getDestinationEntity());
    }

    public DomainAttribute getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(DomainAttribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public DomainAttribute getDestinationAttribute() {
        return destinationAttribute;
    }

    public void setDestinationAttribute(DomainAttribute destinationAttribute) {
        this.destinationAttribute = destinationAttribute;
    }
}
