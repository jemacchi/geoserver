package org.geoserver.appschema.smart.domain.generator;

import java.util.logging.Logger;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geotools.util.logging.Logging;

public class AppSchemaDomainModelVisitor extends DomainModelVisitor {

    private static final Logger LOGGER = Logging.getLogger(AppSchemaDomainModelVisitor.class);

    @Override
    public void visit(DataStoreMetadata dataStoreMetadata) {}

    @Override
    public void visit(DomainModel domainModel) {}

    @Override
    public void visit(DomainEntity domainEntity) {}

    @Override
    public void visit(DomainAttribute domainAttribute) {}

    @Override
    public void visit(DomainRelation domainRelation) {}
}
