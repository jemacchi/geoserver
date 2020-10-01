package org.geoserver.domainmodel.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.geoserver.domainmodel.jdbc.utils.ForeignKeyColumns;
import org.geoserver.domainmodel.jdbc.utils.QueryData;
import org.geoserver.domainmodel.jdbc.utils.ResultForeignKey;
import org.geoserver.domainmodel.jdbc.utils.ResultIndex;
import org.geoserver.domainmodel.jdbc.utils.ResultPrimaryKey;
import org.geotools.jdbc.JDBCDataStore;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
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
                if (tables.getString("TABLE_TYPE").equals("TABLE")) {
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

    public JdbcPrimaryKey getPrimaryKeyColumnsByTable(DatabaseMetaData metaData, JdbcTable table)
            throws Exception {
        ResultSet primaryKeyColumns =
                (table != null)
                        ? metaData.getPrimaryKeys(
                                table.getCatalog(), table.getSchema(), table.getTableName())
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
                                table.getTableName(),
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

    public SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> getForeignKeys(
            DatabaseMetaData metaData, List<JdbcTable> tables) throws Exception {
        if (tables != null) {
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap =
                    new TreeMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>();
            for (JdbcTable table : tables) {
                SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> tableFKMap =
                        getForeignKeysByTable(metaData, table);
                if (tableFKMap != null) {
                    fkMap.putAll(tableFKMap);
                }
            }
            return fkMap;
        }
        return null;
    }

    public SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>
            getForeignKeysByTable(DatabaseMetaData metaData, JdbcTable table) throws Exception {
        ResultSet foreignKeys =
                (table != null)
                        ? metaData.getImportedKeys(
                                table.getCatalog(), table.getSchema(), table.getTableName())
                        : null;
        if (foreignKeys != null && foreignKeys.next()) {
            SortedSetMultimap<JdbcForeignKeyConstraint, ForeignKeyColumns> fkMultimap =
                    TreeMultimap.create();
            JdbcTable fkTable =
                    new JdbcTable(
                            foreignKeys.getString("FKTABLE_CAT"),
                            foreignKeys.getString("FKTABLE_SCHEM"),
                            foreignKeys.getString("FKTABLE_NAME"));
            do {
                String fkConstraintName = foreignKeys.getString("FK_NAME");
                JdbcTable pkTable =
                        new JdbcTable(
                                foreignKeys.getString("PKTABLE_CAT"),
                                foreignKeys.getString("PKTABLE_SCHEM"),
                                foreignKeys.getString("PKTABLE_NAME"));
                JdbcForeignKeyConstraint fkConstraint =
                        new JdbcForeignKeyConstraint(fkTable, fkConstraintName, pkTable);
                ForeignKeyColumns fkColumns =
                        new ForeignKeyColumns(
                                foreignKeys.getString("FKCOLUMN_NAME"),
                                foreignKeys.getString("PKCOLUMN_NAME"));
                fkMultimap.put(fkConstraint, fkColumns);
            } while (foreignKeys.next());
            SortedMap fkMap = new TreeMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>();
            fkMap.putAll(fkMultimap.asMap());
            return fkMap;
        }
        return null;
    }

    public List<QueryData> getReferencedColumnsValues(
            DatabaseMetaData metaData,
            QueryData queryData,
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap,
            int depth)
            throws Exception {
        if (queryData != null
                && queryData.getTable() != null
                && queryData.getTable().getTableName() != null
                && queryData.getColumnNames() != null
                && queryData.getColumnNames().length > 0
                && queryData.getValueList() != null
                && !queryData.getValueList().isEmpty()) {
            StringBuilder sql = new StringBuilder("select * from ");
            sql.append(queryData.getTable().toString());
            sql.append(" where ");

            StringBuilder sqlCondition = new StringBuilder("(");
            for (String columnName : queryData.getColumnNames()) {
                sqlCondition.append(columnName);
                sqlCondition.append(" = ? and ");
            }
            sqlCondition.delete(sqlCondition.length() - 5, sqlCondition.length());
            sqlCondition.append(") or ");

            for (String[] values : queryData.getValueList()) {
                boolean areFilledReferencingColumnsValues = isFilledArray(values);
                if (areFilledReferencingColumnsValues) {
                    sql.append(sqlCondition);
                }
            }
            if (" or ".equals(sql.substring(sql.length() - 4, sql.length()))) {
                sql.delete(sql.length() - 4, sql.length());
            }

            PreparedStatement statement = metaData.getConnection().prepareStatement(sql.toString());
            StringBuilder sqlParams = new StringBuilder();
            int i = 0;
            for (String[] values : queryData.getValueList()) {
                boolean areFilledReferencingColumnsValues = isFilledArray(values);
                if (areFilledReferencingColumnsValues) {
                    sqlParams.append("[");
                    for (String value : values) {
                        statement.setString(i + 1, value);
                        sqlParams.append(value);
                        sqlParams.append(",");
                        i++;
                    }
                    sqlParams.deleteCharAt(sqlParams.length() - 1);
                    sqlParams.append("]");
                }
            }
            System.out.println("depth " + depth + ": " + sql.toString() + sqlParams.toString());
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                SortedSetMultimap<JdbcForeignKeyConstraint, ForeignKeyColumns> tableFkMultimap =
                        TreeMultimap.create();
                for (JdbcForeignKeyConstraint fKConstraint : fkMap.keySet()) {
                    if (queryData.getTable().equals(fKConstraint.getTable())) {
                        tableFkMultimap.putAll(fKConstraint, fkMap.get(fKConstraint));
                    }
                }
                List<QueryData> nextQueryDataList = new ArrayList<QueryData>();
                Map<String, JdbcTable> constraintTableMap = new HashMap<String, JdbcTable>();
                Map<String, String[]> constraintColumnsMap = new HashMap<String, String[]>();
                ListMultimap<String, String[]> constraintValuesMultimap =
                        ArrayListMultimap.create();
                while (rs.next()) {
                    for (JdbcForeignKeyConstraint tableFKConstraint : tableFkMultimap.keySet()) {
                        SortedSet<ForeignKeyColumns> tableFkColumnsList =
                                tableFkMultimap.get(tableFKConstraint);
                        String[] relatedTableColumnNames = new String[tableFkColumnsList.size()];
                        String[] relatedTableColumnValues = new String[tableFkColumnsList.size()];
                        i = 0;
                        for (ForeignKeyColumns tableFKColumns : tableFkColumnsList) {
                            relatedTableColumnNames[i] = tableFKColumns.getRelatedTableColumnName();
                            relatedTableColumnValues[i] =
                                    rs.getString(tableFKColumns.getTableColumnName());
                            i++;
                        }
                        constraintTableMap.put(
                                tableFKConstraint.getConstraintName(),
                                tableFKConstraint.getRelatedTable());
                        constraintColumnsMap.put(
                                tableFKConstraint.getConstraintName(), relatedTableColumnNames);
                        constraintValuesMultimap.put(
                                tableFKConstraint.getConstraintName(), relatedTableColumnValues);
                    }
                }
                for (String constraint : constraintTableMap.keySet()) {
                    JdbcTable table = constraintTableMap.get(constraint);
                    String[] columNames = constraintColumnsMap.get(constraint);
                    List<String[]> columValues = constraintValuesMultimap.get(constraint);
                    QueryData nextQueryData = new QueryData(table, columNames, columValues);
                    nextQueryDataList.add(nextQueryData);
                }
                return nextQueryDataList;
            }
        }
        return null;
    }

    public List<QueryData> getReferencingColumnsValues(
            DatabaseMetaData metaData, QueryData queryData, Map<JdbcTable, JdbcPrimaryKey> pkMap, int depth)
            throws Exception {
        if (queryData != null
                && queryData.getTable() != null
                && queryData.getTable().getTableName() != null
                && queryData.getColumnNames() != null
                && queryData.getColumnNames().length > 0
                && queryData.getValueList() != null
                && !queryData.getValueList().isEmpty()) {
            StringBuilder sql = new StringBuilder("select * from ");
            sql.append(queryData.getTable().toString());
            sql.append(" where ");

            StringBuilder sqlCondition = new StringBuilder("(");
            for (String columnName : queryData.getColumnNames()) {
                sqlCondition.append(columnName);
                sqlCondition.append(" = ? and ");
            }
            sqlCondition.delete(sqlCondition.length() - 5, sqlCondition.length());
            sqlCondition.append(") or ");

            for (String[] values : queryData.getValueList()) {
                boolean areFilledReferencingColumnsValues = isFilledArray(values);
                if (areFilledReferencingColumnsValues) {
                    sql.append(sqlCondition);
                }
            }
            if (" or ".equals(sql.substring(sql.length() - 4, sql.length()))) {
                sql.delete(sql.length() - 4, sql.length());
            }

            PreparedStatement statement = metaData.getConnection().prepareStatement(sql.toString());
            StringBuilder sqlParams = new StringBuilder();
            int i = 0;
            for (String[] values : queryData.getValueList()) {
                boolean areFilledReferencingColumnsValues = isFilledArray(values);
                if (areFilledReferencingColumnsValues) {
                    sqlParams.append("[");
                    for (String value : values) {
                        statement.setString(i + 1, value);
                        sqlParams.append(value);
                        sqlParams.append(",");
                        i++;
                    }
                    sqlParams.deleteCharAt(sqlParams.length() - 1);
                    sqlParams.append("]");
                }
            }
            System.out.println("depth " + depth + ": " + sql.toString() + sqlParams.toString());
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                JdbcPrimaryKey queryPrimaryKey = pkMap.get(queryData.getTable());
                List<String[]> queryPKValuesList = new ArrayList<String[]>();
                while (rs.next()) {
                    String[] queryPKValues = new String[queryPrimaryKey.getColumnNames().size()];
                    i = 0;
                    for (String queryPKColumnName : queryPrimaryKey.getColumnNames()) {
                        queryPKValues[i] = rs.getString(queryPKColumnName);
                        i++;
                    }
                    queryPKValuesList.add(queryPKValues);
                }
                SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> inversedFkMap =
                        getInversedForeignKeysByTable(metaData, queryData.getTable());
                if (inversedFkMap != null) {
                    List<QueryData> nextQueryDataList = new ArrayList<QueryData>();
                    for (JdbcForeignKeyConstraint inversedFKConstraint : inversedFkMap.keySet()) {
                        if (queryPrimaryKey
                                .getConstraintName()
                                .equals(inversedFKConstraint.getConstraintName())) {
                            Collection<ForeignKeyColumns> inversedFkColumnsList =
                                    inversedFkMap.get(inversedFKConstraint);
                            String[] relatedTableColumnNames =
                                    new String[queryPrimaryKey.getColumnNames().size()];

                            for (ForeignKeyColumns inversedFkColumns : inversedFkColumnsList) {
                                relatedTableColumnNames[
                                                queryPrimaryKey
                                                        .getColumnOrderMap()
                                                        .get(
                                                                inversedFkColumns
                                                                        .getTableColumnName())] =
                                        inversedFkColumns.getRelatedTableColumnName();
                            }
                            QueryData nextQueryData =
                                    new QueryData(
                                            inversedFKConstraint.getRelatedTable(),
                                            relatedTableColumnNames,
                                            queryPKValuesList);
                            nextQueryDataList.add(nextQueryData);
                        }
                    }
                    return nextQueryDataList;
                }
            }
        }
        return null;
    }

    public SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>>
            getInversedForeignKeysByTable(DatabaseMetaData metaData, JdbcTable table) throws Exception {
        ResultSet foreignKeys =
                (table != null)
                        ? metaData.getExportedKeys(
                                table.getCatalog(), table.getSchema(), table.getTableName())
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
            SortedMap<JdbcForeignKeyConstraint, Collection<ForeignKeyColumns>> fkMap,
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap,
            SortedMap<String, Collection<String>> uniqueIndexMap) {
        if (fkMap != null) {
            List<ResultForeignKey> resultForeignKeyList = new ArrayList<ResultForeignKey>();
            for (JdbcForeignKeyConstraint fkConstraint : fkMap.keySet()) {
                Collection<ForeignKeyColumns> fkColumnsList = fkMap.get(fkConstraint);

                StringBuilder fkColumnsStr = new StringBuilder();
                StringBuilder pkColumnsStr = new StringBuilder();
                if (fkColumnsList != null && !fkColumnsList.isEmpty()) {
                    for (ForeignKeyColumns fkColumns : fkColumnsList) {
                        fkColumnsStr.append(fkColumns.getTableColumnName());
                        fkColumnsStr.append(",");
                        pkColumnsStr.append(fkColumns.getRelatedTableColumnName());
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
                                    fkConstraint.getConstraintName(),
                                    primaryKey.getConstraintName()));
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
                                        fkConstraint.getConstraintName(),
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
                                        fkConstraint.getConstraintName(),
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
            Collection<ForeignKeyColumns> fkColumnsList,
            SortedMap<JdbcTable, JdbcPrimaryKey> pkMap) {
        JdbcPrimaryKey primaryKey = pkMap.get(table);
        for (String columnName : primaryKey.getColumnNames()) {
            boolean containsPkColumnName = false;
            for (ForeignKeyColumns fkColumns : fkColumnsList) {
                if (columnName.equals(fkColumns.getTableColumnName())) {
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
            Collection<ForeignKeyColumns> fkColumnsList,
            SortedMap<String, Collection<String>> uniqueIndexMap) {
        indexLoop:
        for (String uniqueIndexConstraint : uniqueIndexMap.keySet()) {
            if (uniqueIndexConstraint.startsWith(table.toString() + " - ")) {
                Collection<String> uniqueIndexColumns = uniqueIndexMap.get(uniqueIndexConstraint);
                for (String uniqueIndexColumn : uniqueIndexColumns) {
                    boolean containsUniqueIndexColumn = false;
                    for (ForeignKeyColumns fkColumns : fkColumnsList) {
                        if (uniqueIndexColumn.equals(fkColumns.getTableColumnName())) {
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
