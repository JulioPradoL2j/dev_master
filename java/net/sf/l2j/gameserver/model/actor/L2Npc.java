package net.sf.l2j.gameserver.model.actor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.event.bossevent.KTBConfig;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.bossevent.KTBManager;
import net.sf.l2j.event.bossevent.VoicedEventKTB;
import net.sf.l2j.event.championInvade.ChampionInvade;
import net.sf.l2j.event.championInvade.InitialChampionInvade;
import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMConfig;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.deathmath.DMManager;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.fortress.FOSManager;
import net.sf.l2j.event.lastman.CheckNextEvent;
import net.sf.l2j.event.partyfarm.InitialPartyFarm;
import net.sf.l2j.event.partyfarm.PartyFarm;
import net.sf.l2j.event.rewardsoloevent.InitialSoloEvent;
import net.sf.l2j.event.rewardsoloevent.RewardSoloEvent;
import net.sf.l2j.event.soloboss.InitialSoloBossEvent;
import net.sf.l2j.event.soloboss.SoloBoss;
import net.sf.l2j.event.spoil.InitialSpoilEvent;
import net.sf.l2j.event.spoil.SpoilEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.event.tournament.ArenaEvent;
import net.sf.l2j.event.tournament.ArenaTask;
import net.sf.l2j.event.tvt.TvTConfig;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.events.eventpvp.PvPEvent;
import net.sf.l2j.events.eventpvp.PvPEventNext;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.HelperBuffTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.datatables.xml.FakePcsTable;
import net.sf.l2j.gameserver.datatables.xml.IconTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.games.Lottery;
import net.sf.l2j.gameserver.model.FakePc;
import net.sf.l2j.gameserver.model.HelperBuff;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.instance.L2ChestInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2FishermanInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2TeleporterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2TournamentInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2WarehouseInstance;
import net.sf.l2j.gameserver.model.actor.knownlist.NpcKnownList;
import net.sf.l2j.gameserver.model.actor.stat.NpcStat;
import net.sf.l2j.gameserver.model.actor.status.NpcStatus;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate.AIType;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate.Race;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.zone.type.L2TownZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scriptings.EventType;
import net.sf.l2j.gameserver.scriptings.Quest;
import net.sf.l2j.gameserver.scriptings.QuestState;
import net.sf.l2j.gameserver.scriptings.ScriptManager;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.gameserver.util.Broadcast;

/**
 * This class represents a Non-Player-Character in the world. They are split in :
 * <ul>
 * <li>L2ControlTowerInstance</li>
 * <li>L2FlameTowerInstance</li>
 * <li>L2SiegeFlagInstance</li>
 * <li>L2Attackable</li>
 * <li>L2NpcInstance</li>
 * </ul>
 */
public class L2Npc extends Creature
{
	public static final int INTERACTION_DISTANCE = 150;
	private static final int SOCIAL_INTERVAL = 12000;
	
	private L2Spawn _spawn;
	
	volatile boolean _isDecayed = false;
	private FakePc _fakePc = null;
	private int _castleIndex = -2;
	private boolean _isInTown = false;
	
	private int _spoilerId = 0;
	public boolean _isKTBEvent = false;
	private long _lastSocialBroadcast = 0;
	
	private int _currentLHandId;
	private int _currentRHandId;
	private int _currentEnchant;
	
	private double _currentCollisionHeight; // used for npc grow effect skills
	private double _currentCollisionRadius; // used for npc grow effect skills
	protected static final int PAGE_LIMIT = Config.RAID_BOSS_DROP_PAGE_LIMIT;
	protected static final Map<Integer, Integer> LAST_PAGE = new ConcurrentHashMap<>();
	protected static final String[][] MESSAGE =
	{
		{
			"<font color=\"LEVEL\">%player%</font>, are you not afraid?",
			"Be careful <font color=\"LEVEL\">%player%</font>!"
		},
		{
			"Here is the drop list of <font color=\"LEVEL\">%boss%</font>!",
			"Seems that <font color=\"LEVEL\">%boss%</font> has good drops."
		},
	};
	private int _currentSsCount = 0;
	private int _currentSpsCount = 0;
	private int _shotsMask = 0;
	
	private int _scriptValue = 0;
	
	/**
	 * Send a packet SocialAction to all L2PcInstance in the _KnownPlayers of the L2Npc and create a new RandomAnimation Task.
	 * @param id the animation id.
	 */
	public void onRandomAnimation(int id)
	{
		final long now = System.currentTimeMillis();
		if (now - _lastSocialBroadcast > SOCIAL_INTERVAL)
		{
			_lastSocialBroadcast = now;
			broadcastPacket(new SocialAction(this, id));
		}
	}
	
	/**
	 * Create a RandomAnimation Task that will be launched after the calculated delay.
	 */
	public void startRandomAnimationTimer()
	{
		if (!hasRandomAnimation())
			return;
		
		final int timer = (isMob()) ? Rnd.get(Config.MIN_MONSTER_ANIMATION, Config.MAX_MONSTER_ANIMATION) : Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION);
		RandomAnimationTaskManager.getInstance().add(this, timer);
	}
	
	/**
	 * @return true if the server allows Random Animation, false if not or the AItype is a corpse.
	 */
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_NPC_ANIMATION > 0 && !getTemplate().getAiType().equals(AIType.CORPSE));
	}
	
	/**
	 * Constructor of L2Npc (use L2Character constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to set the _template of the L2Character (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2Character</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li><BR>
	 * <BR>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2NpcTemplate to apply to the NPC
	 */
	public L2Npc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		for (L2Skill skill : template.getSkills().values())
			addStatFuncs(skill.getStatFuncs(this));
		
		initCharStatusUpdateValues();
		
		// initialize the "current" equipment
		_currentLHandId = template.getLeftHand();
		_currentRHandId = template.getRightHand();
		_currentEnchant = template.getEnchantEffect();
		
		// initialize the "current" collisions
		_currentCollisionHeight = template.getCollisionHeight();
		_currentCollisionRadius = template.getCollisionRadius();
		
		// initialize Npc Pc
		_fakePc = FakePcsTable.getInstance().getFakePc(template.getNpcId());
		
		// Set the name of the L2Character
		setName(template.getName());
	}
	
	public FakePc getFakePc()
	{
		return _fakePc;
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new NpcKnownList(this));
	}
	
	@Override
	public NpcKnownList getKnownList()
	{
		return (NpcKnownList) super.getKnownList();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new NpcStat(this));
	}
	
	@Override
	public NpcStat getStat()
	{
		return (NpcStat) super.getStat();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new NpcStatus(this));
	}
	
	@Override
	public NpcStatus getStatus()
	{
		return (NpcStatus) super.getStatus();
	}
	
	/** Return the L2NpcTemplate of the L2Npc. */
	@Override
	public final NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	/**
	 * @return the generic Identifier of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getNpcId()
	{
		return getTemplate().getNpcId();
	}
	
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	/**
	 * Return the Level of this L2Npc contained in the L2NpcTemplate.
	 */
	@Override
	public final int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * @return True if the L2Npc is agressive (ex : L2MonsterInstance in function of aggroRange).
	 */
	public boolean isAggressive()
	{
		return false;
	}
	
	/**
	 * Return True if this L2Npc is undead in function of the L2NpcTemplate.
	 */
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == Race.UNDEAD;
	}
	
	/**
	 * Send a packet NpcInfo with state of abnormal effect to all L2PcInstance in the _KnownPlayers of the L2Npc.
	 */
	@Override
	public void updateAbnormalEffect()
	{
		// Send NpcInfo with state of abnormal effect to all L2PcInstance in the _KnownPlayers of the L2Npc
		for (Player player : getKnownList().getKnownType(Player.class))
		{
			if (getMoveSpeed() == 0)
				player.sendPacket(new ServerObjectInfo(this, player));
			else
				player.sendPacket(new NpcInfo(this, player));
		}
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}
	
	/**
	 * @return the Identifier of the item in the left hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getLeftHandItem()
	{
		return _currentLHandId;
	}
	
	/**
	 * @return the Identifier of the item in the right hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getRightHandItem()
	{
		return _currentRHandId;
	}
	
	public int getEnchantEffect()
	{
		return _currentEnchant;
	}
	
	public final int getSpoilerId()
	{
		return _spoilerId;
	}
	
	public final void setSpoilerId(int value)
	{
		_spoilerId = value;
	}
	
	/**
	 * Overidden in L2CastleWarehouse, L2ClanHallManager and L2Warehouse.
	 * @return true if this L2Npc instance can be warehouse manager.
	 */
	public boolean isWarehouse()
	{
		return false;
	}
	
	@Override
	public void onAction(Player player)
	{
		if (!player.isGM() && KTBEvent.isStarted() && KTBEvent.onAction(player, getObjectId()) && getNpcId() == KTBConfig.LIST_KTB_EVENT_BOSS_ID.get(Rnd.get(KTBConfig.LIST_KTB_EVENT_BOSS_ID.size())))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Set the target of the L2PcInstance player
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			// Check if the player is attackable (without a forced attack) and isn't dead
			if (isAutoAttackable(player))
			{
				player.getAI().setIntention(CtrlIntention.ATTACK, this);
			}
			else
			{
				// Calculate the distance between the L2PcInstance and the L2Npc
				if (!canInteract(player))
				{
					// Notify the L2PcInstance AI with INTERACT
					player.getAI().setIntention(CtrlIntention.INTERACT, this);
				}
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
					
					// Send ActionFailed to the player in order to avoid he stucks
					player.sendPacket(ActionFailed.STATIC_PACKET);
					
					if (hasRandomAnimation())
						onRandomAnimation(Rnd.get(8));
					 
					
					if (!KTBEvent.isPlayerParticipant(player.getObjectId()) && KTBEvent.isStarted() && getNpcId() == KTBConfig.KTB_EVENT_PARTICIPATION_NPC_ID)
					{
						VoicedEventKTB.showKTBStatuPage(player);
						return;
					}
					
					List<Quest> qlsa = getTemplate().getEventQuests(EventType.QUEST_START);
					if (qlsa != null && !qlsa.isEmpty())
						player.setLastQuestNpcObject(getObjectId());
					
					List<Quest> qlst = getTemplate().getEventQuests(EventType.ON_FIRST_TALK);
					if (qlst != null && qlst.size() == 1)
						qlst.get(0).notifyFirstTalk(this, player);
					else
						showChatWindow(player);
				}
			}
		}
	}
	
	@Override
	public void onActionShift(Player player)
	{
		if (!player.isGM() && KTBEvent.isStarted() && KTBEvent.onAction(player, getObjectId()) && getNpcId() == KTBConfig.LIST_KTB_EVENT_BOSS_ID.get(Rnd.get(KTBConfig.LIST_KTB_EVENT_BOSS_ID.size())))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Check if the L2PcInstance is a GM
		if (player.isGM())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/admin/npcinfo.htm");
			html.replace("%class%", getClass().getSimpleName());
			html.replace("%id%", getTemplate().getNpcId());
			html.replace("%lvl%", getTemplate().getLevel());
			html.replace("%name%", getTemplate().getName());
			html.replace("%race%", getTemplate().getRace().toString());
			html.replace("%tmplid%", getTemplate().getIdTemplate());
			html.replace("%aggro%", getTemplate().getAggroRange());
			html.replace("%corpse%", StringUtil.getTimeStamp(getTemplate().getCorpseTime()));
			html.replace("%enchant%", getTemplate().getEnchantEffect());
			html.replace("%hp%", (int) getCurrentHp());
			html.replace("%hpmax%", getMaxHp());
			html.replace("%mp%", (int) getCurrentMp());
			html.replace("%mpmax%", getMaxMp());
			html.replace("%patk%", getPAtk(null));
			html.replace("%matk%", getMAtk(null, null));
			html.replace("%pdef%", getPDef(null));
			html.replace("%mdef%", getMDef(null, null));
			html.replace("%accu%", getAccuracy());
			html.replace("%evas%", getEvasionRate(null));
			html.replace("%crit%", getCriticalHit(null, null));
			html.replace("%rspd%", getMoveSpeed());
			html.replace("%aspd%", getPAtkSpd());
			html.replace("%cspd%", getMAtkSpd());
			html.replace("%str%", getSTR());
			html.replace("%dex%", getDEX());
			html.replace("%con%", getCON());
			html.replace("%int%", getINT());
			html.replace("%wit%", getWIT());
			html.replace("%men%", getMEN());
			html.replace("%loc%", getX() + " " + getY() + " " + getZ());
			html.replace("%dist%", (int) Math.sqrt(player.getDistanceSq(this)));
			html.replace("%ele_fire%", getDefenseElementValue((byte) 2));
			html.replace("%ele_water%", getDefenseElementValue((byte) 3));
			html.replace("%ele_wind%", getDefenseElementValue((byte) 1));
			html.replace("%ele_earth%", getDefenseElementValue((byte) 4));
			html.replace("%ele_holy%", getDefenseElementValue((byte) 5));
			html.replace("%ele_dark%", getDefenseElementValue((byte) 6));
			
			if (getSpawn() != null)
			{
				html.replace("%spawn%", getSpawn().getLoc().toString());
				html.replace("%loc2d%", (int) Math.sqrt(getPlanDistanceSq(getSpawn().getLocX(), getSpawn().getLocY())));
				html.replace("%loc3d%", (int) Math.sqrt(getDistanceSq(getSpawn().getLocX(), getSpawn().getLocY(), getSpawn().getLocZ())));
				html.replace("%resp%", StringUtil.getTimeStamp(getSpawn().getRespawnDelay()));
				html.replace("%rand_resp%", StringUtil.getTimeStamp(getSpawn().getRespawnRandom()));
			}
			else
			{
				html.replace("%spawn%", "<font color=FF0000>null</font>");
				html.replace("%loc2d%", "<font color=FF0000>--</font>");
				html.replace("%loc3d%", "<font color=FF0000>--</font>");
				html.replace("%resp%", "<font color=FF0000>--</font>");
				html.replace("%rand_resp%", "<font color=FF0000>--</font>");
			}
			
			if (hasAI())
			{
				html.replace("%ai_intention%", "<font color=\"LEVEL\">Intention</font><table width=\"100%\"><tr><td><font color=\"LEVEL\">Intention:</font></td><td>" + getAI().getIntention().name() + "</td></tr>");
				html.replace("%ai%", "<tr><td><font color=\"LEVEL\">AI:</font></td><td>" + getAI().getClass().getSimpleName() + "</td></tr></table><br>");
			}
			else
			{
				html.replace("%ai_intention%", "");
				html.replace("%ai%", "");
			}
			
			html.replace("%ai_type%", getTemplate().getAiType().name());
			html.replace("%ai_clan%", (getTemplate().getClans() != null) ? "<tr><td width=100><font color=\"LEVEL\">Clan:</font></td><td align=right width=170>" + Arrays.toString(getTemplate().getClans()) + " " + getTemplate().getClanRange() + "</td></tr>" + ((getTemplate().getIgnoredIds() != null) ? "<tr><td width=100><font color=\"LEVEL\">Ignored ids:</font></td><td align=right width=170>" + Arrays.toString(getTemplate().getIgnoredIds()) + "</td></tr>" : "") : "");
			html.replace("%ai_move%", String.valueOf(getTemplate().canMove()));
			html.replace("%ai_seed%", String.valueOf(getTemplate().isSeedable()));
			html.replace("%ai_ssinfo%", _currentSsCount + "[" + getTemplate().getSsCount() + "] - " + getTemplate().getSsRate() + "%");
			html.replace("%ai_spsinfo%", _currentSpsCount + "[" + getTemplate().getSpsCount() + "] - " + getTemplate().getSpsRate() + "%");
			html.replace("%butt%", ((this instanceof L2MerchantInstance) ? "<button value=\"Shop\" action=\"bypass -h admin_show_shop " + getNpcId() + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">" : ""));
			player.sendPacket(html);
		}
		else if (Config.ENABLE_SHIFT_CLICK)
		{
			// if (this instanceof L2MonsterInstance && !isDead() || this instanceof L2RaidBossInstance && !isDead() || this instanceof L2GrandBossInstance && !isDead() || this instanceof L2ChestInstance && !isDead())
			// {
			// player.sendPacket(ActionFailed.STATIC_PACKET);
			// return;
			// }
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			// The player.getLevel() - getLevel() permit to display the correct color in the select window
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			
			if (this instanceof L2MonsterInstance || this instanceof L2RaidBossInstance || this instanceof L2GrandBossInstance || this instanceof L2ChestInstance)
				ShiffNpcDropList(player, getTemplate().getNpcId(), 1);
		}
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static void ShiffNpcDropList(Player player, int npcId, int page)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
		if (template == null)
			return;
		
		if (template.getDropData().isEmpty())
		{
			player.sendMessage("This target have not drop info.");
			return;
		}
		
		final List<DropCategory> list = new ArrayList<>();
		template.getDropData().forEach(c -> list.add(c));
		Collections.reverse(list);
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<center>");
		sb.append("<body>");
		// sb.append("<br>");
		sb.append("<table width=\"256\">");
		sb.append("</table>");
		// sb.append("<br>");
		sb.append("<table width=\"224\" bgcolor=\"000000\">");
		// sb.append("<tr><td width=\"224\" align=\"center\">Raid Boss Drops</td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		
		for (DropCategory cat : list)
		{
			if (shown == PAGE_LIMIT)
			{
				hasMore = true;
				break;
			}
			
			for (DropData drop : cat.getAllDrops())
			{
				double mind = 0;
				double maxd = 0;
				double chance = (double) drop.getChance() / 10000;
				
				mind = Config.RATE_DROP_ITEMS * drop.getMinDrop();
				maxd = Config.RATE_DROP_ITEMS * drop.getMaxDrop();
				
				String smind = null, smaxd = null, drops = null;
				if (mind > 999999)
				{
					DecimalFormat df = new DecimalFormat("###.#");
					smind = df.format(((mind)) / 1000000) + "KK";
					smaxd = df.format(((maxd)) / 1000000) + "KK";
				}
				else if (mind > 999)
				{
					smind = (mind / 1000) + "K";
					smaxd = (maxd / 1000) + "K";
				}
				else
				{
					smind = Float.toString((float) mind);
					smaxd = Float.toString((float) maxd);
				}
				
				if (chance <= 0.001)
				{
					DecimalFormat df = new DecimalFormat("#.####");
					drops = df.format(chance);
				}
				else if (chance <= 0.01)
				{
					DecimalFormat df = new DecimalFormat("#.###");
					drops = df.format(chance);
				}
				else
				{
					DecimalFormat df = new DecimalFormat("##.##");
					drops = df.format(chance);
				}
				Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
				String name = item.getName();
				
				if (name.length() >= 22)
					name = name.substring(0, 21) + "...";
				if (myPage != page)
				{
					i++;
					if (i == PAGE_LIMIT)
					{
						myPage++;
						i = 0;
					}
					continue;
				}
				
				if (shown == PAGE_LIMIT)
				{
					hasMore = true;
					break;
				}
				
				sb.append("<table width=295 bgcolor=000000>");
				sb.append("<tr>");
				sb.append("<td align=left width=32><button width=32 height=32 back=" + IconTable.getIcon(item.getItemId()) + " fore=" + IconTable.getIcon(item.getItemId()) + "></td>");
				sb.append("<td align=left width=263>");
				sb.append("<table>");
				sb.append("<tr><td align=left width=263>" + (cat.isSweep() ? "<font color=FF0099>Sweep Chance</font>" : "<font color=00FF00>Drop Chance</font>") + " : (" + drops + "%)</td></tr>");
				
				if (Config.NOT_SHOW_DROP_INFO.contains(Integer.valueOf(item.getItemId())))
					sb.append("<tr><td align=left width=263><font color=F01E23>" + name + "</font></td></tr>");
				else
					sb.append("<tr><td align=left width=263><font color=F9FF00>" + name + "</font> - Min: <font color=00ECFF>" + smind + "</font> Max: <font color=FF0C0C>" + smaxd + "</font></td></tr>");
				
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<img src=l2ui.squaregray width=294 height=1>");
				shown++;
				
			}
		}
		
		// Build page footer.
		sb.append("<br><img src=\"L2UI.SquareGray\" width=277 height=1><table width=\"100%\" bgcolor=000000><tr>");
		
		if (page > 1)
			StringUtil.append(sb, "<td align=left width=70><a action=\"bypass droplist " + npcId + " ", (page - 1), "\">Previous</a></td>");
		else
			StringUtil.append(sb, "<td align=left width=70>Previous</td>");
		
		StringUtil.append(sb, "<td align=center width=100>Page ", page, "</td>");
		
		if (page < shown)
			StringUtil.append(sb, "<td align=right width=70>" + (hasMore ? "<a action=\"bypass droplist " + npcId + " " + (page + 1) + "\">Next</a>" : "") + "</td>");
		else
			StringUtil.append(sb, "<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
		// sb.append("<br>");
		sb.append("<center>");
		sb.append("<table width=\"160\" cellspacing=\"2\">");
		sb.append("<tr>");
		sb.append("<tr><td><center><a action=\"bypass -h voiced_menu\">Return</a></center></td></tr>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</center>");
		sb.append("</body>");
		sb.append("</html>");
		
		final NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setHtml(sb.toString());
		player.sendPacket(htm);
	}
	
	/**
	 * @return the L2Castle this L2Npc belongs to.
	 */
	public final Castle getCastle()
	{
		// Get castle this NPC belongs to (excluding L2Attackable)
		if (_castleIndex < 0)
		{
			L2TownZone town = MapRegionTable.getTown(getX(), getY(), getZ());
			
			if (town != null)
				_castleIndex = CastleManager.getInstance().getCastleIndex(town.getTaxById());
			
			if (_castleIndex < 0)
				_castleIndex = CastleManager.getInstance().findNearestCastleIndex(this);
			else
				_isInTown = true; // Npc was spawned in town
		}
		
		if (_castleIndex < 0)
			return null;
		
		return CastleManager.getInstance().getCastles().get(_castleIndex);
	}
	
	public final boolean getIsInTown()
	{
		if (_castleIndex < 0)
			getCastle();
		
		return _isInTown;
	}
	
	/**
	 * Open a quest or chat window on client with the text of the L2Npc in function of the command.
	 * @param player The player to test
	 * @param command The command string received from client
	 */
	public void onBypassFeedback(Player player, String command)
	{
		 
		
		if (command.equalsIgnoreCase("TerritoryStatus"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (getCastle().getOwnerId() > 0)
			{
				html.setFile("data/html/territorystatus.htm");
				L2Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
			}
			else
				html.setFile("data/html/territorynoclan.htm");
			
			html.replace("%castlename%", getCastle().getName());
			html.replace("%taxpercent%", getCastle().getTaxPercent());
			html.replace("%objectId%", getObjectId());
			
			if (getCastle().getCastleId() > 6)
				html.replace("%territory%", "The Kingdom of Elmore");
			else
				html.replace("%territory%", "The Kingdom of Aden");
			
			player.sendPacket(html);
		}
		else if (command.startsWith("Quest"))
		{
			String quest = "";
			try
			{
				quest = command.substring(5).trim();
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			
			if (quest.isEmpty())
				showQuestWindowGeneral(player, this);
			else
				showQuestWindowSingle(player, this, ScriptManager.getInstance().getQuest(quest));
		}
		else if (command.startsWith("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			showChatWindow(player, val);
		}
		else if (command.startsWith("Link"))
		{
			String path = command.substring(5).trim();
			if (path.indexOf("..") != -1)
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/" + path);
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (command.startsWith("Loto"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			if (val == 0)
			{
				// new loto ticket
				for (int i = 0; i < 5; i++)
					player.setLoto(i, 0);
			}
			showLotoWindow(player, val);
		}
		else if (command.startsWith("CPRecovery"))
		{
			makeCPRecovery(player);
		}
		else if (command.startsWith("SupportMagic"))
		{
			makeSupportMagic(player);
		}
		else if (command.startsWith("multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(9).trim(), player, this, false);
		}
		else if (command.startsWith("exc_multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(13).trim(), player, this, true);
		}
		else if (command.startsWith("Augment"))
		{
			int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
			switch (cmdChoice)
			{
				case 1:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
					player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
				case 2:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
					player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
			}
		}
		else if (command.startsWith("EnterRift"))
		{
			try
			{
				Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
				DimensionalRiftManager.getInstance().start(player, b1, this);
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("ChangeRiftRoom"))
		{
			if (player.isInParty() && player.getParty().isInDimensionalRift())
				player.getParty().getDimensionalRift().manualTeleport(player, this);
		}
		else if (command.startsWith("ExitRift"))
		{
			if (player.isInParty() && player.getParty().isInDimensionalRift())
				player.getParty().getDimensionalRift().manualExitRift(player, this);
		}
	}
	
	/**
	 * Collect quests in progress and possible quests and show proper quest window.
	 * @param player The L2PcInstance that talk with the L2Npc.
	 * @param npc The L2Npc instance.
	 */
	public static void showQuestWindowGeneral(Player player, L2Npc npc)
	{
		List<Quest> quests = new ArrayList<>();
		
		List<Quest> awaits = npc.getTemplate().getEventQuests(EventType.ON_TALK);
		if (awaits != null)
		{
			for (Quest quest : awaits)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				QuestState qs = player.getQuestState(quest.getName());
				if (qs == null || qs.isCreated())
					continue;
				
				quests.add(quest);
			}
		}
		
		List<Quest> starts = npc.getTemplate().getEventQuests(EventType.QUEST_START);
		if (starts != null)
		{
			for (Quest quest : starts)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				quests.add(quest);
			}
		}
		
		if (quests.isEmpty())
			showQuestWindowSingle(player, npc, null);
		else if (quests.size() == 1)
			showQuestWindowSingle(player, npc, quests.get(0));
		else
			showQuestWindowChoose(player, npc, quests);
	}
	
	/**
	 * Open a quest window on client with the text of the L2Npc. Create the QuestState if not existing.
	 * @param player : the L2PcInstance that talk with the L2Npc.
	 * @param npc : the L2Npc instance.
	 * @param quest : the quest HTMLs to show.
	 */
	public static void showQuestWindowSingle(Player player, L2Npc npc, Quest quest)
	{
		if (quest == null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml(Quest.getNoQuestMsg());
			player.sendPacket(html);
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (quest.isRealQuest() && (player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 <= player.getInventory().getSize()))
		{
			player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
			return;
		}
		
		QuestState qs = player.getQuestState(quest.getName());
		if (qs == null)
		{
			if (quest.isRealQuest() && player.getAllQuests(false).size() >= 25)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setHtml(Quest.getTooMuchQuestsMsg());
				player.sendPacket(html);
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			List<Quest> qlst = npc.getTemplate().getEventQuests(EventType.QUEST_START);
			if (qlst != null && qlst.contains(quest))
				qs = quest.newQuestState(player);
		}
		
		if (qs != null)
			quest.notifyTalk(npc, qs.getPlayer());
	}
	
	/**
	 * Shows the list of available quest of the L2Npc.
	 * @param player The L2PcInstance that talk with the L2Npc.
	 * @param npc The L2Npc instance.
	 * @param quests The list containing quests of the L2Npc.
	 */
	public static void showQuestWindowChoose(Player player, L2Npc npc, List<Quest> quests)
	{
		final StringBuilder sb = new StringBuilder("<html><body>");
		
		for (Quest q : quests)
		{
			StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Quest ", q.getName(), "\">[", q.getDescr());
			
			final QuestState qs = player.getQuestState(q.getName());
			if (qs != null && qs.isStarted())
				sb.append(" (In Progress)]</a><br>");
			else if (qs != null && qs.isCompleted())
				sb.append(" (Done)]</a><br>");
			else
				sb.append("]</a><br>");
		}
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", npc.getObjectId());
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instancies).<BR>
	 * <BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	/**
	 * Return the weapon item equipped in the right hand of the L2Npc or null.
	 */
	@Override
	public Weapon getActiveWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int weaponId = getTemplate().getRightHand();
		if (weaponId < 1)
			return null;
		
		// Get the weapon item equipped in the right hand of the L2Npc
		Item item = ItemTable.getInstance().getTemplate(weaponId);
		if (!(item instanceof Weapon))
			return null;
		
		return (Weapon) item;
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instancies).
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	/**
	 * Return the item equipped in the left hand of the L2Npc or null.
	 */
	@Override
	public Item getSecondaryWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int itemId = getTemplate().getLeftHand();
		if (itemId < 1)
			return null;
		
		// Return the item equipped in the left hand of the L2Npc
		return ItemTable.getInstance().getTemplate(itemId);
	}
	
	/**
	 * <B><U> Format of the pathfile </U> :</B><BR>
	 * <BR>
	 * <li>if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li>
	 * <li>if the file exists on the server (page number > 0) : <B>data/html/default/12006-1.htm</B> (npcId-page number)</li>
	 * <li>if the file doesn't exist on the server : <B>data/html/npcdefault.htm</B> (message : "I have nothing to say to you")</li><BR>
	 * <BR>
	 * <B><U> Overriden in </U> :</B><BR>
	 * <BR>
	 * <li>L2GuardInstance : Set the pathfile to data/html/guard/12006-1.htm (npcId-page number)</li><BR>
	 * <BR>
	 * @param npcId The Identifier of the L2Npc whose text must be display
	 * @param val The number of the page to display
	 * @return the pathfile of the selected HTML file in function of the npcId and of the page number.
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String filename;
		
		if (val == 0)
			filename = "data/html/default/" + npcId + ".htm";
		else
			filename = "data/html/default/" + npcId + "-" + val + ".htm";
		
		if (HtmCache.getInstance().isLoadable(filename))
			return filename;
		
		return "data/html/npcdefault.htm";
	}
	
	/**
	 * Make the NPC speaks to his current knownlist.
	 * @param message The String message to send.
	 */
	public void broadcastNpcSay(String message)
	{
		broadcastPacket(new NpcSay(getObjectId(), Say2.ALL, getNpcId(), message));
	}
	
	/**
	 * Open a Loto window on client with the text of the L2Npc.
	 * @param player The L2PcInstance that talk with the L2Npc
	 * @param val The number of the page of the L2Npc to display
	 */
	// 0 - first buy lottery ticket window
	// 1-20 - buttons
	// 21 - second buy lottery ticket window
	// 22 - selected ticket with 5 numbers
	// 23 - current lottery jackpot
	// 24 - Previous winning numbers/Prize claim
	// >24 - check lottery ticket by item object id
	public void showLotoWindow(Player player, int val)
	{
		int npcId = getTemplate().getNpcId();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		if (val == 0) // 0 - first buy lottery ticket window
		{
			html.setFile(getHtmlPath(npcId, 1));
		}
		else if (val >= 1 && val <= 21) // 1-20 - buttons, 21 - second buy lottery ticket window
		{
			if (!Lottery.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			html.setFile(getHtmlPath(npcId, 5));
			
			int count = 0;
			int found = 0;
			// counting buttons and unsetting button if found
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == val)
				{
					// unsetting button
					player.setLoto(i, 0);
					found = 1;
				}
				else if (player.getLoto(i) > 0)
				{
					count++;
				}
			}
			
			// if not rearched limit 5 and not unseted value
			if (count < 5 && found == 0 && val <= 20)
				for (int i = 0; i < 5; i++)
					if (player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}
				
			// setting pusshed buttons
			count = 0;
			for (int i = 0; i < 5; i++)
				if (player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if (player.getLoto(i) < 10)
						button = "0" + button;
					String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}
			
			if (count == 5)
			{
				String search = "0\">Return";
				String replace = "22\">The winner selected the numbers above.";
				html.replace(search, replace);
			}
		}
		else if (val == 22) // 22 - selected ticket with 5 numbers
		{
			if (!Lottery.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			int price = Config.ALT_LOTTERY_TICKET_PRICE;
			int lotonumber = Lottery.getInstance().getId();
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == 0)
					return;
				
				if (player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			
			if (!player.reduceAdena("Loto", price, this, true))
				return;
			
			Lottery.getInstance().increasePrize(price);
			
			ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4442);
			item.setCount(1);
			item.setCustomType1(lotonumber);
			item.setEnchantLevel(enchant);
			item.setCustomType2(type2);
			
			player.addItem("Loto", item, player, false);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(4442));
			
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 23) // 23 - current lottery jackpot
		{
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 24) // 24 - Previous winning numbers/Prize claim
		{
			final int lotoNumber = Lottery.getInstance().getId();
			
			final StringBuilder sb = new StringBuilder();
			for (ItemInstance item : player.getInventory().getItems())
			{
				if (item == null)
					continue;
				
				if (item.getItemId() == 4442 && item.getCustomType1() < lotoNumber)
				{
					StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Loto ", item.getObjectId(), "\">", item.getCustomType1(), " Event Number ");
					
					int[] numbers = Lottery.decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for (int i = 0; i < 5; i++)
						StringUtil.append(sb, numbers[i], " ");
					
					int[] check = Lottery.checkTicket(item);
					if (check[0] > 0)
					{
						switch (check[0])
						{
							case 1:
								sb.append("- 1st Prize");
								break;
							case 2:
								sb.append("- 2nd Prize");
								break;
							case 3:
								sb.append("- 3th Prize");
								break;
							case 4:
								sb.append("- 4th Prize");
								break;
						}
						StringUtil.append(sb, " ", check[1], "a.");
					}
					sb.append("</a><br>");
				}
			}
			
			if (sb.length() == 0)
				sb.append("There is no winning lottery ticket...<br>");
			
			html.setFile(getHtmlPath(npcId, 4));
			html.replace("%result%", sb.toString());
		}
		else if (val == 25) // 25 - lottery instructions
		{
			html.setFile(getHtmlPath(npcId, 2));
			html.replace("%prize5%", Config.ALT_LOTTERY_5_NUMBER_RATE * 100);
			html.replace("%prize4%", Config.ALT_LOTTERY_4_NUMBER_RATE * 100);
			html.replace("%prize3%", Config.ALT_LOTTERY_3_NUMBER_RATE * 100);
			html.replace("%prize2%", Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
		}
		else if (val > 25) // >25 - check lottery ticket by item object id
		{
			int lotonumber = Lottery.getInstance().getId();
			ItemInstance item = player.getInventory().getItemByObjectId(val);
			if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
				return;
			int[] check = Lottery.checkTicket(item);
			
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(4442));
			
			int adena = check[1];
			if (adena > 0)
				player.addAdena("Loto", adena, this, true);
			player.destroyItem("Loto", item, this, false);
			return;
		}
		html.replace("%objectId%", getObjectId());
		html.replace("%race%", Lottery.getInstance().getId());
		html.replace("%adena%", Lottery.getInstance().getPrize());
		html.replace("%ticket_price%", Config.ALT_LOTTERY_TICKET_PRICE);
		html.replace("%enddate%", DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void makeCPRecovery(Player player)
	{
		if (getNpcId() != 31225 && getNpcId() != 31226)
			return;
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendMessage("Go away, you're not welcome here.");
			return;
		}
		
		// Consume 100 adenas
		if (player.reduceAdena("RestoreCP", 100, player.getCurrentFolkNPC(), true))
		{
			setTarget(player);
			doCast(FrequentSkill.ARENA_CP_RECOVERY.getSkill());
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addPcName(player));
		}
	}
	
	/**
	 * Add Newbie helper buffs to L2Player according to its level.
	 * @param player The L2PcInstance that talk with the L2Npc
	 */
	public void makeSupportMagic(Player player)
	{
		if (player == null)
			return;
		
		// Prevent a cursed weapon wielder of being buffed.
		if (player.isCursedWeaponEquipped())
			return;
		
		int playerLevel = player.getLevel();
		int lowestLevel = 0;
		int higestLevel = 0;
		
		// Select the player.
		setTarget(player);
		
		// Calculate the min and max level between which the player must be to obtain buff.
		if (player.isMageClass())
		{
			lowestLevel = HelperBuffTable.getInstance().getMagicClassLowestLevel();
			higestLevel = HelperBuffTable.getInstance().getMagicClassHighestLevel();
		}
		else
		{
			lowestLevel = HelperBuffTable.getInstance().getPhysicClassLowestLevel();
			higestLevel = HelperBuffTable.getInstance().getPhysicClassHighestLevel();
		}
		
		// If the player is too high level, display a message and return.
		if (playerLevel > higestLevel || !player.isNewbie())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Newbie Guide:<br>Only a <font color=\"LEVEL\">novice character of level " + higestLevel + " or less</font> can receive my support magic.<br>Your novice character is the first one that you created and raised in this world.</body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		
		// If the player is too low level, display a message and return.
		if (playerLevel < lowestLevel)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Come back here when you have reached level " + lowestLevel + ". I will give you support magic then.</body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		
		// Go through the Helper Buff list and cast skills.
		for (HelperBuff buff : HelperBuffTable.getInstance().getHelperBuffTable())
		{
			if (buff.isMagicClassBuff() == player.isMageClass() && playerLevel >= buff.getLowerLevel() && playerLevel <= buff.getUpperLevel())
			{
				L2Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());
				if (skill.getSkillType() == L2SkillType.SUMMON)
					player.doCast(skill);
				else
					doCast(skill);
			}
		}
	}
	
	/**
	 * Returns true if html exists
	 * @param player
	 * @param type
	 * @return boolean
	 */
	protected boolean showPkDenyChatWindow(Player player, String type)
	{
		String content = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");
		if (content != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(content);
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Open a chat window on client with the text of the L2Npc.
	 * @param player The L2PcInstance that talk with the L2Npc
	 */
	public void showChatWindow(Player player)
	{
		showChatWindow(player, 0);
	}
	
	public void showChatWindow(Player player, int val)
	{
		if (player.getKarma() > 0)
		{
			if (!Config.KARMA_PLAYER_CAN_SHOP && this instanceof L2MerchantInstance)
			{
				if (showPkDenyChatWindow(player, "merchant"))
					return;
			}
			else if (!Config.KARMA_PLAYER_CAN_USE_GK && this instanceof L2TeleporterInstance)
			{
				if (showPkDenyChatWindow(player, "teleporter"))
					return;
			}
			else if (!Config.KARMA_PLAYER_CAN_USE_WH && this instanceof L2WarehouseInstance)
			{
				if (showPkDenyChatWindow(player, "warehouse"))
					return;
			}
			else if (!Config.KARMA_PLAYER_CAN_SHOP && this instanceof L2FishermanInstance)
			{
				if (showPkDenyChatWindow(player, "fisherman"))
					return;
			}
		}
		if (!ArenaTask.is_started() && getNpcId() == ArenaConfig.ARENA_NPC)
		{
			L2TournamentInstance.NoStartEvent(player);
			return;
		}
		
		String filename;
		
		if (getNpcId() >= 31865 && getNpcId() <= 31918)
			filename = SevenSigns.SEVEN_SIGNS_HTML_PATH + "rift/GuardianOfBorder.htm";
		else
			filename = getHtmlPath(getNpcId(), val);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%name%", getName());
		html.replace("%countbuff%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%objectId%", getObjectId());
		
		html.replace("%lmTime%", CheckNextEvent.getInstance().getNextLMTime());
		if (Config.PVP_EVENT_ENABLED)
		{
			if (PvPEvent.getInstance().isActive())
				html.replace("%pvp%", "In Progress");
			else
				html.replace("%pvp%", PvPEventNext.getInstance().getNextTime().toString());
		}
		if (Config.SOLO_FARM_BY_TIME_OF_DAY)
		{
			if (RewardSoloEvent.is_started())
				html.replace("%solofarm%", "In Progress");
			else
				html.replace("%solofarm%", InitialSoloEvent.getInstance().getRestartNextTime().toString());
		}
		if (Config.CHAMPION_FARM_BY_TIME_OF_DAY)
		{
			if (ChampionInvade.is_started())
				html.replace("%champion%", "In Progress");
			else
				html.replace("%champion%", InitialChampionInvade.getInstance().getRestartNextTime().toString());
		}
		// Tournament na html EventsTime.
		if (ArenaConfig.TOURNAMENT_EVENT_TIME)
		{
			if (ArenaTask.is_started())
				html.replace("%arena%", "In Progress");
			else
				html.replace("%arena%", ArenaEvent.getInstance().getNextTime().toString());
		}
		if (TvTConfig.TVT_EVENT_ENABLED)
		{
			if (TvTEvent.isStarted())
			{
				html.replace("%tvt%", "In Progress");
			}
			else
			{
				html.replace("%tvt%", CheckNextEvent.getInstance().getNextTvTTime());
			}
		}
		
		if (CTFConfig.CTF_EVENT_ENABLED)
		{
			if (CTFEvent.isStarted())
			{
				html.replace("%ctf%", "In Progress");
			}
			else
			{
				html.replace("%ctf%", CheckNextEvent.getInstance().getNextCTFTime());
			}
		}
		// Party farm EventsTime.
		if (Config.START_PARTY)
		{
			if (PartyFarm.is_started())
				html.replace("%partyfarm%", "In Progress");
			else
				html.replace("%partyfarm%", InitialPartyFarm.getInstance().getRestartNextTime().toString());
		}
		if (Config.START_SPOIL)
		{
			if (SpoilEvent.is_started())
				html.replace("%spoilevent%", "In Progress");
			else
				html.replace("%spoilevent%", InitialSpoilEvent.getInstance().getRestartNextTime().toString());
		}
		if (KTBConfig.KTB_EVENT_ENABLED)
		{
			if (KTBEvent.isStarted())
				html.replace("%ktbTime%", "In Progress");
			else
				html.replace("%ktbTime%", KTBManager.getInstance().getNextTime());
		}
		if (Config.SOLO_BOSS_EVENT)
		{
			if (SoloBoss.is_started())
				html.replace("%bossSolo%", "In Progress");
			else
				html.replace("%bossSolo%", InitialSoloBossEvent.getInstance().getRestartNextTime().toString());
		}
		if (FOSConfig.FOS_EVENT_ENABLED)
		{
			if (FOSEvent.isStarted())
				html.replace("%FOSTime%", "In Progress");
			else
				html.replace("%FOSTime%", FOSManager.getInstance().getNextTime());
		}
		if (DMConfig.DM_EVENT_ENABLED)
		{
			if (DMEvent.isStarted())
				html.replace("%DMTime%", "In Progress");
			else
				html.replace("%DMTime%", DMManager.getInstance().getNextTime());
		}
		// CTF na html EventsTime.
		
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Open a chat window on client with the text specified by the given file name and path,<BR>
	 * relative to the datapack root. <BR>
	 * <BR>
	 * Added by Tempy
	 * @param player The L2PcInstance that talk with the L2Npc
	 * @param filename The filename that contains the text to send
	 */
	public void showChatWindow(Player player, String filename)
	{
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * @return the Exp Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_XP).
	 */
	public int getExpReward()
	{
		return (int) (getTemplate().getRewardExp() * Config.RATE_XP);
	}
	
	/**
	 * @return the SP Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_SP).
	 */
	public int getSpReward()
	{
		return (int) (getTemplate().getRewardSp() * Config.RATE_SP);
	}
	
	/**
	 * Kill the L2Npc (the corpse disappeared after 7 seconds).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Create a DecayTask to remove the corpse of the L2Npc after 7 seconds</li>
	 * <li>Set target to null and cancel Attack or Cast</li>
	 * <li>Stop movement</li>
	 * <li>Stop HP/MP/CP Regeneration task</li>
	 * <li>Stop all active skills effects in progress on the L2Character</li>
	 * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform</li>
	 * <li>Notify L2Character AI</li><BR>
	 * <BR>
	 * <B><U> Overriden in </U> :</B><BR>
	 * <BR>
	 * <li>L2Attackable</li><BR>
	 * <BR>
	 * @param killer The L2Character who killed it
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
			
		// normally this wouldn't really be needed, but for those few exceptions,
		// we do need to reset the weapons back to the initial templated weapon.
		_currentLHandId = getTemplate().getLeftHand();
		_currentRHandId = getTemplate().getRightHand();
		_currentEnchant = getTemplate().getEnchantEffect();
		_currentCollisionHeight = getTemplate().getCollisionHeight();
		_currentCollisionRadius = getTemplate().getCollisionRadius();
		if (killer instanceof Player)
		{
			
			if (_isKTBEvent && ((Player) killer).isInKTBEvent())
				KTBManager.getInstance().raidKilled();
		}
		DecayTaskManager.getInstance().add(this, getTemplate().getCorpseTime());
		return true;
	}
	
	/**
	 * Set the spawn of the L2Npc.<BR>
	 * <BR>
	 * @param spawn The L2Spawn that manage the L2Npc
	 */
	public void setSpawn(L2Spawn spawn)
	{
		_spawn = spawn;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// initialize ss/sps counts.
		_currentSsCount = getTemplate().getSsCount();
		_currentSpsCount = getTemplate().getSpsCount();
		
		List<Quest> quests = getTemplate().getEventQuests(EventType.ON_SPAWN);
		if (quests != null)
			for (Quest quest : quests)
				quest.notifySpawn(this);
	}
	
	/**
	 * Remove the L2Npc from the world and update its spawn object (for a complete removal use the deleteMe method).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Remove the L2Npc from the world when the decay task is launched</li>
	 * <li>Decrease its spawn counter</li>
	 * <li>Manage Siege task (killFlag, killCT)</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
	 * <BR>
	 */
	@Override
	public void onDecay()
	{
		if (isDecayed())
			return;
		
		setDecayed(true);
		
		List<Quest> quests = getTemplate().getEventQuests(EventType.ON_DECAY);
		if (quests != null)
			for (Quest quest : quests)
				quest.notifyDecay(this);
			
		// Remove the L2Npc from the world when the decay task is launched.
		super.onDecay();
		
		// Respawn it, if possible.
		if (_spawn != null)
			_spawn.doRespawn();
	}
	
	/**
	 * Remove PROPERLY the L2Npc from the world.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Remove the L2Npc from the world and update its spawn object</li>
	 * <li>Remove all L2Object from _knownObjects and _knownPlayer of the L2Npc then cancel Attak or Cast and notify AI</li>
	 * <li>Remove L2Object object from _allObjects of L2World</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
	 * <BR>
	 */
	@Override
	public void deleteMe()
	{
		// Decay
		onDecay();
		
		// Remove all L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI
		getKnownList().removeAllKnownObjects();
		
		super.deleteMe();
	}
	
	/**
	 * @return the L2Spawn object that manage this L2Npc.
	 */
	public L2Spawn getSpawn()
	{
		return _spawn;
	}
	
	@Override
	public String toString()
	{
		return getTemplate().getName();
	}
	
	public boolean isDecayed()
	{
		return _isDecayed;
	}
	
	public void setDecayed(boolean decayed)
	{
		_isDecayed = decayed;
	}
	
	public void endDecayTask()
	{
		if (!isDecayed())
		{
			DecayTaskManager.getInstance().cancel(this);
			onDecay();
		}
	}
	
	/**
	 * Used for animation timers, overridden in L2Attackable.
	 * @return true if L2Attackable, false otherwise.
	 */
	public boolean isMob()
	{
		return false;
	}
	
	public void setLHandId(int newWeaponId)
	{
		_currentLHandId = newWeaponId;
	}
	
	public void setRHandId(int newWeaponId)
	{
		_currentRHandId = newWeaponId;
	}
	
	public void setEnchant(int enchant)
	{
		_currentEnchant = enchant;
	}
	
	public void setCollisionHeight(double height)
	{
		_currentCollisionHeight = height;
	}
	
	@Override
	public double getCollisionHeight()
	{
		return _currentCollisionHeight;
	}
	
	public void setCollisionRadius(double radius)
	{
		_currentCollisionRadius = radius;
	}
	
	@Override
	public double getCollisionRadius()
	{
		return _currentCollisionRadius;
	}
	
	public int getScriptValue()
	{
		return _scriptValue;
	}
	
	public void setScriptValue(int val)
	{
		_scriptValue = val;
	}
	
	public boolean isScriptValue(int val)
	{
		return _scriptValue == val;
	}
	
	public L2Npc scheduleDespawn(long delay)
	{
		ThreadPool.schedule(this.new DespawnTask(), delay);
		return this;
	}
	
	protected class DespawnTask implements Runnable
	{
		@Override
		public void run()
		{
			if (!isDecayed())
				deleteMe();
		}
	}
	
	@Override
	protected final void notifyQuestEventSkillFinished(L2Skill skill, L2Object target)
	{
		try
		{
			List<Quest> quests = getTemplate().getEventQuests(EventType.ON_SPELL_FINISHED);
			if (quests != null)
			{
				Player player = null;
				if (target != null)
					player = target.getActingPlayer();
				
				for (Quest quest : quests)
					quest.notifySpellFinished(this, player, skill);
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || !getTemplate().canMove() || getTemplate().getAiType().equals(AIType.CORPSE);
	}
	
	@Override
	public boolean isCoreAIDisabled()
	{
		return super.isCoreAIDisabled() || getTemplate().getAiType().equals(AIType.CORPSE);
	}
	
	@Override
	public void sendInfo(Player activeChar)
	{
		if (getMoveSpeed() == 0)
			activeChar.sendPacket(new ServerObjectInfo(this, activeChar));
		else
			activeChar.sendPacket(new NpcInfo(this, activeChar));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (physical)
		{
			if (_currentSsCount <= 0)
				return;
			
			if (Rnd.get(100) > getTemplate().getSsRate())
				return;
			
			_currentSsCount--;
			Broadcast.toSelfAndKnownPlayersInRadiusSq(this, new MagicSkillUse(this, this, 2154, 1, 0, 0), 360000);
			setChargedShot(ShotType.SOULSHOT, true);
		}
		
		if (magic)
		{
			if (_currentSpsCount <= 0)
				return;
			
			if (Rnd.get(100) > getTemplate().getSpsRate())
				return;
			
			_currentSpsCount--;
			Broadcast.toSelfAndKnownPlayersInRadiusSq(this, new MagicSkillUse(this, this, 2061, 1, 0, 0), 360000);
			setChargedShot(ShotType.SPIRITSHOT, true);
		}
	}
	
	/**
	 * This method is overidden on L2PcInstance, L2Summon and L2Npc.
	 * @return the skills list of this L2Character.
	 */
	@Override
	public Map<Integer, L2Skill> getSkills()
	{
		return getTemplate().getSkills();
	}
	
	/** The _ ct f_ flag team name. */
	public boolean _isFOS_Artifact = false;
	
}