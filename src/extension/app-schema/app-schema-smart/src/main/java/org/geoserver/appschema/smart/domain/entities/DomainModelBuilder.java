package org.geoserver.appschema.smart.domain.entities;

import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.domain.DomainModelConfig;
import org.geoserver.appschema.smart.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public final class DomainModelBuilder {

    private final DataStoreMetadata dataStoreMetadata;
    private final DomainModelConfig domainModelConfig;

    private final Map<String, DomainEntity> domainEntitiesIndex = new HashMap<>();

    public DomainModelBuilder(
            DataStoreMetadata dataStoreMetadata, DomainModelConfig domainModelConfig) {
        this.dataStoreMetadata = dataStoreMetadata;
        this.domainModelConfig = domainModelConfig;
    }

    private DomainModel build() {
        EntityMetadata rootEntityMetadata =
                dataStoreMetadata.getEntityMetadata(domainModelConfig.getRootEntityName());
        return null;
    }

    private DomainEntity buildDomainEntity(EntityMetadata entityMetadata) {
        // TODO: validate the entity metadata
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
                            EntityMetadata destinationEntityMetadata =
                                    dataStoreMetadata.getEntityMetadata(
                                            relationMetadata.getDestinationEntity());
                            DomainEntity destinationDomainEntity =
                                    buildDomainEntity(destinationEntityMetadata);
                            DomainRelation domainRelation = new DomainRelation();
                            domainRelation.setSource(domainEntity);
                            domainRelation.setDestination(destinationDomainEntity);
                            domainEntity.add(domainRelation);
                        });
        return domainEntity;
    }

    private DomainAttribute buildDomainAttribute(AttributeMetadata attributeMetadata) {
        // TODO: validate the attribute metadata
        DomainAttribute domainAttribute = new DomainAttribute();
        domainAttribute.setName(attributeMetadata.getName());
        switch (attributeMetadata.getType().toLowerCase()) {
            case "number":
                domainAttribute.setType(DomainAttributeType.NUMBER);
                break;
            case "text":
                domainAttribute.setType(DomainAttributeType.TEXT);
                break;
            case "time":
                domainAttribute.setType(DomainAttributeType.DATE);
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
