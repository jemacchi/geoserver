package org.geoserver.appschema.smart.metadata.jdbc;

import java.sql.DatabaseMetaData;
import org.geoserver.appschema.smart.metadata.EntityMetadata;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.junit.Test;

/**
 * Tests in Smart AppSchema related to use of a DataStoreMetadata linked to a JDBC connection.
 *
 * @author Jose Macchi - Geosolutions
 */
public class InspireJdbcDataStoreMetadataTest extends JDBCTestSupport {

    private String SCHEMA = "public";

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
    public void testPublicIndicatorsEntityAttributes() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        EntityMetadata entity =
                new JdbcTableMetadata(metaData.getConnection(), null, SCHEMA, "indicators");
        SmartAppSchemaTestHelper.printObjectsFromList(entity.getAttributes());
        metaData.getConnection().close();
    }

    @Test
    public void testPublicIndicatorsEntityRelations() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        EntityMetadata entity =
                new JdbcTableMetadata(metaData.getConnection(), null, SCHEMA, "indicators");
        SmartAppSchemaTestHelper.printObjectsFromList(entity.getRelations());
        metaData.getConnection().close();
    }
}
