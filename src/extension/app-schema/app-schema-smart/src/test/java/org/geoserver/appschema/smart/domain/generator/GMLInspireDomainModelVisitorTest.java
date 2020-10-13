package org.geoserver.appschema.smart.domain.generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;

import org.apache.commons.io.IOUtils;
import org.geoserver.appschema.smart.domain.DomainModelBuilder;
import org.geoserver.appschema.smart.domain.DomainModelConfig;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaJdbcTestSetup;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.junit.Test;

/** @author Jose Macchi - Geosolutions */
public final class GMLInspireDomainModelVisitorTest extends JDBCTestSupport {

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
    public void testEndpointRootEntity() throws Exception {
        // Define JdbcMetadataStoreConfig
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
        // Build DataStoreMetadata based on Config
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        // Define root entity
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("endpoint");
        // Build AppSchema DomainModel
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = dmb.getDomainModel();
        GMLDomainModelVisitor dmv = new GMLDomainModelVisitor();
        dm.accept(dmv);
        
        SmartAppSchemaTestHelper.printDocument(dmv.getDocument(), System.out);
        //SmartAppSchemaTestHelper.saveDocumentToFile(dmv.getDocument(), "~/observations-gml.xsd");

        // Close JDBC connection
        metaData.getConnection().close();
    }
    
    @Test
    public void testIndicatorInitiativeAssRootEntity() throws Exception {
        // Define JdbcMetadataStoreConfig
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(metaData.getConnection(), null, SCHEMA);
        // Build DataStoreMetadata based on Config
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        // Define root entity
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("indicator_initiative_ass");
        // Build AppSchema DomainModel
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);

        DomainModel dm = dmb.getDomainModel();
        GMLDomainModelVisitor dmv = new GMLDomainModelVisitor();
        dm.accept(dmv);
        
        SmartAppSchemaTestHelper.printDocument(dmv.getDocument(), System.out);
        //SmartAppSchemaTestHelper.saveDocumentToFile(dmv.getDocument(), "~/observations-gml.xsd");

        // Close JDBC connection
        metaData.getConnection().close();
    }

}
