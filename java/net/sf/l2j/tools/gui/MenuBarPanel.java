package net.sf.l2j.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sf.l2j.tools.gui.sql.SqlInstallDialog;
import net.sf.l2j.tools.gui.sql.SqlRunner;

public final class MenuBarPanel extends JMenuBar
{
	private static final long serialVersionUID = 1L;
	
	private static final String GITHUB_URL = "https://github.com/JulioPradoL2j";
	private static final String WHATSAPP_URL = "https://wa.me/5564984083891";
	
	public MenuBarPanel(DatabaseMainFrame frame, DataViewPanel dataView)
	{
		JMenu file = new JMenu("Arquivo");
		
		JMenuItem importSql = new JMenuItem("Executar arquivo .sql...");
		importSql.addActionListener(e -> SqlRunner.runSingleSqlFile(frame, dataView));
		
		JMenuItem install = new JMenuItem("Instalador SQL (tools/sql)...");
		install.addActionListener(e -> SqlInstallDialog.open(frame, dataView));
		
		JMenuItem exit = new JMenuItem("Sair");
		exit.addActionListener(e -> frame.requestClose());
		
		file.add(importSql);
		file.add(install);
		file.addSeparator();
		file.add(exit);
		add(file);
		
		JMenu help = new JMenu("Ajuda");
		JMenuItem about = new JMenuItem("Sobre");
		about.addActionListener(e -> showAbout(frame));
		help.add(about);
		add(help);
	}
	
	// =====================================================================================
	// ABOUT (Premium)
	// =====================================================================================
	private static void showAbout(Component parent)
	{
		// theme tokens (fallbacks defensivos)
		final Color bg = getColor("l2jdev.bg", new Color(0x10, 0x12, 0x16));
		final Color panel = getColor("l2jdev.panel", new Color(0x16, 0x19, 0x20));
		final Color surface = getColor("l2jdev.surface", new Color(0x12, 0x14, 0x18));
		final Color text = getColor("l2jdev.text", new Color(0xE6, 0xEA, 0xF2));
		final Color muted = getColor("l2jdev.muted", new Color(0xA8, 0xB0, 0xC0));
		final Color line = getColor("l2jdev.line", new Color(0x2A, 0x2F, 0x3A));
		final Color accent = getColor("l2jdev.accent", new Color(0x3D, 0x7A, 0xFF));
		
		final Font h1 = new Font("Segoe UI", Font.BOLD, 18);
		final Font h2 = new Font("Segoe UI", Font.PLAIN, 12);
		final Font body = new Font("Segoe UI", Font.PLAIN, 12);
		
		final Window owner = SwingUtilities.getWindowAncestor(parent);
		final JDialog dlg = new JDialog(owner, "Sobre - L2JDev Database Panel", JDialog.ModalityType.APPLICATION_MODAL);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_32x32.png").getImage());
		dlg.setIconImages(icons);
		
		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(bg);
		root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		
		// ===== Header (logo + title/subtitle) =====
		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);
		
		JLabel logo = new JLabel("DB");
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setVerticalAlignment(SwingConstants.CENTER);
		logo.setForeground(text);
		logo.setFont(new Font("Segoe UI", Font.BOLD, 14));
		logo.setOpaque(true);
		logo.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 45));
		logo.setBorder(BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 110), 1));
		logo.setPreferredSize(new Dimension(42, 42));
		
		JPanel titleBox = new JPanel();
		titleBox.setOpaque(false);
		titleBox.setLayout(new BorderLayout(0, 4));
		
		JLabel title = new JLabel("L2JDev Database Panel");
		title.setFont(h1);
		title.setForeground(text);
		
		JLabel subtitle = new JLabel("Administração de banco de dados para servidores L2J — rápido, seguro e direto.");
		subtitle.setFont(h2);
		subtitle.setForeground(muted);
		
		titleBox.add(title, BorderLayout.NORTH);
		titleBox.add(subtitle, BorderLayout.SOUTH);
		
		header.add(logo, BorderLayout.WEST);
		header.add(titleBox, BorderLayout.CENTER);
		
		// ===== Content grid (cards) =====
		JPanel grid = new JPanel(new GridBagLayout());
		grid.setOpaque(false);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 12, 0);
		
		// Card: Overview
		JPanel overview = card(panel, line);
		overview.setLayout(new BorderLayout(0, 10));
		overview.add(cardTitle("Resumo", text), BorderLayout.NORTH);
		
		JTextArea overviewText = new JTextArea("Ferramenta focada em produtividade no dia a dia do admin:\n" + "• Paginação e busca visual por tabelas\n" + "• Edição segura via PRIMARY KEY (quando disponível)\n" + "• Execução de .sql e instalador integrado (tools/sql)\n" + "• Compatível com MariaDB / MySQL\n");
		styleTextArea(overviewText, surface, text, body, line);
		overview.add(overviewText, BorderLayout.CENTER);
		
		grid.add(overview, gc);
		
		// Card: Developer / Runtime
		gc.gridy++;
		JPanel meta = card(panel, line);
		meta.setLayout(new GridBagLayout());
		
		GridBagConstraints mc = new GridBagConstraints();
		mc.gridx = 0;
		mc.gridy = 0;
		mc.weightx = 1;
		mc.fill = GridBagConstraints.HORIZONTAL;
		mc.insets = new Insets(0, 0, 8, 0);
		
		meta.add(cardTitle("Informações", text), mc);
		
		mc.gridy++;
		meta.add(metaRow("Desenvolvedor", "BAN - L2JDEV", text, muted, body), mc);
		
		mc.gridy++;
		meta.add(metaRow("Ambiente", "Java 11+", text, muted, body), mc);
		
		mc.gridy++;
		meta.add(metaRow("Foco", "Segurança • Organização • Produtividade", text, muted, body), mc);
		
		grid.add(meta, gc);
		
		// Card: Links
		gc.gridy++;
		gc.insets = new Insets(0, 0, 0, 0);
		
		JPanel links = card(panel, line);
		links.setLayout(new BorderLayout(0, 10));
		links.add(cardTitle("Contato e Links", text), BorderLayout.NORTH);
		
		JPanel linkList = new JPanel();
		linkList.setOpaque(false);
		linkList.setLayout(new BorderLayout(0, 8));
		
		linkList.add(linkRow("GitHub", "github.com/JulioPradoL2j", GITHUB_URL, parent, text, muted, accent, body), BorderLayout.NORTH);
		linkList.add(linkRow("WhatsApp", "+55 64 9 8408-3891", WHATSAPP_URL, parent, text, muted, accent, body), BorderLayout.SOUTH);
		
		links.add(linkList, BorderLayout.CENTER);
		
		grid.add(links, gc);
		
		// ===== Footer actions =====
		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);
		
		JPanel leftHint = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftHint.setOpaque(false);
		JLabel hint = new JLabel("Dica: clique nos links para abrir no navegador.");
		hint.setForeground(new Color(muted.getRed(), muted.getGreen(), muted.getBlue(), 200));
		hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		leftHint.add(hint);
		
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		actions.setOpaque(false);
		
		// use o seu tema (RoundedButtonUI) automaticamente
		JButton btnGit = themedButton("Abrir GitHub");
		btnGit.addActionListener(e -> openUrl(parent, GITHUB_URL, "GitHub"));
		
		JButton btnZap = themedButton("Falar no WhatsApp");
		// dá um “accent” leve sem quebrar o tema: clientProperty opcional
		btnZap.putClientProperty("l2jdev.variant", "accent");
		btnZap.addActionListener(e -> openUrl(parent, WHATSAPP_URL, "WhatsApp"));
		
		JButton ok = themedButton("OK");
		ok.addActionListener(e -> dlg.dispose());
		
		actions.add(btnGit);
		actions.add(btnZap);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(ok);
		
		footer.add(leftHint, BorderLayout.WEST);
		footer.add(actions, BorderLayout.EAST);
		
		root.add(header, BorderLayout.NORTH);
		root.add(grid, BorderLayout.CENTER);
		root.add(footer, BorderLayout.SOUTH);
		
		dlg.setContentPane(root);
		dlg.setMinimumSize(new Dimension(760, 620));
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}
	
	// =====================================================================================
	// UI helpers
	// =====================================================================================
	private static JPanel card(Color bg, Color line)
	{
		JPanel p = new JPanel();
		p.setBackground(bg);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(line.getRed(), line.getGreen(), line.getBlue(), 160), 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
		return p;
	}
	
	private static JLabel cardTitle(String title, Color fg)
	{
		JLabel l = new JLabel(title);
		l.setForeground(fg);
		l.setFont(new Font("Segoe UI", Font.BOLD, 13));
		return l;
	}
	
	private static void styleTextArea(JTextArea t, Color bg, Color fg, Font font, Color line)
	{
		t.setEditable(false);
		t.setFocusable(false);
		t.setOpaque(true);
		t.setBackground(bg);
		t.setForeground(fg);
		t.setFont(font);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(line.getRed(), line.getGreen(), line.getBlue(), 140), 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
	}
	
	private static JPanel metaRow(String k, String v, Color text, Color muted, Font font)
	{
		JPanel row = new JPanel(new BorderLayout(10, 0));
		row.setOpaque(false);
		
		JLabel lk = new JLabel(k + ":");
		lk.setForeground(muted);
		lk.setFont(font);
		
		JLabel lv = new JLabel(v);
		lv.setForeground(text);
		lv.setFont(font);
		
		row.add(lk, BorderLayout.WEST);
		row.add(lv, BorderLayout.CENTER);
		return row;
	}
	
	private static JPanel linkRow(String title, String label, String url, Component parent, Color text, Color muted, Color accent, Font font)
	{
		JPanel row = new JPanel(new BorderLayout(10, 0));
		row.setOpaque(false);
		
		JLabel lt = new JLabel(title + ":");
		lt.setForeground(muted);
		lt.setFont(font);
		
		JLabel link = new JLabel(label);
		link.setForeground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 235));
		link.setFont(font);
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		link.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				openUrl(parent, url, title);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				link.setForeground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 255));
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				link.setForeground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 235));
			}
		});
		
		row.add(lt, BorderLayout.WEST);
		row.add(link, BorderLayout.CENTER);
		return row;
	}
	
	private static JButton themedButton(String text)
	{
		JButton b = new JButton(text);
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// deixa o seu RoundedButtonUI fazer o trabalho
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		
		// fontes do tema (se existir)
		Font f = UIManager.getFont("Button.font");
		if (f != null)
			b.setFont(f);
		
		return b;
	}
	
	private static Color getColor(String key, Color fallback)
	{
		Color c = UIManager.getColor(key);
		return (c != null) ? c : fallback;
	}
	
	private static void openUrl(Component parent, String url, String label)
	{
		try
		{
			if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "Não foi possível abrir automaticamente.\n\n" + label + ":\n" + url, "Abrir link", javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			Desktop.getDesktop().browse(new URI(url));
		}
		catch (Exception e)
		{
			javax.swing.JOptionPane.showMessageDialog(parent, "Falha ao abrir o link.\n\n" + label + ":\n" + url + "\n\nDetalhes: " + e.getMessage(), "Erro ao abrir link", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}
}
