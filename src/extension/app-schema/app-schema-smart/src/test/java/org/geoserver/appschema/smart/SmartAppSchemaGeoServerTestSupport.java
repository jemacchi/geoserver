package org.geoserver.appschema.smart;

import static org.geoserver.appschema.smart.data.PostgisSmartAppSchemaDataAccessFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.appschema.smart.data.PostgisSmartAppSchemaDataAccessFactory;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.DataStoreMetadataFactory;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcDataStoreMetadataConfig;
import org.geoserver.appschema.smart.metadata.jdbc.SmartAppSchemaPostgisTestSetup;
import org.geoserver.appschema.smart.metadata.jdbc.utils.JdbcUrlSplitter;
import org.geoserver.appschema.smart.utils.SmartAppSchemaTestHelper;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.AdminRequest;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.DataAccessNewPage;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.SQLDialect;
import org.geotools.test.FixtureUtilities;
import org.geotools.util.logging.Logging;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Implementation of GeoServerWicketTestSupport for SmartAppSchema Postgis tests, including
 * Geoserver and Wicket support.
 *
 * @author Jose Macchi - GeoSolutions
 */
public class SmartAppSchemaGeoServerTestSupport extends GeoServerWicketTestSupport {

    static final Logger LOGGER = Logging.getLogger(SmartAppSchemaGeoServerTestSupport.class);

    public static final String ONLINE_TEST_PROFILE = "onlineTestProfile";
    protected static Map<String, Boolean> online = new HashMap<String, Boolean>();
    protected static Map<String, Boolean> found = new HashMap<String, Boolean>();
    protected static Param PASSWORD = new Param("password", String.class, "Password", true);

    public String SMARTAPPSCHEMA_DATASTORE_NAME = "smartAppSchemaDataStore";
    public String JDBC_DATASTORE_NAME = "jdbcDataStore";
    public String SCHEMA = SmartAppSchemaPostgisTestSetup.ONLINE_DB_SCHEMA;
    public String NAMESPACE_PREFIX = "mt";
    public String TARGET_NAMESPACE = "http://www.geo-solutions.it/smartappschema/1.0";

    private final DataAccessFactory dataStoreFactory = new PostgisSmartAppSchemaDataAccessFactory();

    protected String MOCK_SQL_SCRIPT = "meteo_db.sql";
    protected SmartAppSchemaPostgisTestSetup setup;
    protected JDBCDataStore jdbcDataStore;
    protected SQLDialect dialect;
    protected Properties fixture;

    protected DataAccessNewPage startPage() {
        AdminRequest.start(new Object());
        login();
        final DataAccessNewPage page = new DataAccessNewPage(dataStoreFactory.getDisplayName());
        tester.startPage(page);
        return page;
    }

    protected DataStoreMetadata getDataStoreMetadata(DatabaseMetaData metaData) throws Exception {
        DataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(SCHEMA, metaData.getConnection(), null, SCHEMA);
        DataStoreMetadata dsm = (new DataStoreMetadataFactory()).getDataStoreMetadata(config);
        return dsm;
    }

    /**
     * Helper method that allows to remove sourceDataStore node from AppSchema xml doc. Useful to
     * clean xml docs when required to compare assertXML (since those sections of documents contains
     * specific information from dataStores based on JDBC Connection, it's required to avoid the
     * comparision.
     *
     * @param appSchemaDoc
     */
    protected void removeSourceDataStoresNode(Document appSchemaDoc) {
        NodeList sds = appSchemaDoc.getElementsByTagName("sourceDataStores");
        if (sds != null && sds.getLength() > 0) {
            sds.item(0).getParentNode().removeChild(sds.item(0));
        }
    }

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        configureFixture();
        connect();
        // setup geoserver required namespaces and data for SmartAppSchema tests
        setupGeoserverTestData();
    }

    @Override
    protected void onTearDown(SystemTestData testData) throws Exception {
        disconnect();
    }

    protected SmartAppSchemaPostgisTestSetup createTestSetup() {
        String sql;
        try {
            sql =
                    IOUtils.toString(
                            SmartAppSchemaTestHelper.getResourceAsStream(
                                    "mockdata/" + MOCK_SQL_SCRIPT),
                            Charset.defaultCharset());
            return SmartAppSchemaPostgisTestSetup.getInstance(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void connect() throws Exception {
        // create the test harness
        if (setup == null) {
            setup = createTestSetup();
        }

        setup.setFixture(fixture);
        setup.setUp();

        // initialize the database
        setup.initializeDatabase();

        // initialize the data
        setup.setUpData();

        // create the dataStore
        HashMap params = getJdbcDataStoreParams();
        if (jdbcDataStore == null) {
            JDBCDataStoreFactory factory = setup.createDataStoreFactory();
            jdbcDataStore = factory.createDataStore(params);
        }
        setup.setUpDataStore(jdbcDataStore);
        dialect = jdbcDataStore.getSQLDialect();
    }

    /** Return the DataStore parameters from fixture definition. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private HashMap getJdbcDataStoreParams() throws Exception {
        HashMap params = new HashMap();
        params.put(JDBCDataStoreFactory.NAMESPACE.key, TARGET_NAMESPACE);
        params.put(JDBCDataStoreFactory.SCHEMA.key, SCHEMA);
        params.put(JDBCDataStoreFactory.PASSWD.key, fixture.getProperty("password"));
        params.put(JDBCDataStoreFactory.USER.key, fixture.getProperty("user"));

        String url = fixture.getProperty("url");
        if (url != null) {
            JdbcUrlSplitter jdbc = new JdbcUrlSplitter(url);
            params.put(JDBCDataStoreFactory.DATABASE.key, jdbc.database);
            params.put(JDBCDataStoreFactory.HOST.key, jdbc.host);
            params.put(JDBCDataStoreFactory.PORT.key, jdbc.port);
        } else {
            throw new RuntimeException("URL parameter is missed on fixture.");
        }
        return params;
    }

    protected void disconnect() throws Exception {
        setup.tearDown();
        jdbcDataStore.dispose();
    }

    protected String getFixtureId() {
        return createTestSetup().getDatabaseID();
    }

    protected Properties createOfflineFixture() {
        return createTestSetup().createOfflineFixture();
    }

    protected Properties createExampleFixture() {
        return createTestSetup().createExampleFixture();
    }

    /** Load fixture configuration. Create example if absent. */
    protected void configureFixture() {
        if (fixture == null) {
            String fixtureId = getFixtureId();
            if (fixtureId == null) {
                return; // not available (turn test off)
            }
            try {
                // load the fixture
                File base = FixtureUtilities.getFixtureDirectory();
                // look for a "profile", these can be used to group related fixtures
                String profile = System.getProperty(ONLINE_TEST_PROFILE);
                if (profile != null && !"".equals(profile)) {
                    base = new File(base, profile);
                }
                File fixtureFile = FixtureUtilities.getFixtureFile(base, fixtureId);
                Boolean exists = found.get(fixtureFile.getCanonicalPath());
                if (exists == null || exists.booleanValue()) {
                    if (fixtureFile.exists()) {
                        fixture = FixtureUtilities.loadProperties(fixtureFile);
                        found.put(fixtureFile.getCanonicalPath(), true);
                    } else {
                        // no fixture file, if no profile was specified write out a template
                        // fixture using the offline fixture properties
                        if (profile == null) {
                            Properties exampleFixture = createExampleFixture();
                            if (exampleFixture != null) {
                                File exFixtureFile =
                                        new File(fixtureFile.getAbsolutePath() + ".example");
                                if (!exFixtureFile.exists()) {
                                    createExampleFixture(exFixtureFile, exampleFixture);
                                }
                            }
                        }
                        found.put(fixtureFile.getCanonicalPath(), false);
                    }
                }
                if (fixture == null) {
                    fixture = createOfflineFixture();
                }
                if (fixture == null && exists == null) {
                    // only report if exists == null since it means that this is
                    // the first time trying to load the fixture
                    FixtureUtilities.printSkipNotice(fixtureId, fixtureFile);
                }
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.INFO, "", e);
            }
        }
    }

    protected Map<String, Serializable> getSmartAppSchemaDataStoreParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(DBTYPE.key, PostgisSmartAppSchemaDataAccessFactory.DBTYPE_STRING);
        params.put(NAMESPACE.key, TARGET_NAMESPACE);
        params.put(
                PostgisSmartAppSchemaDataAccessFactory.DATASTORE_NAME.key,
                SMARTAPPSCHEMA_DATASTORE_NAME);
        params.put(ROOT_ENTITY.key, "");
        params.put(POSTGIS_DATASTORE_METADATA.key, JDBC_DATASTORE_NAME);
        params.put(DOMAIN_MODEL_EXCLUSIONS.key, "");
        return params;
    }

    protected void setFormValues(
            FormTester ft,
            String datastoreName,
            Map<String, Serializable> params,
            String rootentity) {
        String postgisMetadataStore = (String) params.get(POSTGIS_DATASTORE_METADATA.key);
        String modelExclusions = (String) params.get(DOMAIN_MODEL_EXCLUSIONS.key);

        WorkspacePanel workspacePanel =
                (WorkspacePanel) ft.getForm().getPage().get("dataStoreForm:workspacePanel");
        ft.select("workspacePanel:border:border_body:paramValue", 0);
        ft.select("parametersPanel:postgisdatastore:border:border_body:paramValue", 0);
        ft.select("parametersPanel:rootentities:border:border_body:paramValue", 0);
        // ft.setValue("workspacePanel:border:border_body:paramValue", NAMESPACE_PREFIX);
        ft.setValue("dataStoreNamePanel:border:border_body:paramValue", datastoreName);
        // ft.setValue("parametersPanel:postgisdatastore:border:border_body:paramValue",
        // postgisMetadataStore);
        // ft.setValue("parametersPanel:rootentities:border:border_body:paramValue", rootentity);
        ft.setValue("parametersPanel:exclusions:border:border_body:paramValue", modelExclusions);
        ft.setValue("parametersPanel:datastorename:border:border_body:paramValue", datastoreName);
    }

    private void createExampleFixture(File exFixtureFile, Properties exampleFixture) {
        try {
            exFixtureFile.getParentFile().mkdirs();
            exFixtureFile.createNewFile();

            try (FileOutputStream fout = new FileOutputStream(exFixtureFile)) {

                exampleFixture.store(
                        fout,
                        "This is an example fixture. Update the "
                                + "values and remove the .example suffix to enable the test");
                fout.flush();
            }
        } catch (IOException ioe) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.INFO, "", ioe);
        }
    }

    private void setupGeoserverTestData() throws IOException, SQLException {
        // insert workspace with defined prefix into geoserver
        Catalog catalog = ((GeoServer) GeoServerExtensions.bean("geoServer")).getCatalog();
        // create the namespace
        NamespaceInfoImpl namespace = new NamespaceInfoImpl();
        namespace.setPrefix(NAMESPACE_PREFIX);
        namespace.setURI(TARGET_NAMESPACE);
        namespace.setIsolated(false);
        catalog.add(namespace);
        // create the workspace
        WorkspaceInfoImpl workspace = new WorkspaceInfoImpl();
        workspace.setName(NAMESPACE_PREFIX);
        workspace.setIsolated(false);
        catalog.add(workspace);
        // create the jdbc datastore
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        DataStoreInfo storeInfo = cb.buildDataStore(JDBC_DATASTORE_NAME);
        ((DataStoreInfoImpl) storeInfo).setId("1");
        JdbcDataStoreMetadataConfig config =
                new JdbcDataStoreMetadataConfig(jdbcDataStore, fixture.getProperty("password"));
        storeInfo.getConnectionParameters().putAll(config.getParameters());
        WorkspaceInfo wi = getCatalog().getWorkspaceByName(this.NAMESPACE_PREFIX);
        storeInfo.setWorkspace(wi);
        getCatalog().add(storeInfo);
    }

    public Properties getFixture() {
        return fixture;
    }

    @After
    public void clearAdminRequest() {
        AdminRequest.finish();
    }

    @Test
    public void testPageRendersOnLoad() {
        startPage();
        tester.assertLabel("dataStoreForm:storeType", dataStoreFactory.getDisplayName());
        tester.assertLabel("dataStoreForm:storeTypeDescription", dataStoreFactory.getDescription());
        tester.assertComponent("dataStoreForm:workspacePanel", WorkspacePanel.class);
    }

    @Test
    public void testParametersHidden() {
        startPage();
        MarkupContainer container =
                (MarkupContainer)
                        tester.getComponentFromLastRenderedPage("dataStoreForm:parametersPanel");
        assertFalse(container.get("exclusions").isVisible());
        assertFalse(container.get("datastorename").isVisible());
    }

    @Test
    public void testParametersListed() {
        startPage();
        MarkupContainer container =
                (MarkupContainer)
                        tester.getComponentFromLastRenderedPage("dataStoreForm:parametersPanel");
        assertEquals(
                "AppSchema Smart", ((DataStoreInfo) container.getDefaultModelObject()).getType());
        assertTrue(container.isVisible());
        assertNotNull(container.get("rootentities"));
        assertNotNull(container.get("exclusions"));
        assertNotNull(container.get("postgisdatastore"));
        assertNotNull(container.get("datastorename"));
    }
}
