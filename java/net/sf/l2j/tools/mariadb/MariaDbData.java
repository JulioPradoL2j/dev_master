package net.sf.l2j.tools.mariadb;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.templates.StatsSet;

import org.w3c.dom.Document;

public class MariaDbData implements IXmlReader
{
	private static final String[] CANDIDATES =
	{
		"./mariadb.xml"
	};
	
	private String _host = "127.0.0.1";
	private int _port = 3306;
	private String _database = "l2jdb";
	private String _user = "root";
	private String _password = "root";
	private boolean _useSSL = false;
	private String _serverTimezone = "America/Sao_Paulo";
	
	private Path _loadedFrom;
	
	private MariaDbData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_loadedFrom = resolveConfigPath();
		if (_loadedFrom == null)
			return;
		
		parseFile(_loadedFrom.toString());
	}
	
	public void reload()
	{
		load();
	}
	
	private static Path resolveConfigPath()
	{
		for (String rel : CANDIDATES)
		{
			Path p = Paths.get(rel).normalize();
			if (Files.exists(p))
				return p;
		}
		return null;
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "mariadb", root -> {
			forEach(root, "db", dbNode -> {
				final StatsSet attrs = parseAttributes(dbNode);
				
				_host = attrs.getString("host", _host);
				_port = attrs.getInteger("port", _port);
				_database = attrs.getString("database", _database);
				_user = attrs.getString("user", _user);
				_password = attrs.getString("password", _password);
				
				_useSSL = attrs.getBool("useSSL", _useSSL);
				_serverTimezone = attrs.getString("serverTimezone", _serverTimezone);
			});
		});
	}
	
	public String buildJdbcUrl()
	{
		return "jdbc:mariadb://" + _host + ":" + _port + "/" + _database + "?useSSL=" + _useSSL + "&serverTimezone=" + _serverTimezone;
	}
	
	public String getUser()
	{
		return _user;
	}
	
	public String getPassword()
	{
		return _password;
	}
	
	public String getHost()
	{
		return _host;
	}
	
	public int getPort()
	{
		return _port;
	}
	
	public String getDatabase()
	{
		return _database;
	}
	
	public boolean isUseSSL()
	{
		return _useSSL;
	}
	
	public String getServerTimezone()
	{
		return _serverTimezone;
	}
	
	public String buildAdminJdbcUrl()
	{
		// sem schema (só server)
		return "jdbc:mariadb://" + _host + ":" + _port + "/?useSSL=" + _useSSL + "&serverTimezone=" + _serverTimezone;
	}
	
	public static MariaDbData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final MariaDbData INSTANCE = new MariaDbData();
	}
}
