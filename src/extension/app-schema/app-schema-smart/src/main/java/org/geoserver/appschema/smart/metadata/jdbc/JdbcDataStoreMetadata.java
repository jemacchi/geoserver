package org.geoserver.appschema.smart.metadata.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataImpl;
import org.geoserver.appschema.smart.metadata.EntityMetadata;
import org.geoserver.appschema.smart.metadata.RelationMetadata;


/**
 * Concrete class that implements access to JDBC dataStore metadata model, extending DataStoreMetadataImpl.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */public class JdbcDataStoreMetadata extends DataStoreMetadataImpl {

    public JdbcDataStoreMetadata(DataStoreMetadataConfig config) {
        super(config);
    }

    @Override
    public void load() throws Exception {
    	JdbcDataStoreMetadataConfig jdbcConfig = (JdbcDataStoreMetadataConfig) this.config;
        // Load entities
    	entities = new ArrayList<EntityMetadata>();
        List<JdbcTableMetadata> tableList =
                JdbcHelper.getInstance()
                        .getSchemaTables(
                        		jdbcConfig.getConnection().getMetaData(), jdbcConfig.getSchema());
        entities.addAll(tableList);
        // Load attributes and relations for each entity
        relations = new ArrayList<RelationMetadata>();
        Iterator<JdbcTableMetadata> iTables = tableList.iterator();
        while (iTables.hasNext()) {
        	JdbcTableMetadata jTable = iTables.next();
        	// Load attributes
        	List<AttributeMetadata> attributes = JdbcHelper.getInstance().getColumnsByTable(jdbcConfig.getConnection().getMetaData(), jTable);
        	attributes.forEach(
                    attributeMetadata -> {
                    	jTable.addAttribute(attributeMetadata);
                    });
        	// Load relations
        	List<RelationMetadata> tableRelations = JdbcHelper.getInstance().getRelationsByTable(jdbcConfig.getConnection().getMetaData(), jTable);
        	tableRelations.forEach(
                    relationMetadata -> {
                    	jTable.addRelation(relationMetadata);
                    	relations.add(relationMetadata);
                    });
        }
    }

 	@Override
	public EntityMetadata getEntityMetadata(String name) {
        Iterator<EntityMetadata> ie = this.entities.iterator();
        while (ie.hasNext()) {
            EntityMetadata e = ie.next();
            if (e.getName().equals(name)) {
                return e;
            }
            ;
        }
		return null;
	}

}
