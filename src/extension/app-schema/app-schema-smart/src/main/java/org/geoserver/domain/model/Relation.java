package org.geoserver.domain.model;

public abstract class Relation extends DomainObject {

    public enum Cardinality {
        ZEROONE,
        ONEONE,
        MULTIPLEONE
    }

    public Relation(String name, Cardinality cardinality) {
        super(name);
        this.cardinality = cardinality;
    }

    protected Attribute sourceAttribute;
    protected Attribute destinationAttribute;

    protected Cardinality cardinality;

    public boolean participatesIn(Entity e) {
        if ((sourceAttribute.getEntity().compareTo(e) == 0)
                || (destinationAttribute.getEntity().compareTo(e) == 0)) return true;
        return false;
    }

    public Attribute getSourceAttribute() {
        return this.sourceAttribute;
    }

    public Attribute getDestinationAttribute() {
        return this.destinationAttribute;
    }

    public Cardinality getCardinality() {
        return this.cardinality;
    }
}
