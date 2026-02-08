package net.sf.l2j.mods.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.l2j.gameserver.ThreadPool;

public class PhantomPanel extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static final int PAGE_SIZE = 10;
	
	private int currentPage = 0;
	private String filterText = "";
	
	private final JLabel onlineLbl = uiBig();
	
	private final JLabel offlineLbl = uiBig();
	
	private final JLabel statusLbl = new JLabel("Ready.");
	
	private final JTextField searchField = new JTextField(22);
	private final JButton refreshBtn = new JButton("Refresh");
	
	private final FakeTableModel model = new FakeTableModel();
	private final JTable table = new JTable(model);
	private final PaginationPanel pager = new PaginationPanel();
	
	private List<FakeAdminService.FakeRow> allRows = List.of();
	private List<FakeAdminService.FakeRow> filtered = List.of();
	
	private FakeCommandPanel cmdPanel;
	
	public PhantomPanel()
	{
		super("FakePlayer Runtime");
		
		List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images/l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images/l2jdev_32x32.png").getImage());
		setIconImages(icons);
		
		setSize(1180, 720);
		setMinimumSize(new Dimension(1020, 650));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		root.setBackground(new Color(18, 18, 18));
		
		root.add(header(), BorderLayout.NORTH);
		root.add(center(), BorderLayout.CENTER);
		root.add(footer(), BorderLayout.SOUTH);
		
		add(root);
		
		refreshBtn.addActionListener(e -> ThreadPool.execute(this::refreshSafe));
		wireSearch();
		
		ThreadPool.scheduleAtFixedRate(this::refreshSafe, 1000, 2000);
		
		setVisible(true);
	}
	
	public FakeTableModel getModel()
	{
		return model;
	}
	
	public void runOnEdt(Runnable r)
	{
		SwingUtilities.invokeLater(r);
	}
	
	public void toast(String msg)
	{
		SwingUtilities.invokeLater(() -> statusLbl.setText(msg));
	}
	
	private JPanel header()
	{
		JPanel top = new JPanel(new BorderLayout(12, 10));
		top.setOpaque(false);
		
		JPanel cards = new JPanel(new GridLayout(1, 3, 10, 10));
		cards.setOpaque(false);
		cards.add(card("Online", onlineLbl));
		
		cards.add(card("Offline", offlineLbl));
		
		JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		search.setOpaque(false);
		
		JLabel s = new JLabel("Search:");
		s.setForeground(new Color(200, 200, 200));
		
		search.add(s);
		search.add(searchField);
		search.add(refreshBtn);
		
		top.add(cards, BorderLayout.CENTER);
		top.add(search, BorderLayout.EAST);
		return top;
	}
	
	private JPanel center()
	{
		table.setRowHeight(24);
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		// renderer do online
		table.getColumnModel().getColumn(1).setCellRenderer(new OnlineBadgeRenderer());
		
		var cPinned = table.getColumnModel().getColumn(0);
		cPinned.setMinWidth(28);
		cPinned.setMaxWidth(28);
		cPinned.setPreferredWidth(28);
		
		// Online (badge)
		var cOnline = table.getColumnModel().getColumn(1);
		cOnline.setMinWidth(72);
		cOnline.setMaxWidth(72);
		cOnline.setPreferredWidth(72);
		
		// Lvl
		var cLvl = table.getColumnModel().getColumn(4);
		cLvl.setMinWidth(46);
		cLvl.setMaxWidth(46);
		cLvl.setPreferredWidth(46);
		
		// ====== COLUNAS “FLEX” (podem crescer) ======
		table.getColumnModel().getColumn(2).setPreferredWidth(160); // Name
		table.getColumnModel().getColumn(3).setPreferredWidth(170); // Class
		table.getColumnModel().getColumn(5).setPreferredWidth(130); // State
		
		JPanel wrap = new JPanel(new BorderLayout(10, 10));
		wrap.setOpaque(false);
		
		cmdPanel = new FakeCommandPanel(this);
		
		wrap.add(cmdPanel, BorderLayout.NORTH);
		wrap.add(new JScrollPane(table), BorderLayout.CENTER);
		wrap.add(pager, BorderLayout.SOUTH);
		
		return wrap;
	}
	
	private JPanel footer()
	{
		JButton pinPage = new JButton("SELECT PAGE");
		JButton pinFiltered = new JButton("SELECT FILTERED");
		JButton unpinAll = new JButton("UNSELECT ALL");
		
		pinPage.addActionListener(e -> runOnEdt(() -> {
			model.setPinnedForAllRows(true);
			toast("Selected page (" + model.getRowCount() + ").");
			if (cmdPanel != null)
				cmdPanel.onModelChanged();
		}));
		
		pinFiltered.addActionListener(e -> runOnEdt(() -> {
			model.setPinnedForIds(getFilteredIds(), true);
			toast("Selected filtered (" + model.getPinnedCount() + ").");
			if (cmdPanel != null)
				cmdPanel.onModelChanged();
		}));
		
		unpinAll.addActionListener(e -> runOnEdt(() -> {
			model.clearPins();
			toast("Selection cleared.");
			if (cmdPanel != null)
				cmdPanel.onModelChanged();
		}));
		
		statusLbl.setForeground(new Color(200, 200, 200));
		
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		left.setOpaque(false);
		left.add(pinPage);
		left.add(pinFiltered);
		left.add(unpinAll);
		
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		right.setOpaque(false);
		right.add(statusLbl);
		
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add(left, BorderLayout.WEST);
		p.add(right, BorderLayout.EAST);
		return p;
	}
	
	public List<Integer> getFilteredIds()
	{
		List<Integer> ids = new ArrayList<>(filtered.size());
		for (FakeAdminService.FakeRow r : filtered)
			ids.add(r.objectId);
		return ids;
	}
	
	private void wireSearch()
	{
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				onSearchChanged();
			}
		});
	}
	
	private void onSearchChanged()
	{
		filterText = (searchField.getText() != null) ? searchField.getText().trim() : "";
		currentPage = 0;
		ThreadPool.execute(this::applyFilterAndPageSafe);
	}
	
	private void refreshSafe()
	{
		final List<FakeAdminService.FakeRow> loaded = FakeAdminService.loadAllRows();
		
		int online = 0;
		
		for (FakeAdminService.FakeRow r : loaded)
		{
			if (r.online)
			{
				online++;
				
			}
		}
		
		final int onlineCount = online;
		
		final int offlineCount = Math.max(0, loaded.size() - onlineCount);
		
		SwingUtilities.invokeLater(() -> {
			allRows = loaded;
			
			onlineLbl.setText(String.valueOf(onlineCount));
			
			offlineLbl.setText(String.valueOf(offlineCount));
			
			applyFilterAndPage();
		});
	}
	
	private void applyFilterAndPageSafe()
	{
		SwingUtilities.invokeLater(this::applyFilterAndPage);
	}
	
	private void applyFilterAndPage()
	{
		filtered = FakeAdminService.filter(allRows, filterText);
		
		int total = filtered.size();
		int pages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
		currentPage = Math.max(0, Math.min(currentPage, pages - 1));
		
		int from = currentPage * PAGE_SIZE;
		int to = Math.min(from + PAGE_SIZE, total);
		List<FakeAdminService.FakeRow> page = (from < to) ? filtered.subList(from, to) : List.of();
		
		model.setData(page);
		
		pager.configure(currentPage, pages, () -> {
			currentPage = pager.getPage();
			applyFilterAndPage();
		});
		
		if (cmdPanel != null)
			cmdPanel.onModelChanged();
	}
	
	private static JPanel card(String title, JLabel value)
	{
		JPanel p = new JPanel(new BorderLayout(0, 4));
		p.setBackground(new Color(28, 28, 28));
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		
		JLabel t = new JLabel(title);
		t.setForeground(new Color(160, 160, 160));
		t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		p.add(t, BorderLayout.NORTH);
		p.add(value, BorderLayout.CENTER);
		return p;
	}
	
	private static JLabel uiBig()
	{
		JLabel l = new JLabel("0");
		l.setFont(new Font("Segoe UI", Font.BOLD, 22));
		l.setForeground(Color.WHITE);
		return l;
	}
}
