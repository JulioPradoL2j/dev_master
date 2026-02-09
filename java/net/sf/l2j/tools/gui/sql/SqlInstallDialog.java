package net.sf.l2j.tools.gui.sql;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

/**
 * SqlInstallDialog v2.1 - Clean logger: phases + compact progress (no spam) - Progress bound to SwingWorker.setProgress() - Best-effort cancel (real cancel requires SqlInstaller to check interrupted/cancel flag)
 */
public final class SqlInstallDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public static void open(DatabaseMainFrame owner, DataViewPanel dataView)
	{
		SqlInstallDialog d = new SqlInstallDialog(owner, dataView);
		d.setVisible(true);
	}
	
	private final JTextArea log = new JTextArea();
	private final JProgressBar bar = new JProgressBar(0, 100);
	
	private final JButton btnFull = new JButton("FULL (drop + create + import)");
	private final JButton btnClose = new JButton("Close");
	
	private final DatabaseMainFrame mainFrame;
	private volatile SwingWorker<Void, String> activeWorker;
	
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
		bar.setValue(0);
		bar.setString("Idle");
		bottom.add(bar, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.add(btnFull);
		buttons.add(btnClose);
		bottom.add(buttons, BorderLayout.SOUTH);
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		btnFull.addActionListener(e -> runInstall(InstallMode.FULL));
		btnClose.addActionListener(e -> onCloseClicked());
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				cancelActiveWorker();
			}
		});
	}
	
	private void onCloseClicked()
	{
		SwingWorker<Void, String> w = activeWorker;
		if (w != null && !w.isDone())
		{
			bar.setString("Cancelling...");
			cancelActiveWorker();
			return;
		}
		dispose();
	}
	
	private void runInstall(InstallMode mode)
	{
		cancelActiveWorker();
		
		setControlsEnabled(false);
		log.setText("");
		bar.setValue(0);
		bar.setString("Starting...");
		
		final SwingWorker<Void, String> worker = new SwingWorker<>()
		{
			// ===== clean logger state (per-run) =====
			private String lastPhase = "";
			private int lastPercent = -1;
			private long lastUiLogAtMs = 0L;
			
			@Override
			protected Void doInBackground() throws Exception
			{
				File sqlDir = SqlPaths.resolveToolsSqlDir();
				if (sqlDir == null || !sqlDir.isDirectory())
					throw new IllegalStateException("tools/sql folder not found (working dir issue).");
				
				uiLog("• Folder: " + sqlDir.getAbsolutePath());
				uiHeader("Installing");
				
				SqlInstaller installer = new SqlInstaller(sqlDir, mode, (msg, current, total) -> {
					onInstallerProgress(msg, current, total);
					
					int percent = (total <= 0) ? 0 : (int) Math.round((current * 100.0) / total);
					setProgress(clamp(percent, 0, 100));
				});
				
				installer.run();
				return null;
			}
			
			// publish wrapper (do SwingWorker!)
			private void uiLog(String line)
			{
				if (line == null || line.isEmpty())
					return;
				publish(line);
			}
			
			private void uiHeader(String title)
			{
				uiLog("\n== " + title + " ==");
			}
			
			private void onInstallerProgress(String msg, int current, int total)
			{
				String phase = inferPhase(msg);
				
				int percent = (total <= 0) ? 0 : (int) Math.round((current * 100.0) / total);
				long now = System.currentTimeMillis();
				
				// 1) Phase change -> log once
				if (!phase.equals(lastPhase))
				{
					lastPhase = phase;
					uiHeader(phase);
					lastUiLogAtMs = now;
					lastPercent = percent;
					return;
				}
				
				// 2) Throttle periodic line (every ~8% or 900ms)
				boolean percentJump = (lastPercent < 0) || (percent >= lastPercent + 8);
				boolean timeJump = (now - lastUiLogAtMs) >= 900;
				
				if (percentJump || timeJump)
				{
					lastPercent = percent;
					lastUiLogAtMs = now;
					
					if (total > 0)
						uiLog("• " + phase + " " + current + "/" + total);
					else
						uiLog("• " + phase);
				}
				
				// 3) forward only important lines
				if (msg != null)
				{
					String up = msg.toUpperCase();
					if (up.contains("ERROR") || up.contains("FAILED") || up.contains("WARN"))
						uiLog("! " + trimOneLine(msg));
				}
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
				log.setCaretPosition(log.getDocument().getLength());
			}
			
			@Override
			protected void done()
			{
				try
				{
					if (isCancelled())
					{
						bar.setString("Cancelled.");
						uiHeader("Cancelled");
						return;
					}
					
					get();
					
					bar.setValue(100);
					bar.setString("Done.");
					uiLog("• Done.");
					
					if (mainFrame != null)
						mainFrame.refreshUiAfterDbChange();
				}
				catch (Exception e)
				{
					bar.setString("Error.");
					appendError(e);
				}
				finally
				{
					if (activeWorker == this)
						activeWorker = null;
					
					setControlsEnabled(true);
				}
			}
		};
		
		// Bind progress -> bar
		final PropertyChangeListener pcl = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (!"progress".equals(evt.getPropertyName()))
					return;
				
				Object v = evt.getNewValue();
				int p = (v instanceof Integer) ? (Integer) v : 0;
				
				bar.setValue(clamp(p, 0, 100));
				bar.setString(p + "%");
			}
		};
		
		worker.addPropertyChangeListener(pcl);
		worker.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("state".equals(evt.getPropertyName()) && evt.getNewValue() == SwingWorker.StateValue.DONE)
				{
					worker.removePropertyChangeListener(pcl);
					worker.removePropertyChangeListener(this);
				}
			}
		});
		
		activeWorker = worker;
		worker.execute();
	}
	
	private static String inferPhase(String msg)
	{
		if (msg == null || msg.trim().isEmpty())
			return "Working";
		
		String m = msg.toLowerCase();
		
		if (m.contains("drop"))
			return "Dropping database";
		if (m.contains("create database") || m.contains("creating database"))
			return "Creating database";
		if (m.contains("import"))
			return "Importing SQL";
		if (m.contains("truncate"))
			return "Truncating";
		if (m.contains("connect"))
			return "Connecting";
		if (m.contains("ok"))
			return "Importing SQL";
		
		return "Working";
	}
	
	private static String trimOneLine(String s)
	{
		String t = s.replace('\r', ' ').replace('\n', ' ').trim();
		if (t.length() > 180)
			return t.substring(0, 177) + "...";
		return t;
	}
	
	private void cancelActiveWorker()
	{
		SwingWorker<Void, String> w = activeWorker;
		if (w != null && !w.isDone())
		{
			try
			{
				w.cancel(true);
			}
			catch (Exception ignored)
			{
			}
		}
	}
	
	private void appendError(Exception e)
	{
		Throwable t = e;
		while (t.getCause() != null)
			t = t.getCause();
		
		String msg = (t.getMessage() != null && !t.getMessage().trim().isEmpty()) ? t.getMessage() : t.toString();
		log.append("\nERROR: " + msg + "\n");
		log.setCaretPosition(log.getDocument().getLength());
	}
	
	private void setControlsEnabled(boolean enabled)
	{
		btnFull.setEnabled(enabled);
		btnClose.setEnabled(true);
		btnClose.setText(enabled ? "Close" : "Cancel");
	}
	
	private static int clamp(int v, int min, int max)
	{
		return Math.max(min, Math.min(max, v));
	}
}
