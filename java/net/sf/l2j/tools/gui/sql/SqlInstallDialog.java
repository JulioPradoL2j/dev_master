package net.sf.l2j.tools.gui.sql;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.sf.l2j.tools.gui.DataViewPanel;
import net.sf.l2j.tools.gui.DatabaseMainFrame;

public final class SqlInstallDialog extends JDialog
{
	
	private static final long serialVersionUID = 1L;
	
	public static void open(DatabaseMainFrame owner, DataViewPanel dataView)
	{
		SqlInstallDialog d = new SqlInstallDialog(owner, dataView);
		d.setVisible(true);
	}
	
	private final JTextArea log = new JTextArea();
	private final JProgressBar bar = new JProgressBar();
	private final JButton btnFull = new JButton("FULL (drop + create + import)");

	private final JButton btnClose = new JButton("Close");
	
	private final DatabaseMainFrame mainFrame;
	
	private SqlInstallDialog(DatabaseMainFrame owner, DataViewPanel dataView)
	{
		super(owner, "SQL Installer", true);
		this.mainFrame = owner;
		setLayout(new BorderLayout(10, 10));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(720, 420));
		setLocationRelativeTo(owner);
		
		log.setEditable(false);
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		top.add(new JLabel("Install from tools/sql using JDBC (no mysql.exe needed)."), BorderLayout.CENTER);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		center.add(new JScrollPane(log), BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new BorderLayout(10, 10));
		bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		bar.setStringPainted(true);
		bottom.add(bar, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.add(btnFull);
	
		buttons.add(btnClose);
		
		bottom.add(buttons, BorderLayout.SOUTH);
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		btnClose.addActionListener(e -> dispose());
		
		btnFull.addActionListener(e -> runInstall(InstallMode.FULL));
		
	}
	
	private void runInstall(InstallMode mode)
	{
		setControlsEnabled(false);
		log.setText("");
		bar.setValue(0);
		bar.setString("Starting...");
		
		new SwingWorker<Void, String>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				File sqlDir = SqlPaths.resolveToolsSqlDir();
				if (sqlDir == null || !sqlDir.isDirectory())
					throw new IllegalStateException("tools/sql folder not found (working dir issue).");
				
				publish("SQL folder: " + sqlDir.getAbsolutePath());
				
				SqlInstaller installer = new SqlInstaller(sqlDir, mode, this::publishProgress);
				
				installer.run();
				
				return null;
			}
			
			private void publishProgress(String msg, int current, int total)
			{
				publish(msg);
				
				int percent = total <= 0 ? 0 : (int) Math.round((current * 100.0) / total);
				setProgress(Math.max(0, Math.min(100, percent)));
			}
			
			@Override
			protected void process(java.util.List<String> chunks)
			{
				for (String s : chunks)
				{
					log.append(s);
					if (!s.endsWith("\n"))
						log.append("\n");
				}
			}
			
			@Override
			protected void done()
			{
				try
				{
					get();
					bar.setValue(100);
					bar.setString("Done.");
					
					if (mainFrame != null)
						mainFrame.refreshUiAfterDbChange(); // ✅ atualiza esquerda + direita
				}
				catch (Exception e)
				{
					bar.setString("Error.");
					log.append("\nERROR: " + e.getMessage() + "\n");
				}
				finally
				{
					setControlsEnabled(true);
				}
			}
			
		}.execute();
		
		// vincula progress
		// (SwingWorker progress property)
		// opcional: listener se você quiser
	}
	
	private void setControlsEnabled(boolean enabled)
	{
		btnFull.setEnabled(enabled);
		
		btnClose.setEnabled(enabled);
	}
}
