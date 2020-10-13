package org.geoserver.appschema.smart.domain;

import java.util.HashMap;
import java.util.Map;

import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainAttributeType;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.EntityMetadata;

/**
 * Smart AppSchema model builder.
 * Given a DomainModelConfig object and a DataStoreMetadata it allows to get the Smart AppSchema model.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public final class DomainModelBuilder {

    private final DataStoreMetadata dataStoreMetadata;
    private final DomainModelConfig domainModelConfig;

    private final Map<String, DomainEntity> domainEntitiesIndex = new HashMap<>();

    public DomainModelBuilder(
            DataStoreMetadata dataStoreMetadata, DomainModelConfig domainModelConfig) {
        this.dataStoreMetadata = dataStoreMetadata;
        this.domainModelConfig = domainModelConfig;
    }

    public DomainModel getDomainModel() throws Exception {
        EntityMetadata rootEntityMetadata =
                dataStoreMetadata.getEntityMetadata(domainModelConfig.getRootEntityName());
        DomainEntity rootEntity = this.buildDomainEntity(rootEntityMetadata);
        if (rootEntity != null) {
        	DomainModel dm = new DomainModel(this.dataStoreMetadata, rootEntity);
            return dm;	
        } else {
        	throw new Exception("Root entity name does not exists!");
        }
        
    }

    private DomainEntity buildDomainEntity(EntityMetadata entityMetadata) {
    	// Build domainEntity only if it's present in dataStoreMetadata
    	if (this.dataStoreMetadata.getDataStoreEntities().contains(entityMetadata)) {
 
	        DomainEntity candidateDomainEntity = domainEntitiesIndex.get(entityMetadata.getName());
	        if (candidateDomainEntity != null) {
	            // we are done, we already build or are building this entity
	            return candidateDomainEntity;
	        }
	        DomainEntity domainEntity = new DomainEntity();
	        domainEntity.setName(entityMetadata.getName());
	        domainEntitiesIndex.put(domainEntity.getName(), domainEntity);
	        entityMetadata
	                .getAttributes()
	                .forEach(
	                        attributeMetadata -> {
	                            DomainAttribute domainAttribute =
	                                    buildDomainAttribute(attributeMetadata);
	                            domainEntity.add(domainAttribute);
	                        });
	        entityMetadata
	                .getRelations()
	                .forEach(
	                        relationMetadata -> {
	                        	/*AttributeMetadata sourceAttributeMetadata = relationMetadata.getSourceAttribute();
	                            DomainAttribute sourceDomainAttribute =
	                                    buildDomainAttribute(sourceAttributeMetadata);
	                        	*/
	                            EntityMetadata destinationEntityMetadata = relationMetadata.getDestinationAttribute().getEntity();
	                            DomainEntity destinationDomainEntity =
	                                    buildDomainEntity(destinationEntityMetadata);
	                            
	                            DomainRelation domainRelation = new DomainRelation();
	                            domainRelation.setSource(domainEntity);
	                            domainRelation.setDestination(destinationDomainEntity);
	                            //domainRelation.setSource(sourceDomainAttribute);
	                            //domainRelation.setDestination(destinationDomainAttribute);
	                            domainRelation.setRelationType(relationMetadata.getRelationType());
	                            domainEntity.add(domainRelation);
	                            
	                        });
	        
	        return domainEntity;
    	}
    	return null;
    }

    private DomainAttribute buildDomainAttribute(AttributeMetadata attributeMetadata) {
        // TODO: validate the attribute metadata
        DomainAttribute domainAttribute = new DomainAttribute();
        domainAttribute.setName(attributeMetadata.getName());
        domainAttribute.setType(DomainAttributeType.TEXT);
        domainAttribute.setEntity(this.buildDomainEntity(attributeMetadata.getEntity()));
        
        switch (attributeMetadata.getType().toLowerCase()) {
            case "number":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "serial":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "int4":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "float8":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "text":
                domainAttribute.setType(DomainAttributeType.TEXT);
                break;
            case "varchar":
                domainAttribute.setType(DomainAttributeType.TEXT);
                break;
            case "time":
                domainAttribute.setType(DomainAttributeType.DATE);
                break;
            case "timestamp":
                domainAttribute.setType(DomainAttributeType.DATE);
                break;
            case "geometry":
                domainAttribute.setType(DomainAttributeType.GEOMETRY);
                break;
            default:
            	domainAttribute.setType(DomainAttributeType.TEXT);
                /*throw new RuntimeException(
                        String.format(
                                "Attribute type '%s' is unknown.",
                                attributeMetadata.getType().toLowerCase()));*/
        }
        return domainAttribute;
    }
}
