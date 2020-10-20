package org.geoserver.appschema.smart.domain.meteo;

import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.appschema.smart.SmartAppSchemaPostgisTestSupport;
import org.geoserver.appschema.smart.domain.DomainModelBuilder;
import org.geoserver.appschema.smart.domain.DomainModelConfig;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.domain.generator.GMLDomainModelVisitor;
import org.geoserver.appschema.smart.domain.generator.SmartAppSchemaDomainModelVisitor;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests related to SmartAppSchemaDomainModelVisitor
 *
 * @author Jose Macchi - Geosolutions
 */
public final class SmartAppSchemaDomainModelVisitorTest extends SmartAppSchemaPostgisTestSupport {

    @Test
    public void testObservationsRootEntity() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_observations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        SmartAppSchemaDomainModelVisitor dmv =
                new SmartAppSchemaDomainModelVisitor(
                        NAMESPACE_PREFIX, TARGET_NAMESPACE, "./meteo-observations-gml.xsd");
        dm.accept(dmv);

        SmartAppSchemaTestHelper.saveDocumentToFile(dmv.getDocument(), "/home/jmacchi/meteo-observations-appschema.xml");
        InputStream is =
                SmartAppSchemaTestHelper.getFileFromResourceAsStream(
                        "meteo-observations-appschema.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff d = XMLUnit.compareXML(control, dmv.getDocument());

        assertEquals(true, d.similar());

        metaData.getConnection().close();
    }

    @Test
    public void testStationsRootEntity() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_stations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        SmartAppSchemaDomainModelVisitor dmv = new SmartAppSchemaDomainModelVisitor(NAMESPACE_PREFIX, TARGET_NAMESPACE, "./meteo-stations-gml.xsd");
        dm.accept(dmv);

        SmartAppSchemaTestHelper.saveDocumentToFile(dmv.getDocument(), "/home/jmacchi/meteo-stations-appschema.xml");
        InputStream is =
                SmartAppSchemaTestHelper.getFileFromResourceAsStream(
                        "meteo-stations-appschema.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff d = XMLUnit.compareXML(control, dmv.getDocument());

        assertEquals(true, d.similar());

        metaData.getConnection().close();
    }
    
    @Test
    public void testStationsRootEntityTailoredModel() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_stations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        
        // keep stations and observations, removed parameters
        List<DomainRelation> rel = dm.getRootEntity().getRelations();
        DomainRelation rm =rel.get(0);
        rm.getSourceEntity().getRelations().clear();
        
        SmartAppSchemaDomainModelVisitor dmv = new SmartAppSchemaDomainModelVisitor(NAMESPACE_PREFIX, TARGET_NAMESPACE, "./meteo-stations-tailored-gml.xsd");
        dm.accept(dmv);

        SmartAppSchemaTestHelper.saveDocumentToFile(dmv.getDocument(), "/home/jmacchi/meteo-stations-tailored-appschema.xml");
        /*InputStream is =
                SmartAppSchemaTestHelper.getFileFromResourceAsStream("meteo-stations-gml.xsd");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        Diff d = XMLUnit.compareXML(control, dmv.getDocument());

        assertEquals(true, d.similar());*/

        metaData.getConnection().close();
    }
}
