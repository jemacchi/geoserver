package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.DatabaseMetaData;
import java.util.Iterator;
import java.util.List;

import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.EntityMetadata;
import org.geoserver.appschema.smart.metadata.RelationMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.utils.SmartAppSchemaTestHelper;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.junit.Test;

/**
 * Tests in Smart AppSchema related to use of a DataStoreMetadata linked to a JDBC connection.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public class JdbcDataStoreMetadataTest extends JDBCTestSupport {
	
	private String SCHEMA = "meteo";
	
    @Override
    protected JDBCTestSetup createTestSetup() {
    	try {
			return new SmartAppSchemaJdbcTestSetup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @Test
    public void testJdbcDataStoreMetadataLoad() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadataConfig dmc = new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
    	
    	DataStoreMetadata dm = (new DataStoreMetadataFactory()).getDataStoreMetadata(dmc);
   	
    	List<EntityMetadata> entities = dm.getDataStoreEntities();
    	SmartAppSchemaTestHelper.printObjectsFromList(entities);

    	Iterator<EntityMetadata> iEntity = entities.iterator();
    	while (iEntity.hasNext()) {
    		EntityMetadata e = iEntity.next();
        	List<AttributeMetadata> attributes = e.getAttributes();
        	SmartAppSchemaTestHelper.printObjectsFromList(attributes);
    	}

    	List<RelationMetadata> relations = dm.getDataStoreRelations();
    	SmartAppSchemaTestHelper.printObjectsFromList(relations);
  	
    	metaData.getConnection().close();
    }
   
    @Test
    public void testMeteoObservationsEntityAttributes() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        EntityMetadata entity = new JdbcTableMetadata(metaData.getConnection(), null, SCHEMA, "meteo_observations");
        SmartAppSchemaTestHelper.printObjectsFromList(entity.getAttributes());
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoObservationsEntityRelations() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        EntityMetadata entity = new JdbcTableMetadata(metaData.getConnection(), null, SCHEMA, "meteo_observations");
        SmartAppSchemaTestHelper.printObjectsFromList(entity.getRelations());
    	metaData.getConnection().close();
    }
}
