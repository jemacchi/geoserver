package org.geoserver.domainmodel;

abstract public class Relation extends AbstractDomainObject {

	public enum Cardinality {
	    ZEROONE,
	    ONEONE,
	    MULTIPLEONE
	}

	public Relation(String name) {
		super(name);
	}
	
	protected Attribute sourceAttribute;
	protected Attribute destinationAttribute;
	
	private Cardinality cardinality;
	
	public boolean participatesIn(Entity e) {
		if ((sourceAttribute.getEntity().compareTo(e) == 0) || (destinationAttribute.getEntity().compareTo(e) == 0)) 
			return true;
		return false;
	}
	
	public Attribute getSourceAttribute() {
		return this.sourceAttribute;
	}

	public Attribute getDestinationAttribute() {
		return this.destinationAttribute;
	}
	
	public Cardinality getCardinality( ) {
		return this.cardinality;
	}

}
