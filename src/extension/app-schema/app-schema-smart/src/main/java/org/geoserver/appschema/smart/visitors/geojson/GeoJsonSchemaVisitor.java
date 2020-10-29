package org.geoserver.appschema.smart.visitors.geojson;

import java.util.logging.Logger;

import org.geoserver.appschema.smart.domain.DomainModelVisitorImpl;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainEntitySimpleAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geotools.util.logging.Logging;

/**
 * DomainModel visitor that implements methods to generate GeoJson template output. 
 * 
 * @author Jose Macchi - GeoSolutions
 *
 */
public final class GeoJsonSchemaVisitor extends DomainModelVisitorImpl {

    private static final Logger LOGGER = Logging.getLogger(GeoJsonSchemaVisitor.class);

    private final String targetNamespacePrefix;

    public GeoJsonSchemaVisitor(String targetNamespacePrefix, String targetNamespaceUrl) {
        this.targetNamespacePrefix = targetNamespacePrefix;
    }

    @Override
    public void visitDomainRootEntity(DomainEntity entity) {
    }

    @Override
    public void visitDomainChainedEntity(DomainEntity entity) {
    }

    @Override
    public void visitDomainRelation(DomainRelation relation) {
    }

    @Override
    public void visitDomainEntitySimpleAttribute(DomainEntitySimpleAttribute attribute) {
    }

}
