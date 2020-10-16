package org.geoserver.appschema.smart.domain.inspire;

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
import org.geoserver.appschema.smart.domain.generator.SmartAppSchemaDomainModelVisitor;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests for inspire use case
 *  
 * @author Jose Macchi - Geosolutions 
 * */
public final class SmartAppSchemaDomainModelVisitorTest extends SmartAppSchemaPostgisTestSupport {

    public SmartAppSchemaDomainModelVisitorTest() {
        SCHEMA = "public";
        NAMESPACE_PREFIX = "inspire";
        TARGET_NAMESPACE = "http://www.api4inspire.it/smartappschema/1.0";    
	}

    @Test
    public void testIndicatorInitiativeAssRootEntity() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	DataStoreMetadata dsm = this.getDataStoreMetadata(metaData);
        DomainModelConfig dmc = new DomainModelConfig();
        dmc.setRootEntityName("indicator_initiative_ass");
        DomainModelBuilder dmb = new DomainModelBuilder(dsm, dmc);
        DomainModel dm = dmb.getDomainModel();
        SmartAppSchemaDomainModelVisitor dmv = new SmartAppSchemaDomainModelVisitor(NAMESPACE_PREFIX, TARGET_NAMESPACE, "./inspire-indicator_initiative_ass-gml.xsd");
        dm.accept(dmv);

        InputStream is = SmartAppSchemaTestHelper.getFileFromResourceAsStream("inspire-indicator_initiative_ass-appschema.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document control = dBuilder.parse(is);  
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);;
        Diff d = XMLUnit.compareXML(control, dmv.getDocument());

        metaData.getConnection().close();
    }
   
}
