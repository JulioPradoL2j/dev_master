package net.sf.l2j.mods.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.manager.FakePlayerManager;

 
public class FakePlayerRestoreEngine
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerRestoreEngine.class.getName());
	private final String SELECT_FAKE_ACCOUNTS = "SELECT login FROM accounts WHERE access_level = -1 AND login LIKE 'AutoPilot_%'";
	private final String SELECT_CHARACTERS_BY_ACCOUNT = "SELECT obj_id, char_name, classid, face, hairStyle, hairColor, sex, x, y, z " + "FROM characters WHERE account_name = ?";
	
	public void collectAll()
	{
		
		try (Connection con = ConnectionPool.getConnection(); PreparedStatement psAcc = con.prepareStatement(SELECT_FAKE_ACCOUNTS); ResultSet rsAcc = psAcc.executeQuery())
		{
			while (rsAcc.next())
			{
				String account = rsAcc.getString("login");
				
				try (PreparedStatement ps = con.prepareStatement(SELECT_CHARACTERS_BY_ACCOUNT))
				{
					ps.setString(1, account);
					
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							int objectId = rs.getInt("obj_id");
							if (FakePlayerManager.getInstance().getPlayer(objectId) == null)
							{
								FakePlayerRestoreQueue.add(objectId);
								
							}
							
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.severe("Error collecting fake players: " + e.getMessage());
		}
	}
	
	public void restoreSingle(int objectId)
	{
		try
		{
			FakePlayer fake = FakePlayer.restore(objectId);
			CharNameTable.getInstance().register(fake);
			FakePlayerManager.getInstance().register(fake);
			L2GameClient client = new L2GameClient(null);
			client.setActiveChar(fake);
			fake.setClient(client);
			
			fake.spawnMe();
			fake.assignDefaultAI();
		}
		catch (Exception e)
		{
			LOGGER.warning("Failed restoring fake " + objectId + ": " + e.getMessage());
		}
	}
	
	public static FakePlayerRestoreEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakePlayerRestoreEngine INSTANCE = new FakePlayerRestoreEngine();
	}
	
}