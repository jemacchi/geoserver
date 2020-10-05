package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;

public final class DomainModel {

    private final DataStoreMetadata dataStoreMetadata;
    private final DomainEntity rootEntity;

    DomainModel(DataStoreMetadata dataStoreMetadata, DomainEntity rootEntity) {
        this.dataStoreMetadata = dataStoreMetadata;
        this.rootEntity = rootEntity;
    }

    void accept(DomainModelVisitor visitor) {
        visitor.visit(dataStoreMetadata);
        visitor.visit(rootEntity);
    }
}
