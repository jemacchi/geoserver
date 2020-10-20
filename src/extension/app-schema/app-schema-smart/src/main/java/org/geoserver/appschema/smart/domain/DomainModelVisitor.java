package org.geoserver.appschema.smart.domain;

import org.geoserver.appschema.smart.domain.entities.DomainEntityAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;

public interface DomainModelVisitor {
	
	void visitDataStoreMetadata(DataStoreMetadata dataStoreMetadata);

    void visitDomainModel(DomainModel model);

    void visitDomainEntity(DomainEntity entity);

    void visitDomainEntityAttribute(DomainEntityAttribute attribute);

    void visitDomainRelation(DomainRelation relation);
    
    void visitDomainRootEntity(DomainEntity entity);
    
    void visitRelationSourceEntity(DomainEntity source, DomainEntity target);
    
    void visitRelationTargetEntity(DomainEntity source, DomainEntity target);

}
