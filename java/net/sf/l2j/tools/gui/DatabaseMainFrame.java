package net.sf.l2j.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import net.sf.l2j.tools.gui.theme.RoundedBorder;
import net.sf.l2j.tools.gui.theme.RoundedButtonUI;
import net.sf.l2j.tools.gui.theme.SlimScrollBarUI;
import net.sf.l2j.tools.mariadb.DatabaseFactory;

public class DatabaseMainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private final TableListPanel tableList;
	private final DataViewPanel dataView;
	
	private volatile boolean closing = false;
	
	public DatabaseMainFrame()
	{
		super("L2JDev Database Panel");
		
		applyDarkTheme();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		tableList = new TableListPanel(this);
		dataView = new DataViewPanel(this);
		
		setJMenuBar(new MenuBarPanel(this, dataView));
		
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_32x32.png").getImage());
		setIconImages(icons);
		
		tableList.setTableSelectionListener(dataView::loadTable);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableList, dataView);
		split.setDividerLocation(300);
		split.setContinuousLayout(true);
		split.setBorder(null);
		
		add(split, BorderLayout.CENTER);
		
		// ✅ Captura o X (fechar janela)
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				requestClose();
			}
		});
	}
	
	public void requestClose()
	{
		if (closing)
			return;
		
		closing = true;
		
		int confirm = JOptionPane.showConfirmDialog(this, "Exit L2JDev Database Panel?\n\nAny running tasks will be stopped.", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (confirm != JOptionPane.YES_OPTION)
		{
			closing = false;
			return;
		}
		
		// ✅ 1) Cancela workers PRIMEIRO (antes de mexer no pool)
		try
		{
			dataView.cancelWorkers();
		}
		catch (Exception ignored)
		{
		}
		
		// ✅ 2) Fecha UI imediatamente
		dispose();
		
		// ✅ 3) Shutdown fora do EDT, MAS com fallback de saída
		Thread shutdownThread = new Thread(() -> {
			try
			{
				DatabaseFactory.shutdown(); // pode travar dependendo do driver/pool
			}
			catch (Exception ignored)
			{
			}
			finally
			{
				// ✅ garante saída mesmo se shutdown travar
				System.exit(0);
			}
		}, "db-shutdown");
		
		shutdownThread.setDaemon(true);
		shutdownThread.start();
		
		// ✅ 4) Fallback hard: se em X ms ainda não saiu, força halt
		new Thread(() -> {
			try
			{
				Thread.sleep(2500);
			}
			catch (InterruptedException ignored)
			{
			}
			Runtime.getRuntime().halt(0); // encerra SEM esperar hooks
		}, "force-halt").start();
	}
	
	public void refreshUiAfterDbChange()
	{
		SwingUtilities.invokeLater(() -> {
			// 1) Recarrega lista de tabelas
			tableList.reloadTables();
			
			dataView.reloadCurrentPageSafe();
		});
	}
	
	private static void applyDarkTheme()
	{
		// ========= TOKENS (grafite premium) =========
		final Color bg = new Color(0x0F, 0x11, 0x15);
		final Color panel = new Color(0x15, 0x18, 0x1F);
		final Color surface = new Color(0x11, 0x13, 0x19);
		final Color text = new Color(0xE8, 0xEC, 0xF6);
		final Color muted = new Color(0xA8, 0xB0, 0xC0);
		final Color line = new Color(0x2A, 0x30, 0x3B);
		
		final Color accent = new Color(0x3D, 0x7A, 0xFF);
		final Color selBg = new Color(0x1F, 0x3A, 0x66);
		
		final Color btn = new Color(0x1A, 0x20, 0x29);
		final Color btnHover = new Color(0x22, 0x2B, 0x38);
		final Color btnDown = new Color(0x16, 0x1C, 0x25);
		
		final Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);
		final Font uiFontBold = uiFont.deriveFont(Font.BOLD);
		final Font monoFont = new Font("Consolas", Font.PLAIN, 13);
		
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception ignored)
		{
		}
		
		// ========= Exporta tokens =========
		UIManager.put("l2jdev.bg", bg);
		UIManager.put("l2jdev.panel", panel);
		UIManager.put("l2jdev.surface", surface);
		UIManager.put("l2jdev.text", text);
		UIManager.put("l2jdev.muted", muted);
		UIManager.put("l2jdev.line", line);
		UIManager.put("l2jdev.accent", accent);
		UIManager.put("l2jdev.btn", btn);
		UIManager.put("l2jdev.btnHover", btnHover);
		UIManager.put("l2jdev.btnDown", btnDown);
		
		// ========= Nimbus base (isso é o que “desbranqueia”) =========
		UIManager.put("control", new ColorUIResource(panel));
		UIManager.put("info", new ColorUIResource(panel));
		
		UIManager.put("nimbusBase", new ColorUIResource(new Color(0x1A, 0x22, 0x2E)));
		UIManager.put("nimbusBlueGrey", new ColorUIResource(panel)); // MUITO importante
		UIManager.put("nimbusLightBackground", new ColorUIResource(surface)); // MUITO importante
		UIManager.put("nimbusFocus", new ColorUIResource(accent));
		UIManager.put("nimbusSelectionBackground", new ColorUIResource(selBg));
		UIManager.put("text", new ColorUIResource(text));
		
		// Text keys que Nimbus usa internamente
		UIManager.put("controlText", new ColorUIResource(text));
		UIManager.put("infoText", new ColorUIResource(text));
		UIManager.put("textText", new ColorUIResource(text));
		UIManager.put("nimbusDisabledText", new ColorUIResource(new Color(0x73, 0x7B, 0x8D)));
		
		// ========= Defaults (fonte + fundo) =========
		UIManager.put("defaultFont", uiFont);
		
		UIManager.put("Panel.background", panel);
		UIManager.put("RootPane.background", bg);
		UIManager.put("Viewport.background", surface);
		UIManager.put("ScrollPane.background", panel);
		
		UIManager.put("Label.foreground", muted);
		UIManager.put("Label.font", uiFont);
		
		// ========= Bordas =========
		final Border rounded = new RoundedBorder(line, 10, 1);
		final Border roundedPad = BorderFactory.createCompoundBorder(rounded, BorderFactory.createEmptyBorder(6, 10, 6, 10));
		
		// ========= Inputs =========
		UIManager.put("TextField.background", surface);
		UIManager.put("TextField.foreground", text);
		UIManager.put("TextField.caretForeground", accent);
		UIManager.put("TextField.selectionBackground", selBg);
		UIManager.put("TextField.selectionForeground", text);
		UIManager.put("TextField.border", roundedPad);
		UIManager.put("TextField.font", uiFont);
		
		UIManager.put("PasswordField.background", surface);
		UIManager.put("PasswordField.foreground", text);
		UIManager.put("PasswordField.caretForeground", accent);
		UIManager.put("PasswordField.selectionBackground", selBg);
		UIManager.put("PasswordField.selectionForeground", text);
		UIManager.put("PasswordField.border", roundedPad);
		UIManager.put("PasswordField.font", uiFont);
		
		UIManager.put("TextArea.background", surface);
		UIManager.put("TextArea.foreground", text);
		UIManager.put("TextArea.caretForeground", accent);
		UIManager.put("TextArea.selectionBackground", selBg);
		UIManager.put("TextArea.selectionForeground", text);
		UIManager.put("TextArea.border", BorderFactory.createCompoundBorder(rounded, BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		UIManager.put("TextArea.font", monoFont);
		
		UIManager.put("ComboBox.background", surface);
		UIManager.put("ComboBox.foreground", text);
		UIManager.put("ComboBox.selectionBackground", selBg);
		UIManager.put("ComboBox.selectionForeground", text);
		UIManager.put("ComboBox.border", BorderFactory.createCompoundBorder(rounded, BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		UIManager.put("ComboBox.font", uiFont);
		
		// ========= Buttons =========
		UIManager.put("Button.background", btn);
		UIManager.put("Button.foreground", text);
		UIManager.put("Button.font", uiFontBold);
		UIManager.put("Button.border", BorderFactory.createEmptyBorder(8, 14, 8, 14));
		UIManager.put("ButtonUI", RoundedButtonUI.class.getName());
		
		// ========= Menus =========
		UIManager.put("MenuBar.background", panel);
		UIManager.put("MenuBar.foreground", text);
		UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, line));
		
		UIManager.put("Menu.background", panel);
		UIManager.put("Menu.foreground", text);
		UIManager.put("Menu.selectionBackground", selBg);
		UIManager.put("Menu.selectionForeground", text);
		
		UIManager.put("MenuItem.background", panel);
		UIManager.put("MenuItem.foreground", text);
		UIManager.put("MenuItem.selectionBackground", selBg);
		UIManager.put("MenuItem.selectionForeground", text);
		
		UIManager.put("PopupMenu.background", panel);
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(line));
		
		// ========= Tables =========
		UIManager.put("Table.background", surface);
		UIManager.put("Table.foreground", text);
		UIManager.put("Table.selectionBackground", selBg);
		UIManager.put("Table.selectionForeground", text);
		UIManager.put("Table.gridColor", line);
		UIManager.put("Table.font", uiFont);
		UIManager.put("Table.rowHeight", 24);
		
		UIManager.put("TableHeader.background", panel);
		UIManager.put("TableHeader.foreground", text);
		UIManager.put("TableHeader.font", uiFontBold);
		UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 1, 0, line));
		
		// ========= Lists =========
		UIManager.put("List.background", surface);
		UIManager.put("List.foreground", text);
		UIManager.put("List.selectionBackground", new ColorUIResource(new Color(0x22, 0x35, 0x55)));
		UIManager.put("List.selectionForeground", text);
		UIManager.put("List.font", uiFont);
		
		// ========= Scrollbar =========
		UIManager.put("ScrollBarUI", SlimScrollBarUI.class.getName());
		UIManager.put("ScrollBar.thumb", new ColorUIResource(new Color(0x2B, 0x31, 0x3C)));
		UIManager.put("ScrollBar.track", new ColorUIResource(panel));
		UIManager.put("ScrollBar.width", 10);
		
		// ========= Dialog / Tooltip =========
		UIManager.put("OptionPane.background", panel);
		UIManager.put("OptionPane.messageForeground", text);
		
		UIManager.put("ToolTip.background", panel);
		UIManager.put("ToolTip.foreground", text);
		UIManager.put("ToolTip.border", rounded);
	}
	
}
