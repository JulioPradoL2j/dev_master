package net.sf.l2j.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.sf.l2j.tools.gui.model.EditablePagedTableModel;
import net.sf.l2j.tools.mariadb.DatabaseFactory;

public final class DataViewPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final int AUTO_COL_MAX_WIDTH = 520;
	private static final int AUTO_COL_SAMPLE_ROWS = 30;
	
	private final JTable table = new JTable();
	private final JScrollPane scroll = new JScrollPane(table);
	
	// bottom bar widgets
	private final JLabel status = new JLabel("Ready");
	private final JLabel pageInfo = new JLabel("Page 0/0");
	
	private final JButton btnFirst = navButton("<<", "First page");
	private final JButton btnPrev = navButton("<", "Previous page");
	private final JButton btnNext = navButton(">", "Next page");
	private final JButton btnLast = navButton(">>", "Last page");
	
	private final JComboBox<Integer> pageSize = new JComboBox<>(new Integer[]
	{
		100,
		500,
		1000,
		5000
	});
	
	private String currentTable;
	private int currentOffset = 0;
	private int totalRows = -1;
	private volatile SwingWorker<?, ?> activeWorker;
	private volatile Statement runningStatement; // ✅ para cancelar query atual
	
	private final DatabaseMainFrame mainFrame;
	
	public DataViewPanel(DatabaseMainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setOpaque(true);
		setBackground(UIManager.getColor("l2jdev.panel")); // grafite
		
		// Table
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(22);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, new ZebraTableRenderer());
		table.setAutoCreateRowSorter(true);
		
		// ✅ multi-selection (Ctrl/Shift)
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		scroll.setBorder(BorderFactory.createLineBorder(UIManager.getColor("l2jdev.line")));
		scroll.setOpaque(true);
		scroll.setBackground(UIManager.getColor("l2jdev.panel"));
		scroll.getViewport().setOpaque(true);
		scroll.getViewport().setBackground(UIManager.getColor("l2jdev.surface"));
		
		add(scroll, BorderLayout.CENTER);
		add(buildBottomBar(), BorderLayout.SOUTH);
		
		// actions
		btnFirst.addActionListener(e -> goFirst());
		btnPrev.addActionListener(e -> goPrev());
		btnNext.addActionListener(e -> goNext());
		btnLast.addActionListener(e -> goLast());
		
		pageSize.addActionListener(e -> {
			currentOffset = 0;
			reloadCurrentPage();
		});
		
		installTableContextMenu();
		updateNavButtons();
	}
	
	// =========================
	// Bottom Bar (professional)
	// =========================
	private JPanel buildBottomBar()
	{
		JPanel bar = new JPanel(new BorderLayout(10, 0));
		bar.setOpaque(true);
		bar.setBackground(UIManager.getColor("l2jdev.panel"));
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("l2jdev.line")), BorderFactory.createEmptyBorder(10, 0, 0, 0)));
		
		// Left: page size
		JPanel left = new JPanel();
		left.setOpaque(false);
		
		JLabel rowsLbl = new JLabel("Rows/page:");
		rowsLbl.setForeground(UIManager.getColor("l2jdev.muted"));
		rowsLbl.setFont(uiFont());
		
		pageSize.setFont(uiFont());
		pageSize.setFocusable(false);
		pageSize.setBackground(UIManager.getColor("l2jdev.surface"));
		pageSize.setForeground(UIManager.getColor("l2jdev.text"));
		
		left.add(rowsLbl);
		left.add(pageSize);
		
		// Center: status
		status.setForeground(UIManager.getColor("l2jdev.text"));
		status.setFont(uiFont());
		status.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Right: paging controls
		JPanel right = new JPanel();
		right.setOpaque(false);
		
		pageInfo.setForeground(UIManager.getColor("l2jdev.muted"));
		pageInfo.setFont(uiFont());
		
		right.add(btnFirst);
		right.add(btnPrev);
		right.add(Box.createHorizontalStrut(6));
		right.add(pageInfo);
		right.add(Box.createHorizontalStrut(6));
		right.add(btnNext);
		right.add(btnLast);
		
		// Add
		bar.add(left, BorderLayout.WEST);
		bar.add(status, BorderLayout.CENTER);
		bar.add(right, BorderLayout.EAST);
		
		return bar;
	}
	
	private static Font uiFont()
	{
		return new Font("Segoe UI", Font.PLAIN, 12);
	}
	
	// =========================
	// Context Menu
	// =========================
	private void installTableContextMenu()
	{
		final JPopupMenu menu = new JPopupMenu();
		
		final JMenuItem miReload = new JMenuItem("Reload page");
		miReload.addActionListener(e -> reloadCurrentPageSafe());
		
		final JMenuItem miDelete = new JMenuItem("Delete selected row(s)");
		miDelete.addActionListener(e -> deleteSelectedRows());
		
		final JMenuItem miTruncate = new JMenuItem("Clear table (TRUNCATE)");
		miTruncate.addActionListener(e -> truncateCurrentTable());
		
		menu.add(miReload);
		menu.add(miDelete);
		menu.addSeparator();
		menu.add(miTruncate);
		
		table.addMouseListener(new java.awt.event.MouseAdapter()
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
					
				// ✅ NÃO destruir multi-seleção:
				// Se o clique for numa linha não selecionada, troca seleção pra ela.
				// Se já estiver selecionada, mantém o conjunto atual (Ctrl/Shift).
				Point p = e.getPoint();
				int viewRow = table.rowAtPoint(p);
				if (viewRow >= 0 && !table.isRowSelected(viewRow))
				{
					table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
				}
				
				boolean hasTable = !isBlank(currentTable);
				miReload.setEnabled(hasTable);
				miTruncate.setEnabled(hasTable);
				
				boolean canDelete = false;
				if (hasTable && table.getSelectedRowCount() > 0 && table.getModel() instanceof EditablePagedTableModel)
				{
					EditablePagedTableModel m = (EditablePagedTableModel) table.getModel();
					canDelete = m.isEditable() && m.getPrimaryKeys() != null && !m.getPrimaryKeys().isEmpty();
				}
				miDelete.setEnabled(canDelete);
				
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	// =========================
	// Public API
	// =========================
	public JTable getTable()
	{
		return table;
	}
	
	public String getCurrentTable()
	{
		return currentTable;
	}
	
	public void loadTable(final String tableName)
	{
		if (isBlank(tableName))
			return;
		
		currentTable = tableName;
		currentOffset = 0;
		totalRows = -1;
		
		loadPage();
	}
	
	// =========================
	// Paging / Reload
	// =========================
	private void reloadCurrentPage()
	{
		if (isBlank(currentTable))
			return;
		
		loadPage();
	}
	
	public void reloadCurrentPageSafe()
	{
		if (isBlank(currentTable))
			return;
		
		if (!tableExists(currentTable))
		{
			currentTable = null;
			currentOffset = 0;
			totalRows = -1;
			table.setModel(new DefaultTableModel());
			
			pageInfo.setText("Page 0/0");
			status.setText("Table no longer exists (schema changed).");
			updateNavButtons();
			return;
		}
		
		reloadCurrentPage();
	}
	
	private int getLimit()
	{
		Integer v = (Integer) pageSize.getSelectedItem();
		return (v != null) ? v.intValue() : 500;
	}
	
	private void loadPage()
	{
		final String tableName = currentTable;
		final int limit = getLimit();
		final int offset = currentOffset;
		
		// ✅ cancela qualquer load anterior ainda rodando
		cancelWorkers();
		
		status.setText("Loading...");
		setControlsEnabled(false);
		
		SwingWorker<EditablePagedTableModel, Void> worker = new SwingWorker<>()
		{
			private List<String> pkCols = new ArrayList<>();
			
			@Override
			protected EditablePagedTableModel doInBackground() throws Exception
			{
				if (isCancelled())
					return null;
				
				try (Connection con = DatabaseFactory.getConnection())
				{
					totalRows = countRows(con, tableName);
					pkCols = loadPrimaryKeys(con, tableName);
					
					String sql = "SELECT * FROM " + q(tableName) + " LIMIT " + limit + " OFFSET " + offset;
					
					try (Statement st = con.createStatement())
					{
						// ✅ timeout curto pra não “segurar” o app fechando
						// (0 = infinito; recomendo 2..5)
						st.setQueryTimeout(3);
						
						runningStatement = st;
						
						try (ResultSet rs = st.executeQuery(sql))
						{
							return buildEditableModel(tableName, rs, pkCols);
						}
						finally
						{
							// libera ponteiro
							if (runningStatement == st)
								runningStatement = null;
						}
					}
				}
			}
			
			@Override
			protected void done()
			{
				try
				{
					// ✅ se foi cancelado, não faz nada
					if (isCancelled())
					{
						status.setText("Cancelled.");
						return;
					}
					
					EditablePagedTableModel model = get(); // pode lançar exceção
					if (model == null)
						return;
					
					table.setModel(model);
					
					autoSizeColumns(AUTO_COL_MAX_WIDTH, AUTO_COL_SAMPLE_ROWS);
					updateAutoResizeMode();
					
					int page = (limit == 0) ? 0 : (offset / limit) + 1;
					int pages = (totalRows <= 0 || limit <= 0) ? 1 : (int) Math.ceil(totalRows / (double) limit);
					pageInfo.setText("Page " + page + "/" + pages + "  (" + totalRows + " rows)");
					
					if (!model.isEditable())
						status.setText("Loaded. Read-only (no primary key).");
					else
						status.setText("Loaded. Editable (PK: " + String.join(", ", pkCols) + ").");
				}
				catch (Exception e)
				{
					status.setText("Error");
					JOptionPane.showMessageDialog(DataViewPanel.this, e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					// ✅ limpa referência do worker (somente se ainda for o mesmo)
					if (activeWorker == this)
						activeWorker = null;
					
					setControlsEnabled(true);
					updateNavButtons();
				}
			}
		};
		
		// ✅ registra como worker ativo e executa
		activeWorker = worker;
		worker.execute();
	}
	
	public void cancelWorkers()
	{
		// 1) tenta cancelar query SQL em andamento
		Statement st = runningStatement;
		if (st != null)
		{
			try
			{
				st.cancel();
			}
			catch (Exception ignored)
			{
			}
			try
			{
				st.close();
			}
			catch (Exception ignored)
			{
			}
			runningStatement = null;
		}
		
		// 2) cancela o worker
		SwingWorker<?, ?> w = activeWorker;
		if (w != null && !w.isDone())
		{
			try
			{
				w.cancel(true);
			}
			catch (Exception ignored)
			{
			}
		}
	}
	
	private void updateAutoResizeMode()
	{
		// roda no EDT (seguro)
		if (!SwingUtilities.isEventDispatchThread())
		{
			SwingUtilities.invokeLater(this::updateAutoResizeMode);
			return;
		}
		
		int colCount = table.getColumnModel().getColumnCount();
		if (colCount == 0)
			return;
		
		int viewportW = scroll.getViewport().getExtentSize().width;
		if (viewportW <= 0)
			return;
		
		// soma a largura preferida atual
		int totalPref = 0;
		int[] pref = new int[colCount];
		for (int i = 0; i < colCount; i++)
		{
			int w = table.getColumnModel().getColumn(i).getPreferredWidth();
			pref[i] = Math.max(30, w); // mínimo defensivo
			totalPref += pref[i];
		}
		
		// Se NÃO couber -> rolagem horizontal normal
		if (totalPref >= viewportW)
		{
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			return;
		}
		
		// Se couber -> distribuir o espaço para ocupar 100%
		int extra = viewportW - totalPref;
		
		// Distribui proporcionalmente ao tamanho atual (mantém o “peso” de cada coluna)
		// share_i = extra * (pref_i / totalPref)
		int distributed = 0;
		int[] add = new int[colCount];
		
		for (int i = 0; i < colCount; i++)
		{
			int inc = (int) Math.floor(extra * (pref[i] / (double) totalPref));
			add[i] = inc;
			distributed += inc;
		}
		
		// Sobras por arredondamento: espalha 1px por coluna até acabar
		int remainder = extra - distributed;
		int idx = 0;
		while (remainder > 0 && colCount > 0)
		{
			add[idx % colCount]++;
			remainder--;
			idx++;
		}
		
		// Aplica as novas larguras
		for (int i = 0; i < colCount; i++)
		{
			TableColumn tc = table.getColumnModel().getColumn(i);
			tc.setPreferredWidth(pref[i] + add[i]);
		}
		
		// Mantém OFF porque estamos controlando as larguras manualmente
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		table.revalidate();
		table.repaint();
	}
	
	private void goFirst()
	{
		currentOffset = 0;
		loadPage();
	}
	
	private void goPrev()
	{
		int limit = getLimit();
		currentOffset = Math.max(0, currentOffset - limit);
		loadPage();
	}
	
	private void goNext()
	{
		int limit = getLimit();
		currentOffset = currentOffset + limit;
		loadPage();
	}
	
	private void goLast()
	{
		int limit = getLimit();
		if (totalRows < 0 || limit <= 0)
			return;
		
		int lastOffset = Math.max(0, ((totalRows - 1) / limit) * limit);
		currentOffset = lastOffset;
		loadPage();
	}
	
	private void updateNavButtons()
	{
		int limit = getLimit();
		
		boolean hasTable = !isBlank(currentTable);
		boolean hasCount = totalRows >= 0;
		
		btnFirst.setEnabled(hasTable && currentOffset > 0);
		btnPrev.setEnabled(hasTable && currentOffset > 0);
		
		if (!hasTable)
		{
			btnNext.setEnabled(false);
			btnLast.setEnabled(false);
			return;
		}
		
		if (!hasCount)
		{
			btnNext.setEnabled(true);
			btnLast.setEnabled(false);
			return;
		}
		
		boolean hasMore = (currentOffset + limit) < totalRows;
		btnNext.setEnabled(hasMore);
		btnLast.setEnabled(hasMore);
	}
	
	private void setControlsEnabled(boolean enabled)
	{
		pageSize.setEnabled(enabled);
		btnFirst.setEnabled(enabled);
		btnPrev.setEnabled(enabled);
		btnNext.setEnabled(enabled);
		btnLast.setEnabled(enabled);
	}
	
	// =========================
	// DELETE / TRUNCATE
	// =========================
	private void truncateCurrentTable()
	{
		if (isBlank(currentTable))
			return;
		
		int confirm = JOptionPane.showConfirmDialog(this, "Clear ALL data from table:\n\n" + currentTable + "\n\nThis cannot be undone.", "Confirm TRUNCATE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if (confirm != JOptionPane.YES_OPTION)
			return;
		
		final String sql = "TRUNCATE TABLE " + q(currentTable);
		
		status.setText("Truncating...");
		setControlsEnabled(false);
		
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
					
					if (mainFrame != null)
						mainFrame.refreshUiAfterDbChange();
					else
						reloadCurrentPageSafe();
					
					status.setText("Truncate OK.");
				}
				catch (Exception e)
				{
					status.setText("Error");
					JOptionPane.showMessageDialog(DataViewPanel.this, e.getMessage(), "TRUNCATE Error", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					setControlsEnabled(true);
					updateNavButtons();
				}
			}
		}.execute();
	}
	
	private void deleteSelectedRows()
	{
		if (isBlank(currentTable))
			return;
		
		if (!(table.getModel() instanceof EditablePagedTableModel))
		{
			JOptionPane.showMessageDialog(this, "This table model is not deletable.", "Delete", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		EditablePagedTableModel model = (EditablePagedTableModel) table.getModel();
		
		if (!model.isEditable() || model.getPrimaryKeys() == null || model.getPrimaryKeys().isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Delete requires a PRIMARY KEY.", "Delete", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int[] viewRows = table.getSelectedRows();
		if (viewRows == null || viewRows.length == 0)
			return;
		
		final List<String> pkCols = model.getPrimaryKeys();
		final String tableName = model.getTableName();
		
		String msg = (viewRows.length == 1) ? "Delete the selected row?\n\nTable: " + tableName + "\nPK: " + String.join(", ", pkCols) + "\n\nThis cannot be undone." : "Delete " + viewRows.length + " selected rows?\n\nTable: " + tableName + "\nPK: " + String.join(", ", pkCols) + "\n\nThis cannot be undone.";
		
		int confirm = JOptionPane.showConfirmDialog(this, msg, "Confirm DELETE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.YES_OPTION)
			return;
		
		final StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(q(tableName)).append(" WHERE ");
		for (int i = 0; i < pkCols.size(); i++)
		{
			if (i > 0)
				sb.append(" AND ");
			sb.append(q(pkCols.get(i))).append("=?");
		}
		final String sql = sb.toString();
		
		status.setText("Deleting...");
		setControlsEnabled(false);
		
		new SwingWorker<Integer, Void>()
		{
			@Override
			protected Integer doInBackground() throws Exception
			{
				int affected = 0;
				
				try (Connection con = DatabaseFactory.getConnection(); java.sql.PreparedStatement ps = con.prepareStatement(sql))
				{
					for (int vr : viewRows)
					{
						int mr = table.convertRowIndexToModel(vr);
						
						int param = 1;
						for (String pk : pkCols)
						{
							int pkIndex = model.getColumnIndex(pk);
							Object pkValue = model.getValueAt(mr, pkIndex);
							
							if (pkValue == null)
								throw new IllegalStateException("PK column '" + pk + "' is NULL on selected row (cannot delete safely).");
							
							ps.setObject(param++, pkValue);
						}
						
						ps.addBatch();
					}
					
					int[] res = ps.executeBatch();
					for (int r : res)
					{
						if (r > 0)
							affected += r;
					}
				}
				
				return affected;
			}
			
			@Override
			protected void done()
			{
				try
				{
					int affected = get();
					
					if (mainFrame != null)
						mainFrame.refreshUiAfterDbChange();
					else
						reloadCurrentPageSafe();
					
					status.setText("Delete OK. Affected: " + affected);
				}
				catch (Exception e)
				{
					status.setText("Error");
					JOptionPane.showMessageDialog(DataViewPanel.this, e.getMessage(), "DELETE Error", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					setControlsEnabled(true);
					updateNavButtons();
				}
			}
		}.execute();
	}
	
	// =========================
	// Build editable model
	// =========================
	private static EditablePagedTableModel buildEditableModel(String tableName, ResultSet rs, List<String> pkCols) throws Exception
	{
		ResultSetMetaData md = rs.getMetaData();
		int cc = md.getColumnCount();
		
		List<String> cols = new ArrayList<>(cc);
		List<Class<?>> types = new ArrayList<>(cc);
		Map<String, Integer> indexMap = new HashMap<>(cc * 2);
		
		for (int i = 1; i <= cc; i++)
		{
			String col = md.getColumnLabel(i);
			cols.add(col);
			types.add(resolveType(md.getColumnClassName(i)));
			indexMap.put(col, i - 1);
		}
		
		List<Object[]> rows = new ArrayList<>();
		while (rs.next())
		{
			Object[] row = new Object[cc];
			for (int i = 1; i <= cc; i++)
				row[i - 1] = rs.getObject(i);
			rows.add(row);
		}
		
		boolean allowUnsafeEdit = pkCols.isEmpty(); // ou ligado a um checkbox no UI
		return new EditablePagedTableModel(tableName, cols, types, rows, pkCols, indexMap, allowUnsafeEdit, DatabaseFactory::getConnection);
		
	}
	
	private static Class<?> resolveType(String className)
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
	
	// =========================
	// Meta queries
	// =========================
	private static int countRows(Connection con, String table) throws Exception
	{
		String sql = "SELECT COUNT(*) FROM " + q(table);
		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql))
		{
			rs.next();
			return rs.getInt(1);
		}
	}
	
	private static List<String> loadPrimaryKeys(Connection con, String table) throws Exception
	{
		List<String> pk = new ArrayList<>();
		
		DatabaseMetaData meta = con.getMetaData();
		String catalog = con.getCatalog();
		
		try (ResultSet rs = meta.getPrimaryKeys(catalog, null, table))
		{
			while (rs.next())
			{
				String col = rs.getString("COLUMN_NAME");
				if (col != null && !col.isEmpty())
					pk.add(col);
			}
		}
		
		return pk;
	}
	
	private static boolean tableExists(String tableName)
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			DatabaseMetaData meta = con.getMetaData();
			String catalog = con.getCatalog();
			
			try (ResultSet rs = meta.getTables(catalog, null, tableName, new String[]
			{
				"TABLE"
			}))
			{
				return rs.next();
			}
		}
		catch (Exception e)
		{
			return true;
		}
	}
	
	// =========================
	// Column sizing
	// =========================
	private void autoSizeColumns(final int maxWidth, final int sampleRows)
	{
		TableModel model = table.getModel();
		if (model == null)
			return;
		
		FontMetrics fm = table.getFontMetrics(table.getFont());
		int rowsToCheck = Math.min(table.getRowCount(), Math.max(1, sampleRows));
		
		for (int col = 0; col < table.getColumnCount(); col++)
		{
			TableColumn tc = table.getColumnModel().getColumn(col);
			
			int width = 40;
			
			String header = String.valueOf(tc.getHeaderValue());
			width = Math.max(width, fm.stringWidth(header) + 26);
			
			for (int row = 0; row < rowsToCheck; row++)
			{
				Object v = model.getValueAt(row, col);
				if (v != null)
				{
					int w = fm.stringWidth(String.valueOf(v)) + 26;
					if (w > width)
						width = w;
				}
			}
			
			tc.setPreferredWidth(Math.min(width, maxWidth));
		}
	}
	
	// =========================
	// Helpers
	// =========================
	private static boolean isBlank(String s)
	{
		return s == null || s.trim().isEmpty();
	}
	
	private static String q(String ident)
	{
		return "`" + ident.replace("`", "``") + "`";
	}
	
	private static JButton navButton(String text, String tooltip)
	{
		JButton b = new JButton(text);
		b.setToolTipText(tooltip);
		
		b.setFocusable(false);
		b.setRolloverEnabled(true);
		
		// marca como "nav" para o RoundedButtonUI poder desenhar diferente
		b.putClientProperty("l2jdev.variant", "nav");
		
		// mais compacto
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		
		// usa tema
		b.setForeground(UIManager.getColor("l2jdev.muted"));
		b.setBackground(UIManager.getColor("l2jdev.panel"));
		
		// deixa o UI custom pintar
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		
		return b;
	}
	
	private static final class ZebraTableRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		private static final Color ROW_A = new Color(0x12, 0x14, 0x18);
		private static final Color ROW_B = new Color(0x10, 0x12, 0x16);
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!isSelected)
				c.setBackground((row % 2 == 0) ? ROW_A : ROW_B);
			return c;
		}
	}
	
}
