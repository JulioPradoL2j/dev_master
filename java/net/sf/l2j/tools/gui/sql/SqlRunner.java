package net.sf.l2j.tools.gui.sql;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.l2j.tools.gui.DataViewPanel;
import net.sf.l2j.tools.gui.DatabaseMainFrame;

public final class SqlRunner
{
	private SqlRunner()
	{
	}
	
	public static void runSingleSqlFile(DatabaseMainFrame mainFrame, DataViewPanel dataView)
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("SQL files (*.sql)", "sql"));
		fc.setDialogTitle("Select .sql file");
		
		if (fc.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fc.getSelectedFile();
		if (f == null || !f.isFile())
			return;
		
		new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
 
				SqlFileExecutor.execute(f);
				return null;
			}
			
			@Override
			protected void done()
			{
				try
				{
					get(); // garante sucesso
					
					mainFrame.refreshUiAfterDbChange();
					
					JOptionPane.showMessageDialog(mainFrame, "SQL executado com sucesso:\n" + f.getName(), "SQL", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(mainFrame, String.valueOf(e.getMessage()), "SQL Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}
}
