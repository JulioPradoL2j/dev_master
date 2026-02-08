package net.sf.l2j.mods.actor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.dailyreward.DailyRewardManager;
import net.sf.l2j.dailyreward.PlayerVariables;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.lastman.LMEvent;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportWhereType;
import net.sf.l2j.gameserver.handler.chathandlers.ChatAll;
import net.sf.l2j.gameserver.handler.chathandlers.ChatShout;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.instancemanager.PrivateMessageManager.PrivateMessage;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.appearance.PcAppearance;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SummonInstance;
import net.sf.l2j.gameserver.model.actor.template.PcTemplate;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.ClassType;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.base.SubClass;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.RecipeList;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.TeleportToLocation;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillSiegeFlag;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.mods.ai.AbstractFakePlayerAI;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.ai.combat.DefaultCombatBehaviorAI;
import net.sf.l2j.mods.ai.darkelf.fighter.DarkElfFighterAI;
import net.sf.l2j.mods.ai.darkelf.fighter.GhostHunterAI;
import net.sf.l2j.mods.ai.darkelf.fighter.GhostSentinelAI;
import net.sf.l2j.mods.ai.darkelf.mystic.DarkElfMysticAI;
import net.sf.l2j.mods.ai.darkelf.mystic.StormScreamerAI;
import net.sf.l2j.mods.ai.drawn.fighter.DrawnFighterAI;
import net.sf.l2j.mods.ai.drawn.fighter.FortuneSeekerAI;
import net.sf.l2j.mods.ai.drawn.fighter.MaestroAI;
import net.sf.l2j.mods.ai.elf.fighter.ElfFighterAI;
import net.sf.l2j.mods.ai.elf.fighter.MoonlightSentinelAI;
import net.sf.l2j.mods.ai.elf.fighter.WindRiderAI;
import net.sf.l2j.mods.ai.elf.mystic.ElementalMasterAI;
import net.sf.l2j.mods.ai.elf.mystic.ElfMysticAI;
import net.sf.l2j.mods.ai.elf.mystic.MysticMuseAI;
import net.sf.l2j.mods.ai.human.fighter.AdventureAI;
import net.sf.l2j.mods.ai.human.fighter.DuelistAI;
import net.sf.l2j.mods.ai.human.fighter.HawkeyeAI;
import net.sf.l2j.mods.ai.human.fighter.HumanAI;
import net.sf.l2j.mods.ai.human.fighter.PhoenixKnightAI;
import net.sf.l2j.mods.ai.human.fighter.SaggitariusAI;
import net.sf.l2j.mods.ai.human.fighter.TreasureAI;
import net.sf.l2j.mods.ai.human.mystic.ArchmageAI;
import net.sf.l2j.mods.ai.human.mystic.CardinalAI;
import net.sf.l2j.mods.ai.human.mystic.HumanMysticAI;
import net.sf.l2j.mods.ai.human.mystic.SoltakerAI;
import net.sf.l2j.mods.ai.orc.fighter.GrandKhavatariAI;
import net.sf.l2j.mods.ai.orc.fighter.OrcFighterAI;
import net.sf.l2j.mods.ai.orc.fighter.TitanAI;
import net.sf.l2j.mods.ai.orc.mystic.DominatorAI;
import net.sf.l2j.mods.ai.orc.mystic.DoomcryerAI;
import net.sf.l2j.mods.ai.orc.mystic.OrcMysticAI;
import net.sf.l2j.mods.data.FakeChatData;
import net.sf.l2j.mods.data.FakeChatData.ChatContext;
import net.sf.l2j.mods.enums.ExplorerContext;
import net.sf.l2j.mods.interfaces.ICrafter;
import net.sf.l2j.mods.manager.FakePlayerManager;
import net.sf.l2j.mods.party.FakePartyManager;

 

public class FakePlayer extends Player
{
	private static final String AUTOCREATE_ACCOUNTS_INSERT = "INSERT INTO accounts (login, password, lastactive, access_level) values (?, ?, ?, ?)";
	private L2GameClient _client;
	private L2Summon _summon;
	private boolean _destroyed;
	protected int _baseClass;
	private int _karma;
	private int _pvpKills;
	private int _pkKills;
	private byte _pvpFlag;
	private boolean _isOnline = true;
	private AbstractFakePlayerAI _fakeAI;
	private int SOCIAL_RADIUS = 400;
	private int SOCIAL_MIN_PLAYERS = 2;
	private int _team;
	private long _lastAccess;
	
	public FakePlayer(int objectId, PcTemplate template, String accountName, PcAppearance app)
	{
		super(objectId, template, accountName, app);
	}
	
	public AbstractFakePlayerAI getFakeAi()
	{
		return _fakeAI;
	}
	
	public void setFakeAi(AbstractFakePlayerAI fakeAi)
	{
		_fakeAI = fakeAi;
	}
	
	@Override
	public L2GameClient getClient()
	{
		return _client;
	}
	
	@Override
	public void setClient(L2GameClient client)
	{
		_client = client;
	}
	
	@Override
	public long getLastAccess()
	{
		return _lastAccess;
	}
	
	@Override
	public int getPvpKills()
	{
		return _pvpKills;
	}
	
	@Override
	public void setPvpKills(int pvpKills)
	{
		_pvpKills = pvpKills;
	}
	
	@Override
	public int getPkKills()
	{
		return _pkKills;
	}
	
	@Override
	public void setPkKills(int pkKills)
	{
		_pkKills = pkKills;
	}
	
	@Override
	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = (byte) pvpFlag;
	}
	
	@Override
	public byte getPvpFlag()
	{
		return _pvpFlag;
	}
	
	@Override
	public int getKarma()
	{
		return _karma;
	}
	
	@Override
	public void setKarma(int karma)
	{
		if (karma < 0)
			karma = 0;
		
		if (_karma > 0 && karma == 0)
		{
			sendPacket(new UserInfo(this));
			broadcastRelationsChanges();
		}
		
		_karma = karma;
		broadcastKarma();
	}
	
	public boolean isFakeClientAlive()
	{
		return _client != null;
	}
	
	@Override
	public boolean isOnline()
	{
		return _isOnline;
	}
	
	@Override
	public L2Summon getPet()
	{
		return _summon;
	}
	
	@Override
	public boolean hasServitor()
	{
		return _summon instanceof L2SummonInstance;
	}
	
	@Override
	public void setPet(L2Summon summon)
	{
		_summon = summon;
	}
	
	@Override
	public int getSp()
	{
		return getStat().getSp();
	}
	
	@Override
	public long getExp()
	{
		return getStat().getExp();
	}
	
	public boolean isDestroyed()
	{
		return _destroyed;
	}
	
	public void markDestroyed()
	{
		_destroyed = true;
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (getFakeAi() instanceof CombatBehaviorAI)
		{
			((CombatBehaviorAI) getFakeAi()).clearTarget();
		}
		
		if (isPrivateBuying())
			stopPrivateBuy();
		if (isPrivateSelling())
			stopPrivateSell();
		final Player targetPlayer = killer.getActingPlayer();
		if (targetPlayer == null || targetPlayer == this)
			return false;
		
		if (checkIfPvP(targetPlayer) || (targetPlayer.getClan() != null && getClan() != null && getClan().isAtWarWith(targetPlayer.getClanId()) && targetPlayer.getClan().isAtWarWith(getClanId()) && targetPlayer.getPledgeType() != L2Clan.SUBUNIT_ACADEMY && getPledgeType() != L2Clan.SUBUNIT_ACADEMY) || (targetPlayer.getKarma() > 0 && Config.KARMA_AWARD_PK_KILL) || LMEvent.isStarted() && LMEvent.isPlayerParticipant(getObjectId()) || DMEvent.isStarted() && DMEvent.isPlayerParticipant(getObjectId()) || FOSEvent.isStarted() && FOSEvent.isPlayerParticipant(getObjectId()) || TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(getObjectId()) || CTFEvent.isStarted() && CTFEvent.isPlayerParticipant(getObjectId()))
		{
			// Add PvP point to attacker.
			targetPlayer.setPvpKills(targetPlayer.getPvpKills() + 1);
			
			if (Config.ANNOUNCE_PVP_KILL)
			{
				String msg = "";
				msg = "- " + Config.ANNOUNCE_PVP_MSG.replace("$killer", targetPlayer.getName()).replace("$target", getName());
				Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addZoneName(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ()).addString(msg));
			}
			
			// Send UserInfo packet to attacker with its Karma and PK Counter
			targetPlayer.sendPacket(new UserInfo(targetPlayer));
			
		}
		
		if (targetPlayer.getKarma() == 0 && targetPlayer.getPvpFlag() == 0)
		{
			// PK Points are increased only if you kill a player.
			
			targetPlayer.setPkKills(targetPlayer.getPkKills() + 1);
			
			if (!(killer instanceof L2Summon))
			{
				String msg = "";
				msg = "- " + Config.ANNOUNCE_PK_MSG.replace("$killer", targetPlayer.getName()).replace("$target", getName());
				Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addZoneName(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ()).addString(msg));
			}
			
			// Calculate new karma.
			targetPlayer.setKarma(getKarma() + Formulas.calculateKarmaGain(targetPlayer.getPkKills(), killer instanceof L2Summon));
			
			// Send UserInfo packet to attacker with its Karma and PK Counter
			targetPlayer.sendPacket(new UserInfo(targetPlayer));
		}
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		
		try
		{
			if (getFakeAi() instanceof CombatBehaviorAI)
			{
				CombatBehaviorAI ai = (CombatBehaviorAI) getFakeAi();
				ai.clearTarget();
			}
			
			markDestroyed();
			
			FakePlayerManager.getInstance().unregister(this);
			
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on FakePlayer deleteMe()" + e.getMessage(), e);
		}
		super.deleteMe();
		
	}
	
	@Override
	public void teleToLocation(int x, int y, int z, int randomOffset)
	{
		if (isPrivateBuying())
			stopPrivateBuy();
		
		if (isPrivateSelling())
			stopPrivateSell();
		// Stop movement
		stopMove(null);
		abortAttack();
		abortCast();
		if (getFakeAi() instanceof CombatBehaviorAI)
		{
			CombatBehaviorAI ai = (CombatBehaviorAI) getFakeAi();
			ai.clearTarget();
		}
		setIsTeleporting(true);
		setTarget(null);
		
		getAI().setIntention(CtrlIntention.ACTIVE);
		
		if (randomOffset > 0)
		{
			x += Rnd.get(-randomOffset, randomOffset);
			y += Rnd.get(-randomOffset, randomOffset);
		}
		
		z += 5;
		
		// Send TeleportToLocationt to the L2Character AND to all L2PcInstance in the _KnownPlayers of the L2Character
		broadcastPacket(new TeleportToLocation(this, x, y, z));
		
		// remove the object from its old location
		decayMe();
		
		// Set the x,y,z position of the L2Object and if necessary modify its _worldRegion
		setXYZ(x, y, z);
		
		onTeleported();
		
		revalidateZone(true);
		
		if (getParty() != null && isPartyLeader())
		{
			for (Player member : getParty().getPartyMembers())
			{
				if (member == this)
					continue;
				
				if (member instanceof FakePlayer)
				{
					FakePlayer fp = (FakePlayer) member;
					if (fp != this && !fp.isDead())
						fp.teleToLocation(x, y, z, randomOffset);
				}
			}
		}
	}
	
	@Override
	public void teleToLocation(Location loc, int randomOffset)
	{
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		
		teleToLocation(x, y, z, randomOffset);
	}
	
	@Override
	public void teleToLocation(TeleportWhereType teleportWhere)
	{
		teleToLocation(MapRegionTable.getInstance().getTeleToLocation(this, teleportWhere), 20);
	}
	
	public boolean isMage()
	{
		return getClassId().getType() == ClassType.MYSTIC;
	}
	
	public boolean isDagger()
	{
		return getClassId().equalsOrChildOf(ClassId.ADVENTURER) || getClassId().equalsOrChildOf(ClassId.TREASURE_HUNTER) || getClassId().equalsOrChildOf(ClassId.WIND_RIDER) || getClassId().equalsOrChildOf(ClassId.GHOST_HUNTER);
	}
	
	public boolean isArcher()
	{
		return getClassId().equalsOrChildOf(ClassId.SAGGITARIUS) || getClassId().equalsOrChildOf(ClassId.MOONLIGHT_SENTINEL) || getClassId().equalsOrChildOf(ClassId.GHOST_SENTINEL);
	}
	
	public boolean isWarrior()
	{
		return !isMage() && !isArcher() && !isDagger();
	}
	
	public boolean isBusy()
	{
		return isCastingNow() || isAttackingNow() || getAttackEndTime() > System.currentTimeMillis() || isStunned();
	}
	
	private long _lastPotionUse;
	
	public boolean canUsePotion(long delay)
	{
		return System.currentTimeMillis() - _lastPotionUse >= delay;
	}
	
	public void markPotionUsed()
	{
		_lastPotionUse = System.currentTimeMillis();
	}
	
	public boolean shouldUseHpPotion()
	{
		return getCurrentHp() / getMaxHp() < 0.65;
	}
	
	public boolean shouldUseMpPotion()
	{
		return getCurrentMp() / getMaxMp() < 0.50;
	}
	
	public boolean shouldUseCpPotion()
	{
		return getCurrentCp() / getMaxCp() < 0.60;
	}
	
	public void tryUsePotion()
	{
		if (isDead())
			return;
		
		if (isStunned())
			return;
		
		if (isBusy())
			return;
		
		if (!canUsePotion(3000))
			return;
		
		// HP
		if (shouldUseHpPotion())
		{
			heal(getMaxHp() * 0.35);
			broadcastPotion(1539);
			markPotionUsed();
			return;
		}
		
		// MP
		if (shouldUseMpPotion())
		{
			setCurrentMp(Math.min(getMaxMp(), getCurrentMp() + getMaxMp() * 0.30));
			broadcastPotion(728);
			markPotionUsed();
			return;
		}
		
		if (shouldUseCpPotion())
		{
			setCurrentCp(Math.min(getMaxCp(), getCurrentCp() + getMaxCp() * 0.40));
			broadcastPotion(5591);
			markPotionUsed();
		}
		
	}
	
	private void broadcastPotion(int itemId)
	{
		Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item == null)
			return;
		
		IntIntHolder[] skills = item.getSkills();
		if (skills == null || skills.length == 0)
			return;
		
		IntIntHolder holder = skills[0];
		
		int skillId = holder.getId();
		int skillLevel = holder.getValue();
		
		broadcastPacket(new MagicSkillUse(this, this, skillId, skillLevel, 0, 0));
	}
	
	public void heal(double amount)
	{
		if (amount <= 0)
			return;
		
		double newHp = Math.min(getCurrentHp() + amount, getMaxHp());
		if (newHp <= getCurrentHp())
			return;
		
		setCurrentHp(newHp);
		
		// Delay fake de resposta do client
		broadcastStatusUpdate();
	}
	
	@Override
	public void onActionShift(Player player)
	{
		if (player.isGM())
		{
			showFakePanel(player);
		}
		
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
		}
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void showFakePanel(Player gm)
	{
		// Build HTML programmatically to avoid external file dependency and allow
		// finer control over layout and escaping of dynamic values.
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		StringBuilder sb = new StringBuilder(512);
		sb.append("<html><body>");
		sb.append("<center><table width=300 cellpadding=4 cellspacing=0>");
		sb.append("<tr><td align=center><font color=LEVEL><b>FakePlayer Panel</b></font></td></tr>");
		sb.append("<tr><td>");
		
		sb.append("<table width=100%>");
		sb.append("<tr><td width=30%><b>Name:</b></td><td>").append(escapeHtml(getName())).append("</td></tr>");
		sb.append("<tr><td><b>Level:</b></td><td>").append(getLevel()).append("</td></tr>");
		sb.append("<tr><td><b>Town:</b></td><td>").append(escapeHtml(String.valueOf(getCurrentTownName()))).append("</td></tr>");
		sb.append("<tr><td><b>Class:</b></td><td>").append(escapeHtml(getTemplate().getClassName())).append("</td></tr>");
		sb.append("<tr><td><b>Status:</b></td><td>").append(escapeHtml(getCurrentAction())).append("</td></tr>");
		sb.append("</table>");
		
		sb.append("</td></tr>");
		
		sb.append("<tr><td align=center>");
		// Center controls
		sb.append("<button value=\"Teleport Here\" action=\"bypass -h admin_fake_tp ").append(getObjectId()).append("\" width=120 height=20>");
		sb.append("&nbsp;");
		sb.append("<button value=\"Go To\" action=\"bypass -h admin_fake_go ").append(getObjectId()).append("\" width=80 height=20>");
		sb.append("&nbsp;");
		sb.append("<button value=\"Despawn\" action=\"bypass -h admin_fake_despawn ").append(getObjectId()).append("\" width=80 height=20>");
		sb.append("</td></tr>");
		
		sb.append("</table></center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		gm.sendPacket(html);
	}
	
	// Simple HTML escaper for dynamic values inserted into generated HTML.
	private static String escapeHtml(String s)
	{
		if (s == null)
			return "";
		StringBuilder out = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			switch (c)
			{
				case '<':
					out.append("&lt;");
					break;
				case '>':
					out.append("&gt;");
					break;
				case '&':
					out.append("&amp;");
					break;
				case '"':
					out.append("&quot;");
					break;
				case '\'':
					out.append("&#x27;");
					break;
				default:
					out.append(c);
			}
		}
		return out.toString();
	}
	
	public void assignDefaultAI()
	{
		try
		{
			Class<? extends AbstractFakePlayerAI> aiClass = getCombatAIByClassId(getClassId());
			
			setFakeAi(aiClass.getConstructor(FakePlayer.class).newInstance(this));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static Class<? extends AbstractFakePlayerAI> getCombatAIByClassId(ClassId classId)
	{
		Class<? extends AbstractFakePlayerAI> ai = getAllAIs().get(classId);
		if (ai == null)
			return DefaultCombatBehaviorAI.class;
		
		return ai;
	}
	
	public static Map<ClassId, Class<? extends AbstractFakePlayerAI>> getAllAIs()
	{
		Map<ClassId, Class<? extends AbstractFakePlayerAI>> ais = new HashMap<>();
		
		ais.put(ClassId.HUMAN_FIGHTER, HumanAI.class);
		
		ais.put(ClassId.TREASURE_HUNTER, TreasureAI.class);
		ais.put(ClassId.HAWKEYE, HawkeyeAI.class);
		
		ais.put(ClassId.DUELIST, DuelistAI.class);
		ais.put(ClassId.PHOENIX_KNIGHT, PhoenixKnightAI.class);
		ais.put(ClassId.SAGGITARIUS, SaggitariusAI.class);
		ais.put(ClassId.ADVENTURER, AdventureAI.class);
		
		ais.put(ClassId.HUMAN_MYSTIC, HumanMysticAI.class);
		ais.put(ClassId.ARCHMAGE, ArchmageAI.class);
		ais.put(ClassId.SOULTAKER, SoltakerAI.class);
		ais.put(ClassId.CARDINAL, CardinalAI.class);
		
		ais.put(ClassId.ELVEN_FIGHTER, ElfFighterAI.class);
		ais.put(ClassId.WIND_RIDER, WindRiderAI.class);
		ais.put(ClassId.MOONLIGHT_SENTINEL, MoonlightSentinelAI.class);
		
		ais.put(ClassId.ELVEN_MYSTIC, ElfMysticAI.class);
		ais.put(ClassId.MYSTIC_MUSE, MysticMuseAI.class);
		ais.put(ClassId.ELEMENTAL_MASTER, ElementalMasterAI.class);
		
		ais.put(ClassId.DARK_FIGHTER, DarkElfFighterAI.class);
		ais.put(ClassId.GHOST_HUNTER, GhostHunterAI.class);
		ais.put(ClassId.GHOST_SENTINEL, GhostSentinelAI.class);
		
		ais.put(ClassId.DARK_MYSTIC, DarkElfMysticAI.class);
		ais.put(ClassId.STORM_SCREAMER, StormScreamerAI.class);
		
		ais.put(ClassId.ORC_FIGHTER, OrcFighterAI.class);
		ais.put(ClassId.TITAN, TitanAI.class);
		ais.put(ClassId.GRAND_KHAVATARI, GrandKhavatariAI.class);
		
		ais.put(ClassId.ORC_MYSTIC, OrcMysticAI.class);
		ais.put(ClassId.DOMINATOR, DominatorAI.class);
		ais.put(ClassId.DOOMCRYER, DoomcryerAI.class);
		
		ais.put(ClassId.DWARVEN_FIGHTER, DrawnFighterAI.class);
		ais.put(ClassId.FORTUNE_SEEKER, FortuneSeekerAI.class);
		ais.put(ClassId.MAESTRO, MaestroAI.class);
		
		return ais;
	}
	
	public void startPrivateSell(String title)
	{
		
		if (_privateSellActive)
			return;
		
		stopMove(null);
		abortAttack();
		abortCast();
		
		getSellList().clear();
		getSellList().setTitle(title);
		setStoreType(StoreType.SELL);
		sitDown();
		_privateSellActive = true;
		broadcastUserInfo();
	}
	
	public void addSellItem(ItemInstance item, long count, int price)
	{
		
		if (!_privateSellActive)
			return;
		
		if (item == null || count <= 0 || price < 0)
			return;
		
		getSellList().addItem(item.getObjectId(), (int) count, price);
	}
	
	public void stopPrivateSell()
	{
		
		if (!_privateSellActive)
			return;
		// remove todos os itens de venda do inventário
		for (var item : getSellList().getItems())
			destroyItemByItemId("PrivateSell", item.getItem().getItemId(), item.getCount(), this, false);
		
		getSellList().clear();
		
		setStoreType(StoreType.NONE);
		
		standUp();
		_privateSellActive = false;
		broadcastUserInfo();
	}
	
	private boolean _privateSellActive;
	
	public boolean isPrivateSelling()
	{
		return _privateSellActive;
	}
	
	public void startPrivateBuy(String title)
	{
		
		if (_privateBuyActive)
			return;
		
		stopMove(null);
		abortAttack();
		abortCast();
		
		getBuyList().clear();
		getBuyList().setTitle(title);
		setStoreType(StoreType.BUY);
		sitDown();
		_privateBuyActive = true;
		broadcastUserInfo();
	}
	
	public void addBuyItem(ItemInstance item, long count, int price)
	{
		
		if (!_privateBuyActive)
			return;
		
		if (item == null || count <= 0 || price < 0)
			return;
		
		getBuyList().addItem(item.getObjectId(), (int) count, price);
	}
	
	public void stopPrivateBuy()
	{
		
		if (!_privateBuyActive)
			return;
		// remove todos os itens de venda do inventário
		for (var item : getBuyList().getItems())
			destroyItemByItemId("PrivateBuy", item.getItem().getItemId(), item.getCount(), this, false);
		
		getBuyList().clear();
		
		setStoreType(StoreType.NONE);
		
		standUp();
		_privateBuyActive = false;
		broadcastUserInfo();
	}
	
	private boolean _privateBuyActive;
	
	public boolean isPrivateBuying()
	{
		return _privateBuyActive;
	}
	
	public String getCurrentAction()
	{
		// 1. Private Store
		if (isPrivateSelling())
			return "Selling";
		
		if (isPickingUp())
			return "PickUp";
		
		if (isPrivateManufactureing())
			return "Crafting";
		
		if (isDead())
			return "Death";
		
		if (isPrivateBuying())
			return "Buying";
		// 2. PK
		if (getKarma() > 0 && isInsideZone(ZoneId.TOWN))
			return "PK (Hiding)";
		
		// 3. PvP
		if (getPvpFlag() > 0)
			return "PvP";
		
		// 4. Cidade
		if (isInsideZone(ZoneId.TOWN))
			return getCurrentTownName();
		
		// 5. Farm
		if (!isInsideZone(ZoneId.TOWN))
		{
			if (isMoving())
				return "Walking";
			return "Farming";
		}
		
		// 6. Fallback
		return "Idle";
	}
	
	private ItemInstance _pickupTarget;
	private long _pickupUntil;
	
	public boolean isPickingUp()
	{
		if (_pickupTarget == null)
			return false;
		
		// item sumiu
		if (!_pickupTarget.isVisible())
		{
			clearPickup();
			return false;
		}
		
		// tempo acabou
		if (System.currentTimeMillis() > _pickupUntil)
		{
			clearPickup();
			return false;
		}
		
		return true;
	}
	
	public void startPickup(ItemInstance item, long durationMs)
	{
		_pickupTarget = item;
		_pickupUntil = System.currentTimeMillis() + durationMs;
	}
	
	public ItemInstance getPickupTarget()
	{
		return _pickupTarget;
	}
	
	public void clearPickup()
	{
		if (_pickupTarget != null)
		{
			_pickupTarget.releasePickupReservation(getObjectId());
		}
		
		_pickupTarget = null;
		_pickupUntil = 0;
	}
	
	public void registerRecipes()
	{
		for (RecipeList recipe : ICrafter.getValidRecipes())
		{
			if (hasRecipeList(recipe.getId()))
				continue;
			
			if (recipe.isDwarvenRecipe())
				registerDwarvenRecipeList(recipe);
			else
				registerCommonRecipeList(recipe);
		}
	}
	
	private boolean _privateManufactureActive;
	
	public boolean isPrivateManufactureing()
	{
		return _privateManufactureActive;
	}
	
	public void setPrivateManufactureing(boolean val)
	{
		_privateManufactureActive = val;
	}
	
	public void deleteCharByObjId(int objectId)
	{
		if (objectId < 0)
			return;
		
		CharNameTable.getInstance().unregister(objectId);
		
		try (Connection con = ConnectionPool.getConnection())
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
			
		}
		catch (Exception e)
		{
			_log.info("Couldn't delete player." + e);
		}
	}
	
	private String _lang;
	private static final String DEFAULT_LANG = "pt_BR";
	
	public String getLang()
	{
		if (_lang == null)
		{
			Set<String> langs = FakeChatData.getInstance().getAvailableLanguages(Say2.TELL);
			
			if (langs != null && !langs.isEmpty())
				_lang = Rnd.get(langs.iterator());
			else
				_lang = DEFAULT_LANG;
		}
		return _lang;
	}
	
	public void setLang(String lang)
	{
		_lang = lang;
	}
	
	@Override
	public void onPrivateMessage(PrivateMessage msg)
	{
		super.onPrivateMessage(msg);
		((CombatBehaviorAI) getFakeAi()).onPrivateMessage(msg);
	}
	
	public void sendTell(String targetName, String text, int type)
	{
		if (isPrivateBuying() || isPrivateSelling() || isPrivateManufactureing())
			return;
		
		Player target = L2World.getInstance().getPlayer(targetName);
		if (target == null)
			return;
		
		target.sendPacket(new CreatureSay(getObjectId(), type, getName(), text));
		
		sendPacket(new CreatureSay(getObjectId(), type, "->" + target.getName(), text));
	}
	
	public ChatContext resolveContext()
	{
		if (isDead())
			return ChatContext.DEAD;
		
		// Combate sempre tem prioridade
		if (isInCombat() || getPvpFlag() > 0)
			return ChatContext.COMBAT;
		
		int region = MapRegionTable.getMapRegion(getX(), getY());
		
		// Regiões conhecidas de cidade
		switch (region)
		{
			case 0: // TI
			case 1: // Elven
			case 2: // DE
			case 3: // Orc
			case 4: // Dwarf
			case 5: // Gludio
			case 6: // Gludin
			case 7: // Dion
			case 8: // Giran
			case 9: // Oren
			case 10: // Aden
			case 11: // HV
			case 13: // Heine
			case 14: // Rune
			case 15: // Goddard
			case 16: // Schuttgart
			case 17: // Floran
				return ChatContext.CITY;
		}
		
		return ChatContext.FARM;
	}
	
	public void sendGlobalMessage(String text, int chatId)
	{
		if (isPrivateBuying() || isPrivateSelling() || isPrivateManufactureing())
			return;
		if (chatId == 1)
		{
			ChatShout chat = new ChatShout();
			chat.handleChat(chatId, this, null, text);
		}
		if (chatId == 0)
		{
			
			ChatAll chat = new ChatAll();
			chat.handleChat(chatId, this, null, text);
		}
		
	}
	
	public ExplorerContext resolveExplorerContext()
	{
		if (isDead() || isBusy())
			return ExplorerContext.IDLE;
		
		if (isInCombat() || getPvpFlag() > 0)
			return ExplorerContext.DANGEROUS;
		
		int region = MapRegionTable.getMapRegion(getX(), getY());
		
		switch (region)
		{
			case 0: // TI
			case 1: // Elven
			case 2: // DE
			case 3: // Orc
			case 4: // Dwarf
			case 5: // Gludio
			case 6: // Gludin
			case 7: // Dion
			case 8: // Giran
			case 9: // Oren
			case 10: // Aden
			case 11: // HV
			case 13: // Heine
			case 14: // Rune
			case 15: // Goddard
			case 16: // Schuttgart
			case 17: // Floran
				
				return ExplorerContext.CITY_WALK;
		}
		
		int realPlayers = 0;
		
		for (Player pc : getKnownList().getKnownTypeInRadius(Player.class, SOCIAL_RADIUS))
		{
			if (pc == this)
				continue;
			
			if (pc.getClient() != null && !pc.getClient().isDetached())
				realPlayers++;
		}
		
		if (realPlayers > SOCIAL_MIN_PLAYERS)
		{
			
			return ExplorerContext.SOCIAL;
		}
		return ExplorerContext.FARM_WALK;
	}
	
	private volatile long _lastChatGlobalTime = 0L;
	
	public long getLastChatGlobalTime()
	{
		return _lastChatGlobalTime;
	}
	
	public void setLastChatGlobalTime(long time)
	{
		_lastChatGlobalTime = time;
	}
	
	public boolean isInsideZoneTown()
	{
		// Prioridade absoluta: Zone TOWN
		if (isInsideZone(ZoneId.TOWN))
			return true;
		
		// Fallback seguro via MapRegion
		int region = MapRegionTable.getMapRegion(getX(), getY());
		
		switch (region)
		{
			case 0: // Talking Island
			case 1: // Elven Village
			case 2: // Dark Elven Village
			case 3: // Orc Village
			case 4: // Dwarven Village
			case 5: // Gludio
			case 6: // Gludin
			case 7: // Dion
			case 8: // Giran
			case 9: // Oren
			case 10: // Aden
			case 11: // Hunters Village
			case 13: // Heine
			case 14: // Rune
			case 15: // Goddard
			case 16: // Schuttgart
			case 17: // Floran
				return true;
		}
		
		return false;
	}
	
	public String getCurrentTownName()
	{
		// Zona oficial tem prioridade
		if (isInsideZone(ZoneId.TOWN))
		{
			int region = MapRegionTable.getMapRegion(getX(), getY());
			return getTownNameByRegion(region);
		}
		
		// Fallback MapRegion
		int region = MapRegionTable.getMapRegion(getX(), getY());
		return getTownNameByRegion(region);
	}
	
	private static String getTownNameByRegion(int region)
	{
		switch (region)
		{
			case 0:
				return "Talking Island";
			case 1:
				return "Elven Village";
			case 2:
				return "Dark Elven Village";
			case 3:
				return "Orc Village";
			case 4:
				return "Dwarven Village";
			case 5:
				return "Gludio";
			case 6:
				return "Gludin";
			case 7:
				return "Dion";
			case 8:
				return "Giran";
			case 9:
				return "Oren";
			case 10:
				return "Aden";
			case 11:
				return "Hunters Village";
			case 13:
				return "Heine";
			case 14:
				return "Rune";
			case 15:
				return "Goddard";
			case 16:
				return "Schuttgart";
			case 17:
				return "Floran";
			default:
				return "Unknown Town";
		}
	}
	
	@Override
	public boolean isBehind(Creature target)
	{
		if (target == null)
			return false;
		
		final double maxAngleDiff = target instanceof Player ? 60 : 120;
		
		double angleChar = Util.calculateAngleFrom(this, target);
		double angleTarget = Util.convertHeadingToDegree(target.getHeading());
		double angleDiff = angleChar - angleTarget;
		
		if (angleDiff <= -360 + maxAngleDiff)
			angleDiff += 360;
		
		if (angleDiff >= 360 - maxAngleDiff)
			angleDiff -= 360;
		
		return Math.abs(angleDiff) <= maxAngleDiff;
	}
	
	@Override
	public boolean isBehindTarget()
	{
		L2Object target = getTarget();
		if (target instanceof Creature)
			return isBehind((Creature) target);
		
		return false;
	}
	
	@Override
	public boolean isInFrontOf(Creature target)
	{
		if (target == null)
			return false;
		
		final double maxAngleDiff = 60;
		
		double angleTarget = Util.calculateAngleFrom(target, this);
		double angleChar = Util.convertHeadingToDegree(target.getHeading());
		double angleDiff = angleChar - angleTarget;
		
		if (angleDiff <= -360 + maxAngleDiff)
			angleDiff += 360;
		
		if (angleDiff >= 360 - maxAngleDiff)
			angleDiff -= 360;
		
		return Math.abs(angleDiff) <= maxAngleDiff;
	}
	
	@Override
	public boolean isFacing(L2Object target, int maxAngle)
	{
		if (target == null)
			return false;
		
		double maxAngleDiff = maxAngle / 2;
		double angleTarget = Util.calculateAngleFrom(this, target);
		double angleChar = Util.convertHeadingToDegree(getHeading());
		double angleDiff = angleChar - angleTarget;
		
		if (angleDiff <= -360 + maxAngleDiff)
			angleDiff += 360;
		
		if (angleDiff >= 360 - maxAngleDiff)
			angleDiff -= 360;
		
		return Math.abs(angleDiff) <= maxAngleDiff;
	}
	
	@Override
	public boolean isInFrontOfTarget()
	{
		L2Object target = getTarget();
		if (target instanceof Creature)
			return isInFrontOf((Creature) target);
		
		return false;
	}
	
	public boolean checkUseMagicConditions(L2Skill skill, boolean forceUse, boolean dontMove)
	{
		if (skill == null)
			return false;
		
		if (isDead() || isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (isSkillDisabled(skill))
			return true;
		
		L2SkillType sklType = skill.getSkillType();
		
		if (isFishing() && (sklType != L2SkillType.PUMPING && sklType != L2SkillType.REELING && sklType != L2SkillType.FISHING))
		{
			return true;
		}
		
		if (inObserverMode())
		{
			abortCast();
			return true;
		}
		
		if (isSitting())
		{
			if (skill.isToggle())
			{
				L2Effect effect = getFirstEffect(skill.getId());
				if (effect != null)
				{
					effect.exit();
					return true;
				}
			}
			return true;
		}
		
		if (skill.isToggle())
		{
			L2Effect effect = getFirstEffect(skill.getId());
			
			if (effect != null)
			{
				if (skill.getId() != 60)
					effect.exit();
				
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
		}
		
		if (isFakeDeath())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		L2Object target = null;
		SkillTargetType sklTargetType = skill.getTargetType();
		Location worldPosition = getCurrentSkillWorldPosition();
		
		if (sklTargetType == SkillTargetType.TARGET_GROUND && worldPosition == null)
		{
			_log.info("WorldPosition is null for skill: " + skill.getName() + ", player: " + getName() + ".");
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		switch (sklTargetType)
		{
			// Target the player if skill type is AURA, PARTY, CLAN or SELF
			case TARGET_AURA:
			case TARGET_FRONT_AURA:
			case TARGET_BEHIND_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_PARTY:
			case TARGET_ALLY:
			case TARGET_CLAN:
			case TARGET_GROUND:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
			case TARGET_AREA_SUMMON:
				target = this;
				break;
			case TARGET_PET:
			case TARGET_SUMMON:
				target = getPet();
				break;
			default:
				target = getTarget();
				break;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (target instanceof L2DoorInstance)
		{
			if (!((L2DoorInstance) target).isAutoAttackable(this) // Siege doors only hittable during siege
				|| (((L2DoorInstance) target).isUnlockable() && skill.getSkillType() != L2SkillType.UNLOCK)) // unlockable doors
			{
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
		}
		
		// Are the target and the player in the same duel?
		if (isInDuel())
		{
			if (target instanceof Playable)
			{
				// Get Player
				Player cha = target.getActingPlayer();
				if (cha.getDuelId() != getDuelId())
				{
					sendPacket(SystemMessageId.INCORRECT_TARGET);
					sendPacket(ActionFailed.STATIC_PACKET);
					return true;
				}
			}
		}
		
		// ************************************* Check casting conditions *******************************************
		
		// Check if all casting conditions are completed
		if (!skill.checkCondition(this, target, false))
		{
			// Send ActionFailed to the Player
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		// ************************************* Check Skill Type *******************************************
		
		// Check if this is offensive magic skill
		if (skill.isOffensive())
		{
			if (isInsidePeaceZone(this, target))
			{
				// If Creature or target is in a peace zone, send a system message TARGET_IN_PEACEZONE ActionFailed
				sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
			
			if (isInOlympiadMode() && !isOlympiadStart())
			{
				// if Player is in Olympia and the match isn't already start, send ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
			
			// Check if the target is attackable
			if (!target.isAttackable() && !getAccessLevel().allowPeaceAttack())
			{
				// If target is not attackable, send ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
			
			// Check if a Forced ATTACK is in progress on non-attackable target
			if (!target.isAutoAttackable(this) && !forceUse)
			{
				switch (sklTargetType)
				{
					case TARGET_AURA:
					case TARGET_FRONT_AURA:
					case TARGET_BEHIND_AURA:
					case TARGET_AURA_UNDEAD:
					case TARGET_CLAN:
					case TARGET_ALLY:
					case TARGET_PARTY:
					case TARGET_SELF:
					case TARGET_GROUND:
					case TARGET_CORPSE_ALLY:
					case TARGET_AREA_SUMMON:
						break;
					default: // Send ActionFailed to the Player
						sendPacket(ActionFailed.STATIC_PACKET);
						return true;
				}
			}
			
			// Check if the target is in the skill cast range
			if (dontMove)
			{
				// Calculate the distance between the Player and the target
				if (sklTargetType == SkillTargetType.TARGET_GROUND)
				{
					if (!isInsideRadius(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), (int) (skill.getCastRange() + getCollisionRadius()), false, false))
					{
						// Send a System Message to the caster
						sendPacket(SystemMessageId.TARGET_TOO_FAR);
						
						// Send ActionFailed to the Player
						sendPacket(ActionFailed.STATIC_PACKET);
						return true;
					}
				}
				else if (skill.getCastRange() > 0 && !isInsideRadius(target, (int) (skill.getCastRange() + getCollisionRadius()), false, false))
				{
					// Send a System Message to the caster
					sendPacket(SystemMessageId.TARGET_TOO_FAR);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return true;
				}
			}
		}
		
		// Check if the skill is defensive
		if (!skill.isOffensive() && target instanceof L2MonsterInstance && !forceUse)
		{
			// check if the target is a monster and if force attack is set.. if not then we don't want to cast.
			switch (sklTargetType)
			{
				case TARGET_PET:
				case TARGET_SUMMON:
				case TARGET_AURA:
				case TARGET_FRONT_AURA:
				case TARGET_BEHIND_AURA:
				case TARGET_AURA_UNDEAD:
				case TARGET_CLAN:
				case TARGET_SELF:
				case TARGET_CORPSE_ALLY:
				case TARGET_PARTY:
				case TARGET_ALLY:
				case TARGET_CORPSE_MOB:
				case TARGET_AREA_CORPSE_MOB:
				case TARGET_GROUND:
					break;
				default:
				{
					switch (sklType)
					{
						case BEAST_FEED:
						case DELUXE_KEY_UNLOCK:
						case UNLOCK:
							break;
						default:
							sendPacket(ActionFailed.STATIC_PACKET);
							return true;
					}
					break;
				}
			}
		}
		
		// Check if the skill is Spoil type and if the target isn't already spoiled
		if (sklType == L2SkillType.SPOIL)
		{
			if (!(target instanceof L2MonsterInstance))
			{
				// Send a System Message to the Player
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				
				// Send ActionFailed to the Player
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
		}
		
		// Check if the skill is Sweep type and if conditions not apply
		if (sklType == L2SkillType.SWEEP && target instanceof Attackable)
		{
			if (((Attackable) target).isDead())
			{
				final int spoilerId = ((Attackable) target).getSpoilerId();
				if (spoilerId == 0)
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return true;
				}
				
				if (!isLooterOrInLooterParty(spoilerId))
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.SWEEP_NOT_ALLOWED);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return true;
				}
			}
		}
		
		// Check if the skill is Drain Soul (Soul Crystals) and if the target is a MOB
		if (sklType == L2SkillType.DRAIN_SOUL)
		{
			if (!(target instanceof L2MonsterInstance))
			{
				// Send a System Message to the Player
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				
				// Send ActionFailed to the Player
				sendPacket(ActionFailed.STATIC_PACKET);
				return true;
			}
		}
		
		// Check if this is a Pvp skill and target isn't a non-flagged/non-karma player
		switch (sklTargetType)
		{
			case TARGET_PARTY:
			case TARGET_ALLY: // For such skills, checkPvpSkill() is called from L2Skill.getTargetList()
			case TARGET_CLAN: // For such skills, checkPvpSkill() is called from L2Skill.getTargetList()
			case TARGET_AURA:
			case TARGET_FRONT_AURA:
			case TARGET_BEHIND_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_GROUND:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
			case TARGET_AREA_SUMMON:
				break;
			default:
				if (!checkPvpSkill(target, skill) && !getAccessLevel().allowPeaceAttack())
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return true;
				}
		}
		
		if ((sklTargetType == SkillTargetType.TARGET_HOLY && !checkIfOkToCastSealOfRule(CastleManager.getInstance().getCastle(this), false, skill, target)) || (sklType == L2SkillType.SIEGEFLAG && !L2SkillSiegeFlag.checkIfOkToPlaceFlag(this, false)) || (sklType == L2SkillType.STRSIEGEASSAULT && !checkIfOkToUseStriderSiegeAssault(skill)) || (sklType == L2SkillType.SUMMON_FRIEND && !(checkSummonerStatus(this) && checkSummonTargetStatus(target, this))))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			abortCast();
			return true;
		}
		if (!moveOrCheckRange(this, (Creature) target, skill.getCastRange()))
			return true;
		
		// finally, after passing all conditions
		return false;
	}
	
	private static boolean moveOrCheckRange(FakePlayer player, Creature target, int range)
	{
		if (!player.isInsideRadius(target, range, true, false))
		{
			Location pos = getCombatPosition(target, range);
			player.getAI().setIntention(CtrlIntention.MOVE_TO, pos);
			return false;
		}
		return true;
	}
	
	private static Location getCombatPosition(Creature target, int range)
	{
		double angle = Math.toRadians(Rnd.get(0, 360));
		int x = (int) (target.getX() + Math.cos(angle) * range);
		int y = (int) (target.getY() + Math.sin(angle) * range);
		return new Location(x, y, target.getZ());
	}
	
	@Override
	public void broadcastPacket(L2GameServerPacket mov)
	{
		Broadcast.toSelfAndKnownPlayers(this, mov);
	}
	
	@Override
	public void broadcastPacket(L2GameServerPacket mov, int radius)
	{
		Broadcast.toSelfAndKnownPlayersInRadius(this, mov, radius);
	}
	
	@Override
	public void setTeam(int team)
	{
		_team = team;
	}
	
	@Override
	public int getTeam()
	{
		return _team;
	}
	
	public static FakePlayer restore(int objectId)
	{
		FakePlayer player = null;
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement(RESTORE_CHARACTER);
			statement.setInt(1, objectId);
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				final int activeClassId = rset.getInt("classid");
				final PcTemplate template = CharTemplateTable.getInstance().getTemplate(activeClassId);
				final PcAppearance app = new PcAppearance(rset.getByte("face"), rset.getByte("hairColor"), rset.getByte("hairStyle"), Sex.values()[rset.getInt("sex")]);
				
				player = new FakePlayer(objectId, template, rset.getString("account_name"), app);
				player.setName(rset.getString("char_name"));
				player._lastAccess = rset.getLong("lastAccess");
				
				player.getStat().setExp(rset.getLong("exp"));
				player.setExpBeforeDeath(rset.getLong("expBeforeDeath"));
				player.getStat().setLevel(rset.getByte("level"));
				player.getStat().setSp(rset.getInt("sp"));
				
				player.setWantsPeace(rset.getInt("wantspeace") == 1);
				
				player.setHeading(rset.getInt("heading"));
				
				player.setKarma(rset.getInt("karma"));
				player.setPvpKills(rset.getInt("pvpkills"));
				player.setPkKills(rset.getInt("pkkills"));
				player.setOnlineTime(rset.getLong("onlinetime"));
				player.setNoble(rset.getInt("nobless") == 1, false);
				
				player.setClanJoinExpiryTime(rset.getLong("clan_join_expiry_time"));
				if (player.getClanJoinExpiryTime() < System.currentTimeMillis())
					player.setClanJoinExpiryTime(0);
				
				player.setClanCreateExpiryTime(rset.getLong("clan_create_expiry_time"));
				if (player.getClanCreateExpiryTime() < System.currentTimeMillis())
					player.setClanCreateExpiryTime(0);
				
				player.setPowerGrade(rset.getInt("power_grade"));
				player.setPledgeType(rset.getInt("subpledge"));
				player.setLastRecomUpdate(rset.getLong("last_recom_date"));
				
				int clanId = rset.getInt("clanid");
				if (clanId > 0)
					player.setClan(ClanTable.getInstance().getClan(clanId));
				
				if (player.getClan() != null)
				{
					if (player.getClan().getLeaderId() != player.getObjectId())
					{
						if (player.getPowerGrade() == 0)
							player.setPowerGrade(5);
						
						player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
					}
					else
					{
						player.setClanPrivileges(L2Clan.CP_ALL);
						player.setPowerGrade(1);
					}
				}
				else
					player.setClanPrivileges(L2Clan.CP_NOTHING);
				
				player.setDeleteTimer(rset.getLong("deletetime"));
				
				player.setTitle(rset.getString("title"));
				player.setAccessLevel(rset.getInt("accesslevel"));
				player.setFistsWeaponItem(findFistsWeaponItem(activeClassId));
				player.setUptime(System.currentTimeMillis());
				
				// Check recs
				player.checkRecom(rset.getInt("rec_have"), rset.getInt("rec_left"));
				
				player._classIndex = 0;
				try
				{
					player.setBaseClass(rset.getInt("base_class"));
				}
				catch (Exception e)
				{
					player.setBaseClass(activeClassId);
				}
				
				// Restore Subclass Data (cannot be done earlier in function)
				if (restoreSubClassData(player))
				{
					if (activeClassId != player.getBaseClass())
					{
						for (SubClass subClass : player.getSubClasses().values())
							if (subClass.getClassId() == activeClassId)
								player._classIndex = subClass.getClassIndex();
					}
				}
				if (player.getClassIndex() == 0 && activeClassId != player.getBaseClass())
				{
					// Subclass in use but doesn't exist in DB -
					// a possible restart-while-modifysubclass cheat has been attempted.
					// Switching to use base class
					player.setClassId(player.getBaseClass());
					_log.warning("Player " + player.getName() + " reverted to base class. Possibly has tried a relogin exploit while subclassing.");
				}
				else
					player._activeClass = activeClassId;
				
				player.setApprentice(rset.getInt("apprentice"));
				player.setSponsor(rset.getInt("sponsor"));
				player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
				player.setIsIn7sDungeon(rset.getInt("isin7sdungeon") == 1);
				player.setPunishLevel(rset.getInt("punish_level"));
				if (player.getPunishLevel() != PunishLevel.NONE)
					player.setPunishTimer(rset.getLong("punish_timer"));
				else
					player.setPunishTimer(0);
				
				CursedWeaponsManager.getInstance().checkPlayer(player);
				
				player.setAllianceWithVarkaKetra(rset.getInt("varka_ketra_ally"));
				
				player.setDeathPenaltyBuffLevel(rset.getInt("death_penalty_level"));
				
				player.setPcBang(rset.getInt("pc_point"));
				
				player.getAppearance().setNameColor(Integer.decode((new StringBuilder()).append("0x").append(rset.getString("name_color")).toString()).intValue());
				player.getAppearance().setTitleColor(Integer.decode((new StringBuilder()).append("0x").append(rset.getString("title_color")).toString()).intValue());
				
				player.setVip(rset.getInt("vip") == 1 ? true : false);
				player.setVipEndTime(rset.getLong("vip_end"));
				
				final int fakeWeapon = rset.getInt("fakeWeaponObjectId");
				final ItemInstance weaponItem = player.getInventory().getItemByObjectId(fakeWeapon);
				
				player.setFakeWeaponObjectId(weaponItem != null ? fakeWeapon : 0);
				player.setFakeWeaponItemId(weaponItem != null ? weaponItem.getItemId() : 0);
				
				player.setAio(rset.getInt("aio") == 1 ? true : false);
				player.setAioEndTime(rset.getLong("aio_end"));
				
				// Set the x,y,z position of the L2PcInstance and make it invisible
				player.setXYZInvisible(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
				
				// Set Hero status if it applies
				if (Hero.getInstance().isActiveHero(objectId))
					player.setHero(true);
				
				// Set pledge class rank.
				player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
				
				// Retrieve from the database all secondary data of this L2PcInstance and reward expertise/lucky skills if necessary.
				// Note that Clan, Noblesse and Hero skills are given separately and not here.
				player.restoreCharData();
				player.rewardSkills();
				
				// buff and status icons
				if (Config.STORE_SKILL_COOLTIME)
					player.restoreEffects();
				
				// Restore current CP, HP and MP values
				final double currentHp = rset.getDouble("curHp");
				
				player.setCurrentCp(rset.getDouble("curCp"));
				player.setCurrentHp(currentHp);
				player.setCurrentMp(rset.getDouble("curMp"));
				
				if (currentHp < 0.5)
				{
					player.setIsDead(true);
					player.stopHpMpRegeneration();
				}
				
				// Restore pet if exists in the world
				player.setPet(L2World.getInstance().getPet(player.getObjectId()));
				if (player.getPet() != null)
					player.getPet().setOwner(player);
				
				player.refreshOverloaded();
				player.refreshExpertisePenalty();
				
				player.restoreFriendList();
				
				PlayerVariables.loadVariables(player);
				DailyRewardManager.getInstance().onPlayerEnter(player);
				
				// Retrieve the name and ID of the other characters assigned to this account.
				PreparedStatement stmt = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id<>?");
				stmt.setString(1, player._accountName);
				stmt.setInt(2, objectId);
				ResultSet chars = stmt.executeQuery();
				
				while (chars.next())
					player.getAccountChars().put(chars.getInt("obj_Id"), chars.getString("char_name"));
				
				chars.close();
				stmt.close();
				break;
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.severe("Could not restore char data: " + e);
		}
		
		return player;
	}
	
	public void registerAccount()
	{
		// ===============================
		// ACCOUNT AUTO CREATE (SAFE)
		// ===============================
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement ps = con.prepareStatement(AUTOCREATE_ACCOUNTS_INSERT);
			
			String rawPass = generateSecurePassword();
			String hash = hashPassword(rawPass);
			
			ps.setString(1, _accountName);
			ps.setString(2, hash);
			ps.setLong(3, System.currentTimeMillis());
			ps.setInt(4, -1);
			
			ps.execute();
		}
		catch (Exception e)
		{
			_log.severe("Could not create fake account: " + e);
		}
	}
	
	private static String hashPassword(String password) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("SHA");
		return Base64.getEncoder().encodeToString(md.digest(password.getBytes(StandardCharsets.UTF_8)));
	}
	
	private static String generateSecurePassword()
	{
		byte[] bytes = new byte[32];
		new SecureRandom().nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	@Override
	public void setPartyInvite(Player requestor, Player target)
	{
		if (Rnd.get(100) > 50)
		{
			target.setActiveRequester(null);
			if (requestor.isInParty())
				requestor.getParty().setPendingInvitation(false);
			requestor.onTransactionResponse();
			
			if (Rnd.get(100) > 50)
			{
				
			}
			else
			{
				Player targetsys = L2World.getInstance().getPlayer(requestor.getName());
				if (targetsys == null)
					return;
				
				targetsys.sendPacket(new CreatureSay(getObjectId(), Say2.TELL, getName(), "?"));
				
				sendPacket(new CreatureSay(getObjectId(), Say2.TELL, "->" + targetsys.getName(), "?"));
			}
		}
		else
			FakePartyManager.getInstance().requestPlayer(requestor, (FakePlayer) target);
	}
}
