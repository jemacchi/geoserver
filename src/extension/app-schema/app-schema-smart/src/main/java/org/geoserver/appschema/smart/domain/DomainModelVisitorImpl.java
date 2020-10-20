package org.geoserver.appschema.smart.domain;

import org.geoserver.appschema.smart.domain.entities.DomainEntityAttribute;
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
public class DomainModelVisitorImpl implements DomainModelVisitor {

	@Override
	public void visitDataStoreMetadata(DataStoreMetadata dataStoreMetadata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDomainModel(DomainModel model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDomainEntity(DomainEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDomainEntityAttribute(DomainEntityAttribute attribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDomainRelation(DomainRelation relation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDomainRootEntity(DomainEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitRelationSourceEntity(DomainEntity source, DomainEntity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitRelationTargetEntity(DomainEntity source, DomainEntity target) {
		// TODO Auto-generated method stub
		
	}

	

}
