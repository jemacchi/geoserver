package org.geoserver.domainmodel;

abstract public class Attribute extends AbstractDomainObject {
	
	private Entity entity;
	private String type;

	public Attribute(Entity entity, String name, String type) {
		super(name);
		this.entity = entity;
		this.setType(type);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Entity getEntity() {
		return entity;
	}

}
