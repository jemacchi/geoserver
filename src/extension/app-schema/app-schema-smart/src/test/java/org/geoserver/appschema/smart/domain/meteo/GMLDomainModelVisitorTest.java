package org.geoserver.appschema.smart.domain.meteo;

import java.io.InputStream;
import java.sql.DatabaseMetaData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.appschema.smart.SmartAppSchemaPostgisTestSupport;
import org.geoserver.appschema.smart.domain.DomainModelBuilder;
import org.geoserver.appschema.smart.domain.DomainModelConfig;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.generator.GMLDomainModelVisitor;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.junit.Test;
import org.w3c.dom.Document;

/** 
 * Tests related to GMLDomainModelVisitor 
 * 
 * @author Jose Macchi - Geosolutions 
 * */
public final class GMLDomainModelVisitorTest extends SmartAppSchemaPostgisTestSupport {

    @Test
    public void testObservationsRootEntity() throws Exception {
        DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("meteo_observations");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        GMLDomainModelVisitor dmv = new GMLDomainModelVisitor(NAMESPACE_PREFIX, TARGET_NAMESPACE);
        dm.accept(dmv);

        InputStream is = SmartAppSchemaTestHelper.getFileFromResourceAsStream("meteo-observations-gml.xsd");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);  
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);;
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
        GMLDomainModelVisitor dmv = new GMLDomainModelVisitor(NAMESPACE_PREFIX, TARGET_NAMESPACE);
        dm.accept(dmv);

        InputStream is = SmartAppSchemaTestHelper.getFileFromResourceAsStream("meteo-stations-gml.xsd");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);  
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);;
        Diff d = XMLUnit.compareXML(control, dmv.getDocument());
        
        assertEquals(true, d.similar());
        
        metaData.getConnection().close();
    }
}