package net.sf.l2j.tools.mariadb;

import java.sql.Connection;
import java.sql.SQLException;

import org.mariadb.jdbc.MariaDbPoolDataSource;

public final class DatabaseFactory
{
	private DatabaseFactory()
	{
		throw new IllegalStateException("Utility class");
	}
	
	private static MariaDbPoolDataSource _source;
	
	public static void init()
	{
		try
		{
			final MariaDbData cfg = MariaDbData.getInstance();
			
			_source = new MariaDbPoolDataSource();
			_source.setUser(cfg.getUser());
			_source.setPassword(cfg.getPassword());
			_source.setUrl(cfg.buildJdbcUrl());
		}
		catch (SQLException e)
		{
			_source = null;
			// Você pode preferir JOptionPane aqui, mas init roda antes do Swing.
			throw new RuntimeException("Failed to init MariaDB pool.", e);
		}
	}
	
	public static void shutdown()
	{
		if (_source != null)
		{
			_source.close();
			_source = null;
		}
	}
	
	public static Connection getConnection() throws SQLException
	{
		if (_source == null)
			throw new SQLException("DatabaseFactory not initialized.");
		
		return _source.getConnection();
	}
}
