package org.geoserver.appschema.smart.domain.generator;

import java.sql.DatabaseMetaData;
import java.util.logging.Logger;

import org.geoserver.appschema.smart.domain.DomainModelBuilder;
import org.geoserver.appschema.smart.domain.DomainModelConfig;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaJdbcTestSetup;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.geoserver.appschema.smart.utils.LoggerDomainModelVisitor;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Test;

/**
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public final class GMLDomainModelVisitorTest extends JDBCTestSupport{

	private static final Logger LOGGER = Logging.getLogger(GMLDomainModelVisitorTest.class);
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
    public void testObservationsRootEntity() throws Exception {
    	// Define JdbcMetadataStoreConfig
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadataConfig config = new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
    	// Build DataStoreMetadata based on Config
    	DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
    	// Define root entity
    	DomainModelConfig dmc = new DomainModelConfig();
    	dmc.setRootEntityName("meteo_observations");
    	// Build AppSchema DomainModel     	
    	DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
    	
    	DomainModel dm = dmb.getDomainModel();
		GMLDomainModelVisitor dmv = new GMLDomainModelVisitor();
		dm.accept(dmv);
        
        SmartAppSchemaTestHelper.printDocument(dmv.getDocument(), System.out);
    	
    	// Close JDBC connection
    	metaData.getConnection().close();
    }
    
    
    @Test
    public void testStationsRootEntity() throws Exception {
    	// Define JdbcMetadataStoreConfig
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadataConfig config = new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
    	// Build DataStoreMetadata based on Config
    	DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
    	// Define root entity
    	DomainModelConfig dmc = new DomainModelConfig();
    	dmc.setRootEntityName("meteo_stations");
    	// Build AppSchema DomainModel     	
    	DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
    	
    	DomainModel dm = dmb.getDomainModel();
		GMLDomainModelVisitor dmv = new GMLDomainModelVisitor();
		dm.accept(dmv);
        
        SmartAppSchemaTestHelper.printDocument(dmv.getDocument(), System.out);
    	
    	// Close JDBC connection
    	metaData.getConnection().close();
    }
}