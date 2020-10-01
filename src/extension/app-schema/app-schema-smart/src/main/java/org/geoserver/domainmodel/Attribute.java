package org.geoserver.domainmodel;

abstract public class Attribute extends AbstractDomainObject {
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
