package net.sf.l2j.gameserver.network;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.gameserver.ConnectionPool;

public class OfflineMemos
{
	private static final String SELECT_INT = "SELECT val FROM character_memo WHERE charid=? AND var=?";
	
	public int getInt(int objectId, String key, int def)
	{
		try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_INT))
		{
			ps.setInt(1, objectId);
			ps.setString(2, key);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					return rs.getInt("val");
				}
			}
		}
		catch (Exception e)
		{
			// silencioso de propósito (select char é crítico)
		}
		return def;
	}
	
	public static OfflineMemos getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OfflineMemos INSTANCE = new OfflineMemos();
	}
}
