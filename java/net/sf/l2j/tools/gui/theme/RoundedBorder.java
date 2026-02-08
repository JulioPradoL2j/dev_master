package net.sf.l2j.tools.gui.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

public final class RoundedBorder implements Border
{
	private final Color color;
	private final int radius;
	private final int thickness;
	
	public RoundedBorder(Color color, int radius, int thickness)
	{
		this.color = color;
		this.radius = radius;
		this.thickness = thickness;
	}
	
	@Override
	public Insets getBorderInsets(Component c)
	{
		return new Insets(2, 2, 2, 2);
	}
	
	@Override
	public boolean isBorderOpaque()
	{
		return false;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		for (int i = 0; i < thickness; i++)
			g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), radius, radius);
		g2.dispose();
	}
}