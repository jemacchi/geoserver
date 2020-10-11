package org.geoserver.appschema.smart.metadata.jdbc.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.appschema.smart.metadata.AttributeMetadata;
import org.geoserver.appschema.smart.metadata.EntityMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcColumnMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcForeignKeyColumnMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcHelper;
import org.geoserver.appschema.smart.metadata.jdbc.JdbcTableMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.constraint.JdbcForeignKeyConstraintMetadata;
import org.geoserver.appschema.smart.metadata.jdbc.constraint.JdbcPrimaryKeyConstraintMetadata;
import org.geotools.util.logging.Logging;

/**
 * Smart AppSchema Helper for testing purposes. 
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public class SmartAppSchemaTestHelper {

	private static final Logger LOGGER = Logging.getLogger(SmartAppSchemaTestHelper.class);
	
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
            	LOGGER.log(Level.INFO, object.toString());
            }
        }
    }
    
	public static void printPrimaryKeys(SortedMap<EntityMetadata, JdbcPrimaryKeyConstraintMetadata>  pkMap) {
        List<ResultPrimaryKey> pkList = getResultPrimaryKeys(pkMap);
        if(pkList != null)
        {
            for(ResultPrimaryKey pk : pkList)
            {
            	LOGGER.log(Level.INFO, pk.toString());
            }
        }
    }
    
	public static void printColumns(SortedMap<JdbcTableMetadata, List<AttributeMetadata>>  cMap) {
        List<ResultColumn> cList = getResultColumns(cMap);
        if(cList != null)
        {
            for(ResultColumn c : cList)
            {
            	LOGGER.log(Level.INFO, c.toString());
            }
        }
    }
    
	public static void printForeignKeys(SortedMap<JdbcForeignKeyConstraintMetadata, Collection<JdbcForeignKeyColumnMetadata>> fkMap, SortedMap<EntityMetadata, JdbcPrimaryKeyConstraintMetadata> pkMap, SortedMap<String, Collection<String>> uniqueIndexMap) {
        List<ResultForeignKey> resultForeignKeyList = getResultForeignKeys(fkMap, pkMap, uniqueIndexMap);
        if(resultForeignKeyList != null)
        {
            for(ResultForeignKey resultForeignKey : resultForeignKeyList)
            {
            	LOGGER.log(Level.INFO, resultForeignKey.toString());
            }
        }
    }
    
	public static void printIndexes(SortedMap<String, Collection<String>> indexMap) {
        List<ResultIndex> resultIndices = getResultIndexes(indexMap);
        if(resultIndices != null)
        {
            for(ResultIndex resultIndex : resultIndices)
            {
            	LOGGER.log(Level.INFO, resultIndex.toString());
            }
        }
    }
	
	private static List<ResultColumn> getResultColumns(SortedMap<JdbcTableMetadata, List<AttributeMetadata>> cMap) {
        if (cMap != null) {
            List<ResultColumn> cList = new ArrayList<ResultColumn>();
            for (Map.Entry<JdbcTableMetadata, List<AttributeMetadata>> cMapEntry : cMap.entrySet()) {
                Iterator<AttributeMetadata> cIterator = cMapEntry.getValue().iterator();
                while (cIterator.hasNext()) {
                    ResultColumn resultC = new ResultColumn((JdbcColumnMetadata) cIterator.next());
                    cList.add(resultC);
                }
            }
            return cList;
        }
        return null;
    }

    private static List<ResultPrimaryKey> getResultPrimaryKeys(
            SortedMap<EntityMetadata, JdbcPrimaryKeyConstraintMetadata> pkMap) {
        if (pkMap != null) {
            List<ResultPrimaryKey> pkList = new ArrayList<ResultPrimaryKey>();
            for (Map.Entry<EntityMetadata, JdbcPrimaryKeyConstraintMetadata> pkMapEntry : pkMap.entrySet()) {
                ResultPrimaryKey resultPk = new ResultPrimaryKey(pkMapEntry.getValue());
                pkList.add(resultPk);
            }
            return pkList;
        }
        return null;
    }

    private static List<ResultForeignKey> getResultForeignKeys(
            SortedMap<JdbcForeignKeyConstraintMetadata, Collection<JdbcForeignKeyColumnMetadata>> fkMap,
            SortedMap<EntityMetadata, JdbcPrimaryKeyConstraintMetadata> pkMap,
            SortedMap<String, Collection<String>> uniqueIndexMap) {
        if (fkMap != null) {
            List<ResultForeignKey> resultForeignKeyList = new ArrayList<ResultForeignKey>();
            for (JdbcForeignKeyConstraintMetadata fkConstraint : fkMap.keySet()) {
                Collection<JdbcForeignKeyColumnMetadata> fkColumnsList = fkMap.get(fkConstraint);

                StringBuilder fkColumnsStr = new StringBuilder();
                StringBuilder pkColumnsStr = new StringBuilder();
                if (fkColumnsList != null && !fkColumnsList.isEmpty()) {
                    for (JdbcForeignKeyColumnMetadata fkColumns : fkColumnsList) {
                        fkColumnsStr.append(fkColumns.getName());
                        fkColumnsStr.append(",");
                        pkColumnsStr.append(fkColumns.getRelatedColumn().getName());
                        pkColumnsStr.append(",");
                    }
                    fkColumnsStr.deleteCharAt(fkColumnsStr.length() - 1);
                    pkColumnsStr.deleteCharAt(pkColumnsStr.length() - 1);
                }

                StringBuilder stringBuilder = new StringBuilder(fkConstraint.getTable().toString());
                stringBuilder.append(fkColumnsStr);
                stringBuilder.append(" -> ");
                stringBuilder.append(fkConstraint.getRelatedTable().toString());
                stringBuilder.append(pkColumnsStr);
                JdbcPrimaryKeyConstraintMetadata primaryKey = JdbcHelper.getInstance().isPrimaryKey(fkConstraint.getTable(), fkColumnsList, pkMap);
                if (primaryKey != null) {
                    resultForeignKeyList.add(
                            new ResultForeignKey(
                                    "1:1",
                                    fkConstraint.getTable().toString(),
                                    fkColumnsStr.toString(),
                                    fkConstraint.getRelatedTable().toString(),
                                    pkColumnsStr.toString(),
                                    fkConstraint.getName(),
                                    primaryKey.getName()));
                } else {
                    String uniqueIndexConstraint = JdbcHelper.getInstance().isUniqueIndex(fkConstraint.getTable(), fkColumnsList, uniqueIndexMap);
                    if (uniqueIndexConstraint != null) {
                        resultForeignKeyList.add(
                                new ResultForeignKey(
                                        "1:1",
                                        fkConstraint.getTable().toString(),
                                        fkColumnsStr.toString(),
                                        fkConstraint.getRelatedTable().toString(),
                                        pkColumnsStr.toString(),
                                        fkConstraint.getName(),
                                        uniqueIndexConstraint.substring(
                                                fkConstraint.getTable().toString().length() + 3)));
                    } else {
                        resultForeignKeyList.add(
                                new ResultForeignKey(
                                        "N:1",
                                        fkConstraint.getTable().toString(),
                                        fkColumnsStr.toString(),
                                        fkConstraint.getRelatedTable().toString(),
                                        pkColumnsStr.toString(),
                                        fkConstraint.getName(),
                                        null));
                    }
                }
            }
            return resultForeignKeyList;
        }
        return null;
    }

    private static List<ResultIndex> getResultIndexes(SortedMap<String, Collection<String>> indexMap) {
        if (indexMap != null) {
            List<ResultIndex> resultIndexes = new ArrayList<ResultIndex>();
            for (String indexConstraint : indexMap.keySet()) {
                String[] indexConstraintAttributes = indexConstraint.split(" - ");

                StringBuilder stringBuilder = new StringBuilder(indexConstraintAttributes[0]);
                Collection<String> indexColumns = indexMap.get(indexConstraint);
                if (indexColumns != null && !indexColumns.isEmpty()) {
                    stringBuilder.append("(");
                    for (String indexColumn : indexColumns) {
                        stringBuilder.append(indexColumn);
                        stringBuilder.append(",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.append(")");
                }
                resultIndexes.add(
                        new ResultIndex(
                                indexConstraintAttributes[0],
                                stringBuilder.toString(),
                                indexConstraintAttributes[1]));
            }
            return resultIndexes;
        }
        return null;
    }
    
}
