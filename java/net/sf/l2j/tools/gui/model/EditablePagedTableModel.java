package net.sf.l2j.tools.gui.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public final class EditablePagedTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    private final String tableName;
    private final List<String> columns;
    private final List<Class<?>> columnTypes;
    private final List<Object[]> rows;

    private final List<String> pkColumns;
    private final int[] pkIndexes; // indexes dentro de "columns"
    private final Map<String, Integer> colIndexMap;

    // ✅ permite editar mesmo sem PK (unsafe: match linha inteira + LIMIT 1)
    private final boolean allowUnsafeEdit;

    public interface ConnectionProvider
    {
        Connection get() throws SQLException;
    }

    private final ConnectionProvider connectionProvider;

    public EditablePagedTableModel(
        String tableName,
        List<String> columns,
        List<Class<?>> columnTypes,
        List<Object[]> rows,
        List<String> pkColumns,
        Map<String, Integer> colIndexMap,
        boolean allowUnsafeEdit,
        ConnectionProvider connectionProvider)
    {
        this.tableName = tableName;
        this.columns = new ArrayList<>(columns);
        this.columnTypes = new ArrayList<>(columnTypes);
        this.rows = new ArrayList<>(rows);

        this.pkColumns = new ArrayList<>(pkColumns);
        this.colIndexMap = colIndexMap;
        this.allowUnsafeEdit = allowUnsafeEdit;
        this.connectionProvider = connectionProvider;

        this.pkIndexes = new int[this.pkColumns.size()];
        for (int i = 0; i < this.pkColumns.size(); i++)
        {
            Integer idx = this.colIndexMap.get(this.pkColumns.get(i));
            this.pkIndexes[i] = (idx == null) ? -1 : idx.intValue();
        }
    }

    @Override
    public int getRowCount()
    {
        return rows.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.size();
    }

    @Override
    public String getColumnName(int column)
    {
        return columns.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return columnTypes.get(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return rows.get(rowIndex)[columnIndex];
    }

   
    public boolean isEditable()
    {
        return hasPrimaryKey() || allowUnsafeEdit;
    }

    public boolean hasPrimaryKey()
    {
        return !pkColumns.isEmpty() && pkIndexesValid();
    }

    public boolean isUnsafeEditEnabled()
    {
        return allowUnsafeEdit && !hasPrimaryKey();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return isEditable();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if (!isEditable())
            return;

        Object[] row = rows.get(rowIndex);
        Object old = row[columnIndex];

        if (equalsDb(old, aValue))
            return;

        // ✅ snapshot do estado da linha antes de alterar (para WHERE completo no unsafe)
        Object[] snapshotBefore = row.clone();

        try
        {
            updateCell(rowIndex, columnIndex, aValue, snapshotBefore);

            // aplica no modelo só se o UPDATE deu certo
            row[columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void updateCell(int rowIndex, int columnIndex, Object newValue, Object[] oldRowSnapshot) throws SQLException
    {
        boolean usePk = hasPrimaryKey();

        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(q(tableName))
           .append(" SET ").append(q(columns.get(columnIndex))).append("=? WHERE ");

        if (usePk)
        {
            // UPDATE ... WHERE pk1=? AND pk2=? ...
            for (int i = 0; i < pkColumns.size(); i++)
            {
                if (i > 0) sql.append(" AND ");
                sql.append(q(pkColumns.get(i))).append("=?");
            }
        }
        else
        {
            if (!allowUnsafeEdit)
                throw new SQLException("Table has no primary key and unsafe edit is disabled.");

            // UPDATE ... WHERE col1 <=> ? AND col2 <=> ? ... LIMIT 1
            sql.append(buildWhereAllColumnsNullSafe()).append(" LIMIT 1");
        }

        try (Connection con = connectionProvider.get();
             PreparedStatement ps = con.prepareStatement(sql.toString()))
        {
            int p = 1;
            ps.setObject(p++, newValue);

            if (usePk)
            {
                Object[] row = rows.get(rowIndex);
                for (int i = 0; i < pkIndexes.length; i++)
                {
                    int idx = pkIndexes[i];
                    ps.setObject(p++, idx >= 0 ? row[idx] : null);
                }
            }
            else
            {
                // usa snapshot anterior como referência exata da linha antes de editar
                for (int i = 0; i < columns.size(); i++)
                    ps.setObject(p++, oldRowSnapshot[i]);
            }

            int affected = ps.executeUpdate();
            if (affected <= 0)
                throw new SQLException("No rows updated. The row may have changed; reload and try again.");
        }
    }

    private String buildWhereAllColumnsNullSafe()
    {
        StringBuilder sb = new StringBuilder(columns.size() * 16);
        for (int i = 0; i < columns.size(); i++)
        {
            if (i > 0) sb.append(" AND ");
            sb.append(q(columns.get(i))).append(" <=> ?");
        }
        return sb.toString();
    }

    private boolean pkIndexesValid()
    {
        for (int idx : pkIndexes)
        {
            if (idx < 0)
                return false;
        }
        return true;
    }

    private static String q(String ident)
    {
        return "`" + ident.replace("`", "``") + "`";
    }

    private static boolean equalsDb(Object a, Object b)
    {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return String.valueOf(a).equals(String.valueOf(b));
    }

    // ========= Helpers pro DataViewPanel (delete unsafe / etc) =========

    public String getTableName()
    {
        return tableName;
    }

    public List<String> getPrimaryKeys()
    {
        return Collections.unmodifiableList(pkColumns);
    }

    public List<String> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }

    public int getColumnIndex(String columnName)
    {
        Integer idx = colIndexMap.get(columnName);
        if (idx == null)
            throw new IllegalArgumentException("Unknown column: " + columnName);
        return idx.intValue();
    }

    public Object[] getRowCopy(int modelRow)
    {
        return rows.get(modelRow).clone();
    }
}
