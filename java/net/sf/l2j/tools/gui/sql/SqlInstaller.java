package net.sf.l2j.tools.gui.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.l2j.tools.mariadb.DatabaseFactory;
import net.sf.l2j.tools.mariadb.MariaDbData;

public final class SqlInstaller
{
	public interface ProgressListener
	{
		void onProgress(String message, int current, int total);
	}
	
	private final File sqlDir;
	private final InstallMode mode;
	private final ProgressListener listener;
	
	public SqlInstaller(File sqlDir, InstallMode mode, ProgressListener listener)
	{
		this.sqlDir = sqlDir;
		this.mode = mode;
		this.listener = listener;
	}
	
	public void run() throws Exception
	{
		List<File> files = listSqlFiles(sqlDir, mode);
		
		if (files.isEmpty())
			throw new IllegalStateException("No .sql files found in: " + sqlDir.getAbsolutePath());
		
		if (mode == InstallMode.FULL)
		{
			dropAndCreateDatabase();
		}
		
		// Importa usando conexão do pool (já aponta para DB_NAME).
		// Se FULL, após CREATE DATABASE, o pool ainda aponta pro mesmo schema.
		// Se seu URL inclui /l2jdb, está OK depois de recriar.
		// Se precisar, você pode reinicializar DatabaseFactory aqui.
		listener.onProgress("Importing " + files.size() + " SQL files...", 0, files.size());
		
		int i = 0;
		for (File f : files)
		{
			i++;
			listener.onProgress("Importing: " + f.getName(), i - 1, files.size());
			executeSqlFile(f);
			listener.onProgress("OK: " + f.getName(), i, files.size());
		}
		
		listener.onProgress("Install completed.", files.size(), files.size());
	}
	
	private static List<File> listSqlFiles(File dir, InstallMode mode)
	{
		File[] arr = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".sql"));
		if (arr == null)
			return new ArrayList<>();
		
		List<File> list = new ArrayList<>(Arrays.asList(arr));
		
		// ordena por nome (igual seu .bat)
		list.sort(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
		
		 
		
		return list;
	}
	
	private static void dropAndCreateDatabase() throws Exception
	{
	    final MariaDbData cfg = MariaDbData.getInstance();
	    final String db = cfg.getDatabase();

	    // 1) conecta no servidor (sem schema)
	    try (Connection con = DriverManager.getConnection(cfg.buildAdminJdbcUrl(), cfg.getUser(), cfg.getPassword());
	         Statement st = con.createStatement())
	    {
	        st.execute("DROP DATABASE IF EXISTS `" + db.replace("`", "``") + "`");
	        st.execute("CREATE DATABASE `" + db.replace("`", "``") + "`");
	    }

	    // 2) recicla o pool, porque ele tinha conexões dentro do schema antigo
	    DatabaseFactory.shutdown();
	    DatabaseFactory.init();
	}
	private static void executeSqlFile(File file) throws Exception
	{
		// Simples e funciona bem se seus .sql não tiverem DELIMITER / PROCEDURE complexas.
		// Para L2J geralmente é CREATE TABLE + INSERT + ALTER.
		String sql = readAll(file);
		
		// split por ';' de forma simples
		List<String> statements = SqlSplitter.splitStatements(sql);
		
		try (Connection con = DatabaseFactory.getConnection(); Statement st = con.createStatement())
		{
			for (String s : statements)
			{
				String trimmed = s.trim();
				if (trimmed.isEmpty())
					continue;
				
				st.execute(trimmed);
			}
		}
	}
	
	private static String readAll(File f) throws Exception
	{
		StringBuilder sb = new StringBuilder((int) Math.min(Integer.MAX_VALUE, Math.max(1024, f.length())));
		try (BufferedReader br = new BufferedReader(new FileReader(f)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				// ignora comentários simples
				String t = line.trim();
				if (t.startsWith("--") || t.startsWith("#"))
					continue;
				
				sb.append(line).append('\n');
			}
		}
		return sb.toString();
	}
}
