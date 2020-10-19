package org.geoserver.appschema.smart.metadata.jdbc;

import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCTestSetup;

/**
 * Implementation of JDBCTestSetup for SmartAppSchema Postgis tests.
 *
 * @author Jose Macchi - Geosolutions
 */
public class SmartAppSchemaPostgisTestSetup extends JDBCTestSetup { //extends AppSchemaTestPostgisSetup {

	/*public SmartAppSchemaPostgisTestSetup(Map<String, File> propertyFiles) throws Exception {
		super(propertyFiles);
	}*/

	@Override
	protected JDBCDataStoreFactory createDataStoreFactory() {
	    return new PostgisNGDataStoreFactory();
	}
	
}
