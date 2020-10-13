package org.geoserver.appschema.smart.domain;

import java.sql.DatabaseMetaData;
import java.util.logging.Logger;

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
public final class DomainModelVisitorTest extends JDBCTestSupport{

	private static final Logger LOGGER = Logging.getLogger(DomainModelVisitorTest.class);
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
    public void testDomainModelVisitWithStations() throws Exception {
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
		DomainModelVisitor dmv = new LoggerDomainModelVisitor();
		dm.accept(dmv);
		
    	// Close JDBC connection
    	metaData.getConnection().close();
    }
    
    @Test
    public void testDomainModelVisitWithObservations() throws Exception {
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
		DomainModelVisitor dmv = new LoggerDomainModelVisitor();
       	dm.accept(dmv);
    	
    	// Close JDBC connection
    	metaData.getConnection().close();
    }
    
}