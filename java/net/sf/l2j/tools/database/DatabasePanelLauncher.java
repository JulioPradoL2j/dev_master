package net.sf.l2j.tools.database;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.sf.l2j.tools.gui.DatabaseMainFrame;
import net.sf.l2j.tools.mariadb.DatabaseFactory;
import net.sf.l2j.tools.mariadb.MariaDbData;

public class DatabasePanelLauncher
{
	public static void main(String[] args)
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("DatabasePanelLauncher").setLevel(Level.OFF);
		
		System.out.println("L2JDev Database Panel starting...");
		
		MariaDbData.getInstance();
		DatabaseFactory.init();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try
			{
				DatabaseFactory.shutdown();
			}
			catch (Exception ignored)
			{
			}
		}, "db-shutdown-hook"));
		
		SwingUtilities.invokeLater(() -> {
			DatabaseMainFrame frame = new DatabaseMainFrame();
			frame.setVisible(true);
		});
	}
}
