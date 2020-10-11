package org.geoserver.appschema.smart.metadata;

import org.geoserver.appschema.smart.domain.entities.DomainRelationType;

import com.google.common.collect.ComparisonChain;

/**
 * Class that represents metadata for relations between entities on the underlying DataStore model.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public abstract class RelationMetadata implements Comparable<RelationMetadata> {

    protected AttributeMetadata sourceAttribute;
    protected AttributeMetadata destinationAttribute;
    protected DomainRelationType type;
    
    public RelationMetadata(DomainRelationType type, AttributeMetadata source, AttributeMetadata destination) {
        this.type = type;
        this.sourceAttribute = source;
        this.destinationAttribute = destination;
    }

    public boolean participatesIn(EntityMetadata e) {
        if ((sourceAttribute.getEntity().compareTo(e) == 0)
                || (destinationAttribute.getEntity().compareTo(e) == 0)) return true;
        return false;
    }

    public AttributeMetadata getSourceAttribute() {
        return this.sourceAttribute;
    }

    public AttributeMetadata getDestinationAttribute() {
        return this.destinationAttribute;
    }

    public DomainRelationType getRelationType() {
        return this.type;
    }
    
    @Override
    public int compareTo(RelationMetadata relation) {
    	if (relation != null) {
           return ComparisonChain.start().compare(this.getSourceAttribute(), relation.getSourceAttribute())
	           .compare(this.getDestinationAttribute(), relation.getDestinationAttribute())
	           .compare(this.getRelationType(), relation.getRelationType())
	           .result();
        }
        return 1;
    }
}
