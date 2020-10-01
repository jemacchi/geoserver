package org.geoserver.datastore.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.geoserver.domainmodel.jdbc.JdbcColumn;
import org.geoserver.domainmodel.jdbc.JdbcForeignKeyColumn;
import org.geoserver.domainmodel.jdbc.JdbcForeignKeyConstraint;
import org.geoserver.domainmodel.jdbc.JdbcPrimaryKey;
import org.geoserver.domainmodel.jdbc.JdbcTable;
import org.geoserver.domainmodel.jdbc.JdbcUtilities;
import org.geoserver.domainmodel.jdbc.utils.ResultColumn;
import org.geoserver.domainmodel.jdbc.utils.ResultForeignKey;
import org.geoserver.domainmodel.jdbc.utils.ResultIndex;
import org.geoserver.domainmodel.jdbc.utils.ResultPrimaryKey;

public class SmartAppSchemaTestUtils {

	private static JdbcUtilities JDBC_UTILITIES = JdbcUtilities.getInstance();
	
	public static DatabaseMetaData getConnectionMetaData(String url, String user, String pass) throws Exception {
        Connection connection = DriverManager.getConnection(url, user, pass);
        if (connection != null) {
            return connection.getMetaData();
        }
        return null;
    }
	
	public static <T> void printObjectsFromList(List<T> list) {
        if (list != null) {
            for (T object : list) {
                System.out.println(object);
            }
        }
    }
    
	public static void printPrimaryKeys(SortedMap<JdbcTable, JdbcPrimaryKey>  pkMap) {
        List<ResultPrimaryKey> pkList = JDBC_UTILITIES.getResultPrimaryKeys(pkMap);
        if(pkList != null)
        {
            for(ResultPrimaryKey pk : pkList)
            {
                System.out.println(pk);
            }
        }
    }
    
	public static void printColumns(SortedMap<JdbcTable, List<JdbcColumn>>  cMap) {
        List<ResultColumn> cList = JDBC_UTILITIES.getResultColumns(cMap);
        if(cList != null)
        {
            for(ResultColumn c : cList)
            {
                System.out.println(c);
            }
        }
    }
    
	public static void printForeignKeys(SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap, SortedMap<JdbcTable, JdbcPrimaryKey> pkMap, SortedMap<String, Collection<String>> uniqueIndexMap) {
        List<ResultForeignKey> resultForeignKeyList = JDBC_UTILITIES.getResultForeignKeys(fkMap, pkMap, uniqueIndexMap);
        if(resultForeignKeyList != null)
        {
            for(ResultForeignKey resultForeignKey : resultForeignKeyList)
            {
                System.out.println(resultForeignKey);
            }
        }
    }
    
	public static void printIndexes(SortedMap<String, Collection<String>> indexMap) {
        List<ResultIndex> resultIndices = JDBC_UTILITIES.getResultIndexes(indexMap);
        if(resultIndices != null)
        {
            for(ResultIndex resultIndex : resultIndices)
            {
                System.out.println(resultIndex);
            }
        }
    }
    
}
