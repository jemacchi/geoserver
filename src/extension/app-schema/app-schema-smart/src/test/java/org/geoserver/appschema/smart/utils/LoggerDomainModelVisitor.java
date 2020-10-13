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
	
	@Override
	public void visit(DataStoreMetadata dataStoreMetadata) {
		LOGGER.log(Level.INFO, dataStoreMetadata.getDataStoreMetadataConfig().toString());
	}

	@Override
	public void visit(DomainModel domainModel) {
		LOGGER.log(Level.INFO, domainModel.toString());
	}

	@Override
	public void visit(DomainEntity domainEntity) {
		LOGGER.log(Level.INFO, domainEntity.getName());
		domainEntity.accept(this);
	}

	@Override
	public void visit(DomainAttribute domainAttribute) {
		LOGGER.log(Level.INFO, domainAttribute.getName());
	}

	@Override
	public void visit(DomainRelation domainRelation) {
		LOGGER.log(Level.INFO, domainRelation.getSource().getName() + " -> " + domainRelation.getDestination().getName());
	}

}
