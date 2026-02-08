package net.sf.l2j.tools.gui.theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Rounded button UI for Nimbus-based dark theme. Supports variants via client properties: - "l2jdev.variant" = "nav" -> compact navigation chip styling Internal state: - "l2jdev.hover" -> hover flag
 */
public final class RoundedButtonUI extends BasicButtonUI
{
	private static final String HOVER = "l2jdev.hover";
	private static final String VARIANT = "l2jdev.variant";
	private static final String VARIANT_NAV = "nav";
	
	private static final int R_NORMAL = 12;
	private static final int R_NAV = 10;
	
	@Override
	public void installUI(JComponent c)
	{
		super.installUI(c);
		
		if (!(c instanceof AbstractButton))
			return;
		
		final AbstractButton b = (AbstractButton) c;
		
		// Let our UI paint everything
		b.setOpaque(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setRolloverEnabled(true);
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		
		// Avoid stacking multiple listeners if UI is reinstalled
		for (var ml : b.getMouseListeners())
		{
			if (ml instanceof HoverMouseListener)
				b.removeMouseListener(ml);
		}
		b.addMouseListener(new HoverMouseListener(b));
	}
	
	@Override
	public void uninstallUI(JComponent c)
	{
		if (c instanceof AbstractButton)
		{
			AbstractButton b = (AbstractButton) c;
			for (var ml : b.getMouseListeners())
			{
				if (ml instanceof HoverMouseListener)
					b.removeMouseListener(ml);
			}
		}
		super.uninstallUI(c);
	}
	
	@Override
	public void paint(Graphics g, JComponent c)
	{
		if (!(c instanceof AbstractButton))
			return;
		
		final AbstractButton b = (AbstractButton) c;
		
		final Graphics2D g2 = (Graphics2D) g.create();
		try
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			final int w = c.getWidth();
			final int h = c.getHeight();
			
			final boolean hover = Boolean.TRUE.equals(b.getClientProperty(HOVER));
			final boolean down = b.getModel().isArmed() && b.getModel().isPressed();
			final boolean enabled = b.isEnabled();
			
			final boolean isNav = VARIANT_NAV.equals(String.valueOf(b.getClientProperty(VARIANT)));
			
			// Theme tokens
			Color bg = color("Button.background", new Color(0x1A, 0x20, 0x29));
			Color fg = color("Button.foreground", new Color(0xE8, 0xEC, 0xF6));
			
			final Color hoverC = UIManager.getColor("l2jdev.btnHover");
			final Color downC = UIManager.getColor("l2jdev.btnDown");
			final Color line = UIManager.getColor("l2jdev.line");
			final Color accent = UIManager.getColor("l2jdev.accent");
			
			final Color surface = UIManager.getColor("l2jdev.surface");
			final Color muted = UIManager.getColor("l2jdev.muted");
			final Color text = UIManager.getColor("l2jdev.text");
			
			// Variant styling: NAV = "chip" more subtle
			if (isNav)
			{
				if (surface != null)
					bg = surface;
				
				if (muted != null)
					fg = muted;
				
				if (hover && hoverC != null)
				{
					bg = hoverC;
					if (text != null)
						fg = text;
				}
				if (down && downC != null)
				{
					bg = downC;
					if (text != null)
						fg = text;
				}
			}
			else
			{
				if (hover && hoverC != null)
					bg = hoverC;
				if (down && downC != null)
					bg = downC;
			}
			
			// Disabled alpha handling (nav shouldn't disappear)
			if (!enabled)
			{
				final int aBg = isNav ? 140 : 110;
				final int aFg = isNav ? 120 : 110;
				bg = withAlpha(bg, aBg);
				fg = withAlpha(fg, aFg);
			}
			
			final int r = isNav ? R_NAV : R_NORMAL;
			
			// Background
			g2.setColor(bg);
			g2.fillRoundRect(0, 0, w, h, r, r);
			
			// Border (subtle for nav)
			if (line != null)
			{
				g2.setColor(withAlpha(line, isNav ? 120 : 255));
				g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
			}
			
			// Focus ring (avoid strong ring on nav; keep subtle)
			if (b.isFocusOwner() && enabled && accent != null)
			{
				g2.setColor(withAlpha(accent, isNav ? 90 : 110));
				g2.setStroke(new BasicStroke(2f));
				g2.drawRoundRect(2, 2, w - 5, h - 5, r, r);
			}
			
			// Text
			final String txt = b.getText();
			if (txt != null && !txt.isEmpty())
			{
				g2.setFont(c.getFont());
				FontMetrics fm = g2.getFontMetrics();
				
				int tw = fm.stringWidth(txt);
				int th = fm.getAscent();
				
				g2.setColor(fg);
				g2.drawString(txt, (w - tw) / 2, (h + th) / 2 - 2);
			}
		}
		finally
		{
			g2.dispose();
		}
	}
	
	private static Color color(String uiKey, Color fallback)
	{
		Color c = UIManager.getColor(uiKey);
		return (c != null) ? c : fallback;
	}
	
	private static Color withAlpha(Color c, int alpha)
	{
		if (c == null)
			return new Color(0, 0, 0, alpha);
		alpha = Math.max(0, Math.min(255, alpha));
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	private static final class HoverMouseListener extends MouseAdapter
	{
		private final AbstractButton b;
		
		private HoverMouseListener(AbstractButton b)
		{
			this.b = b;
		}
		
		@Override
		public void mouseEntered(MouseEvent e)
		{
			b.putClientProperty(HOVER, Boolean.TRUE);
			b.repaint();
		}
		
		@Override
		public void mouseExited(MouseEvent e)
		{
			b.putClientProperty(HOVER, Boolean.FALSE);
			b.repaint();
		}
	}
}
