package org.geoserver.domainmodel;

abstract public class Relation extends AbstractDomainObject {

	public enum Cardinality {
	    ZEROONE,
	    ONE,
	    MULTIPLE
	}
	
	private Entity sourceEntity;
	private Entity destinationEntity;
	
	private Attribute sourceAttribute;
	private Attribute destinationAttribute;
	
	private Cardinality sourceCardinality;
	private Cardinality destinationCardinality;
	
	public boolean belongsToRelation(Entity e) {
		if ((sourceEntity.compareTo(e) == 0) || (destinationEntity.compareTo(e) == 0)) 
			return true;
		return false;
	}
	
}
