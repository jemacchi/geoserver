package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;

/**
 * This class contains the information about model (entities, attributes and relations) that will be
 * used to create the output representations in AppSchema Smart. It's defined by a DataStoreMetadata
 * (source of objects to map) and the rootEntity of the appschema model that will be done.
 *
 * @author Jose Macchi - Geosolutions
 */
public class DomainModel {

    private final DataStoreMetadata dataStoreMetadata;
    private final DomainEntity rootEntity;
    
    public DomainModel(DataStoreMetadata dataStoreMetadata, DomainEntity rootEntity) {
        this.dataStoreMetadata = dataStoreMetadata;
        this.rootEntity = rootEntity;
    }

    public void accept(DomainModelVisitor visitor) {
        // visit me
        visitor.visit(this);
        // visit my childs
        visitor.visit(dataStoreMetadata);
        visitor.visit(rootEntity);
    }
    
    public DomainEntity getRootEntity() {
    	return rootEntity;
    }
    
}
