package org.geoserver.appschema.smart.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geotools.util.logging.Logging;

public class LoggerDomainModelVisitor extends DomainModelVisitor {

    private static final Logger LOGGER = Logging.getLogger(LoggerDomainModelVisitor.class);
    private StringBuilder internalLogger = new StringBuilder();

    @Override
    public void visit(DataStoreMetadata dataStoreMetadata) {
        String ds = dataStoreMetadata.getDataStoreMetadataConfig().toString();
        LOGGER.log(Level.INFO, ds);
        internalLogger.append(ds + "\n");
    }

    @Override
    public void visit(DomainModel domainModel) {
        String dm = domainModel.getClass().getName();
        LOGGER.log(Level.INFO, dm);
        internalLogger.append(dm + "\n");
    }

    @Override
    public void visit(DomainEntity domainEntity) {
        String de = domainEntity.getName();
        LOGGER.log(Level.INFO, de);
        internalLogger.append(de + "\n");
        domainEntity.accept(this);
    }

    @Override
    public void visit(DomainAttribute domainAttribute) {
        String da = domainAttribute.getName();
        LOGGER.log(Level.INFO, domainAttribute.getName());
        internalLogger.append(da + "\n");
    }

    @Override
    public void visit(DomainRelation domainRelation) {
        String dr =
                domainRelation.getSourceEntity().getName()
                        + " -> "
                        + domainRelation.getDestinationEntity().getName();
        LOGGER.log(Level.INFO, dr);
        internalLogger.append(dr + "\n");
    }

    public String getLog() {
        return internalLogger.toString();
    }
}
