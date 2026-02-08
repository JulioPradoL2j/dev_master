package net.sf.l2j.mods.gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.enums.FakeTeleportPoint;
import net.sf.l2j.mods.factory.FakePlayerFactory;
import net.sf.l2j.mods.manager.FakePlayerManager;

 
public final class FakeAdminService
{
	private static final Logger LOGGER = Logger.getLogger(FakeAdminService.class.getName());
	
	private static final String SELECT_FAKE_ACCOUNTS = "SELECT login FROM accounts WHERE access_level = -1 AND login LIKE 'AutoPilot_%'";
	
	private static final String SELECT_CHARS_BY_ACCOUNT = "SELECT obj_id, char_name, classid, level, x, y, z FROM characters WHERE account_name = ?";
	
	private FakeAdminService()
	{
	}
	
	public static class FakeRow
	{
		public final int objectId;
		public final String name;
		public final ClassId classId;
		public final int level;
		public final int x, y, z;
		public final boolean online;
		public final String state;
		
		public FakeRow(int objectId, String name, ClassId classId, int level, int x, int y, int z, boolean online, String state)
		{
			this.objectId = objectId;
			this.name = name;
			this.classId = classId;
			this.level = level;
			this.x = x;
			this.y = y;
			this.z = z;
			this.online = online;
			this.state = state;
		}
	}
	
	public static List<FakeRow> loadAllRows()
	{
		Map<Integer, FakeRow> rows = new LinkedHashMap<>();
		
		try (Connection con = ConnectionPool.getConnection(); PreparedStatement psAcc = con.prepareStatement(SELECT_FAKE_ACCOUNTS); ResultSet rsAcc = psAcc.executeQuery())
		{
			while (rsAcc.next())
			{
				String account = rsAcc.getString("login");
				
				try (PreparedStatement ps = con.prepareStatement(SELECT_CHARS_BY_ACCOUNT))
				{
					ps.setString(1, account);
					
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							int objectId = rs.getInt("obj_id");
							String name = rs.getString("char_name");
							int classid = rs.getInt("classid");
							int level = rs.getInt("level");
							
							int x = rs.getInt("x");
							int y = rs.getInt("y");
							int z = rs.getInt("z");
							
							ClassId cid = (classid >= 0 && classid < ClassId.VALUES.length) ? ClassId.VALUES[classid] : null;
							
							FakePlayer fp = FakePlayerManager.getInstance().getPlayer(objectId);
							
							boolean online = (fp != null);
							String state = "OFFLINE";
							
							if (fp != null)
							{
								state = fp.getCurrentAction();
								x = fp.getX();
								y = fp.getY();
								z = fp.getZ();
							}
							
							rows.put(objectId, new FakeRow(objectId, name, cid, level, x, y, z, online, state));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("Failed loadAllRows: " + e.getMessage());
		}
		
		// Edge case: fake online que não está no DB query (raro)
		for (FakePlayer fp : FakePlayerManager.getInstance().getFakePlayers())
		{
			if (rows.containsKey(fp.getObjectId()))
				continue;
			
			rows.put(fp.getObjectId(), new FakeRow(fp.getObjectId(), fp.getName(), fp.getClassId(), fp.getStat().getLevel(), fp.getX(), fp.getY(), fp.getZ(), true, fp.getCurrentAction()));
		}
		
		return new ArrayList<>(rows.values());
	}
	
	// ========================= CREATE (new) =========================
	public static List<Integer> createAtPoint(FakeTeleportPoint point, ClassId classId, int count, int radius)
	{
		if (point == null)
			throw new IllegalArgumentException("Select a Point.");
		if (classId == null)
			throw new IllegalArgumentException("Select a ClassId.");
		if (count <= 0)
			return List.of();
		
		if (!FakePlayer.getAllAIs().containsKey(classId))
			throw new IllegalStateException("No FakePlayer AI registered for: " + classId);
		
		Location base = point.getLocation();
		List<Integer> createdIds = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int x = base.getX();
			int y = base.getY();
			int z = base.getZ();
			
			if (radius > 0)
			{
				x += Rnd.get(-radius, radius);
				y += Rnd.get(-radius, radius);
			}
			
			FakePlayer fp = FakePlayerFactory.create(classId.getId(), x, y, z);
			createdIds.add(fp.getObjectId());
		}
		
		return createdIds;
	}
	
	// ========================= SPAWN Selected (Pinned) =========================
	public static int spawnPinnedOfflineToPoint(Set<Integer> pinnedIds, FakeTeleportPoint point, int radius)
	{
		if (pinnedIds == null || pinnedIds.isEmpty())
			throw new IllegalStateException("No pinned targets.");
		if (point == null)
			throw new IllegalArgumentException("Select a Point.");
		
		Location base = point.getLocation();
		int spawned = 0;
		
		for (int objId : pinnedIds)
		{
			// já online? não faz nada
			FakePlayer online = FakePlayerManager.getInstance().getPlayer(objId);
			if (online != null)
				continue;
			
			FakePlayer fp = restoreSingle(objId);
			if (fp == null)
				continue;
			
			int x = base.getX();
			int y = base.getY();
			int z = base.getZ();
			
			if (radius > 0)
			{
				x += Rnd.get(-radius, radius);
				y += Rnd.get(-radius, radius);
			}
			
			fp.teleToLocation(x, y, z, 0);
			spawned++;
		}
		
		return spawned;
	}
	
	private static FakePlayer restoreSingle(int objectId)
	{
		try
		{
			FakePlayer already = FakePlayerManager.getInstance().getPlayer(objectId);
			if (already != null)
				return already;
			
			FakePlayer fake = FakePlayer.restore(objectId);
			
			CharNameTable.getInstance().register(fake);
			FakePlayerManager.getInstance().register(fake);
			
			L2GameClient client = new L2GameClient(null);
			client.setActiveChar(fake);
			fake.setClient(client);
			
			fake.spawnMe();
			fake.assignDefaultAI();
			
			return fake;
		}
		catch (Exception e)
		{
			LOGGER.warning("Failed restoring fake " + objectId + ": " + e.getMessage());
			return null;
		}
	}
	
	// ========================= DESPAWN Selected (Pinned) =========================
	public static int despawnPinnedOnline(Set<Integer> pinnedIds)
	{
		if (pinnedIds == null || pinnedIds.isEmpty())
			throw new IllegalStateException("No pinned targets.");
		
		int despawned = 0;
		
		for (int objId : pinnedIds)
		{
			FakePlayer fp = FakePlayerManager.getInstance().getPlayer(objId);
			if (fp == null)
				continue;
			
			fp.abortAttack();
			fp.abortCast();
			fp.deleteMe();
			despawned++;
		}
		
		return despawned;
	}
	
	// ========================= DELETE DB Selected (Pinned) =========================
	public static int deletePinnedDb(Set<Integer> pinnedIds)
	{
		if (pinnedIds == null || pinnedIds.isEmpty())
			throw new IllegalStateException("No pinned targets.");
		
		int deleted = 0;
		
		for (int objId : pinnedIds)
		{
			FakePlayer fp = FakePlayerManager.getInstance().getPlayer(objId);
			if (fp != null)
			{
				fp.abortAttack();
				fp.abortCast();
				fp.deleteMe(); // sai do mundo
			}
			
			if (deleteCharAndMaybeAccount(objId))
				deleted++;
			else
				LOGGER.warning("Delete DB failed objId=" + objId);
		}
		
		return deleted;
	}
	
	public static boolean deleteCharAndMaybeAccount(int objectId)
	{
		if (objectId <= 0)
			return false;
		
		CharNameTable.getInstance().unregister(objectId);
		
		try (Connection con = ConnectionPool.getConnection())
		{
			con.setAutoCommit(false);
			
			// 1) account
			String accountName = getAccountNameByObjId(con, objectId);
			if (accountName == null || accountName.isEmpty())
			{
				con.rollback();
				return false;
			}
			
			// 2) delete char data (SQL do teu core)
			deleteCharacterDataUsingCoreSql(con, objectId);
			
			// 3) se não sobrou char na conta, remove accounts
			int remaining = countCharsByAccount(con, accountName);
			if (remaining == 0)
			{
				deleteAccount(con, accountName);
			}
			
			con.commit();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	private static void deleteCharacterDataUsingCoreSql(Connection con, int objectId) throws SQLException
	{
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? OR friend_id=?"))
		{
			ps.setInt(1, objectId);
			ps.setInt(2, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_memo WHERE charId=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_memo_alt WHERE obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_quests WHERE charId=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM heroes WHERE char_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM augmentations WHERE item_id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE owner_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_raid_points WHERE char_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
		
		// FINALMENTE: remove o char da tabela principal
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM characters WHERE obj_id=?"))
		{
			ps.setInt(1, objectId);
			ps.execute();
		}
	}
	
	private static int deleteAccount(Connection con, String accountName) throws SQLException
	{
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM accounts WHERE login=?"))
		{
			ps.setString(1, accountName);
			return ps.executeUpdate();
		}
	}
	
	private static int countCharsByAccount(Connection con, String accountName) throws SQLException
	{
		try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM characters WHERE account_name=?"))
		{
			ps.setString(1, accountName);
			try (ResultSet rs = ps.executeQuery())
			{
				rs.next();
				return rs.getInt(1);
			}
		}
	}
	
	private static String getAccountNameByObjId(Connection con, int objectId) throws SQLException
	{
		try (PreparedStatement ps = con.prepareStatement("SELECT account_name FROM characters WHERE obj_id=?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rs = ps.executeQuery())
			{
				return rs.next() ? rs.getString(1) : null;
			}
		}
	}
	
	public static List<FakeRow> filter(List<FakeRow> src, String text)
	{
		if (src == null || src.isEmpty())
			return List.of();
		if (text == null || text.isBlank())
			return src;
		
		String q = text.trim().toLowerCase();
		List<FakeRow> out = new ArrayList<>();
		
		for (FakeRow r : src)
		{
			String cls = (r.classId != null) ? r.classId.name().toLowerCase() : "";
			String name = (r.name != null) ? r.name.toLowerCase() : "";
			
			if (name.contains(q) || cls.contains(q))
				out.add(r);
		}
		return out;
	}
}
