package org.geoserver.appschema.smart.domain;

import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;

/**
 * Smart AppSchema model objects visitor interface. Defined with the purpose of accessing elements
 * on model and visiting them in order to build output structure data.
 *
 * @author Jose Macchi - Geosolutions
 */
public abstract class DomainModelVisitor {

    public abstract void visit(DataStoreMetadata dataStoreMetadata);

    public abstract void visit(DomainModel domainModel);

    public abstract void visit(DomainEntity domainEntity);

    public abstract void visit(DomainAttribute domainAttribute);

    public abstract void visit(DomainRelation domainRelation);
}
