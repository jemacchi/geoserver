package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.domain.DomainModelVisitor;

/**
 * Class representing a relation between two entities on the Smart AppSchema model.
 *  
 * @author Jose Macchi - Geosolutions
 *
 */
public final class DomainRelation {

    private DomainEntity source;
    private DomainEntity destination;
    private DomainRelationType relationType;

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

	public DomainRelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(DomainRelationType relationType) {
		this.relationType = relationType;
	}
	
    public void accept(DomainModelVisitor visitor) {
    	visitor.visit(source);
    	visitor.visit(destination);
    }

}
