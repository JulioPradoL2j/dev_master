package net.sf.l2j.gameserver.model.zone.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.lastman.LMEvent;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportWhereType;
import net.sf.l2j.gameserver.instancemanager.custom.EpicZoneManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * @author DaRkRaGe
 */
public class L2BossZone extends L2ZoneType
{
	private static final String SELECT_GRAND_BOSS_LIST = "SELECT * FROM grandboss_list WHERE zone = ?";
	
	// Track the times that players got disconnected. Players are allowed to log back into the zone as long as their log-out was within _timeInvade time...
	private final Map<Integer, Long> _playerAllowEntry = new ConcurrentHashMap<>();
	
	// Track players admitted to the zone who should be allowed back in after reboot/server downtime, within 30min of server restart
	private final List<Integer> _playerAllowed = new CopyOnWriteArrayList<>();
	
	private int _timeInvade;
	private boolean _enabled = true;
	private final int[] _oustLoc = new int[3];
	private boolean _checkLevel;
	private int _MaxLevel;
	private int _maxClanMembers;
	private int _maxAllyMembers;
	private int _minPartyMembers;
	private boolean _checkParty;
	private boolean _checkClan;
	private boolean _checkAlly;
	
	public L2BossZone(int id)
	{
		super(id);
		_maxClanMembers = 0;
		_maxAllyMembers = 0;
		_minPartyMembers = 0;
		_checkParty = false;
		_checkClan = false;
		_checkAlly = false;
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT_GRAND_BOSS_LIST);
			statement.setInt(1, id);
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				allowPlayerEntry(rset.getInt("player_id"));
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "L2BossZone: Could not load grandboss zone id=" + id + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("InvadeTime"))
			_timeInvade = Integer.parseInt(value);
		else if (name.equals("EnabledByDefault"))
			_enabled = Boolean.parseBoolean(value);
		else if (name.equals("oustX"))
			_oustLoc[0] = Integer.parseInt(value);
		else if (name.equals("oustY"))
			_oustLoc[1] = Integer.parseInt(value);
		else if (name.equals("oustZ"))
			_oustLoc[2] = Integer.parseInt(value);
		else if (name.equals("EnableLevelLimite"))
			_checkLevel = Boolean.parseBoolean(value);
		else if (name.equals("LimiteLv"))
		{
			_MaxLevel = Integer.parseInt(value);
		}
		else if (name.equals("MaxClanMembers"))
			_maxClanMembers = Integer.parseInt(value);
		else if (name.equals("MaxAllyMembers"))
			_maxAllyMembers = Integer.parseInt(value);
		else if (name.equals("MinPartyMembers"))
			_minPartyMembers = Integer.parseInt(value);
		else if (name.equals("checkParty"))
			_checkParty = Boolean.parseBoolean(value);
		else if (name.equals("checkClan"))
			_checkClan = Boolean.parseBoolean(value);
		else if (name.equals("checkAlly"))
			_checkAlly = Boolean.parseBoolean(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (_enabled)
		{
			if (character instanceof Player)
			{
				final Player player = (Player) character;
				
				// GMs entram direto sem qualquer checagem
				if (player.isGM())
					return;
				
				player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
				player.setInsideZone(ZoneId.BOSS, true);
				
				if (Config.ENABLE_BOSSZONE_FLAG)
				{
					player.setInsideZone(ZoneId.FLAG_AREA_BOSS, true);
					player.updatePvPFlag(1);
					player.broadcastUserInfo();
				}
				
				if (_checkParty && _minPartyMembers > 0 && !player.isGM())
				{
					if (!player.isInParty() || player.getParty().getMemberCount() < _minPartyMembers)
					{
						player.sendPacket(new ExShowScreenMessage("Your party does not have " + _minPartyMembers + " members to enter on this zone!", 6 * 1000));
						EpicZoneManager.getInstance().RandomTeleport(player);
						return;
					}
				}
				
				if (Config.ENABLE_DUALBOX_BOSSZONE)
				{
					ThreadPool.schedule(() -> {
						MaxPlayersOnArea(player, this);
					}, 2000);
				}
				
				if (_checkClan)
				{
					ThreadPool.schedule(() -> {
						MaxClanMembersOnArea(player, this);
					}, 2000);
				}
				
				if (_checkAlly)
				{
					ThreadPool.schedule(() -> {
						MaxAllyMembersOnArea(player, this);
					}, 2000);
				}
				
				if (_checkLevel && player.getLevel() > _MaxLevel)
				{
					ThreadPool.schedule(() -> {
						player.sendMessage("Epic Zone, Max Lv " + _MaxLevel);
						player.teleToLocation(82488, 149064, -3464, 0);
					}, 2000);
					return;
				}
				
				if (player.getMountType() == 2 && Config.WYVERN_PROTECTION_BOSS_ZONE)
				{
					ThreadPool.schedule(() -> {
						player.sendMessage("Desmounted Wyvern In Boss Zone!");
						player.broadcastUserInfo();
					}, 2000);
				}
				
				final int id = player.getObjectId();
				
				if (_playerAllowed.contains(id))
				{
					final long entryTime = _playerAllowEntry.remove(id);
					if (entryTime > System.currentTimeMillis())
						return;
					
					_playerAllowed.remove(Integer.valueOf(id));
				}
				
				// Se não for permitido pelas regras, expulsa
				if (!(_timeInvade == 0 || Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM || (CTFEvent.isPlayerParticipant(id) && CTFEvent.isStarted()) || (TvTEvent.isPlayerParticipant(id) && TvTEvent.isStarted()) || (KTBEvent.isPlayerParticipant(id) && KTBEvent.isStarted()) || (LMEvent.isStarted() && LMEvent.isPlayerParticipant(id)) || (DMEvent.isStarted() && DMEvent.isPlayerParticipant(id)) || (FOSEvent.isPlayerParticipant(id) && FOSEvent.isStarted())))
				{
					if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
						player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2], 0);
					else
						player.teleToLocation(TeleportWhereType.TOWN);
				}
			}
			else if (character instanceof L2Summon)
			{
				final Player player = ((L2Summon) character).getOwner();
				if (player != null)
				{
					if (_playerAllowed.contains(player.getObjectId()) || player.isGM())
						return;
					
					if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
						player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2], 0);
					else
						player.teleToLocation(TeleportWhereType.TOWN);
				}
				
				((L2Summon) character).unSummon(player);
			}
		}
	}
	
	public boolean MaxClanMembersOnArea(Player activeChar, L2BossZone zone)
	{
		return EpicZoneManager.getInstance().checkClanArea(activeChar, zone, _maxClanMembers, true);
	}
	
	public boolean MaxAllyMembersOnArea(Player activeChar, L2BossZone zone)
	{
		return EpicZoneManager.getInstance().checkAllyArea(activeChar, zone, _maxAllyMembers, L2World.getInstance().getPlayers(), true);
	}
	
	public boolean MaxPlayersOnArea(Player activeChar, L2BossZone zone)
	{
		return EpicZoneManager.getInstance().checkPlayersArea(activeChar, zone, 1, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Playable && _enabled)
		{
			if (character instanceof Player)
			{
				// Get player and set zone info.
				final Player player = (Player) character;
				player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
				player.setInsideZone(ZoneId.BOSS, false);
				player.setInsideZone(ZoneId.FLAG_AREA_BOSS, false);
				
				if (Config.ENABLE_BOSSZONE_FLAG)
				{
					player.setInsideZone(ZoneId.FLAG_AREA_BOSS, false);
					
					((Player) character).updatePvPFlag(0);
					((Player) character).broadcastUserInfo();
					PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
				}
				
				// Get player object id.
				final int id = player.getObjectId();
				
				if (_playerAllowed.contains(id))
				{
					if (!player.isOnline())
					{
						// Player disconnected.
						_playerAllowEntry.put(id, System.currentTimeMillis() + _timeInvade);
					}
					else
					{
						// Player has allowed entry, do not delete from allowed list.
						if (_playerAllowEntry.containsKey(id))
							return;
						
						// Remove player allowed list.
						_playerAllowed.remove(Integer.valueOf(id));
					}
				}
			}
			
			// If playables aren't found, force all bosses to return to spawnpoint.
			if (!_characterList.isEmpty())
			{
				if (!getKnownTypeInside(Playable.class).isEmpty())
					return;
				
				for (Attackable raid : getKnownTypeInside(Attackable.class))
				{
					if (!raid.isRaid())
						continue;
					
					raid.returnHome(true);
				}
			}
		}
		else if (character instanceof Attackable && character.isRaid())
			((Attackable) character).returnHome(true);
	}
	
	/**
	 * Enables the entry of a player to the boss zone for next "duration" seconds. If the player tries to enter the boss zone after this period, he will be teleported out.
	 * @param player : Player to allow entry.
	 * @param duration : Entry permission is valid for this period (in seconds).
	 */
	public void allowPlayerEntry(Player player, int duration)
	{
		// Get player object id.
		final int playerId = player.getObjectId();
		
		// Allow player entry.
		if (!_playerAllowed.contains(playerId))
			_playerAllowed.add(playerId);
		
		// For the given duration.
		_playerAllowEntry.put(playerId, System.currentTimeMillis() + duration * 1000);
	}
	
	/**
	 * Enables the entry of a player to the boss zone after server shutdown/restart. The time limit is specified by each zone via "InvadeTime" parameter. If the player tries to enter the boss zone after this period, he will be teleported out.
	 * @param playerId : The ID of player to allow entry.
	 */
	public void allowPlayerEntry(int playerId)
	{
		// Allow player entry.
		if (!_playerAllowed.contains(playerId))
			_playerAllowed.add(playerId);
		
		// For the given duration.
		_playerAllowEntry.put(playerId, System.currentTimeMillis() + _timeInvade);
	}
	
	/**
	 * Removes the player from allowed list and cancel the entry permition.
	 * @param player : Player to remove from the zone.
	 */
	public void removePlayer(Player player)
	{
		// Get player object id.
		final int id = player.getObjectId();
		
		// Remove player from allowed list.
		_playerAllowed.remove(Integer.valueOf(id));
		
		// Remove player permission.
		_playerAllowEntry.remove(id);
	}
	
	/**
	 * @return the list of all allowed players object ids.
	 */
	public List<Integer> getAllowedPlayers()
	{
		return _playerAllowed;
	}
	
	/**
	 * Some GrandBosses send all players in zone to a specific part of the zone, rather than just removing them all. If this is the case, this command should be used. If this is no the case, then use oustAllPlayers().
	 * @param x
	 * @param y
	 * @param z
	 */
	public void movePlayersTo(int x, int y, int z)
	{
		if (_characterList.isEmpty())
			return;
		
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.isOnline())
				player.teleToLocation(x, y, z, 0);
		}
	}
	
	/**
	 * Occasionally, all players need to be sent out of the zone (for example, if the players are just running around without fighting for too long, or if all players die, etc). This call sends all online players to town and marks offline players to be teleported (by clearing their relog expiration
	 * times) when they log back in (no real need for off-line teleport).
	 */
	public void oustAllPlayers()
	{
		if (_characterList.isEmpty())
			return;
		
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.isOnline())
			{
				if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
					player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2], 0);
				else
					player.teleToLocation(TeleportWhereType.TOWN);
			}
		}
		_playerAllowEntry.clear();
		_playerAllowed.clear();
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
}