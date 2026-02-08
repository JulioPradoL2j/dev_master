package net.sf.l2j.tools.gui.theme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SlimScrollBarUI extends BasicScrollBarUI
{
	@Override
	protected void configureScrollBarColors()
	{
		thumbColor = UIManager.getColor("ScrollBar.thumb");
		trackColor = UIManager.getColor("ScrollBar.track");
	}
	
	@Override
	protected Dimension getMinimumThumbSize()
	{
		return new Dimension(18, 30);
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation)
	{
		return zeroButton();
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation)
	{
		return zeroButton();
	}
	
	private static JButton zeroButton()
	{
		JButton b = new JButton();
		b.setPreferredSize(new Dimension(0, 0));
		b.setMinimumSize(new Dimension(0, 0));
		b.setMaximumSize(new Dimension(0, 0));
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		return b;
	}
	
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle r)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		try
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(trackColor != null ? trackColor : c.getBackground());
			g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
		}
		finally
		{
			g2.dispose();
		}
	}
	
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle r)
	{
		if (r.isEmpty() || !scrollbar.isEnabled())
			return;
		
		Graphics2D g2 = (Graphics2D) g.create();
		try
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Color thumb = thumbColor != null ? thumbColor : new Color(60, 60, 60);
			g2.setColor(thumb);
			
			int arc = 10;
			g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, arc, arc);
		}
		finally
		{
			g2.dispose();
		}
	}
}
