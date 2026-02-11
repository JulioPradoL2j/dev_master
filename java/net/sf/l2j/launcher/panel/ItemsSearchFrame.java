package net.sf.l2j.launcher.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ItemsSearchFrame
{
	private static final File ITEMS_ROOT = new File("../game/data/xml/items");
	
	/* ================= SEARCH ================= */
	
	private static void searchItems(JTextField txtItemName, DefaultTableModel model, JTable table)
	{
		String query = txtItemName.getText().trim().toLowerCase();
		model.setRowCount(0);
		
		if (query.isEmpty())
			return;
		
		boolean searchById = query.matches("\\d+");
		
		// evita busca pesada por nome curto
		if (!searchById && query.length() < 2)
			return;
		
		List<File> files = listXmlRecursively(ITEMS_ROOT);
		if (files.isEmpty())
			return;
		
		for (File file : files)
		{
			try
			{
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				
				NodeList items = doc.getElementsByTagName("item");
				
				for (int i = 0; i < items.getLength(); i++)
				{
					Element item = (Element) items.item(i);
					
					String id = item.getAttribute("id");
					String name = item.getAttribute("name");
					
					if (name == null || name.isEmpty() || id == null || id.isEmpty())
						continue;
					
					boolean match = searchById ? id.equals(query) : name.toLowerCase().contains(query);
					
					if (match)
					{
						model.addRow(new Object[]
						{
							Integer.parseInt(id),
							name
						});
					}
				}
			}
			catch (Exception e)
			{
				// ignora XML inválido sem quebrar a busca
			}
		}
		
		if (model.getRowCount() > 0)
			table.setRowSelectionInterval(0, 0);
	}
	
	/* ================= UI ================= */
	
	public static void openSearchItem()
	{
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "l2jdev_32x32.png").getImage());
		
		JFrame frame = new JFrame("Search Item");
		frame.setSize(420, 260);
		frame.setMinimumSize(new Dimension(420, 260));
		
		frame.setLayout(new BorderLayout());
		frame.setIconImages(icons);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/* ================= TOPO ================= */
		
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		
		JLabel lblItemName = new JLabel("Item Name / ID:");
		JTextField txtItemName = new JTextField(20);
		JButton btnSearch = new JButton("Search");
		
		searchPanel.add(lblItemName);
		searchPanel.add(txtItemName);
		searchPanel.add(btnSearch);
		
		frame.add(searchPanel, BorderLayout.NORTH);
		
		/* ================= TABELA ================= */
		
		DefaultTableModel model = new DefaultTableModel(new Object[]
		{
			"Item ID",
			"Item Name"
		}, 0)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		// depois de criar a JTable
		
		JTable table = new JTable(model);
		table.setRowHeight(22);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		
		// Ajuste de colunas
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(0).setMaxWidth(90);
		
		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(390, 170));
		
		frame.add(scroll, BorderLayout.CENTER);
		
		/* ================= EVENTS ================= */
		
		Runnable doSearch = () -> searchItems(txtItemName, model, table);
		
		btnSearch.addActionListener(e -> doSearch.run());
		txtItemName.addActionListener(e -> doSearch.run());
		
		txtItemName.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				doSearch.run();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				doSearch.run();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				doSearch.run();
			}
		});
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static List<File> listXmlRecursively(File root)
	{
		if (root == null || !root.exists())
			return List.of();
		
		try (Stream<Path> walk = Files.walk(root.toPath()))
		{
			return walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().toLowerCase().endsWith(".xml")).map(Path::toFile).collect(java.util.stream.Collectors.toList());
		}
		catch (Exception e)
		{
			return List.of();
		}
	}
	
}
