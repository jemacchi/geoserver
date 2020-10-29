package org.geoserver.appschema.smart.metadata.jdbc;

import org.geoserver.test.onlineTest.setup.ReferenceDataPostgisSetup;

/**
 * Implementation of ReferenceDataPostgisSetup for SmartAppSchema Postgis tests.
 *
 * @author Jose Macchi - GeoSolutions
 */
public class SmartAppSchemaPostgisTestSetup extends ReferenceDataPostgisSetup { 

	private String sql;
	public static final String ONLINE_DB_SCHEMA = "smartappschematest";

    public static SmartAppSchemaPostgisTestSetup getInstance(String sql)
            throws Exception {
        return new SmartAppSchemaPostgisTestSetup(sql);
    }
    
	public SmartAppSchemaPostgisTestSetup(String sql) throws Exception {
		super();
		this.sql = sql;
	}
	
    protected void runSqlInsertScript() throws Exception {
        this.run(sql, false);
    }
    
    @Override
    public void tearDown() throws Exception {
    	this.run("DROP SCHEMA IF EXISTS "+ONLINE_DB_SCHEMA+" CASCADE;");
    	getDataSource().getConnection().close();
    }
}
