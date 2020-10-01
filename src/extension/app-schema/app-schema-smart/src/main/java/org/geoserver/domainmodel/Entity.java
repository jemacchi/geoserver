package org.geoserver.domainmodel;

import java.util.List;

abstract public class Entity extends AbstractDomainObject {
	
	private List<Attribute> attributes ;

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	

}
