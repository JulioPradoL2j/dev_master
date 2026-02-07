package net.sf.l2j.gameserver.scriptings.scripts.ai.individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.Config;
import net.sf.l2j.bosstimerespawn.TimeEpicBossManager;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2CommandChannel;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.zone.type.L2BossZone;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillCanceld;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.scriptings.scripts.ai.L2AttackableAIScript;
import net.sf.l2j.gameserver.skills.AbnormalEffect;
import net.sf.l2j.gameserver.templates.StatsSet;

public class Frintezza extends L2AttackableAIScript
{
	private static final int ZONE_ID = 110011;
	private static long _LastAction = 0L;
	private static final int FRINTEZZA = 29045;
	private static final int SCARLET1 = 29046;
	private static final int SCARLET2 = 29047;
	
	private static final int GUIDE = 32011;
	private static final int CUBE = 29061;
	
	private static final byte DORMANT = 0;
	private static final byte WAITING = 1;
	private static final byte FIGHTING = 2;
	private static final byte DEAD = 3;
	
	private static final int MIN_LEVEL = 74;
	private static final int MAX_PLAYERS = 45;
	private static final int TALK_RADIUS = 150;
	
	private static final Location CUBE_EXIT = new Location(150037, -57720, -2976);
	
	private static final int ROOM1_ALARMS_TOTAL = 4;
	private static final int ROOM1_PARTIAL_OPEN_AT = 6;
	private static final int ROOM2_CHOIR_TOTAL = 4;
	private static final int ROOM2_CHOIR_CAPTAIN_TOTAL = 8;
	private static final L2BossZone ZONE = ZoneManager.getInstance().getZoneById(ZONE_ID, L2BossZone.class);
	
	private final CopyOnWriteArrayList<Player> _playersInside = new CopyOnWriteArrayList<>();
	private final CopyOnWriteArrayList<L2Npc> _room1Mobs = new CopyOnWriteArrayList<>();
	private L2Npc _frintezzaDummy, _overheadDummy, _portraitDummy1, _portraitDummy3, _scarletDummy;
	public L2GrandBossInstance frintezza, weakScarlet, strongScarlet, activeScarlet;
	
	private volatile int _locCycle = 0;
	private volatile int _room1AlarmsDead = 0;
	private volatile int _room1ChoirDead = 0;
	private volatile int _room1ChoirCaptainDead = 0;
	private volatile int _room1HallPatrolDead = 0;
	private volatile boolean _raidStarted = false;
	
	public volatile boolean _room1PartialOpened = false;
	private volatile boolean _room1AllOpened = false;
	public volatile boolean _room2Opened = false;
	
	private static int _OnSong = 0;
	private static int _OnMorph = 0;
	private static int _ThirdMorph = 0;
	private static int _Abnormal = 0;
	private static int _SecondMorph = 0;
	
	private static int _Angle = 0;
	private static int _Heading = 0;
	
	private static int _OnCheck = 0;
	private static int _CheckDie = 0;
	
	private static int _Scarlet_x = 0;
	private static int _Scarlet_y = 0;
	private static int _Scarlet_z = 0;
	private static int _Scarlet_h = 0;
	
	private L2MonsterInstance demon1;
	private L2MonsterInstance demon2;
	private L2MonsterInstance demon3;
	private L2MonsterInstance demon4;
	private L2MonsterInstance portrait1;
	private L2MonsterInstance portrait2;
	private L2MonsterInstance portrait3;
	private L2MonsterInstance portrait4;
	
	public Frintezza()
	{
		super("ai/individual");
	}
	
	@Override
	protected void registerNpcs()
	{
		for (SpawnPoint sp : ROOM1_SPAWNS)
		{
			addAttackId(sp.npcId);
			addKillId(sp.npcId);
		}
		
		int[] bossesRegister =
		{
			SCARLET1,
			SCARLET2,
			FRINTEZZA
		};
		
		addAttackId(bossesRegister);
		addKillId(bossesRegister);
		
		addStartNpc(GUIDE, CUBE);
		addTalkId(GUIDE, CUBE);
		
		// Verifica o status do boss Frintezza
		StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
		int status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		// Se o status for DEAD (morto)
		if (status == DEAD)
		{
			long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			if (temp > 0L)
			{
				// Se o tempo de respawn não expirou, inicia o timer
				startQuestTimer("frintezza_unlock", temp, null, null, false);
			}
			else
			{
				// Caso o tempo tenha expirado, define o status como DORMANT (dormindo)
				GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
			}
		}
		else if (status != DORMANT)
		{
			// Se o status não for DORMANT, define como DORMANT
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
	}
	
	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final Player player)
	{
		if ("room1_spawn".equalsIgnoreCase(event))
		{
			spawnRoom1();
			tellPlayers("A primeira sala foi ativada. Destruam os alarmes!");
		}
		else if ("close".equalsIgnoreCase(event))
		{
			closeDoors();
		}
		
		else if ("room_final".equalsIgnoreCase(event))
		{
			
			tellPlayers("Tempo excedido. Desafio falhou!");
			
			if (ZONE != null)
				ZONE.oustAllPlayers();
			
			cleanupAll();
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
			cancelQuestTimers("frintezza_despawn");
		}
		else if (event.equalsIgnoreCase("room3_del"))
		{
			if (demon1 != null)
			{
				demon1.deleteMe();
			}
			if (demon2 != null)
			{
				demon2.deleteMe();
			}
			if (demon3 != null)
			{
				demon3.deleteMe();
			}
			if (demon4 != null)
			{
				demon4.deleteMe();
			}
			if (portrait1 != null)
			{
				portrait1.deleteMe();
			}
			if (portrait2 != null)
			{
				portrait2.deleteMe();
			}
			if (portrait3 != null)
			{
				portrait3.deleteMe();
			}
			if (portrait4 != null)
			{
				portrait4.deleteMe();
			}
			if (frintezza != null)
			{
				frintezza.deleteMe();
			}
			if (weakScarlet != null)
			{
				weakScarlet.deleteMe();
			}
			if (strongScarlet != null)
			{
				strongScarlet.deleteMe();
			}
			demon1 = null;
			demon2 = null;
			demon3 = null;
			demon4 = null;
			portrait1 = null;
			portrait2 = null;
			portrait3 = null;
			portrait4 = null;
			frintezza = null;
			weakScarlet = null;
			strongScarlet = null;
			activeScarlet = null;
		}
		
		else if (event.equalsIgnoreCase("waiting"))
		{
			
			startQuestTimer("close", 27000L, npc, null, false);
			startQuestTimer("camera_1", 30000L, npc, null, false);
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new Earthquake(174232, -88020, -5116, 45, 27));
				}
			}
			
		}
		else if (event.equalsIgnoreCase("camera_1"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, FIGHTING);
			
			_frintezzaDummy = addSpawn(29052, 174240, -89805, -5022, 16048, false, 0, false);
			_frintezzaDummy.setIsInvul(true);
			_frintezzaDummy.setIsImmobilized(true);
			
			_overheadDummy = addSpawn(29052, 174232, -88020, -5110, 16384, false, 0, false);
			_overheadDummy.setIsInvul(true);
			_overheadDummy.setIsImmobilized(true);
			_overheadDummy.setCollisionHeight(600);
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new NpcInfo(_overheadDummy, p));
				}
			}
			
			_portraitDummy1 = addSpawn(29052, 172450, -87890, -5100, 16048, false, 0, false);
			_portraitDummy1.setIsImmobilized(true);
			_portraitDummy1.setIsInvul(true);
			
			_portraitDummy3 = addSpawn(29052, 176012, -87890, -5100, 16048, false, 0, false);
			_portraitDummy3.setIsImmobilized(true);
			_portraitDummy3.setIsInvul(true);
			
			_scarletDummy = addSpawn(29053, 174232, -88020, -5110, 16384, false, 0, false);
			_scarletDummy.setIsInvul(true);
			_scarletDummy.setIsImmobilized(true);
			startQuestTimer("stop_pc", 0L, npc, null, false);
			startQuestTimer("camera_2", 1000L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_2"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100));
				}
			}
			startQuestTimer("camera_2b", 0L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_2b"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100));
				}
			}
			
			startQuestTimer("camera_3", 0L, _overheadDummy, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_3"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_overheadDummy.getObjectId(), 300, 90, -10, 6500, 7000));
				}
			}
			
			frintezza = (L2GrandBossInstance) addSpawn(FRINTEZZA, 174240, -89805, -5022, 16048, false, 0, false);
			GrandBossManager.getInstance().addBoss(frintezza);
			frintezza.setIsImmobilized(true);
			frintezza.setIsInvul(true);
			frintezza.disableAllSkills();
			
			demon2 = ((L2MonsterInstance) addSpawn(29051, 175876, -88713, -5100, 28205, false, 0L, false));
			demon2.setIsImmobilized(true);
			demon2.disableAllSkills();
			demon3 = ((L2MonsterInstance) addSpawn(29051, 172608, -88702, -5100, 64817, false, 0L, false));
			demon3.setIsImmobilized(true);
			demon3.disableAllSkills();
			demon1 = ((L2MonsterInstance) addSpawn(29050, 175833, -87165, -5100, 35048, false, 0L, false));
			demon1.setIsImmobilized(true);
			demon1.disableAllSkills();
			demon4 = ((L2MonsterInstance) addSpawn(29050, 172634, -87165, -5100, 57730, false, 0L, false));
			demon4.setIsImmobilized(true);
			demon4.disableAllSkills();
			
			startQuestTimer("camera_4", 4500L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_4"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 1800, 90, 8, 6500, 7000));
				}
			}
			startQuestTimer("camera_5", 500L, _frintezzaDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_5"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 140, 90, 10, 2500, 4500));
				}
			}
			
			startQuestTimer("camera_5b", 4000L, _frintezzaDummy, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_5b"))
		{
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 1000));
				}
			}
			
			startQuestTimer("camera_6", 0L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_6"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 12000));
				}
			}
			
			startQuestTimer("camera_7", 1350L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_7"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SocialAction(frintezza, 2));
				}
			}
			
			startQuestTimer("camera_8", 0L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_8"))
		{
			startQuestTimer("camera_9", 0L, frintezza, null, false);
			
			_frintezzaDummy.deleteMe();
			_frintezzaDummy = null;
		}
		else if (event.equalsIgnoreCase("camera_9"))
		{
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SocialAction(demon2, 1));
					p.sendPacket(new SocialAction(demon3, 1));
				}
			}
			
			startQuestTimer("camera_9b", 400L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_9b"))
		{
			startQuestTimer("camera_9c", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_9c"))
		{
			startQuestTimer("camera_10", 2000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_10"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 0, 0, 1000));
				}
			}
			startQuestTimer("camera_11", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_11"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 25, 5500, 10000));
					p.sendPacket(new SocialAction(frintezza, 3));
				}
			}
			
			_portraitDummy1.deleteMe();
			_portraitDummy3.deleteMe();
			_portraitDummy1 = null;
			_portraitDummy3 = null;
			startQuestTimer("camera_12", 3500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_12"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000));
					
				}
			}
			
			startQuestTimer("camera_13", 300L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_13"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000));
					
				}
			}
			startQuestTimer("camera_14", 1300L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_14"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 120, 180, 45, 1500, 10000));
					p.sendPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
					
				}
			}
			startQuestTimer("camera_16", 1500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_16"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 520, 135, 45, 8000, 10000));
				}
			}
			startQuestTimer("camera_17", 7500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_17"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 1500, 110, 25, 10000, 13000));
				}
			}
			startQuestTimer("camera_18", 9500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_18"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_overheadDummy.getObjectId(), 930, 160, -20, 0, 1000));
				}
			}
			startQuestTimer("camera_18b", 0L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_18b"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_overheadDummy.getObjectId(), 600, 180, -25, 0, 10000));
					p.sendPacket(new MagicSkillUse(_scarletDummy, _overheadDummy, 5004, 1, 5800, 0));
				}
			}
			
			weakScarlet = (L2GrandBossInstance) addSpawn(SCARLET1, 174232, -88020, -5110, 16384, false, 0, false);
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
			
			activeScarlet = weakScarlet;
			
			startQuestTimer("camera_19", 5500L, _scarletDummy, null, false);
			startQuestTimer("camera_19b", 5400L, weakScarlet, null, false);
			
		}
		
		else if (event.equalsIgnoreCase("camera_19"))
		{
			weakScarlet.teleToLocation(174232, -88020, -5110, 0);
		}
		else if (event.equalsIgnoreCase("camera_19b"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(_scarletDummy.getObjectId(), 800, 180, 10, 1000, 10000));
					
				}
			}
			
			startQuestTimer("camera_20", 2100L, _scarletDummy, null, false);
			
		}
		else if (event.equalsIgnoreCase("camera_20"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(weakScarlet.getObjectId(), 300, 60, 8, 0, 10000));
					
				}
			}
			startQuestTimer("camera_21", 2000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_21"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.sendPacket(new SpecialCamera(weakScarlet.getObjectId(), 500, 90, 10, 3000, 5000));
					
				}
			}
			startQuestTimer("camera_22", 3000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_22"))
		{
			_overheadDummy.deleteMe();
			_scarletDummy.deleteMe();
			_overheadDummy = null;
			_scarletDummy = null;
			
			startQuestTimer("camera_23", 2000L, weakScarlet, null, false);
			startQuestTimer("loc_check", 60000L, weakScarlet, null, false);
			startQuestTimer("start_pc", 2000L, weakScarlet, null, false);
			
			startQuestTimer("songs_play", 1000L, frintezza, null, false);
			startQuestTimer("skill01", 1000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_23"))
		{
			weakScarlet.setIsInvul(false);
			weakScarlet.setIsImmobilized(false);
			weakScarlet.enableAllSkills();
			weakScarlet.setRunning();
		}
		else if (event.equalsIgnoreCase("songs_play"))
		{
			if (frintezza != null && !frintezza.isDead() && _OnMorph == 0)
			{
				_OnSong = Rnd.get(1, 5);
				if (_OnSong == 3)
				{ // to fix skill exception
					_OnSong = 2;
				}
				
				String SongName = "";
				
				// Name of the songs are custom, named with client side description.
				switch (_OnSong)
				{
					case 1:
						SongName = "Frintezza's Healing Rhapsody";
						break;
					case 2:
						SongName = "Frintezza's Rampaging Opus";
						break;
					case 3:
						SongName = "Frintezza's Power Concerto";
						break;
					case 4:
						SongName = "Frintezza's Plagued Concerto";
						break;
					case 5:
						SongName = "Frintezza's Psycho Symphony";
						break;
					default:
						SongName = "Frintezza's Song";
						break;
				}
				
				for (Player p : _playersInside)
				{
					if (p != null && p.isOnline())
					{
						// Like L2OFF the skill name is printed on screen
						p.sendPacket(new ExShowScreenMessage(SongName, 6000));
						
					}
				}
				
				if (_OnSong == 1 && _ThirdMorph == 1 && strongScarlet.getCurrentHp() < strongScarlet.getMaxHp() * 0.6 && Rnd.get(100) < 80)
				{
					
					for (Player p : _playersInside)
					{
						if (p != null && p.isOnline())
						{
							// Like L2OFF the skill name is printed on screen
							p.sendPacket(new MagicSkillUse(frintezza, frintezza, 5007, 1, 32000, 0));
							
						}
					}
					
					startQuestTimer("songs_effect", 5000L, frintezza, null, false);
					startQuestTimer("songs_play", 3200L, frintezza, null, false);
					
				}
				else if (_OnSong == 2 || _OnSong == 3)
				{
					for (Player p : _playersInside)
					{
						if (p != null && p.isOnline())
						{
							// Like L2OFF the skill name is printed on screen
							p.sendPacket(new MagicSkillUse(frintezza, frintezza, 5007, _OnSong, 32000, 0));
							
						}
					}
					
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);
					
				}
				else if (_OnSong == 4 && _SecondMorph == 1)
				{
					for (Player p : _playersInside)
					{
						if (p != null && p.isOnline())
						{
							// Like L2OFF the skill name is printed on screen
							p.sendPacket(new MagicSkillUse(frintezza, frintezza, 5007, 4, 31000, 0));
							
						}
					}
					
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 31000 + Rnd.get(10000), frintezza, null, false);
					
				}
				else if (_OnSong == 5 && _ThirdMorph == 1 && _Abnormal == 0)
				{
					_Abnormal = 1;
					
					for (Player p : _playersInside)
					{
						if (p != null && p.isOnline())
						{
							// Like L2OFF the skill name is printed on screen
							p.sendPacket(new MagicSkillUse(frintezza, frintezza, 5007, 5, 35000, 0));
							
						}
					}
					
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 35000 + Rnd.get(10000), frintezza, null, false);
				}
				else
				{
					startQuestTimer("songs_play", 5000 + Rnd.get(5000), frintezza, null, false);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("skill01"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				final int i = Rnd.get(0, 1);
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					// weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill01", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
				
			}
		}
		else if (event.equalsIgnoreCase("skill02"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(2, 5);
				else
					i = Rnd.get(2, 4);
				
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					// weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill02", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
				
				if (i == 5)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 4000, weakScarlet, null, false);
					
				}
			}
		}
		else if (event.equalsIgnoreCase("skill03"))
		{
			if (strongScarlet != null && !strongScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 1 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(6, 10);
				else
					i = Rnd.get(6, 9);
				
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					strongScarlet.stopMove(null);
					strongScarlet.doCast(skill);
				}
				startQuestTimer("skill03", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);
				
				if (i == 10)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 3000, npc, null, false);
					
				}
			}
		}
		else if (event.equalsIgnoreCase("float_effect"))
		{
			if (npc.isCastingNow())
			{
				startQuestTimer("float_effect", 500, npc, null, false);
				
			}
			else
			{
				
				for (Player p : _playersInside)
				{
					if (p != null && p.isOnline())
					{
						if (p.getFirstEffect(5016) != null)
						{
							p.abortAttack();
							p.abortCast();
							p.disableAllSkills();
							p.stopMove(null);
							p.setIsImmobilized(true);
							p.setIsParalyzed(true);
							p.getAI().setIntention(CtrlIntention.IDLE);
							p.startAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
						}
						
					}
				}
				
				startQuestTimer("stop_effect", 25000, npc, null, false);
				
			}
		}
		
		else if (event.equalsIgnoreCase("stop_effect"))
		{
			
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					if (cha.getFirstEffect(5016) != null)
					{
						cha.stopAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
						cha.stopAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
						cha.enableAllSkills();
						cha.setIsImmobilized(false);
						cha.setIsParalyzed(false);
					}
					
				}
			}
			
			_Abnormal = 0;
		}
		else if (event.equalsIgnoreCase("attack_stop"))
		{
			cancelQuestTimer("skill01", npc, null);
			cancelQuestTimer("skill02", npc, null);
			cancelQuestTimer("skill03", npc, null);
			cancelQuestTimer("songs_play", npc, null);
			cancelQuestTimer("songs_effect", npc, null);
			
			if (frintezza != null)
			{
				for (Player cha : _playersInside)
				{
					if (cha != null && cha.isOnline())
					{
						cha.sendPacket(new MagicSkillCanceld(frintezza.getObjectId()));
						
					}
				}
			}
			
		}
		else if (event.equalsIgnoreCase("stop_pc"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					
					cha.abortAttack();
					cha.abortCast();
					cha.disableAllSkills();
					cha.setTarget(null);
					cha.stopMove(null);
					cha.setIsImmobilized(true);
					cha.getAI().setIntention(CtrlIntention.IDLE);
					
				}
			}
		}
		else if (event.equalsIgnoreCase("stop_npc"))
		{
			_Heading = npc.getHeading();
			if (_Heading < 32768)
				_Angle = Math.abs(180 - (int) (_Heading / 182.044444444));
			else
				_Angle = Math.abs(540 - (int) (_Heading / 182.044444444));
		}
		else if (event.equalsIgnoreCase("morph_01"))
		{
			
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 2000, 15000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_02", 3000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_02"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SocialAction(weakScarlet, 1));
				}
			}
			
			weakScarlet.setRHandId(7903);
			startQuestTimer("morph_03", 4000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_03"))
		{
			startQuestTimer("morph_04", 1500L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_04"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SocialAction(weakScarlet, 4));
				}
			}
			
			L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
			if (skill != null)
			{
				skill.getEffects(weakScarlet, weakScarlet);
			}
			startQuestTimer("morph_end", 6000L, weakScarlet, null, false);
			startQuestTimer("start_pc", 3000L, weakScarlet, null, false);
			startQuestTimer("start_npc", 3000L, weakScarlet, null, false);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill02", 10000 + Rnd.get(10000), weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_05a"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SocialAction(frintezza, 4));
				}
			}
			
		}
		else if (event.equalsIgnoreCase("morph_05"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 1000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_06", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_06"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 10000, 0, 0, 1, 0));
				}
			}
			
			cancelQuestTimers("loc_check");
			
			_Scarlet_x = weakScarlet.getX();
			_Scarlet_y = weakScarlet.getY();
			_Scarlet_z = weakScarlet.getZ();
			_Scarlet_h = weakScarlet.getHeading();
			weakScarlet.deleteMe();
			weakScarlet = null;
			activeScarlet = null;
			weakScarlet = ((L2GrandBossInstance) addSpawn(SCARLET1, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0L, false));
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
			weakScarlet.setRHandId(7903);
			
			startQuestTimer("morph_07", 7000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_07"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
					cha.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 500, 70, 15, 3000, 10000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_08", 3000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_08"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 2500, 90, 12, 6000, 10000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_09", 3000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_09"))
		{
			
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 1000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_10", 0L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_10"))
		{
			
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 10000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_11", 500L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_11"))
		{
			
			weakScarlet.doDie(weakScarlet);
			
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 450, _Angle, 14, 8000, 8000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_12", 6250L, weakScarlet, null, false);
			startQuestTimer("morph_13", 7200L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_12"))
		{
			weakScarlet.deleteMe();
			weakScarlet = null;
		}
		else if (event.equalsIgnoreCase("morph_13"))
		{
			strongScarlet = ((L2GrandBossInstance) addSpawn(SCARLET2, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0L, false));
			strongScarlet.setIsInvul(true);
			strongScarlet.setIsImmobilized(true);
			strongScarlet.disableAllSkills();
			activeScarlet = strongScarlet;
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 450, _Angle, 12, 500, 14000, 0, 0, 1, 0));
				}
			}
			
			startQuestTimer("morph_14", 3000L, strongScarlet, null, false);
			startQuestTimer("loc_check", 60000L, strongScarlet, null, true);
		}
		else if (event.equalsIgnoreCase("morph_14"))
		{
			startQuestTimer("morph_15", 5100L, strongScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_15"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.broadcastPacket(new SocialAction(strongScarlet, 2));
				}
			}
			
			L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
			if (skill != null)
			{
				skill.getEffects(strongScarlet, strongScarlet);
			}
			startQuestTimer("morph_end", 9000L, strongScarlet, null, false);
			startQuestTimer("start_pc", 6000L, strongScarlet, null, false);
			startQuestTimer("start_npc", 6000L, strongScarlet, null, false);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill03", 10000 + Rnd.get(10000), strongScarlet, null, false);
		}
		
		else if (event.equalsIgnoreCase("start_npc"))
		{
			npc.setRunning();
			npc.setIsInvul(false);
			npc.setIsImmobilized(false);
			npc.enableAllSkills();
		}
		
		else if (event.equalsIgnoreCase("morph_16"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SpecialCamera(strongScarlet.getObjectId(), 300, _Angle - 180, 5, 0, 7000, 0, 0, 1, 0));
					
				}
			}
			
			startQuestTimer("morph_17", 0L, strongScarlet, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_17"))
		{
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SpecialCamera(strongScarlet.getObjectId(), 200, _Angle, 85, 4000, 10000));
					
				}
			}
			
			startQuestTimer("morph_17b", 7400L, frintezza, null, false);
			startQuestTimer("morph_18", 7500L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_17b"))
		{
			frintezza.doDie(frintezza);
		}
		else if (event.equalsIgnoreCase("morph_18"))
		{
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 120, 5, 0, 7000));
					
				}
			}
			
			startQuestTimer("morph_19", 0L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_19"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 90, 5, 5000, 15000));
					
				}
			}
			
			startQuestTimer("morph_20", 7000L, frintezza, null, false);
			startQuestTimer("spawn_cubes", 7000L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_20"))
		{
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					
					p.sendPacket(new SpecialCamera(frintezza.getObjectId(), 900, 90, 25, 7000, 10000));
					
				}
			}
			
			startQuestTimer("start_pc", 7000L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_end"))
		{
			_OnMorph = 0;
		}
		else if (event.equalsIgnoreCase("start_pc"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.enableAllSkills();
					cha.setIsImmobilized(false);
					
				}
			}
			
		}
		else if (event.equalsIgnoreCase("check_hp"))
		{
			if (npc.isDead())
			{
				_OnMorph = 1;
				
				for (Player cha : _playersInside)
				{
					if (cha != null && cha.isOnline())
					{
						cha.sendPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
						
					}
				}
				
				startQuestTimer("attack_stop", 0, frintezza, null, false);
				startQuestTimer("stop_pc", 0, npc, null, false);
				startQuestTimer("stop_npc", 0, npc, null, false);
				startQuestTimer("morph_16", 0, npc, null, false);
				
			}
			else
			{
				_CheckDie = _CheckDie + 10;
				if (_CheckDie < 3000)
					startQuestTimer("check_hp", 10, npc, null, false);
				
				else
				{
					_OnCheck = 0;
					_CheckDie = 0;
				}
			}
		}
		else if (event.equalsIgnoreCase("morph_16"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new SpecialCamera(strongScarlet.getObjectId(), 300, _Angle - 180, 5, 0, 7000));
					
				}
			}
			
			startQuestTimer("morph_17", 0L, strongScarlet, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_17"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new SpecialCamera(strongScarlet.getObjectId(), 200, _Angle, 85, 4000, 10000));
					
				}
			}
			
			startQuestTimer("morph_17b", 7400L, frintezza, null, false);
			startQuestTimer("morph_18", 7500L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_17b"))
		{
			frintezza.doDie(frintezza);
		}
		else if (event.equalsIgnoreCase("morph_18"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 120, 5, 0, 7000));
					
				}
			}
			
			startQuestTimer("morph_19", 0L, frintezza, null, false);
			
		}
		else if (event.equalsIgnoreCase("morph_19"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new SpecialCamera(frintezza.getObjectId(), 100, 90, 5, 5000, 15000));
					
				}
			}
			
			startQuestTimer("morph_20", 7000L, frintezza, null, false);
			startQuestTimer("spawn_cubes", 7000L, frintezza, null, false);
			
		}
		else if (event.startsWith("spawn_cube"))
		{
			L2Npc npcCube = addSpawn(CUBE, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0, false);
			npcCube.setIsInvul(true);
			npcCube.setIsImmobilized(true);
		}
		else if (event.equalsIgnoreCase("morph_20"))
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new SpecialCamera(frintezza.getObjectId(), 900, 90, 25, 7000, 10000));
					
				}
			}
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.setIsImmobilized(false);
				}
			}
			
		}
		else if (event.equalsIgnoreCase("frintezza_despawn"))
		{
			long temp = 0L;
			
			temp = System.currentTimeMillis() - _LastAction;
			if (temp > 900000L)
			{
				
				cancelQuestTimers("waiting");
				cancelQuestTimers("loc_check");
				cancelQuestTimers("room_final");
				cancelQuestTimers("spawn_minion");
				startQuestTimer("clean", 1000L, npc, null, false);
				startQuestTimer("close", 1000L, npc, null, false);
				startQuestTimer("attack_stop", 1000L, npc, null, false);
				startQuestTimer("room1_del", 1000L, npc, null, false);
				startQuestTimer("room2_del", 1000L, npc, null, false);
				startQuestTimer("room3_del", 1000L, npc, null, false);
				startQuestTimer("minions_despawn", 1000L, npc, null, false);
				
				GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 0);
				
				cancelQuestTimers("frintezza_despawn");
			}
		}
		else if (event.equalsIgnoreCase("frintezza_unlock"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isPet)
	{
		if (npc == null)
			return null;
		
		if (npc.getNpcId() == SCARLET2)
		{
			for (Player cha : _playersInside)
			{
				if (cha != null && cha.isOnline())
				{
					cha.sendPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
					
				}
			}
			cancelQuestTimers("frintezza_despawn");
			
			for (Player p : _playersInside)
			{
				if (p != null && p.isOnline())
				{
					p.setIsImmobilized(true);
				}
			}
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DEAD);
			
			long respawnTime = TimeEpicBossManager.getInstance().getMillisUntilNextRespawn(FRINTEZZA);
			
			if (respawnTime <= 0)
			{
				// fallback para o cálculo antigo
				respawnTime = (long) Config.SPAWN_INTERVAL_FRINTEZZA + Rnd.get(-Config.RANDOM_SPAWN_TIME_FRINTEZZA, Config.RANDOM_SPAWN_TIME_FRINTEZZA);
				respawnTime *= 3600000; // converte horas para ms
				_log.warning("TimeEpicBoss: No respawn configured for Frintezza (" + FRINTEZZA + "), using fallback.");
			}
			startQuestTimer("close", 0, null, null, false);
			startQuestTimer("frintezza_unlock", respawnTime, null, null, false);
			StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(FRINTEZZA, info);
		}
		// não é alarme da sala 1? ignora
		if (_room1Mobs.contains(npc))
		{
			if (npc.getNpcId() == 18332)
			{
				// já finalizou sala 1? não faz mais nada
				if (_room1AllOpened)
					return super.onKill(npc, killer, isPet);
				
				final int dead = ++_room1HallPatrolDead;
				if (dead >= ROOM1_PARTIAL_OPEN_AT)
				{
					for (int i = 25150051; i <= 25150058; i++)
						openDoor(i);
				}
			}
			
			if (npc.getNpcId() == 18328)
			{
				final int dead = ++_room1AlarmsDead;
				
				if (dead >= ROOM1_ALARMS_TOTAL)
				{
					_room1AllOpened = true;
					
					tellPlayers("Todos os alarmes foram destruídos. Prossigam!");
					
					_room2Opened = true;
					
					tellPlayers("O caminho para a próxima sala foi liberado!");
					openDoor(25150042);
					openDoor(25150043);
					
					for (L2Npc n : _room1Mobs)
					{
						if (n != null)
						{
							for (int i = 18329; i <= 18333; i++)
							{
								if (n.getNpcId() == i)
									n.deleteMe();
							}
							
						}
					}
					
				}
			}
			if (npc.getNpcId() == 18339)
			{
				final int dead = ++_room1ChoirDead;
				
				if (dead >= ROOM2_CHOIR_TOTAL)
				{
					for (int i = 25150067; i <= 25150069; i++)
						openDoor(i);
					
					for (int i = 25150062; i <= 25150064; i++)
						openDoor(i);
				}
				
			}
			if (npc.getNpcId() == 18334)
			{
				final int dead = ++_room1ChoirCaptainDead;
				
				if (dead >= ROOM2_CHOIR_CAPTAIN_TOTAL)
				{
					for (int i = 25150045; i <= 25150046; i++)
						openDoor(i);
					
					for (L2Npc n : _room1Mobs)
					{
						if (n != null)
						{
							n.deleteMe();
						}
					}
					startQuestTimer("waiting", Config.WAIT_TIME_FRINTEZZA, npc, null, false);
					cancelQuestTimers("room_final");
				}
			}
			
		}
		
		return super.onKill(npc, killer, isPet);
		
	}
	
	@Override
	public String onAttack(L2Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		_LastAction = System.currentTimeMillis();
		
		final Integer status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (npc.getNpcId() == FRINTEZZA)
		{
			npc.setCurrentHpMp(npc.getMaxHp(), 0);
		}
		
		if (npc.getNpcId() == SCARLET1 && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.75 && status == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);
			
			_SecondMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 1000, npc, null, false);
			startQuestTimer("stop_npc", 1000, npc, null, false);
			startQuestTimer("morph_01", 1100, npc, null, false);
			
		}
		else if (npc.getNpcId() == SCARLET1 && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.5 && status == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);
			
			_ThirdMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 2000, npc, null, false);
			startQuestTimer("stop_npc", 2000, npc, null, false);
			startQuestTimer("morph_05a", 2000, npc, null, false);
			startQuestTimer("morph_05", 2100, npc, null, false);
			
		}
		else if (npc.getNpcId() == SCARLET2 && _SecondMorph == 1 && _ThirdMorph == 1 && _OnCheck == 0 && damage >= npc.getCurrentHp() && status == FIGHTING)
		{
			_OnCheck = 1;
			startQuestTimer("check_hp", 0, npc, null, false);
			
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	private static void closeDoors()
	{
		for (int i = 25150051; i <= 25150058; i++)
			closeDoor(i);
		
		for (int i = 25150061; i <= 25150070; i++)
			closeDoor(i);
		
		closeDoor(25150042);
		closeDoor(25150043);
		closeDoor(25150045);
		closeDoor(25150046);
	}
	
	private static void closeDoor(int doorId)
	{
		final var door = DoorTable.getInstance().getDoor(doorId);
		if (door != null)
			door.closeMe();
	}
	
	private static void openDoor(int doorId)
	{
		final var door = DoorTable.getInstance().getDoor(doorId);
		if (door != null)
			door.openMe();
	}
	
	private void cleanupAll()
	{
		_raidStarted = false;
		_LastAction = 0L;
		// despawn room1 mobs
		for (L2Npc n : _room1Mobs)
		{
			if (n != null)
				n.deleteMe();
		}
		_room1Mobs.clear();
		
		_room1AlarmsDead = 0;
		_room1HallPatrolDead = 0;
		_room1ChoirDead = 0;
		_room1ChoirCaptainDead = 0;
		_playersInside.clear();
		
	}
	
	private void tellPlayers(String msg)
	{
		for (Player p : _playersInside)
		{
			if (p != null && p.isOnline())
				p.sendPacket(new CreatureSay(0, 1, "Frintezza", msg));
		}
	}
	
	private void allowAndTeleport(Player player)
	{
		if (ZONE != null)
			ZONE.allowPlayerEntry(player, 300);
		
		final Location loc = nextEntryPoint();
		player.teleToLocation(loc.getX() + Rnd.get(50), loc.getY() + Rnd.get(50), loc.getZ(), 0);
	}
	
	private Location nextEntryPoint()
	{
		final int idx = (_locCycle++ % ENTRY_POINTS.size());
		return ENTRY_POINTS.get(idx);
	}
	
	private static void teleportToCubeExit(Player player)
	{
		player.teleToLocation(CUBE_EXIT.getX() + Rnd.get(500), CUBE_EXIT.getY() + Rnd.get(500), CUBE_EXIT.getZ(), 0);
	}
	
	private static final class EntryCheckResult
	{
		final boolean _ok;
		final String _htmlFail;
		final List<Player> _toTeleport;
		
		private EntryCheckResult(boolean ok, String htmlFail, List<Player> toTeleport)
		{
			_ok = ok;
			_htmlFail = htmlFail;
			_toTeleport = toTeleport;
		}
		
		static EntryCheckResult fail(String htmlFail)
		{
			return new EntryCheckResult(false, htmlFail, Collections.emptyList());
		}
		
		static EntryCheckResult ok(List<Player> toTeleport)
		{
			return new EntryCheckResult(true, null, toTeleport);
		}
	}
	
	private EntryCheckResult buildEntryList(Player leader, L2Npc npc, boolean bypassChecks)
	{
		
		if (!bypassChecks && !leader.isInsideRadius(npc, TALK_RADIUS, false, false))
			return EntryCheckResult.fail(html("Muito longe", "Aproxime-se do Guide para iniciar a entrada."));
		
		if (_playersInside.size() >= MAX_PLAYERS)
			return EntryCheckResult.fail(html("Lotado", "O número máximo de participantes já foi atingido."));
		
		if (bypassChecks)
			return EntryCheckResult.ok(Collections.singletonList(leader));
		
		// Aqui segue exatamente sua lógica atual:
		final boolean strictParty = Config.BYPASS_FRINTEZZA_PARTIES_CHECK;
		
		if (!leader.isInParty())
		{
			if (strictParty)
				return EntryCheckResult.fail(html("Requer party", "Este servidor exige Party/Command Channel para entrar.<br1>" + "Min parties: <font color=\"LEVEL\">" + Config.FRINTEZZA_MIN_PARTIES + "</font> " + "| Max parties: <font color=\"LEVEL\">" + Config.FRINTEZZA_MAX_PARTIES + "</font>"));
			
			if (leader.getLevel() < MIN_LEVEL)
				return EntryCheckResult.fail(html("Nível insuficiente", "Você precisa de nível <font color=\"LEVEL\">" + MIN_LEVEL + "</font>+ para entrar."));
			
			return EntryCheckResult.ok(Collections.singletonList(leader));
		}
		
		final L2Party party = leader.getParty();
		final L2CommandChannel cc = party.getCommandChannel();
		
		if (strictParty)
		{
			if (!party.isLeader(leader))
				return EntryCheckResult.fail(html("Somente líder", "A entrada deve ser iniciada pelo <font color=\"LEVEL\">Líder da Party</font>."));
			
			if (cc != null && cc.getChannelLeader() != leader)
				return EntryCheckResult.fail(html("Somente líder do CC", "A entrada deve ser iniciada pelo <font color=\"LEVEL\">Líder do Command Channel</font>."));
			
			if (cc != null)
			{
				final int parties = cc.getPartys().size();
				if (parties < Config.FRINTEZZA_MIN_PARTIES || parties > Config.FRINTEZZA_MAX_PARTIES)
				{
					return EntryCheckResult.fail(html("Quantidade de parties inválida", "Seu Command Channel precisa ter entre <font color=\"LEVEL\">" + Config.FRINTEZZA_MIN_PARTIES + "</font> e " + "<font color=\"LEVEL\">" + Config.FRINTEZZA_MAX_PARTIES + "</font> parties."));
				}
			}
		}
		
		final List<Player> result = new ArrayList<>(MAX_PLAYERS);
		
		if (cc != null)
		{
			for (L2Party p : cc.getPartys())
			{
				if (p == null)
					continue;
				
				collectEligibleFromParty(p, npc, result);
				if (result.size() >= MAX_PLAYERS)
					break;
			}
		}
		else
		{
			collectEligibleFromParty(party, npc, result);
		}
		
		if (result.isEmpty())
			return EntryCheckResult.fail(html("Ninguém elegível", "Nenhum membro elegível foi encontrado próximo ao Guide.<br1>" + "Requisitos: nível " + MIN_LEVEL + "+ e estar a até " + TALK_RADIUS + " de distância."));
		
		// Respeita limite global
		if ((_playersInside.size() + result.size()) > MAX_PLAYERS)
		{
			final int free = Math.max(0, MAX_PLAYERS - _playersInside.size());
			if (free <= 0)
				return EntryCheckResult.fail(html("Lotado", "O número máximo de participantes já foi atingido."));
			
			return EntryCheckResult.ok(result.subList(0, Math.min(free, result.size())));
		}
		
		return EntryCheckResult.ok(result);
	}
	
	private void collectEligibleFromParty(L2Party party, L2Npc npc, List<Player> out)
	{
		for (Player m : party.getPartyMembers())
		{
			if (m == null)
				continue;
			
			if (m.getLevel() < MIN_LEVEL)
				continue;
			
			if (!m.isInsideRadius(npc, TALK_RADIUS, false, false))
				continue;
			
			if (_playersInside.contains(m))
				continue;
			
			out.add(m);
			
			if (out.size() >= MAX_PLAYERS)
				return;
		}
	}
	
	private static String html(String title, String body)
	{
		return "<html><body><center>" + "<table width=290 cellpadding=0 cellspacing=0>" + "<tr><td align=center><font color=\"LEVEL\">" + escape(title) + "</font></td></tr>" + "<tr><td height=8></td></tr>" + "<tr><td>" + body + "</td></tr>" + "<tr><td height=10></td></tr>" + "<tr><td><img src=\"L2UI.SquareGray\" width=290 height=1></td></tr>" + "<tr><td height=6></td></tr>" + "<tr><td><font color=\"808080\">Requisitos: Lv " + MIN_LEVEL + "+ • Limite: " + MAX_PLAYERS + "</font></td></tr>" + "</table></center></body></html>";
	}
	
	private static String escape(String s)
	{
		if (s == null || s.isEmpty())
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
	
	private void spawnRoom1()
	{
		_room1Mobs.clear();
		_room1AlarmsDead = 0;
		_room1ChoirDead = 0;
		for (SpawnPoint sp : ROOM1_SPAWNS)
		{
			final L2Npc mob = addSpawn(sp.npcId, sp.loc.getX(), sp.loc.getY(), sp.loc.getZ(), sp.heading, false, 0, false);
			
			if (mob != null)
				_room1Mobs.add(mob);
		}
	}
	
	private static final List<Location> ENTRY_POINTS;
	static
	{
		final List<Location> list = new ArrayList<>();
		list.add(new Location(174102, -76039, -5105));
		list.add(new Location(173235, -76884, -5105));
		list.add(new Location(175003, -76933, -5105));
		list.add(new Location(174196, -76190, -5105));
		list.add(new Location(174013, -76120, -5105));
		list.add(new Location(173263, -75161, -5105));
		ENTRY_POINTS = Collections.unmodifiableList(list);
	}
	
	private static final int[][] _skill =
	{
		{
			5015,
			1,
			5000
		},
		{
			5015,
			4,
			5000
		},
		{
			5015,
			2,
			5000
		},
		{
			5015,
			5,
			5000
		},
		{
			5018,
			1,
			10000
		},
		{
			5016,
			1,
			5000
		},
		{
			5015,
			3,
			5000
		},
		{
			5015,
			6,
			5000
		},
		{
			5018,
			2,
			10000
		},
		{
			5019,
			1,
			10000
		},
		{
			5016,
			1,
			5000
		}
	};
	
	private static final class SpawnPoint
	{
		final int npcId;
		final Location loc;
		final int heading;
		
		public SpawnPoint(int npcId, int x, int y, int z, int heading)
		{
			this.npcId = npcId;
			this.loc = new Location(x, y, z);
			this.heading = heading;
		}
	}
	
	private static final List<SpawnPoint> ROOM1_SPAWNS;
	static
	{
		final int[][] raw =
		{
			{
				18328,
				172894,
				-76019,
				-5107,
				243
			},
			{
				18328,
				174095,
				-77279,
				-5107,
				16216
			},
			{
				18328,
				174111,
				-74833,
				-5107,
				49043
			},
			{
				18328,
				175344,
				-76042,
				-5107,
				32847
			},
			{
				18330,
				173489,
				-76227,
				-5134,
				63565
			},
			{
				18330,
				173498,
				-75724,
				-5107,
				58498
			},
			{
				18330,
				174365,
				-76745,
				-5107,
				22424
			},
			{
				18330,
				174570,
				-75584,
				-5107,
				31968
			},
			{
				18330,
				174613,
				-76179,
				-5107,
				31471
			},
			{
				18332,
				173620,
				-75981,
				-5107,
				4588
			},
			{
				18332,
				173630,
				-76340,
				-5107,
				62454
			},
			{
				18332,
				173755,
				-75613,
				-5107,
				57892
			},
			{
				18332,
				173823,
				-76688,
				-5107,
				2411
			},
			{
				18332,
				174000,
				-75411,
				-5107,
				54718
			},
			{
				18332,
				174487,
				-75555,
				-5107,
				33861
			},
			{
				18332,
				174517,
				-76471,
				-5107,
				21893
			},
			{
				18332,
				174576,
				-76122,
				-5107,
				31176
			},
			{
				18332,
				174600,
				-75841,
				-5134,
				35927
			},
			{
				18329,
				173481,
				-76043,
				-5107,
				61312
			},
			{
				18329,
				173539,
				-75678,
				-5107,
				59524
			},
			{
				18329,
				173584,
				-76386,
				-5107,
				3041
			},
			{
				18329,
				173773,
				-75420,
				-5107,
				51115
			},
			{
				18329,
				173777,
				-76650,
				-5107,
				12588
			},
			{
				18329,
				174585,
				-76510,
				-5107,
				21704
			},
			{
				18329,
				174623,
				-75571,
				-5107,
				40141
			},
			{
				18329,
				174744,
				-76240,
				-5107,
				29202
			},
			{
				18329,
				174769,
				-75895,
				-5107,
				29572
			},
			{
				18333,
				173861,
				-76011,
				-5107,
				383
			},
			{
				18333,
				173872,
				-76461,
				-5107,
				8041
			},
			{
				18333,
				173898,
				-75668,
				-5107,
				51856
			},
			{
				18333,
				174422,
				-75689,
				-5107,
				42878
			},
			{
				18333,
				174460,
				-76355,
				-5107,
				27311
			},
			{
				18333,
				174483,
				-76041,
				-5107,
				30947
			},
			{
				18331,
				173515,
				-76184,
				-5107,
				6971
			},
			{
				18331,
				173516,
				-75790,
				-5134,
				3142
			},
			{
				18331,
				173696,
				-76675,
				-5107,
				6757
			},
			{
				18331,
				173766,
				-75502,
				-5134,
				60827
			},
			{
				18331,
				174473,
				-75321,
				-5107,
				37147
			},
			{
				18331,
				174493,
				-76505,
				-5107,
				34503
			},
			{
				18331,
				174568,
				-75654,
				-5134,
				41661
			},
			{
				18331,
				174584,
				-76263,
				-5107,
				31729
			},
			{
				18339,
				173892,
				-81592,
				-5123,
				50849
			},
			{
				18339,
				173958,
				-81820,
				-5123,
				7459
			},
			{
				18339,
				174128,
				-81805,
				-5150,
				21495
			},
			{
				18339,
				174245,
				-81566,
				-5123,
				41760
			},
			{
				18334,
				173264,
				-81529,
				-5072,
				1646
			},
			{
				18334,
				173265,
				-81656,
				-5072,
				441
			},
			{
				18334,
				173267,
				-81889,
				-5072,
				0
			},
			{
				18334,
				173271,
				-82015,
				-5072,
				65382
			},
			{
				18334,
				174867,
				-81655,
				-5073,
				32537
			},
			{
				18334,
				174868,
				-81890,
				-5073,
				32768
			},
			{
				18334,
				174869,
				-81485,
				-5073,
				32315
			},
			{
				18334,
				174871,
				-82017,
				-5073,
				33007
			},
			{
				18335,
				173074,
				-80817,
				-5107,
				8353
			},
			{
				18335,
				173128,
				-82702,
				-5107,
				5345
			},
			{
				18335,
				173181,
				-82544,
				-5107,
				65135
			},
			{
				18335,
				173191,
				-80981,
				-5107,
				6947
			},
			{
				18335,
				174859,
				-80889,
				-5134,
				24103
			},
			{
				18335,
				174924,
				-82666,
				-5107,
				38710
			},
			{
				18335,
				174947,
				-80733,
				-5107,
				22449
			},
			{
				18335,
				175096,
				-82724,
				-5107,
				42205
			},
			{
				18336,
				173435,
				-80512,
				-5107,
				65215
			},
			{
				18336,
				173440,
				-82948,
				-5107,
				417
			},
			{
				18336,
				173443,
				-83120,
				-5107,
				1094
			},
			{
				18336,
				173463,
				-83064,
				-5107,
				286
			},
			{
				18336,
				173465,
				-80453,
				-5107,
				174
			},
			{
				18336,
				173465,
				-83006,
				-5107,
				2604
			},
			{
				18336,
				173468,
				-82889,
				-5107,
				316
			},
			{
				18336,
				173469,
				-80570,
				-5107,
				65353
			},
			{
				18336,
				173469,
				-80628,
				-5107,
				166
			},
			{
				18336,
				173492,
				-83121,
				-5107,
				394
			},
			{
				18336,
				173493,
				-80683,
				-5107,
				0
			},
			{
				18336,
				173497,
				-80510,
				-5134,
				417
			},
			{
				18336,
				173499,
				-82947,
				-5107,
				0
			},
			{
				18336,
				173521,
				-83063,
				-5107,
				316
			},
			{
				18336,
				173523,
				-82889,
				-5107,
				128
			},
			{
				18336,
				173524,
				-80627,
				-5134,
				65027
			},
			{
				18336,
				173524,
				-83007,
				-5107,
				0
			},
			{
				18336,
				173526,
				-80452,
				-5107,
				64735
			},
			{
				18336,
				173527,
				-80569,
				-5134,
				65062
			},
			{
				18336,
				174602,
				-83122,
				-5107,
				33104
			},
			{
				18336,
				174604,
				-82949,
				-5107,
				33184
			},
			{
				18336,
				174609,
				-80514,
				-5107,
				33234
			},
			{
				18336,
				174609,
				-80684,
				-5107,
				32851
			},
			{
				18336,
				174629,
				-80627,
				-5107,
				33346
			},
			{
				18336,
				174632,
				-80570,
				-5107,
				32896
			},
			{
				18336,
				174632,
				-83066,
				-5107,
				32768
			},
			{
				18336,
				174635,
				-82893,
				-5107,
				33594
			},
			{
				18336,
				174636,
				-80456,
				-5107,
				32065
			},
			{
				18336,
				174639,
				-83008,
				-5107,
				33057
			},
			{
				18336,
				174660,
				-80512,
				-5107,
				33057
			},
			{
				18336,
				174661,
				-83121,
				-5107,
				32768
			},
			{
				18336,
				174663,
				-82948,
				-5107,
				32768
			},
			{
				18336,
				174664,
				-80685,
				-5107,
				32676
			},
			{
				18336,
				174687,
				-83008,
				-5107,
				32520
			},
			{
				18336,
				174691,
				-83066,
				-5107,
				32961
			},
			{
				18336,
				174692,
				-80455,
				-5107,
				33202
			},
			{
				18336,
				174692,
				-80571,
				-5107,
				32768
			},
			{
				18336,
				174693,
				-80630,
				-5107,
				32994
			},
			{
				18336,
				174693,
				-82889,
				-5107,
				32622
			},
			{
				18337,
				172837,
				-82382,
				-5107,
				58363
			},
			{
				18337,
				172867,
				-81123,
				-5107,
				64055
			},
			{
				18337,
				172883,
				-82495,
				-5107,
				64764
			},
			{
				18337,
				172916,
				-81033,
				-5107,
				7099
			},
			{
				18337,
				172940,
				-82325,
				-5107,
				58998
			},
			{
				18337,
				172946,
				-82435,
				-5107,
				58038
			},
			{
				18337,
				172971,
				-81198,
				-5107,
				14768
			},
			{
				18337,
				172992,
				-81091,
				-5107,
				9438
			},
			{
				18337,
				173032,
				-82365,
				-5107,
				59041
			},
			{
				18337,
				173064,
				-81125,
				-5107,
				5827
			},
			{
				18337,
				175014,
				-81173,
				-5107,
				26398
			},
			{
				18337,
				175061,
				-82374,
				-5107,
				43290
			},
			{
				18337,
				175096,
				-81080,
				-5107,
				24719
			},
			{
				18337,
				175169,
				-82453,
				-5107,
				37672
			},
			{
				18337,
				175172,
				-80972,
				-5107,
				32315
			},
			{
				18337,
				175174,
				-82328,
				-5107,
				41760
			},
			{
				18337,
				175197,
				-81157,
				-5107,
				27617
			},
			{
				18337,
				175245,
				-82547,
				-5107,
				40275
			},
			{
				18337,
				175249,
				-81075,
				-5107,
				28435
			},
			{
				18337,
				175292,
				-82432,
				-5107,
				42225
			},
			{
				18338,
				173014,
				-82628,
				-5107,
				11874
			},
			{
				18338,
				173033,
				-80920,
				-5107,
				10425
			},
			{
				18338,
				173095,
				-82520,
				-5107,
				49152
			},
			{
				18338,
				173115,
				-80986,
				-5107,
				9611
			},
			{
				18338,
				173144,
				-80894,
				-5107,
				5345
			},
			{
				18338,
				173147,
				-82602,
				-5107,
				51316
			},
			{
				18338,
				174912,
				-80825,
				-5107,
				24270
			},
			{
				18338,
				174935,
				-80899,
				-5107,
				18061
			},
			{
				18338,
				175016,
				-82697,
				-5107,
				39533
			},
			{
				18338,
				175041,
				-80834,
				-5107,
				25420
			},
			{
				18338,
				175071,
				-82549,
				-5107,
				39163
			},
			{
				18338,
				175154,
				-82619,
				-5107,
				36345
			}
		};
		
		final List<SpawnPoint> list = new ArrayList<>(raw.length);
		for (int[] r : raw)
			list.add(new SpawnPoint(r[0], r[1], r[2], r[3], r[4]));
		ROOM1_SPAWNS = Collections.unmodifiableList(list);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		if (npc == null || player == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case CUBE:
				teleportToCubeExit(player);
				return null;
			
			case GUIDE:
				return handleGuideTalk(npc, player);
		}
		return null;
	}
	
	private String handleGuideTalk(L2Npc npc, Player player)
	{
		final byte status = (byte) GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (status == DEAD)
			return html("Entrada bloqueada", "Não há nada além do campo de força. Volte mais tarde.<br1>(Frintezza não está disponível no momento.)");
		
		if (status == FIGHTING)
			return html("Entrada bloqueada", "Alguém já está dentro do campo de força.<br1>Tente novamente mais tarde.");
		
		if (status != DORMANT && status != WAITING)
			return html("Indisponível", "Não foi possível processar a entrada agora. Tente novamente.");
		
		// GM: pode ignorar party/CC/level/range se você quiser
		final boolean bypassChecks = player.isGM();
		
		// Monta lista real de elegíveis
		final EntryCheckResult check = buildEntryList(player, npc, bypassChecks);
		if (!check._ok)
			return check._htmlFail;
		
		// Item obrigatório (GM também consome)
		if (Config.QUEST_FRINTEZZA > 0 && player.getInventory().getItemByItemId(Config.QUEST_FRINTEZZA) == null)
		{
			final String itemName = (ItemTable.getInstance().getTemplate(Config.QUEST_FRINTEZZA) != null) ? ItemTable.getInstance().getTemplate(Config.QUEST_FRINTEZZA).getName() : ("Item ID " + Config.QUEST_FRINTEZZA);
			
			return html("Item necessário", "Você não possui o item necessário para entrar.<br1>Item: <font color=\"LEVEL\">" + itemName + "</font>");
		}
		
		// Inicia raid na primeira entrada
		if (status == DORMANT)
			startRaid(npc);
		
		// Consome 1 item do iniciador (GM incluso)
		if (Config.QUEST_FRINTEZZA > 0)
			player.destroyItemByItemId("Frintezza", Config.QUEST_FRINTEZZA, 1, player, true);
		
		// Teleporta todos
		for (Player member : check._toTeleport)
		{
			_playersInside.addIfAbsent(member);
			allowAndTeleport(member);
		}
		
		tellPlayers("Voce entrou no Imperial Tomb. Prepare-se...");
		_LastAction = System.currentTimeMillis();
		return null;
	}
	
	private void startRaid(L2Npc guideNpc)
	{
		if (_raidStarted)
			return;
		
		_raidStarted = true;
		
		GrandBossManager.getInstance().setBossStatus(FRINTEZZA, WAITING);
		
		// Fecha portas imediatamente
		startQuestTimer("close", 0, guideNpc, null, false);
		
		// Spawna Room1 após 5s
		startQuestTimer("room1_spawn", 5000L, guideNpc, null, false);
		
		// Tempo limite total (Config.WAIT_TIME_FRINTEZZA já deve estar em ms)
		startQuestTimer("room_final", Config.WAIT_TIME_FRINTEZZA * 60000L, guideNpc, null, false);
		startQuestTimer("frintezza_despawn", 60000, null, null, true);
		tellPlayers("A tumba começou a reagir... (Room 1 em 5s)");
	}
}
