package net.sf.l2j.mods.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PaginationPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private int page = 0;
	private int maxPages = 1;
	
	private final JLabel label = new JLabel();
	private Runnable onChange;
	
	public PaginationPanel()
	{
		setOpaque(false);
		
		JButton first = new JButton("<<");
		JButton prev = new JButton("<");
		JButton next = new JButton(">");
		JButton last = new JButton(">>");
		
		first.addActionListener(e -> setPage(0));
		prev.addActionListener(e -> setPage(page - 1));
		next.addActionListener(e -> setPage(page + 1));
		last.addActionListener(e -> setPage(maxPages - 1));
		
		add(first);
		add(prev);
		add(label);
		add(next);
		add(last);
	}
	
	public void configure(int page, int maxPages, Runnable onChange)
	{
		this.page = page;
		this.maxPages = Math.max(1, maxPages);
		this.onChange = onChange;
		updateLabel();
	}
	
	private void setPage(int newPage)
	{
		newPage = Math.max(0, Math.min(newPage, maxPages - 1));
		if (newPage == page)
			return;
		
		page = newPage;
		updateLabel();
		
		if (onChange != null)
			onChange.run();
	}
	
	private void updateLabel()
	{
		label.setText(" Page " + (page + 1) + " / " + maxPages + " ");
	}
	
	public int getPage()
	{
		return page;
	}
}
