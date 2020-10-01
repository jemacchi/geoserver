package org.geoserver.domainmodel.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geoserver.domainmodel.jdbc.utils.ResultColumn;
import org.geoserver.domainmodel.jdbc.utils.ResultForeignKey;
import org.geoserver.domainmodel.jdbc.utils.ResultIndex;
import org.geoserver.domainmodel.jdbc.utils.ResultPrimaryKey;
import org.geotools.jdbc.JDBCDataStore;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * JDBC utilities singleton class. It encapsulates a Geotools JDBCDataStore instance in order to use some
 * useful methods and extends functionallities based on JDBC API use, required for AppSchemaSmart implementation.
 *   
 * @author jmacchi
 *
 */
public class JdbcUtilities {
	
    private static JdbcUtilities single_instance = null; 
  
    private JDBCDataStore jdbcDataStore; 
  
    private JdbcUtilities() 
    { 
        jdbcDataStore = new JDBCDataStore(); 
    } 
  
    public static JdbcUtilities getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new JdbcUtilities(); 
        return single_instance; 
    } 
	
	private static List<JdbcTable> getListOfTablesFromResultSet(ResultSet tables) throws Exception {
		if (tables != null) {
            List<JdbcTable> tableList = new ArrayList<JdbcTable>();
            while (tables.next()) {
                if (tables.getString("TABLE_TYPE")!=null && tables.getString("TABLE_TYPE").equals("TABLE")) {
	            	tableList.add(
	                        new JdbcTable(
	                                tables.getString("TABLE_CAT"),
	                                tables.getString("TABLE_SCHEM"),
	                                tables.getString("TABLE_NAME")));
                }
            }
            return tableList;
        }
        return null;
	}
	
	public List<JdbcTable> getSchemaTables(DatabaseMetaData metaData, String schema) throws Exception {
        ResultSet tables = metaData.getTables(null, jdbcDataStore.escapeNamePattern(metaData, schema), "%", null);
        return getListOfTablesFromResultSet(tables);
    }
    public List<JdbcTable> getTables(DatabaseMetaData metaData) throws Exception {
        ResultSet tables = metaData.getTables(null, null, "%", null);
        return getListOfTablesFromResultSet(tables);
    }
    
    public SortedMap<JdbcTable, JdbcPrimaryKey> getPrimaryKeyColumns(
            DatabaseMetaData metaData, List<JdbcTable> tables) throws Exception {
        if (tables != null) {
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap = new TreeMap<JdbcTable, JdbcPrimaryKey>();
            for (JdbcTable table : tables) {
                JdbcPrimaryKey primaryKey = getPrimaryKeyColumnsByTable(metaData, table);
                if (primaryKey != null) {
                    pkMap.put(table, primaryKey);
                }
            }
            return pkMap;
        }
        return null;
    }
    
    public SortedMap<JdbcTable, List<JdbcColumn>> getColumns(
            DatabaseMetaData metaData, List<JdbcTable> tables) throws Exception {
        if (tables != null) {
            SortedMap<JdbcTable, List<JdbcColumn>> cMap = new TreeMap<JdbcTable, List<JdbcColumn>>();
            for (JdbcTable table : tables) {
                List<JdbcColumn> columnList = getColumnsByTable(metaData, table);
	        	if (columnList != null) {
	                cMap.put(table, columnList);
	            }
            }
            return cMap;
        }
        return null;
    }

    public JdbcPrimaryKey getPrimaryKeyColumnsByTable(DatabaseMetaData metaData, JdbcTable table)
            throws Exception {
        ResultSet primaryKeyColumns =
                (table != null)
                        ? metaData.getPrimaryKeys(
                                table.getCatalog(), table.getSchema(), table.getName())
                        : null;
        if (primaryKeyColumns != null && primaryKeyColumns.next()) {
            JdbcTable pkTable =
                    new JdbcTable(
                            primaryKeyColumns.getString("TABLE_CAT"),
                            primaryKeyColumns.getString("TABLE_SCHEM"),
                            primaryKeyColumns.getString("TABLE_NAME"));
            String pkConstraintName = primaryKeyColumns.getString("PK_NAME");
            List<String> pkColumnNames = new ArrayList<String>();
            do {
                pkColumnNames.add(primaryKeyColumns.getString("COLUMN_NAME"));
            } while (primaryKeyColumns.next());
            JdbcPrimaryKey primaryKey = new JdbcPrimaryKey(pkTable, pkConstraintName, pkColumnNames);
            return primaryKey;
        }
        return null;
    }
    
    public List<JdbcColumn> getColumnsByTable(DatabaseMetaData metaData, JdbcTable table)
            throws Exception {
        ResultSet columns =
                (table != null)
                        ? metaData.getColumns(table.getCatalog(), table.getSchema(), table.getName(),"%")
                        : null;
        if (columns != null && columns.next()) {
            JdbcTable aTable = new JdbcTable(columns.getString("TABLE_CAT"), columns.getString("TABLE_SCHEM"),columns.getString("TABLE_NAME"));
            List<JdbcColumn> columnsList = new ArrayList<JdbcColumn>();
            do {
                JdbcColumn aColumn = new JdbcColumn(aTable, columns.getString("COLUMN_NAME"), columns.getString("TYPE_NAME"));
                columnsList.add(aColumn);
                
            } while (columns.next());
            
            return columnsList;
        }
        return null;
    }

    public JdbcColumn getColumnFromTable(DatabaseMetaData metaData, JdbcTable table, String columnName)
            throws Exception {
        ResultSet columns =
                (table != null)
                        ? metaData.getColumns(table.getCatalog(), table.getSchema(), table.getName(),columnName)
                        : null;
        if (columns != null && columns.next()) {
            JdbcTable aTable = new JdbcTable(columns.getString("TABLE_CAT"), columns.getString("TABLE_SCHEM"),columns.getString("TABLE_NAME"));
            JdbcColumn aColumn = new JdbcColumn(aTable, columns.getString("COLUMN_NAME"), columns.getString("TYPE_NAME"));
            return aColumn;
        }
        return null;
    }
    
    
    public SortedMap<String, Collection<String>> getIndexColumns(
            DatabaseMetaData metaData, List<JdbcTable> tables, boolean unique, boolean approximate)
            throws Exception {
        if (tables != null) {
            SortedMap<String, Collection<String>> indexMap =
                    new TreeMap<String, Collection<String>>();
            for (JdbcTable table : tables) {
                SortedMap<String, Collection<String>> tableIndexMap =
                        getIndexesByTable(metaData, table, unique, approximate);
                if (tableIndexMap != null) {
                    indexMap.putAll(tableIndexMap);
                }
            }
            return indexMap;
        }
        return null;
    }

    public SortedMap<String, Collection<String>> getIndexesByTable(
            DatabaseMetaData metaData, JdbcTable table, boolean unique, boolean approximate)
            throws Exception {
        ResultSet indexColumns =
                (table != null)
                        ? metaData.getIndexInfo(
                                table.getCatalog(),
                                table.getSchema(),
                                table.getName(),
                                unique,
                                approximate)
                        : null;
        if (indexColumns != null && indexColumns.next()) {
            SortedSetMultimap<String, String> indexMultimap = TreeMultimap.create();
            JdbcTable tableAux =
                    new JdbcTable(
                            indexColumns.getString("TABLE_CAT"),
                            indexColumns.getString("TABLE_SCHEM"),
                            indexColumns.getString("TABLE_NAME"));
            do {

                String indexConstraintName = indexColumns.getString("INDEX_NAME");
                JdbcIndexConstraint indexConstraint =
                        new JdbcIndexConstraint(tableAux, indexConstraintName);
                String indexColumnName = indexColumns.getString("COLUMN_NAME");
                indexMultimap.put(indexConstraint.toString(), indexColumnName);
            } while (indexColumns.next());
            SortedMap indexMap = new TreeMap<String, Collection<String>>();
            indexMap.putAll(indexMultimap.asMap());
            return indexMap;
        }
        return null;
    }

    public SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> getForeignKeys(
            DatabaseMetaData metaData, List<JdbcTable> tables) throws Exception {
        if (tables != null) {
            SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap =
                    new TreeMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>>();
            for (JdbcTable table : tables) {
                SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> tableFKMap =
                        getForeignKeysByTable(metaData, table);
                if (tableFKMap != null) {
                    fkMap.putAll(tableFKMap);
                }
            }
            return fkMap;
        }
        return null;
    }

    public SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>>
            getForeignKeysByTable(DatabaseMetaData metaData, JdbcTable table) throws Exception {
        ResultSet foreignKeys =
                (table != null)
                        ? metaData.getImportedKeys(
                                table.getCatalog(), table.getSchema(), table.getName())
                        : null;
        if (foreignKeys != null && foreignKeys.next()) {
            SortedSetMultimap<JdbcForeignKeyConstraint, JdbcForeignKeyColumn> fkMultimap = TreeMultimap.create();
            JdbcTable fkTable =
                    new JdbcTable(
                            foreignKeys.getString("FKTABLE_CAT"),
                            foreignKeys.getString("FKTABLE_SCHEM"),
                            foreignKeys.getString("FKTABLE_NAME"));
            //  Get FKColumn from table just in order to get datatype
            JdbcColumn aColumn = this.getColumnFromTable(metaData, table, foreignKeys.getString("FKCOLUMN_NAME"));
            String columnType = aColumn.getType();
            do {
                String fkConstraintName = foreignKeys.getString("FK_NAME");
                JdbcTable pkTable =
                        new JdbcTable(
                                foreignKeys.getString("PKTABLE_CAT"),
                                foreignKeys.getString("PKTABLE_SCHEM"),
                                foreignKeys.getString("PKTABLE_NAME"));
                JdbcForeignKeyConstraint fkConstraint = new JdbcForeignKeyConstraint(fkTable, fkConstraintName, pkTable);
                JdbcForeignKeyColumn fkColumn = new JdbcForeignKeyColumn(fkTable, foreignKeys.getString("FKCOLUMN_NAME"), columnType, new JdbcColumn(pkTable, foreignKeys.getString("PKCOLUMN_NAME"),columnType));
                fkMultimap.put(fkConstraint, fkColumn);
            } while (foreignKeys.next());
            SortedMap fkMap = new TreeMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>>();
            fkMap.putAll(fkMultimap.asMap());
            return fkMap;
        }
        return null;
    }

    /*public SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>
            getInversedForeignKeysByTable(DatabaseMetaData metaData, JdbcTable table) throws Exception {
        ResultSet foreignKeys =
                (table != null)
                        ? metaData.getExportedKeys(
                                table.getCatalog(), table.getSchema(), table.getName())
                        : null;
        if (foreignKeys != null && foreignKeys.next()) {
            SortedSetMultimap<JdbcForeignKeyConstraint, ForeignKeyColumns> inversedFkMultimap =
                    TreeMultimap.create();
            do {
                JdbcTable pkTable =
                        new JdbcTable(
                                foreignKeys.getString("PKTABLE_CAT"),
                                foreignKeys.getString("PKTABLE_SCHEM"),
                                foreignKeys.getString("PKTABLE_NAME"));
                String pkConstraintName = foreignKeys.getString("PK_NAME");
                JdbcTable fkTable =
                        new JdbcTable(
                                foreignKeys.getString("FKTABLE_CAT"),
                                foreignKeys.getString("FKTABLE_SCHEM"),
                                foreignKeys.getString("FKTABLE_NAME"));
                JdbcForeignKeyConstraint pkConstraint =
                        new JdbcForeignKeyConstraint(pkTable, pkConstraintName, fkTable);
                ForeignKeyColumns fkColumns =
                        new ForeignKeyColumns(
                                foreignKeys.getString("PKCOLUMN_NAME"),
                                foreignKeys.getString("FKCOLUMN_NAME"));
                inversedFkMultimap.put(pkConstraint, fkColumns);
            } while (foreignKeys.next());
            SortedMap inversedFkMap =
                    new TreeMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>();
            inversedFkMap.putAll(inversedFkMultimap.asMap());
            return inversedFkMap;
        }
        return null;
    }*/
    
    public List<ResultColumn> getResultColumns(SortedMap<JdbcTable, List<JdbcColumn>> cMap) {
        if (cMap != null) {
            List<ResultColumn> cList = new ArrayList<ResultColumn>();
            for (Map.Entry<JdbcTable, List<JdbcColumn>> cMapEntry : cMap.entrySet()) {
            	Iterator<JdbcColumn> cIterator = cMapEntry.getValue().iterator();
            	while (cIterator.hasNext()) {
            		ResultColumn resultC = new ResultColumn(cIterator.next());
                    cList.add(resultC);
            	}
            }
            return cList;
        }
        return null;
    }

    public List<ResultPrimaryKey> getResultPrimaryKeys(SortedMap<JdbcTable, JdbcPrimaryKey> pkMap) {
        if (pkMap != null) {
            List<ResultPrimaryKey> pkList = new ArrayList<ResultPrimaryKey>();
            for (Map.Entry<JdbcTable, JdbcPrimaryKey> pkMapEntry : pkMap.entrySet()) {
                ResultPrimaryKey resultPk = new ResultPrimaryKey(pkMapEntry.getValue());
                pkList.add(resultPk);
            }
            return pkList;
        }
        return null;
    }

    public List<ResultForeignKey> getResultForeignKeys(
            SortedMap<JdbcForeignKeyConstraint, Collection<JdbcForeignKeyColumn>> fkMap,
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap,
            SortedMap<String, Collection<String>> uniqueIndexMap) {
        if (fkMap != null) {
            List<ResultForeignKey> resultForeignKeyList = new ArrayList<ResultForeignKey>();
            for (JdbcForeignKeyConstraint fkConstraint : fkMap.keySet()) {
                Collection<JdbcForeignKeyColumn> fkColumnsList = fkMap.get(fkConstraint);

                StringBuilder fkColumnsStr = new StringBuilder();
                StringBuilder pkColumnsStr = new StringBuilder();
                if (fkColumnsList != null && !fkColumnsList.isEmpty()) {
                    for (JdbcForeignKeyColumn fkColumns : fkColumnsList) {
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
                JdbcPrimaryKey primaryKey = isPrimaryKey(fkConstraint.getTable(), fkColumnsList, pkMap);
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
                    String uniqueIndexConstraint =
                            isUniqueIndex(fkConstraint.getTable(), fkColumnsList, uniqueIndexMap);
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

    public List<ResultIndex> getResultIndexes(
            SortedMap<String, Collection<String>> indexMap) {
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

    public JdbcPrimaryKey isPrimaryKey(
            JdbcTable table,
            Collection<JdbcForeignKeyColumn> fkColumnsList,
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap) {
        JdbcPrimaryKey primaryKey = pkMap.get(table);
        for (String columnName : primaryKey.getColumnNames()) {
            boolean containsPkColumnName = false;
            for (JdbcForeignKeyColumn fkColumns : fkColumnsList) {
                if (columnName.equals(fkColumns.getName())) {
                    containsPkColumnName = true;
                }
            }
            if (!containsPkColumnName) {
                return null;
            }
        }
        return primaryKey;
    }

    public String isUniqueIndex(
            JdbcTable table,
            Collection<JdbcForeignKeyColumn> fkColumnsList,
            SortedMap<String, Collection<String>> uniqueIndexMap) {
        indexLoop:
        for (String uniqueIndexConstraint : uniqueIndexMap.keySet()) {
            if (uniqueIndexConstraint.startsWith(table.toString() + " - ")) {
                Collection<String> uniqueIndexColumns = uniqueIndexMap.get(uniqueIndexConstraint);
                for (String uniqueIndexColumn : uniqueIndexColumns) {
                    boolean containsUniqueIndexColumn = false;
                    for (JdbcForeignKeyColumn fkColumns : fkColumnsList) {
                        if (uniqueIndexColumn.equals(fkColumns.getName())) {
                            containsUniqueIndexColumn = true;
                        }
                    }
                    if (!containsUniqueIndexColumn) {
                        continue indexLoop;
                    }
                }
                return uniqueIndexConstraint;
            }
        }
        return null;
    }

    private <T> boolean isFilledArray(T[] array) {
        if (array == null) {
            for (T object : array) {
                if (object == null) {
                    return false;
                }
            }
        }
        return true;
    }

}
