package org.geoserver.appschema.smart.domain;

import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaJdbcTestSetup;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Test;

/**
 * Tests for DomainModelBuilder class.
 *
 * @author Jose Macchi - Geosolutions
 */
public class DomainModelBuilderTest extends JDBCTestSupport {

    private static final Logger LOGGER = Logging.getLogger(DomainModelBuilderTest.class);
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
    public void testDomainModelBuilderWithRootEntityFailure() throws Exception {

        // Define JdbcMetadataStoreConfig
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
        // Build DataStoreMetadata based on Config
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        // Define root entity
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_failure");

        // Build AppSchema DomainModel
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = null;
        try {
            dm = dmb.getDomainModel();
            LOGGER.log(Level.INFO, dm.toString());
        } catch (Exception e) {
            assertEquals(dm, null);
        }

        // Close JDBC connection
        metaData.getConnection().close();
    }

    @Test
    public void testDomainModelBuilderWithJdbcDataStoreMetadataRootStations() throws Exception {

        // Define JdbcMetadataStoreConfig
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
        // Build DataStoreMetadata based on Config
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        // Define root entity
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_stations");

        // Build AppSchema DomainModel
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = dmb.getDomainModel();
        LOGGER.log(Level.INFO, dm.toString());

        // Close JDBC connection
        metaData.getConnection().close();
    }

    @Test
    public void testDomainModelBuilderWithJdbcDataStoreMetadataRootObservations() throws Exception {

        // Define JdbcMetadataStoreConfig
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
        // Build DataStoreMetadata based on Config
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        // Define root entity
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_observations");

        // Build AppSchema DomainModel
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = dmb.getDomainModel();
        LOGGER.log(Level.INFO, dm.toString());

        // Close JDBC connection
        metaData.getConnection().close();
    }
}
