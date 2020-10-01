package org.geoserver.datastore.utils;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geoserver.domainmodel.jdbc.JdbcColumn;
import org.geoserver.domainmodel.jdbc.JdbcForeignKeyColumn;
import org.geoserver.domainmodel.jdbc.JdbcForeignKeyConstraint;
import org.geoserver.domainmodel.jdbc.JdbcPrimaryKey;
import org.geoserver.domainmodel.jdbc.JdbcTable;
import org.geoserver.domainmodel.jdbc.JdbcUtilities;
import org.junit.Test;

import junit.framework.TestCase;

public class SmartAppSchemaTest extends TestCase {
	
	private JdbcUtilities JDBC_UTILITIES = JdbcUtilities.getInstance();
    private String POSTGRES_URL = "jdbc:postgresql://localhost:5432/gis";
    private String POSTGRES_USER = "docker";
    private String POSTGRES_PASS = "docker";
    private String SCHEMA = "meteo";
    
    private DatabaseMetaData metaData ;
  
    @Override
	protected void setUp() throws Exception {
    	this.metaData = SmartAppSchemaTestUtils.getConnectionMetaData(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASS);
	}

    @Test
    public void testMeteoPrimaryKeys() throws Exception {
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SmartAppSchemaTestUtils.printPrimaryKeys(pkMap);
    }
    
    @Test
    public void testMeteoSchemaTables() throws Exception {
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        Iterator<JdbcTable> it = tables.iterator();
        while(it.hasNext()) {
        	JdbcTable t = it.next();
        	System.out.println(t.getCatalog()+" - "+t.getSchema()+" - "+t.getName());
        }
    }
    
    @Test
    public void testMeteoStationsTablePrimaryKey() throws Exception{
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SmartAppSchemaTestUtils.printPrimaryKeys(pkMap);
    }
    
    @Test
    public void testMeteoStationsTableColumns() throws Exception{
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<JdbcTable, List<JdbcColumn>> cMap = JDBC_UTILITIES.getColumns(metaData, tables);
        SmartAppSchemaTestUtils.printColumns(cMap);
    }

    @Test
    public void testMeteoObservationsTableColumns() throws Exception{
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_observations");
        List<JdbcTable> tables = new ArrayList<JdbcTable>();
        tables.add(table);
        SortedMap<JdbcTable, List<JdbcColumn>> cMap = JDBC_UTILITIES.getColumns(metaData, tables);
        SmartAppSchemaTestUtils.printColumns(cMap);
    }
    
    @Test
    public void testMeteoSchemaForeignKeys() throws Exception {
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap = JDBC_UTILITIES.getForeignKeys(metaData, tables);
        SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMap, pkMap, uniqueIndexMultimap);
    }

    @Test
    public void testMeteoObservationsTableForeignKeys() throws Exception {
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_observations");
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMultimap = JDBC_UTILITIES.getForeignKeysByTable(metaData, table);
        JdbcPrimaryKey primaryKey = JDBC_UTILITIES.getPrimaryKeyColumnsByTable(metaData, table);
        SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = new TreeMap<JdbcTable, JdbcPrimaryKey>();
        pkMap.put(table, primaryKey);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMultimap, pkMap, uniqueIndexMultimap);
    }
    
    @Test
    public void testMeteoStationsTableForeignKeys() throws Exception {
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMultimap = JDBC_UTILITIES.getForeignKeysByTable(metaData, table);
        JdbcPrimaryKey primaryKey = JDBC_UTILITIES.getPrimaryKeyColumnsByTable(metaData, table);
        SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = new TreeMap<JdbcTable, JdbcPrimaryKey>();
        pkMap.put(table, primaryKey);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printForeignKeys(fkMultimap, pkMap, uniqueIndexMultimap);
    }
    
    @Test
    public void testIndexes() throws Exception {
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, false, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    }

    @Test
    public void testMeteoStationTableIndexes() throws Exception {
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexesByTable(metaData, table, false, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    }

    @Test
    public void testUniqueIndexes() throws Exception {
        List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
        SortedMap<String, Collection<String>> uniqueIndexMultimap = JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    }

    @Test
    public void testMeteoStationsTableUniqueIndexes() throws Exception {
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        SortedMap<String, Collection<String>> uniqueIndexMultimap =	JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
        SmartAppSchemaTestUtils.printIndexes(uniqueIndexMultimap);
    }

}
