package net.sf.l2j.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sf.l2j.tools.mariadb.DatabaseFactory;

public class TableListPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final DatabaseMainFrame mainFrame;
	
	private final DefaultListModel<String> model = new DefaultListModel<>();
	private final JList<String> tables = new JList<>(model);
	
	private Consumer<String> selectionListener;
	
	public TableListPanel(DatabaseMainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(300, 0));
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2A2F3A)));
		
		tables.setCellRenderer(new ZebraListRenderer());
		add(new JScrollPane(tables), BorderLayout.CENTER);
		
		tables.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && selectionListener != null)
			{
				String table = tables.getSelectedValue();
				if (table != null && !table.isEmpty())
					selectionListener.accept(table);
			}
		});
		
		installContextMenu();
		
		reloadTables();
	}
	
	public void setTableSelectionListener(Consumer<String> listener)
	{
		this.selectionListener = listener;
	}
	
	public void reloadTables()
	{
		final String previouslySelected = tables.getSelectedValue();
		model.clear();
		
		new SwingWorker<Void, String>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				try (Statement st = DatabaseFactory.getConnection().createStatement(); var rs = st.executeQuery("SHOW TABLES"))
				{
					while (rs.next())
						publish(rs.getString(1));
				}
				return null;
			}
			
			@Override
			protected void process(List<String> chunks)
			{
				for (String t : chunks)
					model.addElement(t);
			}
			
			@Override
			protected void done()
			{
				try
				{
					get();
					
					// tenta restaurar seleção
					if (previouslySelected != null)
					{
						int idx = model.indexOf(previouslySelected);
						if (idx >= 0)
							tables.setSelectedIndex(idx);
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(TableListPanel.this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}
	
	// =========================
	// Context menu (Right click)
	// =========================
	
	private void installContextMenu()
	{
		final JPopupMenu menu = new JPopupMenu();
		
		final JMenuItem miReload = new JMenuItem("Reload tables");
		miReload.addActionListener(e -> reloadTables());
		
		final JMenuItem miTruncate = new JMenuItem("Clear data (TRUNCATE)");
		miTruncate.addActionListener(e -> truncateSelectedTable());
		
		final JMenuItem miDrop = new JMenuItem("Delete table (DROP)");
		miDrop.addActionListener(e -> dropSelectedTable());
		
		menu.add(miReload);
		menu.addSeparator();
		menu.add(miTruncate);
		menu.add(miDrop);
		
		// habilita/desabilita conforme seleção (quando menu abre)
		menu.addPopupMenuListener(new PopupMenuListener()
		{
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				String t = tables.getSelectedValue();
				boolean ok = (t != null && !t.isBlank());
				miTruncate.setEnabled(ok);
				miDrop.setEnabled(ok);
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
			}
		});
		
		// Mostra menu em qualquer plataforma + seleciona item sob o mouse
		tables.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShow(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShow(e);
			}
			
			private void maybeShow(MouseEvent e)
			{
				if (!e.isPopupTrigger())
					return;
				
				selectRowAtPoint(e.getPoint());
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	private void selectRowAtPoint(Point p)
	{
		int row = tables.locationToIndex(p);
		if (row >= 0)
			tables.setSelectedIndex(row);
	}
	
	private void truncateSelectedTable()
	{
		final String table = tables.getSelectedValue();
		if (table == null || table.isBlank())
			return;
		
		int confirm = JOptionPane.showConfirmDialog(this, "Clear ALL data from table:\n\n" + table + "\n\nThis cannot be undone.", "Confirm TRUNCATE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if (confirm != JOptionPane.YES_OPTION)
			return;
		
		runSqlAsync("TRUNCATE TABLE " + q(table), "TRUNCATE", true);
	}
	
	private void dropSelectedTable()
	{
		final String table = tables.getSelectedValue();
		if (table == null || table.isBlank())
			return;
		
		// confirmação mais forte
		int confirm = JOptionPane.showConfirmDialog(this, "DELETE table permanently:\n\n" + table + "\n\nThis will DROP the table (cannot be undone).", "Confirm DROP", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		
		if (confirm != JOptionPane.YES_OPTION)
			return;
		
		runSqlAsync("DROP TABLE " + q(table), "DROP", true);
	}
	
	private void runSqlAsync(String sql, String title, boolean refreshAfter)
	{
		new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				try (Statement st = DatabaseFactory.getConnection().createStatement())
				{
					st.execute(sql);
				}
				return null;
			}
			
			@Override
			protected void done()
			{
				try
				{
					get();
					JOptionPane.showMessageDialog(TableListPanel.this, title + " OK", title, JOptionPane.INFORMATION_MESSAGE);
					
					if (refreshAfter && mainFrame != null)
					{
						// Atualiza ESQUERDA + DIREITA
						mainFrame.refreshUiAfterDbChange();
					}
					else
					{
						// fallback
						SwingUtilities.invokeLater(() -> reloadTables());
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(TableListPanel.this, e.getMessage(), title + " Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}
	
	private static String q(String ident)
	{
		return "`" + ident.replace("`", "``") + "`";
	}
	
	private static class ZebraListRenderer extends DefaultListCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		private static final Color ROW_A = new Color(0x12, 0x14, 0x18);
		private static final Color ROW_B = new Color(0x10, 0x12, 0x16);
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			var c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!isSelected)
				c.setBackground((index % 2 == 0) ? ROW_A : ROW_B);
			setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
			return c;
		}
	}
}
