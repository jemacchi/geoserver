package org.geoserver.appschema.smart;

import java.sql.DatabaseMetaData;

import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaPostgisTestSetup;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;

abstract public class SmartAppSchemaPostgisTestSupport extends JDBCTestSupport {
	
    protected String SCHEMA = "meteo";
    protected String NAMESPACE_PREFIX = "mt";
    protected String TARGET_NAMESPACE = "http://www.geo-solutions.it/smartappschema/1.0";
    
    protected String CONNECTION_PASSWORD = "docker";
    
	protected DataStoreMetadata getDataStoreMetadata(DatabaseMetaData metaData) throws Exception {
    	String driver = "org.postgresql.Driver"; 
    	String user = metaData.getConnection().getMetaData().getUserName();
    	String url = metaData.getConnection().getMetaData().getURL();
    	String pass = CONNECTION_PASSWORD;
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(SCHEMA, driver, url , user, pass , null, SCHEMA);
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        return dsm;
    }
	
	@Override
    protected JDBCTestSetup createTestSetup() {
        try {
            return new SmartAppSchemaPostgisTestSetup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
