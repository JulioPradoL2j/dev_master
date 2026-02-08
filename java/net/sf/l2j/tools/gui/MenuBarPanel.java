package net.sf.l2j.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sf.l2j.tools.gui.sql.SqlInstallDialog;
import net.sf.l2j.tools.gui.sql.SqlRunner;

public final class MenuBarPanel extends JMenuBar
{
	private static final long serialVersionUID = 1L;
	
	// Links oficiais
	private static final String GITHUB_URL = "https://github.com/JulioPradoL2j";
	private static final String WHATSAPP_URL = "https://wa.me/5564984083891"; // abre chat direto
	
	public MenuBarPanel(DatabaseMainFrame frame, DataViewPanel dataView)
	{
		// =================
		// Menu Arquivo
		// =================
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
		
		// =================
		// Menu Ajuda
		// =================
		JMenu help = new JMenu("Ajuda");
		
		JMenuItem about = new JMenuItem("Sobre");
		about.addActionListener(e -> showAbout(frame));
		
		help.addSeparator();
		help.add(about);
		
		add(help);
	}
	
	private static void showAbout(Component parent)
	{
		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
		root.setBackground(new Color(0x0F, 0x12, 0x16));
		
		// Header (título)
		JTextArea title = new JTextArea("L2JDev Database Panel");
		title.setEditable(false);
		title.setOpaque(false);
		title.setForeground(new Color(235, 235, 235));
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		title.setFocusable(false);
		
		JTextArea subtitle = new JTextArea("Ferramenta de banco de dados focada em servidores L2J");
		subtitle.setEditable(false);
		subtitle.setOpaque(false);
		subtitle.setForeground(new Color(175, 175, 175));
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subtitle.setFocusable(false);
		
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.add(title, BorderLayout.NORTH);
		header.add(subtitle, BorderLayout.SOUTH);
		
		// Corpo (texto)
		JTextArea body = new JTextArea("Desenvolvedor: BAN - L2JDEV\n\n" + "Objetivo:\n" + "Uma alternativa leve e direta para administrar bases L2J,\n" + "com foco em segurança e produtividade.\n\n" + "Principais recursos:\n" + "• Listagem e visualização de tabelas com paginação\n" + "• Edição segura por PRIMARY KEY (quando disponível)\n" + "• Execução de arquivos .sql e instalador integrado\n" + "• Compatível com MariaDB / MySQL\n\n" + "Ambiente:\n" + "• Java 11+\n");
		body.setEditable(false);
		body.setOpaque(false);
		body.setForeground(new Color(210, 210, 210));
		body.setFont(new Font("Consolas", Font.PLAIN, 12));
		body.setFocusable(false);
		
		// Links clicáveis
		JComponent githubLink = linkLabel("GitHub: " + GITHUB_URL, GITHUB_URL, parent);
		JComponent whatsappLink = linkLabel("WhatsApp: +55 64 9 8408-3891", WHATSAPP_URL, parent);
		
		JPanel links = new JPanel(new BorderLayout(0, 6));
		links.setOpaque(false);
		links.add(githubLink, BorderLayout.NORTH);
		links.add(whatsappLink, BorderLayout.SOUTH);
		
		// Botões (CTA)
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttons.setOpaque(false);
		
		JButton btnGithub = primaryButton("Abrir GitHub");
		btnGithub.addActionListener(e -> openUrl(parent, GITHUB_URL, "GitHub"));
		
		JButton btnWhatsapp = accentButton("Falar no WhatsApp");
		btnWhatsapp.addActionListener(e -> openUrl(parent, WHATSAPP_URL, "WhatsApp"));
		
		// Observação: vamos fechar pelo JOptionPane return, então o Fechar pode só fechar pelo OK,
		// mas deixo o botão como "conforto visual".
		
		buttons.add(btnGithub);
		buttons.add(btnWhatsapp);
		
		JPanel center = new JPanel(new BorderLayout(0, 10));
		center.setOpaque(false);
		center.add(body, BorderLayout.CENTER);
		center.add(links, BorderLayout.SOUTH);
		
		root.add(header, BorderLayout.NORTH);
		root.add(center, BorderLayout.CENTER);
		root.add(buttons, BorderLayout.SOUTH);
		
		UIManager.put("OptionPane.background", new Color(0x0F, 0x12, 0x16));
		UIManager.put("Panel.background", new Color(0x0F, 0x12, 0x16));
		
		JOptionPane.showMessageDialog(parent, root, "Sobre - L2JDev Database Panel", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private static JComponent linkLabel(String text, String url, Component parent)
	{
		JTextArea t = new JTextArea(text);
		t.setEditable(false);
		t.setOpaque(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		t.setForeground(new Color(130, 175, 255));
		t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		t.setFocusable(false);
		t.setAlignmentX(SwingConstants.LEFT);
		
		t.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				openUrl(parent, url, "Link");
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				t.setForeground(new Color(170, 205, 255));
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				t.setForeground(new Color(130, 175, 255));
			}
		});
		
		return t;
	}
	
	private static JButton primaryButton(String text)
	{
		JButton b = new JButton(text);
		b.setFocusPainted(false);
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setForeground(new Color(245, 245, 245));
		b.setBackground(new Color(70, 110, 255));
		b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setOpaque(true);
		b.setBorderPainted(false);
		return b;
	}
	
	private static JButton accentButton(String text)
	{
		JButton b = new JButton(text);
		b.setFocusPainted(false);
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setForeground(new Color(20, 20, 20));
		b.setBackground(new Color(255, 180, 60));
		b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setOpaque(true);
		b.setBorderPainted(false);
		return b;
	}
	
	private static void openUrl(Component parent, String url, String label)
	{
		try
		{
			if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
			{
				JOptionPane.showMessageDialog(parent, "Não foi possível abrir automaticamente.\n\n" + label + ":\n" + url, "Abrir link", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			Desktop.getDesktop().browse(new URI(url));
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(parent, "Falha ao abrir o link.\n\n" + label + ":\n" + url + "\n\nDetalhes: " + e.getMessage(), "Erro ao abrir link", JOptionPane.ERROR_MESSAGE);
		}
	}
}
