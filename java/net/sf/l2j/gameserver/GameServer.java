package net.sf.l2j.gameserver;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.bosstimerespawn.TimeEpicBossManager;
import net.sf.l2j.bosstimerespawn.TimeRaidBossManager;
import net.sf.l2j.clan.ranking.ClanRankingConfig;
import net.sf.l2j.clan.ranking.ClanRankingManager;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.mmocore.SelectorConfig;
import net.sf.l2j.commons.mmocore.SelectorThread;
import net.sf.l2j.community.marketplace.AuctionTableCommunity;
import net.sf.l2j.dailyreward.DailyRewardManager;
import net.sf.l2j.dolls.DollsTable;
import net.sf.l2j.dropdiary.DropDiaryManager;
import net.sf.l2j.dungeon.DungeonConfig;
import net.sf.l2j.dungeon.DungeonManager;
import net.sf.l2j.email.items.MailManager;
import net.sf.l2j.event.bossevent.IPManager;
import net.sf.l2j.event.bossevent.KTBConfig;
import net.sf.l2j.event.bossevent.KTBManager;
import net.sf.l2j.event.championInvade.InitialChampionInvade;
import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.CTFManager;
import net.sf.l2j.event.deathmath.DMConfig;
import net.sf.l2j.event.deathmath.DMManager;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.FOSManager;
import net.sf.l2j.event.lastman.LMConfig;
import net.sf.l2j.event.lastman.LMManager;
import net.sf.l2j.event.partyfarm.InitialPartyFarm;
import net.sf.l2j.event.partyfarm.PartyZoneReward;
import net.sf.l2j.event.pcbang.PcBang;
import net.sf.l2j.event.rewardsoloevent.InitialSoloEvent;
import net.sf.l2j.event.soloboss.InitialSoloBossEvent;
import net.sf.l2j.event.spoil.InitialSpoilEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.event.tournament.ArenaEvent;
import net.sf.l2j.event.tournament.InstanceManager;
import net.sf.l2j.event.tvt.TvTAreasLoader;
import net.sf.l2j.event.tvt.TvTConfig;
import net.sf.l2j.event.tvt.TvTManager;
import net.sf.l2j.events.eventpvp.PvPEventManager;
import net.sf.l2j.fusionitems.FusionItemData;
import net.sf.l2j.gameserver.balance.BalanceManager;
import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.sf.l2j.gameserver.datatables.AccessLevels;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.ArmorSetsTable;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.BookmarkTable;
import net.sf.l2j.gameserver.datatables.BufferTable;
import net.sf.l2j.gameserver.datatables.BuyListTable;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.FishTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.HelperBuffTable;
import net.sf.l2j.gameserver.datatables.HennaTable;
import net.sf.l2j.gameserver.datatables.HerbDropTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.NpcWalkerRoutesTable;
import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.datatables.ServerMemo;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.datatables.SoulCrystalsTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.datatables.SpellbookTable;
import net.sf.l2j.gameserver.datatables.StaticObjects;
import net.sf.l2j.gameserver.datatables.SummonItemsData;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.datatables.xml.FakePcsTable;
import net.sf.l2j.gameserver.datatables.xml.IconTable;
import net.sf.l2j.gameserver.datatables.xml.RouletteData;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.BypassHandler;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.SkillHandler;
import net.sf.l2j.gameserver.handler.UserCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.AuctionManager;
import net.sf.l2j.gameserver.instancemanager.AutoSpawnManager;
import net.sf.l2j.gameserver.instancemanager.BoatManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CastleManorManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.CrownManager;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.instancemanager.DayNightSpawnManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.FishingChampionshipManager;
import net.sf.l2j.gameserver.instancemanager.FourSepulchersManager;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.MercTicketManager;
import net.sf.l2j.gameserver.instancemanager.MovieMakerManager;
import net.sf.l2j.gameserver.instancemanager.PetitionManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossPointsManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.ServerStatusManager;
import net.sf.l2j.gameserver.instancemanager.ServerStatusManager.ServerType;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.SevenSignsFestival;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.AuctionTable;
import net.sf.l2j.gameserver.instancemanager.custom.CharacterKillingManager;
import net.sf.l2j.gameserver.instancemanager.custom.ChatBanManager;
import net.sf.l2j.gameserver.instancemanager.custom.ChatGlobalManager;
import net.sf.l2j.gameserver.instancemanager.custom.ChatHeroManager;
import net.sf.l2j.gameserver.instancemanager.custom.ChatVipManager;
import net.sf.l2j.gameserver.instancemanager.custom.DressMeData;
import net.sf.l2j.gameserver.instancemanager.custom.EpicZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.FarmZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.HeroManagerCustom;
import net.sf.l2j.gameserver.instancemanager.custom.RaidBossInfoManager;
import net.sf.l2j.gameserver.instancemanager.custom.RaidZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.TimeZoneManager;
import net.sf.l2j.gameserver.instancemanager.games.MonsterRace;
import net.sf.l2j.gameserver.model.L2Manor;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchRoomList;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchWaitingList;
import net.sf.l2j.gameserver.model.vehicles.BoatGiranTalking;
import net.sf.l2j.gameserver.model.vehicles.BoatGludinRune;
import net.sf.l2j.gameserver.model.vehicles.BoatInnadrilTour;
import net.sf.l2j.gameserver.model.vehicles.BoatRunePrimeval;
import net.sf.l2j.gameserver.model.vehicles.BoatTalkingGludin;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GamePacketHandler;
import net.sf.l2j.gameserver.scriptings.ScriptManager;
import net.sf.l2j.gameserver.scripts.EventDroplist;
import net.sf.l2j.gameserver.scripts.faenor.FaenorScriptEngine;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.taskmanager.GameTimeController;
import net.sf.l2j.gameserver.taskmanager.GuardAntPkTaskManager;
import net.sf.l2j.gameserver.taskmanager.ItemsOnGroundTaskManager;
import net.sf.l2j.gameserver.taskmanager.KnownListUpdateTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvPZoneTimeTask;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;
import net.sf.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import net.sf.l2j.gameserver.taskmanager.ShadowItemTaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.taskmanager.WaterTaskManager;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;
import net.sf.l2j.hwid.Hwid;
import net.sf.l2j.itemstime.TimeItemData;
import net.sf.l2j.itemstime.TimedItemManager;
import net.sf.l2j.launcher.GameServerLauncher;
import net.sf.l2j.mission.MissionReset;
import net.sf.l2j.mods.FakesEngine;
import net.sf.l2j.shop.offline.OfflinePlayerData;
import net.sf.l2j.shop.offline.OfflineStoresData;
import net.sf.l2j.timezone.TimeFarmZoneData;
import net.sf.l2j.timezone.TimeFarmZoneManager;
import net.sf.l2j.upgrade.UpgradeItemData;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;

public class GameServer
{
	public static Logger _log = Logger.getLogger(GameServer.class.getName());
	public static ServerStatusManager STATUS;
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	private final LoginServerThread _loginThread;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public GameServer() throws Exception
	{
		gameServer = this;
		long serverLoadStart = System.currentTimeMillis();
		IdFactory.getInstance();
		
		new File("./data/crests").mkdirs();
		
		StringUtil.printSection("World");
		L2World.getInstance();
		MapRegionTable.getInstance();
		
		AnnouncementTable.getInstance();
		ServerMemo.getInstance();
		TaskManager.getInstance();
		TimedItemManager.getInstance();
		
		StringUtil.printSection("Instance Manager");
		InstanceManager.getInstance();
		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		
		StringUtil.printSection("Items");
		ItemTable.getInstance();
		SummonItemsData.getInstance();
		BuyListTable.getInstance();
		MultisellData.getInstance();
		RecipeTable.getInstance();
		ArmorSetsTable.getInstance();
		FishTable.getInstance();
		SpellbookTable.getInstance();
		SoulCrystalsTable.load();
		AugmentationData.getInstance();
		CursedWeaponsManager.getInstance();
		FusionItemData.getInstance();
		MailManager.getInstance().load();
		
		StringUtil.printSection("Admins");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		BookmarkTable.getInstance();
		GmListTable.getInstance();
		MovieMakerManager.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		CharTemplateTable.getInstance();
		CharNameTable.getInstance();
		HennaTable.getInstance();
		HelperBuffTable.getInstance();
		TeleportLocationTable.getInstance();
		HtmCache.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		RaidBossPointsManager.getInstance();
		
		StringUtil.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			_log.config("Community server is disabled.");
		
		StringUtil.printSection("Clans");
		CrestCache.getInstance();
		ClanTable.getInstance();
		AuctionManager.getInstance();
		ClanHallManager.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeController.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		KnownListUpdateTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		GuardAntPkTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		
		StringUtil.printSection("Castles");
		CastleManager.getInstance().load();
		CrownManager.getInstance();
		StringUtil.printSection("Seven Signs");
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		
		StringUtil.printSection("Sieges");
		if (Config.ENABLE_SIEGE_MANAGER)
		{
			SiegeManager.getInstance();
			SiegeManager.getSieges();
		}
		else
		{
			_log.info("Siege Manager is disabled.");
		}
		
		MercTicketManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		CastleManorManager.getInstance();
		L2Manor.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferTable.getInstance();
		HerbDropTable.getInstance();
		NpcTable.getInstance();
		NpcWalkerRoutesTable.getInstance();
		DoorTable.getInstance().spawn();
		StaticObjects.load();
		SpawnTable.getInstance();
		RaidBossSpawnManager.getInstance();
		GrandBossManager.getInstance();
		DayNightSpawnManager.getInstance();
		DimensionalRiftManager.getInstance();
		FakePcsTable.getInstance();
		ClanRankingConfig.load();
		ClanRankingManager.getInstance();
		TimeRaidBossManager.getInstance();
		TimeEpicBossManager.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		if (Config.OLY_FIGHT)
		{
			OlympiadGameManager.getInstance();
			Olympiad.getInstance();
			Hero.getInstance();
		}
		else
		{
			_log.info("Olympiad Manager is disabled.");
		}
		
		StringUtil.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance().init();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptManager.getInstance();
		
		StringUtil.printSection("L2JMods");
		FakesEngine.getInstance().onLoad();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		
		StringUtil.printSection("Monster Derby Track");
		MonsterRace.getInstance();
		
		if (Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionshipManager.getInstance();
		}
		
		Hwid.Init();
		
		StringUtil.printSection("Custom World");
		IPManager.getInstance();
		RouletteData.getInstance();
		DressMeData.getInstance();
		HeroManagerCustom.getInstance();
		UpgradeItemData.getInstance();
		DollsTable.getInstance();
		TimeItemData.getInstance();
		DropDiaryManager.load();
		PvPZoneTimeTask.getInstance();
		IconTable.getInstance();
		RaidBossInfoManager.getInstance();
		ChatBanManager.getInstance();
		ChatGlobalManager.getInstance();
		ChatHeroManager.getInstance();
		ChatVipManager.getInstance();
		AuctionTable.getInstance();
		AuctionTableCommunity.getInstance();
		RaidZoneManager.getInstance();
		EpicZoneManager.getInstance();
		FarmZoneManager.getInstance();
		PartyZoneReward.getInstance();
		TimeZoneManager.getInstance();
		
		StringUtil.printSection("Events");
		if (Config.ENABLE_EVENT_2008)
		{
			EventDroplist.getInstance();
			FaenorScriptEngine.getInstance();
		}
		else
		{
			_log.info("Faenor Event is disabled.");
		}
		if (Config.CKM_ENABLED)
		{
			CharacterKillingManager.getInstance().init();
		}
		else
		{
			_log.info("Monuments is disabled.");
		}
		
		StringUtil.printSection("OfflineShop Started");
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
		{
			OfflineStoresData.getInstance().restoreOfflineTraders();
		}
		
		StringUtil.printSection("OfflinePlayers Started");
		if (Config.ENABLE_OFFLINE_PLAYER && Config.RESTORE_OFFLINERS_PLAYERS)
		{
			OfflinePlayerData.getInstance().restoreOfflineTraders();
		}
		
		StringUtil.printSection("Events");
		if (Config.PCB_ENABLE)
		{
			System.out.println("PcBang Event Enabled");
			ThreadPool.scheduleAtFixedRate(PcBang.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		}
		else
		{
			_log.info("PcBang Event is disabled.");
		}
		
		if ((Config.START_SPOIL))
		{
			_log.info("[Start Spawn Spoil Event]: Enabled");
			InitialSpoilEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Spoil Event is disabled.");
		}
		
		if (Config.PVP_EVENT_ENABLED)
		{
			PvPEventManager.getInstance();
		}
		else
		{
			_log.info("PvP Event disabled.");
		}
		
		if ((Config.CHAMPION_FARM_BY_TIME_OF_DAY))
		{
			_log.info("[Champion Invade Event]: Enabled");
			InitialChampionInvade.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Champion Invade Event is disabled.");
		}
		
		if ((Config.SOLO_FARM_BY_TIME_OF_DAY))
		{
			_log.info("[Reward Solo Event]: Enabled");
			InitialSoloEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Reward Solo Event is disabled.");
		}
		TvTAreasLoader.load();
		KTBConfig.init();
		KTBManager.getInstance();
		LMConfig.init();
		LMManager.getInstance();
		DMConfig.init();
		DMManager.getInstance();
		FOSConfig.init();
		FOSManager.getInstance();
		TvTConfig.init();
		TvTManager.getInstance();
		CTFConfig.init();
		CTFManager.getInstance();
		if ((Config.START_PARTY))
		{
			_log.info("[Start Spawn Party Farm]: Enabled");
			InitialPartyFarm.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Party Farm is disabled.");
		}
		
		if ((Config.SOLO_BOSS_EVENT))
		{
			_log.info("[Start Solo Boss Event]: Enabled");
			InitialSoloBossEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Solo Boss Event is disabled.");
		}
		
		StringUtil.printSection("Dungeon Manager Test");
		DungeonManager.getInstance();
		DungeonConfig.init();
		
		StringUtil.printSection("Daily Reward System");
		if (Config.ENABLE_REWARD_DAILY)
		{
			_log.info("Daily Reward is Enabled");
			DailyRewardManager.getInstance();
		}
		else
		{
			_log.info("Daily Reward is disabled.");
		}
		if (Config.ACTIVE_MISSION)
		{
			MissionReset.getInstance().StartNextEventTime();
		}
		else
		{
			_log.info("Mission Reset: desativado...");
		}
		ArenaConfig.init();
		if (ArenaConfig.TOURNAMENT_EVENT_TIME)
		{
			_log.info("Tournament Event is enabled.");
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			_log.info("Tournament Event is disabled");
		}
		
		StringUtil.printSection("Handlers");
		_log.config("AutoSpawnHandler: Loaded " + AutoSpawnManager.getInstance().size() + " handlers.");
		_log.config("AdminCommandHandler: Loaded " + AdminCommandHandler.getInstance().size() + " handlers.");
		_log.config("ChatHandler: Loaded " + ChatHandler.getInstance().size() + " handlers.");
		_log.config("ItemHandler: Loaded " + ItemHandler.getInstance().size() + " handlers.");
		_log.config("SkillHandler: Loaded " + SkillHandler.getInstance().size() + " handlers.");
		_log.config("UserCommandHandler: Loaded " + UserCommandHandler.getInstance().size() + " handlers.");
		_log.config("UserVoicedCommandHandler Loaded " + VoicedCommandHandler.getInstance().size() + " handlers.");
		_log.config("UserBypassCommandHandler: Loaded " + BypassHandler.getInstance().size() + " handlers.");
		
		if (Config.ENABLE_TIME_ZONE_SYSTEM)
		{
			TimeFarmZoneData.loadZonesFromFile();
			TimeFarmZoneManager.init();
		}
		
		StringUtil.printSection("System");
		printStatus();
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		_log.config("IdFactory: Free ObjectIDs remaining: " + IdFactory.getInstance().size());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_log.info("Deadlock detector is enabled. Timer: " + Config.DEADLOCK_CHECK_INTERVAL + "s.");
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_log.info("Deadlock detector is disabled.");
			_deadDetectThread = null;
		}
		
		System.gc();
		
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		StringUtil.printSection("Login");
		_log.info("Gameserver have started, used memory: " + usedMem + " / " + totalMem + " Mo.");
		_log.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		_log.info("Loaded in: " + (System.currentTimeMillis() - serverLoadStart) / 1000 + " seconds");
		
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				_log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all available IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();
		
	}
	
	private static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	public static void main(String[] args) throws Exception
	{
		
		if (isWindows())
		{
			try
			{
				if (!GraphicsEnvironment.isHeadless())
				{
					System.out.println("World: Running in Interface GUI.");
					new GameServerLauncher();
				}
			}
			catch (Throwable t)
			{
				// ignora completamente GUI
			}
		}
		
		startServer();
	}
	
	private static void startServer() throws Exception
	{
		final String LOG_FOLDER = "./log";
		final String LOG_NAME = "config/log.cfg";
		
		new File(LOG_FOLDER).mkdir();
		
		try (InputStream is = new FileInputStream(LOG_NAME))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		StringUtil.printSection("Master");
		
		Config.loadGameServer();
		XMLDocumentFactory.getInstance();
		ConnectionPool.init();
		BalanceManager.load();
		ThreadPool.init();
		STATUS = ServerStatusManager.start(ServerType.GAME);
		gameServer = new GameServer();
	}
	
	public static void printStatus()
	{
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		
		Runtime runtime = Runtime.getRuntime();
		
		long uptime = runtimeBean.getUptime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maxMemory = runtime.maxMemory();
		int threadCount = threadBean.getThreadCount();
		DecimalFormat df = new DecimalFormat("#.##");
		
		System.out.println("========= [SERVER STATUS] =========");
		System.out.println("Uptime: " + (uptime / 1000) + "s");
		System.out.println("CPU cores: " + osBean.getAvailableProcessors());
		System.out.println("System Load: " + df.format(osBean.getSystemLoadAverage()));
		System.out.println("Heap Memory: " + (usedMemory / 1024 / 1024) + " MB / " + (maxMemory / 1024 / 1024) + " MB");
		System.out.println("Threads: " + threadCount);
		System.out.println("Players Online: " + L2World.getInstance().getPlayers().size());
		System.out.println("===================================");
	}
}