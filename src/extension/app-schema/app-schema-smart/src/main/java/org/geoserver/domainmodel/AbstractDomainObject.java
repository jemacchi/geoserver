package org.geoserver.domainmodel;

public abstract class AbstractDomainObject implements Comparable<AbstractDomainObject> {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
