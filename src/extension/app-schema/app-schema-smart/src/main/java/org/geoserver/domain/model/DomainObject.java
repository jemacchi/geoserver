package org.geoserver.domain.model;

public abstract class DomainObject implements Comparable<DomainObject> {

    private final String name;

    public DomainObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
