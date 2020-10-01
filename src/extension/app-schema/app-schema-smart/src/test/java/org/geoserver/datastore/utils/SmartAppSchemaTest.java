package org.geoserver.datastore.utils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geoserver.domainmodel.jdbc.JdbcForeignKeyConstraint;
import org.geoserver.domainmodel.jdbc.JdbcPrimaryKey;
import org.geoserver.domainmodel.jdbc.JdbcTable;
import org.geoserver.domainmodel.jdbc.JdbcUtilities;
import org.geoserver.domainmodel.jdbc.utils.ForeignKeyColumns;
import org.geoserver.domainmodel.jdbc.utils.QueryData;
import org.geoserver.domainmodel.jdbc.utils.ResultForeignKey;
import org.geoserver.domainmodel.jdbc.utils.ResultIndex;
import org.geoserver.domainmodel.jdbc.utils.ResultPrimaryKey;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

public class SmartAppSchemaTest extends TestCase {

	
	private JdbcUtilities JDBC_UTILITIES = JdbcUtilities.getInstance();
    private String POSTGRES_URL = "jdbc:postgresql://localhost:5432/gis";
    private String POSTGRES_USER = "docker";
    private String POSTGRES_PASS = "docker";
    private String SCHEMA = "meteo";
    
    
	/*@After
    public void tearDown() {
    }*/

    private <T> void printObjectsFromList(List<T> list) {
        if (list != null) {
            for (T object : list) {
                System.out.println(object);
            }
        }
    }
    
    private void printPrimaryKeys(SortedMap<JdbcTable, JdbcPrimaryKey>  pkMap) {
        List<ResultPrimaryKey> pkList = JDBC_UTILITIES.getResultPrimaryKeys(pkMap);
        if(pkList != null)
        {
            for(ResultPrimaryKey pk : pkList)
            {
                System.out.println(pk);
            }
        }
    }
    
    private void printForeignKeys(SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap, SortedMap<JdbcTable, JdbcPrimaryKey> pkMap, SortedMap<String, Collection<String>> uniqueIndexMap) {
        List<ResultForeignKey> resultForeignKeyList = JDBC_UTILITIES.getResultForeignKeys(fkMap, pkMap, uniqueIndexMap);
        if(resultForeignKeyList != null)
        {
            for(ResultForeignKey resultForeignKey : resultForeignKeyList)
            {
                System.out.println(resultForeignKey);
            }
        }
    }
    
    private void printIndexes(SortedMap<String, Collection<String>> indexMap) {
        List<ResultIndex> resultIndices = JDBC_UTILITIES.getResultIndexes(indexMap);
        if(resultIndices != null)
        {
            for(ResultIndex resultIndex : resultIndices)
            {
                System.out.println(resultIndex);
            }
        }
    }
    
    private void printEagerResultsQuery(DatabaseMetaData metaData, List<QueryData> queryDataList, int depth, int remainingDepth, SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap) throws Exception {
        if(queryDataList != null && !queryDataList.isEmpty())
        {
            printObjectsFromList(queryDataList);
            if(remainingDepth > 0)
            {
                List<QueryData> nextQueryDataList = new ArrayList<QueryData>();
                for(QueryData queryData : queryDataList)
                {
                    List<QueryData> nextQueryDataListAux = JDBC_UTILITIES.getReferencedColumnsValues(metaData, queryData, fkMap, depth);
                    if(nextQueryDataListAux != null)
                    {
                        nextQueryDataList.addAll(nextQueryDataListAux);
                    }
                }
                printEagerResultsQuery(metaData, nextQueryDataList, depth + 1, remainingDepth - 1, fkMap);
            }
        }
    }
    
    private void printIndirectReferences(DatabaseMetaData metaData, List<QueryData> queryDataList, int depth, int remainingDepth, Map<JdbcTable, JdbcPrimaryKey>  pkMap) throws Exception {
        if(queryDataList != null && !queryDataList.isEmpty())
        {
            printObjectsFromList(queryDataList);
            if(remainingDepth > 0)
            {
                List<QueryData> nextQueryDataList = new ArrayList<QueryData>();
                for(QueryData queryData : queryDataList)
                {
                    List<QueryData> nextQueryDataListAux = JDBC_UTILITIES.getReferencingColumnsValues(metaData, queryData, pkMap, depth);
                    if(nextQueryDataListAux != null)
                    {
                        nextQueryDataList.addAll(nextQueryDataListAux);
                    }
                }
                printIndirectReferences(metaData, nextQueryDataList, depth + 1, remainingDepth - 1, pkMap);
            }
        }
    }
    
    private DatabaseMetaData getConnectionMetaData() throws Exception {
        Connection connection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASS);
        if (connection != null) {
            return connection.getMetaData();
        }
        return null;
    }

    @Test
    public void testMeteoPrimaryKeys() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
            this.printPrimaryKeys(pkMap);
        }
    }
    
    @Test
    public void testMeteoSchemaTables() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
            Iterator<JdbcTable> it = tables.iterator();
            while(it.hasNext()) {
            	JdbcTable t = it.next();
            	System.out.println(t.getCatalog()+" - "+t.getSchema()+" - "+t.getTableName());
            }
        }
        
    }
    
    @Test
    public void testMeteoStationsTablePrimaryKey() throws Exception{
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = new ArrayList<JdbcTable>();
            tables.add(table);
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
            this.printPrimaryKeys(pkMap);
        }
    }

    @Test
    public void testMeteoSchemaForeignKeys() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap =
            		JDBC_UTILITIES.getForeignKeys(metaData, tables);
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
            this.printForeignKeys(fkMap, pkMap, uniqueIndexMultimap);
        }
    }


    @Test
    public void testMeteoStationsTableForeignKeys() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        if (metaData != null) {
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMultimap =
            		JDBC_UTILITIES.getForeignKeysByTable(metaData, table);
            JdbcPrimaryKey primaryKey = JDBC_UTILITIES.getPrimaryKeyColumnsByTable(metaData, table);
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = new TreeMap<JdbcTable, JdbcPrimaryKey>();
            pkMap.put(table, primaryKey);
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
            this.printForeignKeys(fkMultimap, pkMap, uniqueIndexMultimap);
        }
    }

    
    @Test
    public void testIndexes() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexColumns(metaData, tables, false, true);
            this.printIndexes(uniqueIndexMultimap);
        }
    }

    @Test
    public void testMeteoStationTableIndexes() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        if (metaData != null) {
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexesByTable(metaData, table, false, true);
            this.printIndexes(uniqueIndexMultimap);
        }
    }

    @Test
    public void testUniqueIndexes() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getSchemaTables(metaData, SCHEMA);
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexColumns(metaData, tables, true, true);
            this.printIndexes(uniqueIndexMultimap);
        }
    }

    @Test
    public void testMeteoStationsTableUniqueIndexes() throws Exception {
        DatabaseMetaData metaData = this.getConnectionMetaData();
        JdbcTable table = new JdbcTable(null, SCHEMA, "meteo_stations");
        if (metaData != null) {
            SortedMap<String, Collection<String>> uniqueIndexMultimap =
            		JDBC_UTILITIES.getIndexesByTable(metaData, table, true, true);
            this.printIndexes(uniqueIndexMultimap);
        }
    }

    @Ignore
    @Test
    public void testEagerResultsQueries() throws Exception {
        System.out.println();
        System.out.println("QUERIES EAGER RESULTS");
        System.out.println("---------------------");
        DatabaseMetaData metaData = this.getConnectionMetaData();
        int remainingDepth = 1;
        List<QueryData> queryDataList = new ArrayList<QueryData>();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getTables(metaData);
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap =
            		JDBC_UTILITIES.getForeignKeys(metaData, tables);
            this.printEagerResultsQuery(metaData, queryDataList, 1, remainingDepth, fkMap);
        }
    }

    @Ignore    
    @Test
    public void testEagerResultsQuery() throws Exception {
        System.out.println();
        System.out.println("QUERY EAGER RESULTS");
        System.out.println("-------------------");
        DatabaseMetaData metaData = this.getConnectionMetaData();
        int remainingDepth = 1;
        String[] columnNames = null; 
        List<String[]> valueList = new ArrayList<String[]>();
        JdbcTable table = new JdbcTable(null, "meteo", "meteo_stations");
        if (metaData != null) {
            List<QueryData> queryDataList = new ArrayList<QueryData>();
            QueryData queryData = new QueryData(table, columnNames, valueList);
            queryDataList.add(queryData);
            List<JdbcTable> tables = JDBC_UTILITIES.getTables(metaData);
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap =
            		JDBC_UTILITIES.getForeignKeys(metaData, tables);
            this.printEagerResultsQuery(metaData, queryDataList, 1, remainingDepth, fkMap);
        }
    }

    @Ignore
    @Test
    public void testIndirectReferencesQueries() throws Exception {
        System.out.println();
        System.out.println("REFERENCES TO QUERIES RESULTS");
        System.out.println("-----------------------------");
        DatabaseMetaData metaData = this.getConnectionMetaData();
        int remainingDepth = 1;
        List<QueryData> queryDataList = new ArrayList<QueryData>();
        if (metaData != null) {
            List<JdbcTable> tables = JDBC_UTILITIES.getTables(metaData);
            Map<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
            this.printIndirectReferences(metaData, queryDataList, 1, remainingDepth, pkMap);
        }
    }

    @Ignore
    @Test
    public void testIndirectReferencesQuery() throws Exception {
        System.out.println();
        System.out.println("REFERENCES TO QUERY RESULTS");
        System.out.println("---------------------------");
        DatabaseMetaData metaData = this.getConnectionMetaData();
        int remainingDepth = 1;
        String[] columnNames = null; 
        List<String[]> valueList = new ArrayList<String[]>();
        JdbcTable table = new JdbcTable(null, "meteo", "meteo_stations");
        if (metaData != null) {
            List<QueryData> queryDataList = new ArrayList<QueryData>();
            QueryData queryData = new QueryData(table, columnNames, valueList);
            queryDataList.add(queryData);
            List<JdbcTable> tables = JDBC_UTILITIES.getTables(metaData);
            Map<JdbcTable, JdbcPrimaryKey> pkMap = JDBC_UTILITIES.getPrimaryKeyColumns(metaData, tables);
            this.printIndirectReferences(metaData, queryDataList, 1, remainingDepth, pkMap);
        }
    }

}
