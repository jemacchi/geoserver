package org.geoserver.appschema.smart.domain;

import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.appschema.smart.SmartAppSchemaPostgisTestSupport;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geotools.util.logging.Logging;
import org.junit.Test;

/**
 * Tests for DomainModelBuilder class.
 *
 * @author Jose Macchi - Geosolutions
 */
public class DomainModelBuilderTest extends SmartAppSchemaPostgisTestSupport {

    private static final Logger LOGGER = Logging.getLogger(DomainModelBuilderTest.class);

    @Test
    public void testDomainModelBuilderWithRootEntityFailure() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_failure");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = null;
        try {
            dm = dmb.getDomainModel();
            LOGGER.log(Level.INFO, dm.toString());
        } catch (Exception e) {
            assertEquals(dm, null);
        }

        metaData.getConnection().close();
    }

    @Test
    public void testDomainModelBuilderWithJdbcDataStoreMetadataRootStations() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_stations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        LOGGER.log(Level.INFO, dm.toString());
        
        // TODO: add my assertions        

        metaData.getConnection().close();
    }

    @Test
    public void testDomainModelBuilderWithJdbcDataStoreMetadataRootObservations() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_observations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        LOGGER.log(Level.INFO, dm.toString());
        
        // TODO: add my assertions        
        
        metaData.getConnection().close();
    }
}
