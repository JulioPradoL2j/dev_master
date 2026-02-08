package net.sf.l2j.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.Shutdown;
import net.sf.l2j.gameserver.balance.classbalance.ClassProfileHolder;
import net.sf.l2j.gameserver.balance.classbalance.ClassProfileReader;
import net.sf.l2j.gameserver.balance.matchup.MatchupHolder;
import net.sf.l2j.gameserver.balance.matchup.MatchupReader;
import net.sf.l2j.gameserver.balance.skills.SkillBalanceHolder;
import net.sf.l2j.gameserver.balance.skills.SkillBalanceReader;
import net.sf.l2j.launcher.etc.InterfaceInfo;
import net.sf.l2j.launcher.etc.InterfaceLimit;
import net.sf.l2j.launcher.etc.SplashScreen;
import net.sf.l2j.launcher.etc.Thema;
import net.sf.l2j.launcher.panel.DroplistFrame;
import net.sf.l2j.launcher.panel.GameConfigFrame;
import net.sf.l2j.launcher.panel.ItemsSearchFrame;
import net.sf.l2j.mods.gui.PhantomPanel;

public class GameServerLauncher
{
	JTextArea txtrConsole;
	private String currentFontFamily = "Monospaced";
	private int currentFontSize = 12;
	
	static final String[] shutdownOptions =
	{
		"Shutdown",
		"Restart",
		"Cancel"
	};
	static final String[] restartOptions =
	{
		"Restart",
		"Cancel"
	};
	static final String[] abortOptions =
	{
		"Abort",
		"Cancel"
	};
	static final String[] confirmOptions =
	{
		"Confirm",
		"Cancel"
	};
	
	public GameServerLauncher()
	{
		Thema.getInstance().aplly();
		
		// Initialize console.
		txtrConsole = new JTextArea();
		txtrConsole.setEditable(false);
		txtrConsole.setLineWrap(true);
		txtrConsole.setWrapStyleWord(true);
		txtrConsole.setDropMode(DropMode.INSERT);
		Font defaultFont = findBestConsoleFont();
		currentFontFamily = defaultFont.getFamily();
		currentFontSize = defaultFont.getSize();
		applyConsoleFont();
		txtrConsole.getDocument().addDocumentListener(new InterfaceLimit(800));
		
		// Initialize menu items.
		final JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		// ========================= SERVER MENU =========================
		final JMenu mnServer = new JMenu("Game");
		mnServer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnServer);
		
		final JMenu mnFont = new JMenu("Font");
		mnFont.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnFont);
		
		final JMenu mnNpcs = new JMenu("Npcs");
		mnNpcs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnNpcs);
		
		/*
		 * ========================= FONT FAMILY =========================
		 */
		final JMenu mnFontFamily = new JMenu("Family");
		mnFontFamily.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mnFont.add(mnFontFamily);
		
		String[] fontFamilies =
		{
			"JetBrains Mono",
			"Cascadia Code",
			"Source Code Pro",
			"Consolas",
			"Roboto Mono",
			"IBM Plex Mono",
			"Ubuntu Mono",
			"Monospaced",
			"Segoe UI"
		};
		
		ButtonGroup familyGroup = new ButtonGroup();
		
		for (String fontName : fontFamilies)
		{
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(fontName);
			item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			item.setSelected(fontName.equals(currentFontFamily));
			
			item.addActionListener(e -> {
				currentFontFamily = fontName;
				applyConsoleFont();
			});
			
			familyGroup.add(item);
			mnFontFamily.add(item);
		}
		
		/*
		 * ========================= FONT SIZE =========================
		 */
		final JMenu mnFontSize = new JMenu("Size");
		mnFontSize.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mnFont.add(mnFontSize);
		
		int[] sizes =
		{
			11,
			12,
			13,
			14,
			15,
			16,
			18
		};
		
		ButtonGroup sizeGroup = new ButtonGroup();
		
		for (int size : sizes)
		{
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(String.valueOf(size));
			item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			item.setSelected(size == currentFontSize);
			
			item.addActionListener(e -> {
				currentFontSize = size;
				applyConsoleFont();
			});
			
			sizeGroup.add(item);
			mnFontSize.add(item);
		}
		
		final JMenu mnfakes = new JMenu("FakePlayer");
		mnfakes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnfakes);
		
		final JMenuItem mntmfakes = new JMenuItem("Panel");
		mntmfakes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mntmfakes.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				new PhantomPanel();
			}
		});
		mnfakes.add(mntmfakes);
		
		
		
		final JMenu mnAdmin = new JMenu("Item");
		mnAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnAdmin);
		
		final JMenuItem mntmSearchItemss = new JMenuItem("Search");
		mntmSearchItemss.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mntmSearchItemss.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ItemsSearchFrame.openSearchItem();
			}
		});
		mnAdmin.add(mntmSearchItemss);
		
		final JMenu mnReload = new JMenu("Balance");
		mnReload.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnReload);
		
		final JMenuItem mntmBalanceSkills = new JMenuItem("Skill");
		mntmBalanceSkills.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mntmBalanceSkills.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				SkillBalanceHolder.clear();
				new SkillBalanceReader().load();
				GameServer._log.config("[Reload] Skills Balance.");
			}
		});
		mnReload.add(mntmBalanceSkills);
		
		final JMenuItem mntmProfile = new JMenuItem("Profile");
		mntmProfile.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mntmProfile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ClassProfileHolder.clear();
				new ClassProfileReader().load();
				GameServer._log.config("[Reload] Profile Balance.");
			}
		});
		mnReload.add(mntmProfile);
		
		final JMenuItem mntmProfileVsProfile = new JMenuItem("Matchup");
		mntmProfileVsProfile.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mntmProfileVsProfile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				MatchupHolder.clear();
				new MatchupReader().reload();
				GameServer._log.config("[Reload] Matchup Balance.");
			}
		});
		mnReload.add(mntmProfileVsProfile);
		
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_32x32.png").getImage());
		
		final JPanel systemPanel = new InterfaceInfo();
		final JScrollPane scrollPanel = new JScrollPane(txtrConsole);
		scrollPanel.setBounds(0, 0, 300, 160);
		final JLayeredPane layeredPanel = new JLayeredPane();
		layeredPanel.add(scrollPanel, 0, 0);
		layeredPanel.add(systemPanel, 1, 0);
		
		final JFrame frame = new JFrame("Game Server");
		
		final JMenuItem miConfig = new JMenuItem("Config");
		miConfig.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		miConfig.addActionListener(e -> {
			new GameConfigFrame(frame).setVisible(true);
		});
		mnServer.add(miConfig);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				int choice = JOptionPane.showOptionDialog(frame, "What do you want to do with the server?", "Server Control", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, shutdownOptions, shutdownOptions[2] // default = Cancel
				);
				
				switch (choice)
				{
					case 0: // Shutdown
					{
						Shutdown.getInstance().startShutdown(null, "Server shutdown from launcher", 1, false);
						break;
					}
					
					case 1: // Restart
					{
						Shutdown.getInstance().startShutdown(null, "Server restart from launcher", 1, true);
						break;
					}
					
					default: // Cancel or closed dialog
					{
						// Do nothing
						break;
					}
				}
			}
		});
		
		frame.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent ev)
			{
				scrollPanel.setSize(frame.getContentPane().getSize());
				systemPanel.setLocation(frame.getContentPane().getWidth() - systemPanel.getWidth() - 34, systemPanel.getY());
			}
		});
		
		final JMenuItem miDroplist = new JMenuItem("Droplist");
		miDroplist.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		miDroplist.addActionListener(e -> {
			new DroplistFrame(frame).setVisible(true);
		});
		
		mnNpcs.add(miDroplist);
		
		frame.setJMenuBar(menuBar);
		frame.setIconImages(icons);
		frame.add(layeredPanel, BorderLayout.CENTER);
		frame.getContentPane().setPreferredSize(new Dimension(680, 390));
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		redirectSystemStreams();
		
		new SplashScreen(".." + File.separator + "images" + File.separator + "splashscreen.gif", frame);
	}
	
	void updateTextArea(String text)
	{
		SwingUtilities.invokeLater(() -> {
			txtrConsole.append(text);
			txtrConsole.setCaretPosition(txtrConsole.getText().length());
		});
	}
	
	private void redirectSystemStreams()
	{
		final OutputStream out = new OutputStream()
		{
			@Override
			public void write(int b)
			{
				updateTextArea(String.valueOf((char) b));
			}
			
			@Override
			public void write(byte[] b, int off, int len)
			{
				updateTextArea(new String(b, off, len));
			}
			
			@Override
			public void write(byte[] b)
			{
				write(b, 0, b.length);
			}
		};
		
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
	
	public static boolean isDigit(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (char c : text.toCharArray())
		{
			if (!Character.isDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	private void applyConsoleFont()
	{
		txtrConsole.setFont(new Font(currentFontFamily, Font.PLAIN, currentFontSize));
	}
	
	private static Font findBestConsoleFont()
	{
		String[] preferredFonts =
		{
			"JetBrains Mono",
			"Cascadia Code",
			"Source Code Pro",
			"Consolas",
			"Roboto Mono",
			"IBM Plex Mono",
			"Ubuntu Mono",
			"Monospaced",
			"Segoe UI"
		};
		for (String fontName : preferredFonts)
		{
			Font font = new Font(fontName, Font.PLAIN, 12);
			if (font.getFamily().equals(fontName))
				return font;
		}
		return new Font("Segoe UI", Font.PLAIN, 12);
	}
}
