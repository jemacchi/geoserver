package org.geoserver.appschema.smart.domain;

import java.util.HashMap;
import java.util.Map;
import org.geoserver.appschema.smart.domain.entities.DomainEntityAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainAttributeType;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.domain.entities.DomainRelationType;
import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.EntityMetadata;

/**
 * Smart AppSchema model builder. Given a DomainModelConfig object and a DataStoreMetadata it allows
 * to get the Smart AppSchema model.
 *
 * @author Jose Macchi - Geosolutions
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
        // build domainEntity only if it's present in dataStoreMetadata
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
                                DomainEntityAttribute domainAttribute =
                                        buildDomainAttribute(attributeMetadata);
                                domainEntity.add(domainAttribute);
                            });
            entityMetadata
                    .getRelations()
                    .forEach(
                            relationMetadata -> {
                                DomainRelation domainRelation = new DomainRelation();
                                
                                if (relationMetadata.getRelationType().equals(DomainRelationType.ONEMANY)) {
                                    domainRelation.setDestinationAttribute(
                                            buildDomainAttribute(
                                                    relationMetadata.getSourceAttribute()));
                                    domainRelation.setSourceAttribute(
                                            buildDomainAttribute(
                                                    relationMetadata.getDestinationAttribute()));
                                	
                                } else  {
                                    domainRelation.setDestinationAttribute(
                                            buildDomainAttribute(
                                                    relationMetadata.getDestinationAttribute()));
                                    domainRelation.setSourceAttribute(
                                            buildDomainAttribute(
                                                    relationMetadata.getSourceAttribute()));
                                }
                                domainRelation.setRelationType(relationMetadata.getRelationType());
                                domainEntity.add(domainRelation);
                            });

            return domainEntity;
        }
        return null;
    }

    private DomainEntityAttribute buildDomainAttribute(AttributeMetadata attributeMetadata) {
        DomainEntityAttribute domainAttribute = new DomainEntityAttribute();
        domainAttribute.setName(attributeMetadata.getName());
        domainAttribute.setType(DomainAttributeType.TEXT);
        domainAttribute.setEntity(
                this.buildDomainEntity(
                        dataStoreMetadata.getEntityMetadata(
                                attributeMetadata.getEntity().getName())));

        switch (attributeMetadata.getType().toLowerCase()) {
            case "number":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "serial":
                domainAttribute.setType(DomainAttributeType.INT);
                break;
            case "int4":
                domainAttribute.setType(DomainAttributeType.INT);
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
            case "date":
                domainAttribute.setType(DomainAttributeType.DATE);
                break;
            case "timestamp":
                domainAttribute.setType(DomainAttributeType.DATE);
                break;
            case "geometry":
                domainAttribute.setType(DomainAttributeType.GEOMETRY);
                break;
            default:
                throw new RuntimeException(
                        String.format(
                                "Attribute type '%s' is unknown.",
                                attributeMetadata.getType().toLowerCase()));
        }
        return domainAttribute;
    }
}
