package org.geoserver.appschema.smart.metadata.jdbc;

import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCTestSetup;

/**
 * Implementation of JDBCTestSetup for SmartAppSchema.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public class SmartAppSchemaJdbcTestSetup extends JDBCTestSetup {

	@Override
	protected JDBCDataStoreFactory createDataStoreFactory() {
		return new PostgisNGDataStoreFactory();
	}

}
