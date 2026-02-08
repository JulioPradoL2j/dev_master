package net.sf.l2j.tools.gui.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class ResultSetTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private final List<String> columns = new ArrayList<>();
	private final List<Class<?>> columnTypes = new ArrayList<>();
	private final List<Object[]> rows = new ArrayList<>();
	
	private ResultSetTableModel()
	{
		// use factory method
	}
	
	public static ResultSetTableModel from(final ResultSet rs) throws SQLException
	{
		final ResultSetTableModel model = new ResultSetTableModel();
		model.load(rs);
		return model;
	}
	
	private void load(final ResultSet rs) throws SQLException
	{
		columns.clear();
		columnTypes.clear();
		rows.clear();
		
		final ResultSetMetaData md = rs.getMetaData();
		final int columnCount = md.getColumnCount();
		
		// headers/types
		for (int i = 1; i <= columnCount; i++)
		{
			columns.add(md.getColumnLabel(i));
			columnTypes.add(resolveType(md.getColumnClassName(i)));
		}
		
		// rows
		while (rs.next())
		{
			final Object[] row = new Object[columnCount];
			for (int i = 1; i <= columnCount; i++)
				row[i - 1] = rs.getObject(i);
			
			rows.add(row);
		}
	}
	
	private static Class<?> resolveType(final String className)
	{
		if (className == null || className.isEmpty())
			return Object.class;
		
		try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException | LinkageError e)
		{
			return Object.class;
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
	public String getColumnName(final int column)
	{
		return columns.get(column);
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		return columnTypes.get(columnIndex);
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		return rows.get(rowIndex)[columnIndex];
	}
	
	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex)
	{
		return false; // read-only por enquanto
	}
}
