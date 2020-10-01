package org.geoserver.domainmodel;

public abstract class AbstractDomainObject implements Comparable<AbstractDomainObject> {
	
	private final String name;
	
	public AbstractDomainObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
