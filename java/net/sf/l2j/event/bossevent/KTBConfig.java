package net.sf.l2j.event.bossevent;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.commons.lang.StringUtil;

public class KTBConfig
{
	protected static final Logger _log = Logger.getLogger(KTBConfig.class.getName());
	
	private static final String KTM_FILE = "./config/events/BossEvent.properties";
	private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");
	
	public static boolean KTB_EVENT_ENABLED;
	public static List<LocalTime> KTB_EVENT_TIMES;
	
	public static Long KTB_EVENT_PARTICIPATION_TIME;
	public static int KTB_EVENT_RUNNING_TIME;
	public static String KTB_NPC_LOC_NAME;
	public static int KTB_EVENT_PARTICIPATION_NPC_ID;
	public static int[] KTB_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static String KTB_EVENT_BOSS_ID;
	public static List<Integer> LIST_KTB_EVENT_BOSS_ID;
	public static int[] KTB_EVENT_BOSS_COORDINATES = new int[4];
	public static int[] KTB_EVENT_PARTICIPATION_FEE = new int[2];
	public static int KTB_EVENT_MIN_PLAYERS;
	public static int KTB_EVENT_MAX_PLAYERS;
	public static int KTB_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int KTB_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static List<int[]> KTB_EVENT_PLAYER_COORDINATES;
	public static List<int[]> KTB_EVENT_REWARDS;
	public static boolean KTB_EVENT_SCROLL_ALLOWED;
	public static boolean KTB_EVENT_POTIONS_ALLOWED;
	public static boolean KTB_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> KTB_DOORS_IDS_TO_OPEN = new ArrayList<>();
	public static List<Integer> KTB_DOORS_IDS_TO_CLOSE = new ArrayList<>();
	public static byte KTB_EVENT_MIN_LVL;
	public static byte KTB_EVENT_MAX_LVL;
	public static int KTB_EVENT_EFFECTS_REMOVAL;
	public static boolean KTB_EVENT_MULTIBOX_PROTECTION_ENABLE;
	public static int KTB_EVENT_NUMBER_BOX_REGISTER;
	public static Map<Integer, Integer> KTB_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> KTB_EVENT_MAGE_BUFFS;
	public static String DISABLE_ID_CLASSES_STRING;
	public static List<Integer> DISABLE_ID_CLASSES;
	public static boolean ALLOW_EVENT_KTB_COMMANDS;
	public static String KTB_ID_RESTRICT;
	public static List<Integer> KTB_LISTID_RESTRICT;
	public static List<Integer> KTB_SKILL_LIST = new ArrayList<>();
	
	public static void init()
	{
		ExProperties events = load(KTM_FILE);
		
		Long time = 0L;
		KTB_EVENT_ENABLED = events.getProperty("KTBEventEnabled", false);
		
		String raw = events.getProperty("KTBEventInterval", "").trim();
		
		List<LocalTime> times = new ArrayList<>();
		
		if (!raw.isEmpty())
		{
			for (String part : raw.split(","))
			{
				String value = part.trim();
				try
				{
					times.add(LocalTime.parse(value, HHMM));
				}
				catch (DateTimeParseException e)
				{
					_log.warning("KTBEvent: Invalid time '" + value + "' (expected HH:mm)");
				}
			}
		}
		
		if (times.isEmpty())
		{
			_log.warning("KTBEvent: No valid times found in KTBEventInterval!");
		}
		
		KTB_EVENT_TIMES = times;
		
		String[] timeParticipation = events.getProperty("KTBEventParticipationTime", "01:00:00").split(":");
		time = 0L;
		time += Long.parseLong(timeParticipation[0]) * 3600L;
		time += Long.parseLong(timeParticipation[1]) * 60L;
		time += Long.parseLong(timeParticipation[2]);
		KTB_EVENT_PARTICIPATION_TIME = time * 1000L;
		KTB_EVENT_RUNNING_TIME = events.getProperty("KTBEventRunningTime", 1800);
		KTB_NPC_LOC_NAME = events.getProperty("KTBNpcLocName", "Giran Town");
		KTB_EVENT_PARTICIPATION_NPC_ID = events.getProperty("KTBEventParticipationNpcId", 0);
		
		if (KTB_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			KTB_EVENT_ENABLED = false;
			_log.warning("KTBEventEngine[Config.load()]: invalid config property -> KTBEventParticipationNpcId");
		}
		else
		{
			String[] propertySplit = events.getProperty("KTBEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (propertySplit.length < 3)
			{
				KTB_EVENT_ENABLED = false;
				_log.warning("KTBEventEngine[Config.load()]: invalid config property -> KTBEventParticipationNpcCoordinates");
			}
			else
			{
				if (KTB_EVENT_ENABLED)
				{
					KTB_DOORS_IDS_TO_OPEN = new ArrayList<>();
					KTB_DOORS_IDS_TO_CLOSE = new ArrayList<>();
					KTB_EVENT_PLAYER_COORDINATES = new ArrayList<>();
					
					KTB_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
					KTB_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					KTB_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					KTB_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					
					if (propertySplit.length == 4)
						KTB_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
					
					KTB_EVENT_MIN_PLAYERS = events.getProperty("KTBEventMinPlayers", 1);
					KTB_EVENT_MAX_PLAYERS = events.getProperty("KTBEventMaxPlayers", 20);
					KTB_EVENT_MIN_LVL = (byte) events.getProperty("KTBEventMinPlayerLevel", 1);
					KTB_EVENT_MAX_LVL = (byte) events.getProperty("KTBEventMaxPlayerLevel", 80);
					KTB_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("KTBEventRespawnTeleportDelay", 20);
					KTB_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("KTBEventStartLeaveTeleportDelay", 20);
					KTB_EVENT_EFFECTS_REMOVAL = events.getProperty("KTBEventEffectsRemoval", 0);
					KTB_EVENT_MULTIBOX_PROTECTION_ENABLE = events.getProperty("KTBEventMultiBoxEnable", false);
					KTB_EVENT_NUMBER_BOX_REGISTER = events.getProperty("KTBEventNumberBoxRegister", 1);
					for (String id : events.getProperty("KTBSkillIds", "0").split(","))
					{
						KTB_SKILL_LIST.add(Integer.parseInt(id));
					}
					KTB_ID_RESTRICT = events.getProperty("ItemsRestrictionKTB");
					
					KTB_LISTID_RESTRICT = new ArrayList<>();
					for (String id : KTB_ID_RESTRICT.split(","))
						KTB_LISTID_RESTRICT.add(Integer.parseInt(id));
					
					propertySplit = events.getProperty("KTBEventParticipationFee", "0,0").split(",");
					try
					{
						KTB_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
						KTB_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
					}
					catch (NumberFormatException nfe)
					{
						if (propertySplit.length > 0)
							_log.warning("KTBEventEngine[Config.load()]: invalid config property -> KTBEventParticipationFee");
					}
					
					propertySplit = events.getProperty("KTBEventPlayerCoordinates", "0,0,0").split(";");
					for (String coordPlayer : propertySplit)
					{
						String[] coordSplit = coordPlayer.split(",");
						if (coordSplit.length != 3)
							_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventPlayerCoordinates \"", coordPlayer, "\""));
						else
						{
							try
							{
								KTB_EVENT_PLAYER_COORDINATES.add(new int[]
								{
									Integer.parseInt(coordSplit[0]),
									Integer.parseInt(coordSplit[1]),
									Integer.parseInt(coordSplit[2])
								});
							}
							catch (NumberFormatException nfe)
							{
								if (!coordPlayer.isEmpty())
									_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventPlayerCoordinates \"", coordPlayer, "\""));
							}
						}
					}
					
					KTB_EVENT_REWARDS = new ArrayList<>();
					propertySplit = events.getProperty("KTBEventReward", "57,100000").split(";");
					for (String reward : propertySplit)
					{
						String[] rewardSplit = reward.split(",");
						if (rewardSplit.length != 2)
						{
							_log.warning(StringUtil.concat("KTBEventEngine: invalid config property -> KTBEventReward \"", reward, "\""));
						}
						else
						{
							try
							{
								KTB_EVENT_REWARDS.add(new int[]
								{
									Integer.parseInt(rewardSplit[0]),
									Integer.parseInt(rewardSplit[1])
								});
							}
							catch (NumberFormatException nfe)
							{
								if (!reward.isEmpty())
								{
									_log.warning(StringUtil.concat("KTBEventEngine: invalid config property -> KTBEventReward \"", reward, "\""));
								}
							}
						}
					}
					ALLOW_EVENT_KTB_COMMANDS = events.getProperty("AllowEventKTBCommands", false);
					KTB_EVENT_SCROLL_ALLOWED = events.getProperty("KTBEventScrollsAllowed", false);
					KTB_EVENT_POTIONS_ALLOWED = events.getProperty("KTBEventPotionsAllowed", false);
					KTB_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("KTBEventSummonByItemAllowed", false);
					DISABLE_ID_CLASSES_STRING = events.getProperty("KTBDisabledForClasses");
					DISABLE_ID_CLASSES = new ArrayList<>();
					for (String class_id : DISABLE_ID_CLASSES_STRING.split(","))
						DISABLE_ID_CLASSES.add(Integer.parseInt(class_id));
					
					propertySplit = events.getProperty("KTBDoorsToOpen", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							KTB_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBDoorsToOpen \"", door, "\""));
						}
					}
					
					propertySplit = events.getProperty("KTBDoorsToClose", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							KTB_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBDoorsToClose \"", door, "\""));
						}
					}
					
					propertySplit = events.getProperty("KTBEventFighterBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						KTB_EVENT_FIGHTER_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventFighterBuffs \"", skill, "\""));
							else
							{
								try
								{
									KTB_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventFighterBuffs \"", skill, "\""));
								}
							}
						}
					}
					
					propertySplit = events.getProperty("KTBEventMageBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						KTB_EVENT_MAGE_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventMageBuffs \"", skill, "\""));
							else
							{
								try
								{
									KTB_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("KTBEventEngine[Config.load()]: invalid config property -> KTBEventMageBuffs \"", skill, "\""));
								}
							}
						}
					}
				}
			}
			
			// Clan
			KTB_EVENT_BOSS_ID = events.getProperty("KTBEventBossListId", "");
			LIST_KTB_EVENT_BOSS_ID = new ArrayList<>();
			for (String itemId : KTB_EVENT_BOSS_ID.split(","))
				LIST_KTB_EVENT_BOSS_ID.add(Integer.parseInt(itemId));
			
			String[] propertySplitBoss = events.getProperty("KTBEventBossCoordinates", "0,0,0").split(",");
			if (propertySplitBoss.length < 3)
			{
				KTB_EVENT_ENABLED = false;
				_log.warning("KTBEventEngine[Config.load()]: invalid config property -> KTBEventParticipationNpcCoordinates");
			}
			else
			{
				KTB_EVENT_BOSS_COORDINATES = new int[4];
				KTB_EVENT_BOSS_COORDINATES[0] = Integer.parseInt(propertySplitBoss[0]);
				KTB_EVENT_BOSS_COORDINATES[1] = Integer.parseInt(propertySplitBoss[1]);
				KTB_EVENT_BOSS_COORDINATES[2] = Integer.parseInt(propertySplitBoss[2]);
				
				if (propertySplitBoss.length == 4)
					KTB_EVENT_BOSS_COORDINATES[3] = Integer.parseInt(propertySplitBoss[3]);
			}
		}
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		
		return result;
	}
}