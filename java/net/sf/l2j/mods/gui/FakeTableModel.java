package net.sf.l2j.mods.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

public class FakeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMNS =
	{
		"",
		"Online",
		"Name",
		"Class",
		"Lvl",
		"State"
	};
	
	private final List<FakeAdminService.FakeRow> data = new ArrayList<>();
	private final Set<Integer> pinned = new HashSet<>();
	
	public void setData(List<FakeAdminService.FakeRow> list)
	{
		data.clear();
		data.addAll(list);
		fireTableDataChanged();
	}
	
	public FakeAdminService.FakeRow getAt(int row)
	{
		if (row < 0 || row >= data.size())
			return null;
		return data.get(row);
	}
	
	public boolean isPinned(int objectId)
	{
		return pinned.contains(objectId);
	}
	
	public void pin(int objectId)
	{
		pinned.add(objectId);
		fireTableDataChanged();
	}
	
	public void unpin(int objectId)
	{
		pinned.remove(objectId);
		fireTableDataChanged();
	}
	
	public Set<Integer> getPinnedIds()
	{
		return new HashSet<>(pinned);
	}
	
	public boolean hasPinned()
	{
		return !pinned.isEmpty();
	}
	
	@Override
	public int getRowCount()
	{
		return data.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return COLUMNS.length;
	}
	
	@Override
	public String getColumnName(int col)
	{
		return COLUMNS[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col)
	{
		if (col == 0)
			return Boolean.class; // pinned checkbox
		if (col == 1)
			return Boolean.class; // online badge renderer (boolean)
		if (col == 4)
			return Integer.class; // lvl
		return Object.class;
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		FakeAdminService.FakeRow r = data.get(row);
		
		switch (col)
		{
			case 0:
				return pinned.contains(r.objectId);
			case 1:
				return r.online;
			case 2:
				return r.name;
			case 3:
				return r.classId != null ? r.classId.name().replace("_", " ") : "UNKNOWN";
			case 4:
				return r.level;
			case 5:
				return r.state != null ? r.state : (r.online ? "ONLINE" : "OFFLINE");
		}
		return "";
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return col == 0; // só pinned
	}
	
	public void setPinnedForAllRows(boolean value)
	{
		boolean changed = false;
		for (FakeAdminService.FakeRow r : data)
		{
			if (r == null)
				continue;
			changed |= value ? pinned.add(r.objectId) : pinned.remove(r.objectId);
		}
		if (changed)
			fireTableDataChanged();
	}

	public void setPinnedForIds(Iterable<Integer> ids, boolean value)
	{
		boolean changed = false;
		for (Integer id : ids)
		{
			if (id == null)
				continue;
			changed |= value ? pinned.add(id) : pinned.remove(id);
		}
		if (changed)
			fireTableDataChanged();
	}
	
	public int getPinnedCount()
	{
		return pinned.size();
	}
	
	public void clearPins()
	{
		if (pinned.isEmpty())
			return;
		pinned.clear();
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int col)
	{
		if (col != 0)
			return;
		
		FakeAdminService.FakeRow r = getAt(row);
		if (r == null)
			return;
		
		boolean v = (aValue instanceof Boolean) ? (Boolean) aValue : false;
		
		if (v)
			pinned.add(r.objectId);
		else
			pinned.remove(r.objectId);
		
		fireTableCellUpdated(row, col);
	}
}
