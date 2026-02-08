package net.sf.l2j.mods.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class OnlineBadgeRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		boolean online = (value instanceof Boolean) ? (Boolean) value : false;
		
		setText(online ? "ONLINE" : "OFFLINE");
		setHorizontalAlignment(SwingConstants.CENTER);
		setFont(getFont().deriveFont(Font.BOLD, 11f));
		
		if (isSelected)
		{
		    setForeground(Color.WHITE);
		    setBackground(new Color(60, 75, 115));
		    return this;
		}

		
		if (online)
		{
			setForeground(new Color(210, 255, 210));
			setBackground(new Color(25, 45, 28));
		}
		else
		{
			setForeground(new Color(230, 230, 230));
			setBackground(new Color(45, 45, 45));
		}
		
		return this;
	}
}
