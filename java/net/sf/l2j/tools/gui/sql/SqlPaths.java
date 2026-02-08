package net.sf.l2j.tools.gui.sql;

import java.io.File;

public final class SqlPaths
{
	private SqlPaths()
	{
	}
	
	public static File resolveToolsSqlDir()
	{
		// Seu working dir no Eclipse já está em .../tools
		// então tools/sql pode ser:
		// - "./sql" (quando WD = tools/)
		// - "./tools/sql" (quando WD = root do repo)
		File a = new File("./sql");
		if (a.isDirectory())
			return a;
		
		File b = new File("./tools/sql");
		if (b.isDirectory())
			return b;
		
		return null;
	}
}
