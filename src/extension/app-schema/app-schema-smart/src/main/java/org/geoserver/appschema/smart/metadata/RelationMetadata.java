package org.geoserver.appschema.smart.metadata;

public final class RelationMetadata {

    private String sourceAttribute;
    private String destinationAttribute;
    private String destinationEntity;

    public String getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(String sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public String getDestinationAttribute() {
        return destinationAttribute;
    }

    public void setDestinationAttribute(String destinationAttribute) {
        this.destinationAttribute = destinationAttribute;
    }

    public String getDestinationEntity() {
        return destinationEntity;
    }

    public void setDestinationEntity(String destinationEntity) {
        this.destinationEntity = destinationEntity;
    }
}
