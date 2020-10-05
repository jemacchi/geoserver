package org.geoserver.test.onlineTest;

import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCTestSetup;

public class SmartAppSchemaTestSetup extends JDBCTestSetup {

	@Override
	protected JDBCDataStoreFactory createDataStoreFactory() {
		return new PostgisNGDataStoreFactory();
	}

}
