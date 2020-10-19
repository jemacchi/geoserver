package org.geoserver.appschema.smart;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaPostgisTestSetup;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.geoserver.test.onlineTest.setup.AppSchemaTestPostgisSetup;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;

public class SmartAppSchemaPostgisTestSupport extends JDBCTestSupport {

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
                new JdbcDataStoreMetadataConfig(SCHEMA, driver, url, user, pass, null, SCHEMA);
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        return dsm;
    }

    @Override
    protected JDBCTestSetup createTestSetup() {
    	
    	// TODO: set list of files that contains my "database"
    	/*Map<String,File> propertyFiles = new HashMap<String, File>();
    	File f = SmartAppSchemaTestHelper.getResourceAsFile("mockdata/meteo_observations.properties");
    	propertyFiles.put("meteo_observations.properties", f);
    	
        try {
            return AppSchemaTestPostgisSetup.getInstance(propertyFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        */
    	try {
            return new SmartAppSchemaPostgisTestSetup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
