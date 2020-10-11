package org.geoserver.appschema.smart.metadata;

import com.google.common.collect.ComparisonChain;

/**
 * Class that represents metadata for entities' attributes on the underlying DataStore model.
 *  
 * @author Jose Macchi - Geosolutions
 *
 */
public abstract class AttributeMetadata implements Comparable<AttributeMetadata> {

	protected String name;
	protected EntityMetadata entity;
	protected String type;

    public AttributeMetadata(EntityMetadata entity, String name, String type) {
    	this.name = name;
        this.entity = entity;
        this.setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EntityMetadata getEntity() {
        return entity;
    }
    
    public String getName() {
    	return name;
    }
    
    @Override
    public int compareTo(AttributeMetadata attributeMetadata) {
        if (attributeMetadata != null) {
            return ComparisonChain.start()
                    .compare(this.getEntity(), attributeMetadata.getEntity())
                    .compare(this.name, attributeMetadata.getName())
                    .result();
        }
        return 1;
    }

}
