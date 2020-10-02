package org.geoserver.domainmodel;

import java.util.List;

abstract public class Entity extends AbstractDomainObject {
	
	protected List<Attribute> attributes ;
	
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
