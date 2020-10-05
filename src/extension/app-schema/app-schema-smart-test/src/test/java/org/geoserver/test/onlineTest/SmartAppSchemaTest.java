package org.geoserver.test.onlineTest;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geoserver.appschema.smart.DomainModelBuilder;
import org.geoserver.domainmodel.Attribute;
import org.geoserver.domainmodel.DomainModel;
import org.geoserver.domainmodel.Entity;
import org.geoserver.domainmodel.Relation;
import org.geoserver.domainmodel.jdbc.JdbcDomainModel;
import org.geoserver.domainmodel.jdbc.JdbcDomainModelParameters;
import org.geoserver.domainmodel.jdbc.JdbcForeignKeyColumn;
import org.geoserver.domainmodel.jdbc.JdbcTable;
import org.geoserver.domainmodel.jdbc.JdbcUtilities;
import org.geoserver.domainmodel.jdbc.constraint.JdbcForeignKeyConstraint;
import org.geoserver.domainmodel.jdbc.constraint.JdbcPrimaryKeyConstraint;
import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.junit.Test;

public class SmartAppSchemaTest extends JDBCTestSupport {
	
	private JdbcUtilities JDBC_UTILITIES = JdbcUtilities.getInstance();
    private String SCHEMA = "meteo";
    
    @Override
    protected JDBCTestSetup createTestSetup() {
    	try {
			return new SmartAppSchemaTestSetup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @Test
    public void testPopulateJdbcDomainModel() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	JdbcDomainModelParameters jdmp = new JdbcDomainModelParameters(metaData.getConnection(), null, SCHEMA);
    	JdbcDomainModel jdm = new JdbcDomainModel(jdmp);
    	jdm.populateModel();
    	System.out.println("--------------------------------------------------------");
    	List<Entity> tables = jdm.getEntities();
    	SmartAppSchemaTestUtils.printObjectsFromList(tables);
    	System.out.println("--------------------------------------------------------");
    	List<Attribute> attributes = jdm.getAttributes();
    	SmartAppSchemaTestUtils.printObjectsFromList(attributes);
    	System.out.println("--------------------------------------------------------");
    	List<Relation> relations = jdm.getRelations();
    	SmartAppSchemaTestUtils.printObjectsFromList(relations);
    	System.out.println("--------------------------------------------------------");   	
    	metaData.getConnection().close();
    }

    /*@Test
    public void testBuildDomainModel() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	JdbcDomainModelParameters params = new JdbcDomainModelParameters(metaData.getConnection(), null, SCHEMA);
    	

    	DomainModelBuilder dmb = new DomainModelBuilder();
    	DomainModel dm  = dmb.getDomainMetadataStore(params);
    	
    	jdm.populateModel();
    	System.out.println("--------------------------------------------------------");
    	List<Entity> tables = jdm.getEntities();
    	SmartAppSchemaTestUtils.printObjectsFromList(tables);
    	System.out.println("--------------------------------------------------------");
    	List<Attribute> attributes = jdm.getAttributes();
    	SmartAppSchemaTestUtils.printObjectsFromList(attributes);
    	System.out.println("--------------------------------------------------------");
    	List<Relation> relations = jdm.getRelations();
    	SmartAppSchemaTestUtils.printObjectsFromList(relations);
    	System.out.println("--------------------------------------------------------");   	
    	metaData.getConnection().close();
    }
*/
    
    
    @Test
    public void testMeteoPrimaryKeys() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<Entity, JdbcPrimaryKeyConstraint> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SmartAppSchemaTestUtils.printPrimaryKeys(pkMap);
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoSchemaTables() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
    	List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        Iterator<JdbcTable> it = tables.iterator();
        while(it.hasNext()) {
        	JdbcTable t = it.next();
        	System.out.println(t.getCatalog()+" - "+t.getSchema()+" - "+t.getName());
        }
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoStationsTablePrimaryKey() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        JdbcTable table = new JdbcTable(metaData.getConnection(), null, SCHEMA, "meteo_stations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<Entity, JdbcPrimaryKeyConstraint> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SmartAppSchemaTestUtils.printPrimaryKeys(pkMap);
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoStationsTableColumns() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        JdbcTable table = new JdbcTable(metaData.getConnection(), null, SCHEMA, "meteo_stations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<JdbcTable, List<Attribute>> cMap = JDBC_UTILITIES.getColumns(metaData, tables);
        SmartAppSchemaTestUtils.printColumns(cMap);
    	metaData.getConnection().close();
    }

    @Test
    public void testMeteoObservationsTableColumns() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        JdbcTable table = new JdbcTable(metaData.getConnection(), null, SCHEMA, "meteo_observations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<JdbcTable, List<Attribute>> cMap = JDBC_UTILITIES.getColumns(metaData, tables);
        SmartAppSchemaTestUtils.printColumns(cMap);
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoObservationsTableAttributes() throws Exception{
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        JdbcTable table = new JdbcTable(metaData.getConnection(), null, SCHEMA, "meteo_observations");
        SmartAppSchemaTestUtils.printObjectsFromList(table.getAttributes());
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoSchemaForeignKeys() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();    	
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap = JDBC_UTILITIES.getForeignKeys(metaData, tables);
        SortedMap<Entity, JdbcPrimaryKeyConstraint> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMap, pkMap, uniqueIndexMultimap);
    	metaData.getConnection().close();
    }

    @Test
    public void testMeteoObservationsTableForeignKeys() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        JdbcTable table = new JdbcTable(metaData.getConnection(),null, SCHEMA, "meteo_observations");
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMultimap = JDBC_UTILITIES.getForeignKeysByTable(metaData, table);
        JdbcPrimaryKeyConstraint primaryKey = JDBC_UTILITIES.getPrimaryKeyColumnsByTable(metaData, table);
        SortedMap<Entity, JdbcPrimaryKeyConstraint> pkMap = new TreeMap<Entity, JdbcPrimaryKeyConstraint>();
        pkMap.put(table, primaryKey);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMultimap, pkMap, uniqueIndexMultimap);
    	metaData.getConnection().close();
    }
    
    @Test
    public void testMeteoStationsTableForeignKeys() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        JdbcTable table = new JdbcTable(metaData.getConnection(),null, SCHEMA, "meteo_stations");
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMultimap = JDBC_UTILITIES.getForeignKeysByTable(metaData, table);
        JdbcPrimaryKeyConstraint primaryKey = JDBC_UTILITIES.getPrimaryKeyColumnsByTable(metaData, table);
        SortedMap<Entity, JdbcPrimaryKeyConstraint> pkMap = new TreeMap<Entity, JdbcPrimaryKeyConstraint>();
        pkMap.put(table, primaryKey);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMultimap, pkMap, uniqueIndexMultimap);
    	metaData.getConnection().close();
    }
    
    @Test
    public void testIndexes() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, false, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    	metaData.getConnection().close();
    }

    @Test
    public void testMeteoStationTableIndexes() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        JdbcTable table = new JdbcTable(metaData.getConnection(),null, SCHEMA, "meteo_stations");
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, false, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    	metaData.getConnection().close();
    }

    @Test
    public void testUniqueIndexes() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    	metaData.getConnection().close();
    }

    @Test
    public void testMeteoStationsTableUniqueIndexes() throws Exception {
    	DatabaseMetaData metaData = this.setup.getDataSource().getConnection().getMetaData();
        JdbcTable table = new JdbcTable(metaData.getConnection(),null, SCHEMA, "meteo_stations");
        SortedMap<String, Collection<String>> uniqueIndexMultimap =	JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    	metaData.getConnection().close();
    }

}
