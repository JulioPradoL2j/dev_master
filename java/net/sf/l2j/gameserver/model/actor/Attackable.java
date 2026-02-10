package net.sf.l2j.gameserver.model.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.clan.ranking.ClanRankingConfig;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.dropdiary.DropDiaryManager;
import net.sf.l2j.event.partyfarm.PartyFarm;
import net.sf.l2j.event.partyfarm.PartyZoneReward;
import net.sf.l2j.event.rewardsoloevent.RewardSoloEvent;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.model.L2AttackableAI;
import net.sf.l2j.gameserver.ai.model.L2CharacterAI;
import net.sf.l2j.gameserver.ai.model.L2SiegeGuardAI;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.xml.HerbDropData;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.model.AbsorbInfo;
import net.sf.l2j.gameserver.model.AggroInfo;
import net.sf.l2j.gameserver.model.Announcement;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2CommandChannel;
import net.sf.l2j.gameserver.model.L2Manor;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.RewardInfo;
import net.sf.l2j.gameserver.model.actor.instance.L2SummonInstance;
import net.sf.l2j.gameserver.model.actor.knownlist.AttackableKnownList;
import net.sf.l2j.gameserver.model.actor.status.AttackableStatus;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.zone.type.L2TimeFarmZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scriptings.EventType;
import net.sf.l2j.gameserver.scriptings.Quest;
import net.sf.l2j.gameserver.scripts.EventDroplist;
import net.sf.l2j.gameserver.scripts.EventDroplist.DateDrop;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.timezone.TimeFarmZoneManager;

/**
 * This class manages all NPC that can be attacked. It inherits from L2Npc.
 */
public class Attackable extends L2Npc
{
	private final Set<Creature> _attackByList = ConcurrentHashMap.newKeySet();
	
	private final Map<Creature, AggroInfo> _aggroList = new ConcurrentHashMap<>();
	private final Map<Integer, AbsorbInfo> _absorbersList = new ConcurrentHashMap<>();
	
	private final List<IntIntHolder> _sweepItems = new ArrayList<>();
	private final List<IntIntHolder> _harvestItems = new ArrayList<>();
	
	private boolean _isRaid;
	private boolean _isRaidMinion;
	private boolean _isReturningToSpawnPoint;
	private boolean _seeThroughSilentMove;
	
	private int _seedType;
	private int _seederObjId;
	
	private boolean _overhit;
	private double _overhitDamage;
	private Creature _overhitAttacker;
	
	private L2CommandChannel _firstCommandChannelAttacked;
	private CommandChannelTimer _commandChannelTimer;
	private long _commandChannelLastAttack;
	
	public Attackable(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new AttackableKnownList(this));
	}
	
	@Override
	public AttackableKnownList getKnownList()
	{
		return (AttackableKnownList) super.getKnownList();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new AttackableStatus(this));
	}
	
	@Override
	public AttackableStatus getStatus()
	{
		return (AttackableStatus) super.getStatus();
	}
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai;
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2AttackableAI(this);
				
				return _ai;
			}
		}
		return ai;
	}
	
	/**
	 * Reduce the current HP of the L2Attackable.
	 * @param damage The HP decrease value
	 * @param attacker The L2Character who attacks
	 */
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
		reduceCurrentHp(damage, attacker, true, false, skill);
	}
	
	/**
	 * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.
	 * @param attacker The L2Character who attacks
	 * @param awake The awake state (If True : stop sleeping)
	 */
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (isRaid() && !isMinion() && attacker != null && attacker.getParty() != null && attacker.getParty().isInCommandChannel() && attacker.getParty().getCommandChannel().meetRaidWarCondition(this))
		{
			if (_firstCommandChannelAttacked == null) // looting right isn't set
			{
				synchronized (this)
				{
					if (_firstCommandChannelAttacked == null)
					{
						_firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
						if (_firstCommandChannelAttacked != null)
						{
							_commandChannelTimer = new CommandChannelTimer(this);
							_commandChannelLastAttack = System.currentTimeMillis();
							ThreadPool.schedule(_commandChannelTimer, 10000); // check for last attack
							_firstCommandChannelAttacked.broadcastToChannelMembers(new CreatureSay(0, Say2.PARTYROOM_ALL, "", "You have looting rights!")); // TODO: retail msg
						}
					}
				}
			}
			else if (attacker.getParty().getCommandChannel().equals(_firstCommandChannelAttacked)) // is in same channel
				_commandChannelLastAttack = System.currentTimeMillis(); // update last attack time
		}
		
		// Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList
		addDamage(attacker, (int) damage, skill);
		
		// Reduce the current HP of the L2Attackable and launch the doDie Task if necessary
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
	}
	
	/**
	 * Kill the L2Attackable (the corpse disappeared after 7 seconds), distribute rewards (EXP, SP, Drops...) and notify Quest Engine.
	 * <ul>
	 * <li>Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members</li>
	 * <li>Notify the Quest Engine of the L2Attackable death if necessary</li>
	 * <li>Kill the L2Npc (the corpse disappeared after 7 seconds)</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT>
	 * @param killer The L2Character that has killed the L2Attackable
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		// Kill the L2Npc (the corpse disappeared after 7 seconds)
		if (!super.doDie(killer))
			return false;
		
		// Notify the Quest Engine of the L2Attackable death if necessary
		try
		{
			Player player = null;
			
			if (killer != null)
				player = killer.getActingPlayer();
			
			if (player != null)
			{
				
				List<Quest> quests = getTemplate().getEventQuests(EventType.ON_KILL);
				if (quests != null)
					for (Quest quest : quests)
						ThreadPool.schedule(new OnKillNotifyTask(this, quest, player, killer instanceof L2Summon), 3000);
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
		
		_attackByList.clear();
		
		return true;
	}
	
	/**
	 * Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members.
	 * <ul>
	 * <li>Get the L2PcInstance owner of the L2SummonInstance (if necessary) and L2Party in progress</li>
	 * <li>Calculate the Experience and SP rewards in function of the level difference</li>
	 * <li>Add Exp and SP rewards to L2PcInstance (including Summon penalty) and to Party members in the known area of the last attacker</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT>
	 * @param lastAttacker The L2Character that has killed the L2Attackable
	 */
	@Override
	protected void calculateRewards(Creature lastAttacker)
	{
		if (_aggroList.isEmpty())
			return;
		
		// Creates an empty list of rewards.
		final Map<Creature, RewardInfo> rewards = new ConcurrentHashMap<>();
		
		Player maxDealer = null;
		int maxDamage = 0;
		long totalDamage = 0;
		
		// Go through the _aggroList of the L2Attackable.
		for (AggroInfo info : _aggroList.values())
		{
			if (info == null)
				continue;
			
			// Get the L2Character corresponding to this attacker.
			final Player attacker = info.getAttacker().getActingPlayer();
			if (attacker == null)
				continue;
			
			// Get damages done by this attacker.
			final int damage = info.getDamage();
			if (damage <= 1)
				continue;
			
			// Check if attacker isn't too far from this.
			if (!Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, attacker, true))
				continue;
			
			totalDamage += damage;
			
			// Calculate real damages (Summoners should get own damage plus summon's damage).
			RewardInfo reward = rewards.get(attacker);
			if (reward == null)
			{
				reward = new RewardInfo(attacker);
				rewards.put(attacker, reward);
			}
			reward.addDamage(damage);
			
			if (reward.getDamage() > maxDamage)
			{
				maxDealer = attacker;
				maxDamage = reward.getDamage();
			}
		}
		
		// Manage Base, Quests and Sweep drops of the L2Attackable.
		doItemDrop(maxDealer != null && maxDealer.isOnline() ? maxDealer : lastAttacker);
		
		// Manage drop of Special Events created by GM for a defined period
		doEventDrop(maxDealer != null && maxDealer.isOnline() ? maxDealer : lastAttacker);
		
		for (RewardInfo reward : rewards.values())
		{
			if (reward == null)
				continue;
			
			// Attacker to be rewarded.
			final Player attacker = reward.getAttacker();
			
			// Total amount of damage done.
			final int damage = reward.getDamage();
			
			// Get party.
			final L2Party attackerParty = attacker.getParty();
			
			// Penalty applied to the attacker's XP
			final float penalty = attacker.hasServitor() ? ((L2SummonInstance) attacker.getPet()).getExpPenalty() : 0;
			
			// If there's NO party in progress.
			if (attackerParty == null)
			{
				// Calculate Exp and SP rewards.
				if (attacker.getKnownList().knowsObject(this) && !attacker.isDead())
				{
					// Calculate the difference of level between this attacker and the L2Attackable.
					final int levelDiff = attacker.getLevel() - getLevel();
					
					final int[] expSp = calculateExpAndSp(levelDiff, damage, totalDamage);
					long exp = expSp[0];
					int sp = expSp[1];
					
					if (isChampion())
					{
						exp *= Config.CHAMPION_REWARDS;
						sp *= Config.CHAMPION_REWARDS;
					}
					if (attacker.isVip())
					{
						exp *= Config.VIP_XP_SP_RATE;
						sp *= Config.VIP_XP_SP_RATE;
					}
					
					// AQUI: aplica redução se estiver chat banido
					if (attacker.isChatBanned())
					{
						exp /= 2;
						sp /= 2;
					}
					
					exp *= 1 - penalty;
					
					if (isOverhit() && _overhitAttacker != null && _overhitAttacker.getActingPlayer() != null && attacker == _overhitAttacker.getActingPlayer())
					{
						attacker.sendPacket(SystemMessageId.OVER_HIT);
						exp += calculateOverhitExp(exp);
					}
					
					// Set new karma.
					attacker.updateKarmaLoss(exp);
					
					// Distribute the Exp and SP between the L2PcInstance and its L2Summon.
					attacker.addExpAndSp(exp, sp);
					
					if (attacker.getPet() != null)
					{
						L2Summon pet = attacker.getPet();
						
						if (pet != null)
						{
							pet.getStat().setExp(exp / 2);
							pet.getStat().setSp(sp / 2);
						}
					}
					
				}
			}
			// Share with party members.
			else
			{
				int partyDmg = 0;
				float partyMul = 1;
				int partyLvl = 0;
				
				// Get all L2Character that can be rewarded in the party.
				final List<Player> rewardedMembers = new ArrayList<>();
				
				// Go through all L2PcInstance in the party.
				final List<Player> groupMembers = (attackerParty.isInCommandChannel()) ? attackerParty.getCommandChannel().getMembers() : attackerParty.getPartyMembers();
				
				for (Player partyPlayer : groupMembers)
				{
					if (partyPlayer == null || partyPlayer.isDead())
						continue;
					
					// Get the RewardInfo of this L2PcInstance from L2Attackable rewards
					final RewardInfo reward2 = rewards.get(partyPlayer);
					
					// If the L2PcInstance is in the L2Attackable rewards add its damages to party damages
					if (reward2 != null)
					{
						if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true))
						{
							partyDmg += reward2.getDamage(); // Add L2PcInstance damages to party damages
							rewardedMembers.add(partyPlayer);
							
							if (partyPlayer.getLevel() > partyLvl)
								partyLvl = (attackerParty.isInCommandChannel()) ? attackerParty.getCommandChannel().getLevel() : partyPlayer.getLevel();
						}
						rewards.remove(partyPlayer); // Remove the L2PcInstance from the L2Attackable rewards
					}
					// Add L2PcInstance of the party (that have attacked or not) to members that can be rewarded and in range of the monster.
					else
					{
						if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true))
						{
							rewardedMembers.add(partyPlayer);
							if (partyPlayer.getLevel() > partyLvl)
								partyLvl = (attackerParty.isInCommandChannel()) ? attackerParty.getCommandChannel().getLevel() : partyPlayer.getLevel();
						}
					}
				}
				
				// If the party didn't killed this L2Attackable alone
				if (partyDmg < totalDamage)
					partyMul = ((float) partyDmg / totalDamage);
				
				// Calculate the level difference between Party and L2Attackable
				final int levelDiff = partyLvl - getLevel();
				
				// Calculate Exp and SP rewards
				final int[] expSp = calculateExpAndSp(levelDiff, partyDmg, totalDamage);
				long exp = expSp[0];
				int sp = expSp[1];
				
				if (isChampion())
				{
					exp *= Config.CHAMPION_REWARDS;
					sp *= Config.CHAMPION_REWARDS;
				}
				if (attacker.isVip())
				{
					exp *= Config.VIP_XP_SP_RATE;
					sp *= Config.VIP_XP_SP_RATE;
				}
				// AQUI: aplica redução se estiver chat banido
				if (attacker.isChatBanned())
				{
					exp /= 2;
					sp /= 2;
				}
				
				exp *= partyMul;
				sp *= partyMul;
				
				// Check for an over-hit enabled strike
				// (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
				if (isOverhit() && _overhitAttacker != null && _overhitAttacker.getActingPlayer() != null && attacker == _overhitAttacker.getActingPlayer())
				{
					attacker.sendPacket(SystemMessageId.OVER_HIT);
					exp += calculateOverhitExp(exp);
				}
				
				// Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker
				if (partyDmg > 0)
					attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl);
				
				if (attacker.getPet() != null)
				{
					L2Summon pet = attacker.getPet();
					
					if (pet != null)
					{
						pet.getStat().setExp(exp / 2);
						pet.getStat().setSp(sp / 2);
					}
				}
			}
		}
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// Clear mob spoil/seed state
		setSpoilerId(0);
		
		// Clear all aggro char from list
		_aggroList.clear();
		
		// Clear Harvester Reward List
		_harvestItems.clear();
		
		// Clear mod Seeded stat
		_seedType = 0;
		_seederObjId = 0;
		
		// Clear overhit value
		overhitEnabled(false);
		
		_sweepItems.clear();
		resetAbsorberList();
		
		setWalking();
		
		// check the region where this mob is, do not activate the AI if region is inactive.
		if (!isInActiveRegion())
		{
			if (hasAI())
				getAI().stopAITask();
		}
	}
	
	/**
	 * Check if the server allows Random Animation.<BR>
	 * <BR>
	 * This is located here because L2Monster and L2FriendlyMob both extend this class. The other non-pc instances extend either L2Npc or L2MonsterInstance.
	 */
	@Override
	public boolean hasRandomAnimation()
	{
		return Config.MAX_MONSTER_ANIMATION > 0 && !isRaid();
	}
	
	@Override
	public boolean isMob()
	{
		return true; // This means we use MAX_MONSTER_ANIMATION instead of MAX_NPC_ANIMATION
	}
	
	@Override
	public boolean isRaid()
	{
		return _isRaid;
	}
	
	@Override
	public void setIsRaid(boolean isRaid)
	{
		_isRaid = isRaid;
	}
	
	@Override
	public boolean isRaidMinion()
	{
		return _isRaidMinion;
	}
	
	/**
	 * Set this Npc as a Minion instance.
	 * @param val
	 */
	public void setIsRaidMinion(boolean val)
	{
		_isRaid = val;
		_isRaidMinion = val;
	}
	
	@Override
	public boolean isMinion()
	{
		return getLeader() != null;
	}
	
	public void addAttackerToAttackByList(Creature player)
	{
		if (player == null || player == this)
			return;
		
		_attackByList.add(player);
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage The number of damages given by the attacker L2Character
	 * @param skill The skill used to make damage.
	 */
	public void addDamage(Creature attacker, int damage, L2Skill skill)
	{
		if (attacker == null || isDead())
			return;
		
		// Notify the L2Attackable AI with EVT_ATTACKED
		Player player = attacker.getActingPlayer();
		if (player != null)
		{
			List<Quest> quests = getTemplate().getEventQuests(EventType.ON_ATTACK);
			if (quests != null)
				for (Quest quest : quests)
					quest.notifyAttack(this, player, damage, attacker instanceof L2Summon, skill);
		}
		// for now hard code damage hate caused by an L2Attackable
		else
		{
			getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
			addDamageHate(attacker, damage, (damage * 100) / (getLevel() + 7));
		}
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage The number of damages given by the attacker L2Character
	 * @param aggro The hate (=damage) given by the attacker L2Character
	 */
	public void addDamageHate(Creature attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;
		
		// Get or create the AggroInfo of the attacker.
		final AggroInfo ai = _aggroList.computeIfAbsent(attacker, AggroInfo::new);
		ai.addDamage(damage);
		ai.addHate(aggro);
		
		if (aggro == 0)
		{
			final Player targetPlayer = attacker.getActingPlayer();
			if (targetPlayer != null)
			{
				List<Quest> quests = getTemplate().getEventQuests(EventType.ON_AGGRO);
				if (quests != null)
					for (Quest quest : quests)
						quest.notifyAggro(this, targetPlayer, (attacker instanceof L2Summon));
			}
			else
			{
				aggro = 1;
				ai.addHate(1);
			}
		}
		else
		{
			// Set the intention to the L2Attackable to ACTIVE
			if (aggro > 0 && getAI().getIntention() == CtrlIntention.IDLE)
				getAI().setIntention(CtrlIntention.ACTIVE);
		}
	}
	
	/**
	 * Reduce hate for the target. If the target is null, decrease the hate for the whole aggrolist.
	 * @param target The target to check.
	 * @param amount The amount to remove.
	 */
	public void reduceHate(Creature target, int amount)
	{
		if (getAI() instanceof L2SiegeGuardAI)
		{
			stopHating(target);
			setTarget(null);
			getAI().setIntention(CtrlIntention.IDLE);
			return;
		}
		
		if (target == null) // whole aggrolist
		{
			Creature mostHated = getMostHated();
			
			// If not most hated target is found, makes AI passive for a moment more
			if (mostHated == null)
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				return;
			}
			
			for (AggroInfo ai : _aggroList.values())
				ai.addHate(-amount);
			
			amount = getHating(mostHated);
			
			if (amount <= 0)
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				_aggroList.clear();
				getAI().setIntention(CtrlIntention.ACTIVE);
				setWalking();
			}
			return;
		}
		
		AggroInfo ai = _aggroList.get(target);
		if (ai == null)
			return;
		
		ai.addHate(-amount);
		
		if (ai.getHate() <= 0)
		{
			if (getMostHated() == null)
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				_aggroList.clear();
				getAI().setIntention(CtrlIntention.ACTIVE);
				setWalking();
			}
		}
	}
	
	/**
	 * Clears _aggroList hate of the L2Character without removing from the list.
	 * @param target The target to clean from that L2Attackable _aggroList.
	 */
	public void stopHating(Creature target)
	{
		if (target == null)
			return;
		
		AggroInfo ai = _aggroList.get(target);
		if (ai != null)
			ai.stopHate();
	}
	
	/**
	 * @return the most hated L2Character of the L2Attackable _aggroList.
	 */
	public Creature getMostHated()
	{
		if (_aggroList.isEmpty() || isAlikeDead())
			return null;
		
		Creature mostHated = null;
		int maxHate = 0;
		
		// Go through the aggroList of the L2Attackable
		for (AggroInfo ai : _aggroList.values())
		{
			if (ai.checkHate(this) > maxHate)
			{
				mostHated = ai.getAttacker();
				maxHate = ai.getHate();
			}
		}
		return mostHated;
	}
	
	/**
	 * @return the list of hated L2Character. It also make checks, setting hate to 0 following conditions.
	 */
	public List<Creature> getHateList()
	{
		if (_aggroList.isEmpty() || isAlikeDead())
			return Collections.emptyList();
		
		final List<Creature> result = new ArrayList<>();
		for (AggroInfo ai : _aggroList.values())
		{
			ai.checkHate(this);
			result.add(ai.getAttacker());
		}
		return result;
	}
	
	/**
	 * @param target The L2Character whose hate level must be returned
	 * @return the hate level of the L2Attackable against this L2Character contained in _aggroList.
	 */
	public int getHating(final Creature target)
	{
		if (_aggroList.isEmpty() || target == null)
			return 0;
		
		final AggroInfo ai = _aggroList.get(target);
		if (ai == null)
			return 0;
		
		if (ai.getAttacker() instanceof Player && ((Player) ai.getAttacker()).getAppearance().getInvisible())
		{
			// Remove Object Should Use This Method and Can be Blocked While Interating
			_aggroList.remove(target);
			return 0;
		}
		
		if (!ai.getAttacker().isVisible())
		{
			_aggroList.remove(target);
			return 0;
		}
		
		if (ai.getAttacker().isAlikeDead())
		{
			ai.stopHate();
			return 0;
		}
		return ai.getHate();
	}
	
	/**
	 * Calculates quantity of items for specific drop acording to current situation.
	 * @param drop The L2DropData count is being calculated for
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @param isSweep if true, use spoil drop chance.
	 * @return the ItemHolder.
	 */
	private IntIntHolder calculateRewardItem(Player lastAttacker, DropData drop, int levelModifier, boolean isSweep)
	{
		// Get default drop chance
		double dropChance = drop.getChance();
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int deepBlueDrop = 1;
			if (levelModifier > 0)
			{
				// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
				// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
				deepBlueDrop = 3;
				if (drop.getItemId() == 57)
				{
					deepBlueDrop *= isRaid() && !isRaidMinion() ? (int) Config.RATE_DROP_ITEMS_BY_RAID : (int) Config.RATE_DROP_ITEMS;
					if (deepBlueDrop == 0) // avoid div by 0
						deepBlueDrop = 1;
				}
			}
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			dropChance = ((drop.getChance() - ((drop.getChance() * levelModifier) / 100)) / deepBlueDrop);
		}
		
		// Applies Drop rates
		if (drop.getItemId() == 57)
		{
			dropChance *= Config.RATE_DROP_ADENA;
			if (lastAttacker.isVip())
				dropChance *= Config.VIP_ADENA_RATE;
		}
		else if (isSweep)
		{
			dropChance *= Config.RATE_DROP_SPOIL;
			if (lastAttacker.isVip())
				dropChance *= Config.VIP_SPOIL_RATE;
		}
		else
			dropChance *= isRaid() && !isRaidMinion() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;
		if (lastAttacker.isVip() && !isRaid() && !(drop.getItemId() == 57))
			dropChance *= Config.VIP_DROP_RATE;
		
		if (isChampion())
			dropChance *= Config.CHAMPION_REWARDS;
		
		// Set our limits for chance of drop
		if (dropChance < 1)
			dropChance = 1;
		
		// Get min and max Item quantity that can be dropped in one time
		final int minCount = drop.getMinDrop();
		final int maxCount = drop.getMaxDrop();
		
		// Get the item quantity dropped
		int itemCount = 0;
		
		// Check if the Item must be dropped
		int random = Rnd.get(DropData.MAX_CHANCE);
		while (random < dropChance)
		{
			// Get the item quantity dropped
			if (minCount < maxCount)
				itemCount += Rnd.get(minCount, maxCount);
			else if (minCount == maxCount)
				itemCount += minCount;
			else
				itemCount++;
			
			// Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
			dropChance -= DropData.MAX_CHANCE;
		}
		
		if (isChampion())
		{
			if (drop.getItemId() == 57 || (drop.getItemId() >= 6360 && drop.getItemId() <= 6362))
			{
				itemCount *= Config.CHAMPION_ADENAS_REWARDS;
			}
		}
		// Aplica redução de 50% para jogadores com chat ban ativo
		if (lastAttacker.isChatBanned())
		{
			itemCount = (int) Math.floor(itemCount * 0.5);
		}
		
		if (drop.getItemId() >= 6360 && drop.getItemId() <= 6362)
		{
			itemCount *= Config.RATE_DROP_SEAL_STONES;
		}
		
		if (itemCount > 0)
		{
			if (!isRaid())
			{
				if (!DropDiaryManager.canDrop(drop.getItemId()))
				{
					// String itemName = ItemTable.getInstance().getTemplate(drop.getItemId()).getName();
					// lastAttacker.sendMessage("The item \"" + itemName + "\" has reached its drop limit and will no longer drop until the next server restart.");
					return null;
				}
				
				DropDiaryManager.recordDrop(drop.getItemId(), itemCount);
			}
			
			return new IntIntHolder(drop.getItemId(), itemCount, drop.getEnchant(), drop.getEnchantChance());
		}
		
		return null;
	}
	
	/**
	 * Calculates quantity of items for specific drop CATEGORY according to current situation <br>
	 * Only a max of ONE item from a category is allowed to be dropped.
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param categoryDrops The category to make checks on.
	 * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @return the ItemHolder.
	 */
	private IntIntHolder calculateCategorizedRewardItem(Player lastAttacker, DropCategory categoryDrops, int levelModifier)
	{
		if (categoryDrops == null)
			return null;
			
		// Get default drop chance for the category (that's the sum of chances for all items in the category)
		// keep track of the base category chance as it'll be used later, if an item is drop from the category.
		// for everything else, use the total "categoryDropChance"
		int basecategoryDropChance = categoryDrops.getCategoryChance();
		int categoryDropChance = basecategoryDropChance;
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int deepBlueDrop = (levelModifier > 0) ? 3 : 1;
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			categoryDropChance = ((categoryDropChance - ((categoryDropChance * levelModifier) / 100)) / deepBlueDrop);
		}
		
		// Applies Drop rates
		categoryDropChance *= isRaid() && !isRaidMinion() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;
		if (lastAttacker.isVip() && !isRaid())
			categoryDropChance *= Config.VIP_DROP_RATE;
		
		if (isChampion())
			categoryDropChance *= Config.CHAMPION_REWARDS;
		
		// Set our limits for chance of drop
		if (categoryDropChance < 1)
			categoryDropChance = 1;
		
		// Check if an Item from this category must be dropped
		if (Rnd.get(DropData.MAX_CHANCE) < categoryDropChance)
		{
			DropData drop = categoryDrops.dropOne(isRaid() && !isRaidMinion());
			if (drop == null)
				return null;
				
			// Now decide the quantity to drop based on the rates and penalties. To get this value
			// simply divide the modified categoryDropChance by the base category chance. This
			// results in a chance that will dictate the drops amounts: for each amount over 100
			// that it is, it will give another chance to add to the min/max quantities.
			//
			// For example, If the final chance is 120%, then the item should drop between
			// its min and max one time, and then have 20% chance to drop again. If the final
			// chance is 330%, it will similarly give 3 times the min and max, and have a 30%
			// chance to give a 4th time.
			// At least 1 item will be dropped for sure. So the chance will be adjusted to 100%
			// if smaller.
			
			double dropChance = drop.getChance();
			if (drop.getItemId() == 57)
			{
				dropChance *= Config.RATE_DROP_ADENA;
				if (lastAttacker.isVip())
					dropChance *= Config.VIP_ADENA_RATE;
			}
			else
				dropChance *= isRaid() && !isRaidMinion() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;
			if (lastAttacker.isVip() && !isRaid() && !(drop.getItemId() == 57))
				dropChance *= Config.VIP_DROP_RATE;
			
			if (isChampion())
				dropChance *= Config.CHAMPION_REWARDS;
			
			if (dropChance < DropData.MAX_CHANCE)
				dropChance = DropData.MAX_CHANCE;
			
			// Get min and max Item quantity that can be dropped in one time
			final int min = drop.getMinDrop();
			final int max = drop.getMaxDrop();
			
			// Get the item quantity dropped
			int itemCount = 0;
			
			// Check if the Item must be dropped
			int random = Rnd.get(DropData.MAX_CHANCE);
			while (random < dropChance)
			{
				// Get the item quantity dropped
				if (min < max)
					itemCount += Rnd.get(min, max);
				else if (min == max)
					itemCount += min;
				else
					itemCount++;
				
				// Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
				dropChance -= DropData.MAX_CHANCE;
			}
			
			if (isChampion())
			{
				if (drop.getItemId() == 57 || (drop.getItemId() >= 6360 && drop.getItemId() <= 6362))
				{
					itemCount *= Config.CHAMPION_ADENAS_REWARDS;
				}
			}
			
			if (drop.getItemId() >= 6360 && drop.getItemId() <= 6362)
			{
				itemCount *= Config.RATE_DROP_SEAL_STONES;
			}
			if (lastAttacker.isChatBanned())
			{
				itemCount = (int) Math.floor(itemCount * 0.5);
			}
			
			if (itemCount > 0)
			{
				if (!isRaid())
				{
					if (!DropDiaryManager.canDrop(drop.getItemId()))
					{
						// String itemName = ItemTable.getInstance().getTemplate(drop.getItemId()).getName();
						// lastAttacker.sendMessage("The item \"" + itemName + "\" has reached its drop limit and will no longer drop until the next server restart.");
						return null;
					}
					
					DropDiaryManager.recordDrop(drop.getItemId(), itemCount);
				}
				
				return new IntIntHolder(drop.getItemId(), itemCount, drop.getEnchant(), drop.getEnchantChance());
			}
			
		}
		return null;
	}
	
	/**
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @return the level modifier for drop
	 */
	private int calculateLevelModifierForDrop(Player lastAttacker)
	{
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int highestLevel = lastAttacker.getLevel();
			
			// Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
			for (Creature atkChar : _attackByList)
				if (atkChar.getLevel() > highestLevel)
					highestLevel = atkChar.getLevel();
				
			// According to official data (Prima), deep blue mobs are 9 or more levels below players
			if (highestLevel - 9 >= getLevel())
				return ((highestLevel - (getLevel() + 8)) * 9);
		}
		return 0;
	}
	
	private static IntIntHolder calculateCategorizedHerbItem(DropCategory categoryDrops, int levelModifier)
	{
		if (categoryDrops == null)
			return null;
		
		int categoryDropChance = categoryDrops.getCategoryChance();
		
		// Applies Drop rates
		switch (categoryDrops.getCategoryType())
		{
			case 1:
				categoryDropChance *= Config.RATE_DROP_HP_HERBS;
				break;
			case 2:
				categoryDropChance *= Config.RATE_DROP_MP_HERBS;
				break;
			case 3:
				categoryDropChance *= Config.RATE_DROP_SPECIAL_HERBS;
				break;
			default:
				categoryDropChance *= Config.RATE_DROP_COMMON_HERBS;
		}
		
		// Drop chance is affected by deep blue drop rule.
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int deepBlueDrop = (levelModifier > 0) ? 3 : 1;
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			categoryDropChance = ((categoryDropChance - ((categoryDropChance * levelModifier) / 100)) / deepBlueDrop);
		}
		
		// Check if an Item from this category must be dropped
		if (Rnd.get(DropData.MAX_CHANCE) < Math.max(1, categoryDropChance))
		{
			final DropData drop = categoryDrops.dropOne(false);
			if (drop == null)
				return null;
			
			/*
			 * Now decide the quantity to drop based on the rates and penalties. To get this value, simply divide the modified categoryDropChance by the base category chance. This results in a chance that will dictate the drops amounts : for each amount over 100 that it is, it will give another
			 * chance to add to the min/max quantities. For example, if the final chance is 120%, then the item should drop between its min and max one time, and then have 20% chance to drop again. If the final chance is 330%, it will similarly give 3 times the min and max, and have a 30% chance to
			 * give a 4th time. At least 1 item will be dropped for sure. So the chance will be adjusted to 100% if smaller.
			 */
			double dropChance = drop.getChance();
			
			switch (categoryDrops.getCategoryType())
			{
				case 1:
					dropChance *= Config.RATE_DROP_HP_HERBS;
					break;
				case 2:
					dropChance *= Config.RATE_DROP_MP_HERBS;
					break;
				case 3:
					dropChance *= Config.RATE_DROP_SPECIAL_HERBS;
					break;
				default:
					dropChance *= Config.RATE_DROP_COMMON_HERBS;
			}
			
			if (dropChance < DropData.MAX_CHANCE)
				dropChance = DropData.MAX_CHANCE;
			
			// Get min and max Item quantity that can be dropped in one time
			final int min = drop.getMinDrop();
			final int max = drop.getMaxDrop();
			
			// Get the item quantity dropped
			int itemCount = 0;
			
			// Check if the Item must be dropped
			int random = Rnd.get(DropData.MAX_CHANCE);
			while (random < dropChance)
			{
				// Get the item quantity dropped
				if (min < max)
					itemCount += Rnd.get(min, max);
				else if (min == max)
					itemCount += min;
				else
					itemCount++;
				
				// Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
				dropChance -= DropData.MAX_CHANCE;
			}
			
			if (itemCount > 0)
				return new IntIntHolder(drop.getItemId(), itemCount);
		}
		return null;
	}
	
	public void doEventDrop(Creature lastAttacker)
	{
		Player player = null;
		
		if (lastAttacker instanceof Player)
		{
			player = (Player) lastAttacker;
		}
		else if (lastAttacker instanceof L2SummonInstance)
		{
			player = ((L2SummonInstance) lastAttacker).getOwner();
		}
		
		if (player == null)
			return; // Don't drop anything if the last attacker or ownere isn't L2PcInstance
			
		if (player.getLevel() - getLevel() > 9)
			return;
		
		// Go through DateDrop of EventDroplist allNpcDateDrops within the date range
		for (DateDrop drop : EventDroplist.getInstance().getAllDrops())
		{
			if (Rnd.get(DropData.MAX_CHANCE) < drop.chance)
			{
				IntIntHolder item = new IntIntHolder(drop.items[Rnd.get(drop.items.length)], Rnd.get(drop.min, drop.max));
				
				if (Config.AUTO_LOOT)
					player.doAutoLoot(this, item);
				else
					dropItem(player, item);
				
				item = null;
			}
		}
		
		player = null;
	}
	
	/**
	 * Manage Base & Quests drops of L2Attackable (called by calculateRewards).
	 * <ul>
	 * <li>Get all possible drops of this L2Attackable from L2NpcTemplate and add it Quest drops</li>
	 * <li>For each possible drops (base + quests), calculate which one must be dropped (random)</li>
	 * <li>Get each Item quantity dropped (random)</li>
	 * <li>Create this or these ItemInstance corresponding to each Item Identifier dropped</li>
	 * <li>If the autoLoot mode is actif and if the L2Character that has killed the L2Attackable is a L2PcInstance, give this or these Item(s) to the L2PcInstance that has killed the L2Attackable</li>
	 * <li>If the autoLoot mode isn't actif or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these Item(s) in the world as a visible object at the position where mob was last</li>
	 * </ul>
	 * @param mainDamageDealer The L2Character that made the most damage.
	 */
	public void doItemDrop(Creature mainDamageDealer)
	{
		if (ClanRankingConfig.ENABLE_CLAN_RANKING)
		{
			if (mainDamageDealer instanceof Player)
			{
				Player player = mainDamageDealer.getActingPlayer();
				if (player != null && ClanRankingConfig.CLAN_RANKING_BOSS_POINTS_MAIN_DAMAGE.containsKey(Integer.valueOf(getNpcId())))
				{
					L2Clan clan = player.getClan();
					if (clan != null)
					{
						int points = ClanRankingConfig.CLAN_RANKING_BOSS_POINTS_MAIN_DAMAGE.get(Integer.valueOf(getNpcId())).intValue();
						clan.addRankingBossPoints(points);
						
						if (ClanRankingConfig.ENABLE_ANNOUNCE_POINTS_EARNED_ONDAMAGE)
							Broadcast.gameAnnounceToOnlinePlayers("Clan " + clan.getName() + " gained " + points + " points by main damage on Raid!");
					}
				}
			}
		}
		doItemDrop(getTemplate(), mainDamageDealer);
	}
	
	public void doItemDrop(NpcTemplate npcTemplate, Creature mainDamageDealer)
	{
		if (mainDamageDealer == null)
			return;
		
		// Don't drop anything if the last attacker or owner isn't L2PcInstance
		Player player = mainDamageDealer.getActingPlayer();
		if (player == null)
			return;
		
		if (Config.PART_ZONE_MONSTERS_EVENT_ID.contains(Integer.valueOf(npcTemplate.getNpcId())) && PartyFarm.is_started())
		{
			PartyZoneReward.addPartyZoneReward(player, this);
		}
		
		if (Config.SOLO_FARM_BY_TIME_OF_DAY && RewardSoloEvent.is_started() && Config.LIST_NPC_CUSTOM_MONSTER.contains(Integer.valueOf(npcTemplate.getNpcId())))
		{
			for (int[] reward : Config.CUSTOM_MONSTER_DROP)
			{
				if (ItemTable.getInstance().createDummyItem(reward[0]).isStackable())
				{
					if (player.isVip())
						player.addItem("aCis", reward[0], (int) (Rnd.get(reward[1], reward[1] * 1) * Config.VIP_DROP_RATE), player, true);
					else
						player.addItem("aCis", reward[0], Rnd.get(reward[1], reward[1] * 1), player, true);
				}
			}
		}
		
		if (Config.CHAMPION_FARM_BY_TIME_OF_DAY && Config.LIST_NPC_CHAMPION_MONSTER.contains(Integer.valueOf(npcTemplate.getNpcId())) && isChampionInvade())
		{
			for (int[] reward : Config.CHAMPION_INVADE_DROP)
			{
				if (ItemTable.getInstance().createDummyItem(reward[0]).isStackable())
				{
					if (player.isVip() && Config.VIP_REWARD_BONUS_INVADE)
						player.addItem("aCis", reward[0], (int) (Rnd.get(reward[1], reward[1] * 1) * Config.VIP_DROP_RATE), player, true);
					else
						player.addItem("aCis", reward[0], Rnd.get(reward[1], reward[1] * 1), player, true);
				}
			}
		}
		
		// level modifier in %'s (will be subtracted from drop chance)
		int levelModifier = calculateLevelModifierForDrop(player);
		
		CursedWeaponsManager.getInstance().checkDrop(this, player);
		
		// now throw all categorized drops and handle spoil.
		for (DropCategory cat : npcTemplate.getDropData())
		{
			IntIntHolder item = null;
			if (cat.isSweep())
			{
				if (getSpoilerId() != 0)
				{
					for (DropData drop : cat.getAllDrops())
					{
						item = calculateRewardItem(player, drop, levelModifier, true);
						if (item == null)
							continue;
						
						_sweepItems.add(item);
					}
				}
			}
			else
			{
				if (isSeeded())
				{
					DropData drop = cat.dropSeedAllowedDropsOnly();
					if (drop == null)
						continue;
					
					item = calculateRewardItem(player, drop, levelModifier, false);
				}
				else
					item = calculateCategorizedRewardItem(player, cat, levelModifier);
				
				if (item != null)
				{
					boolean isListed = Config.NO_AUTO_LOOT_MOBS.contains(getNpcId());
					
					// Check if the autoLoot mode is active
					if (!isListed && ((isRaid() && Config.AUTO_LOOT_RAID) || (!isRaid() && Config.AUTO_LOOT)))
						player.doAutoLoot(this, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
					else
						dropItem(player, item); // drop the item on the ground
						
					if (Config.ANNUNCIE_LISTID_DROP.contains(Integer.valueOf(item.getId())) && Config.ENABLE_ANNUNCIE_DROP_ITEMS)
					{
						Broadcast.GameAnnuncieDropItems("[CONGRATULATIONS]-->" + player.getName() + " Dropped " + ItemTable.getInstance().getTemplate(item.getId()).getName() + "!");
					}
					
					if (Config.ANNOUNCE_DROP_ITEM && isRaid() && !isRaidMinion())
					{
						if (!(item.getEnchant() > 0))
							Announcement.announceToPlayers(SystemMessage.getSystemMessage(SystemMessageId.S1_DIED_DROPPED_S3_S2).addCharName(this).addItemName(item.getId()).addNumber(item.getValue()));
						else if (item.getEnchant() > 0)
							Broadcast.DropBossCustom("Dropped " + " " + item.getValue() + " " + ItemTable.getInstance().getTemplate(item.getId()).getName() + " +" + item.getEnchant());
					}
					else
					// Broadcast message if RaidBoss was defeated
					if (isRaid() && !isRaidMinion())
						broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DIED_DROPPED_S3_S2).addCharName(this).addItemName(item.getId()).addNumber(item.getValue()));
				}
			}
		}
		
		// Apply special item drop for champions.
		if (isChampion() && Config.CHAMPION_REWARD > 0)
		{
			int dropChance = Config.CHAMPION_REWARD;
			
			// Apply level modifier, if any/wanted.
			if (Config.DEEPBLUE_DROP_RULES)
			{
				int deepBlueDrop = (levelModifier > 0) ? 3 : 1;
				
				// Check if we should apply our maths so deep blue mobs will not drop that easy.
				dropChance = ((Config.CHAMPION_REWARD - ((Config.CHAMPION_REWARD * levelModifier) / 100)) / deepBlueDrop);
			}
			
			if (Rnd.get(100) < dropChance)
			{
				final IntIntHolder item = new IntIntHolder(Config.CHAMPION_REWARD_ID, Math.max(1, Rnd.get(1, Config.CHAMPION_REWARD_QTY)));
				if (Config.AUTO_LOOT)
					player.addItem("ChampionLoot", item.getId(), item.getValue(), this, true);
				else
					dropItem(player, item);
			}
		}
		
		// Herbs.
		final int herbGroup = getTemplate().getDropHerbGroup();
		if (herbGroup > 0)
		{
			final List<DropCategory> drops = HerbDropData.getInstance().resolveDrops(getNpcId(), herbGroup);
			
			for (DropCategory cat : drops)
			{
				final IntIntHolder item = calculateCategorizedHerbItem(cat, levelModifier);
				if (item == null)
					continue;
				
				if (Config.AUTO_LOOT_HERBS)
				{
					player.addItem("Loot", item.getId(), 1, this, true);
					continue;
				}
				
				final int count = item.getValue();
				if (count > 1)
				{
					item.setValue(1);
					for (int i = 0; i < count; i++)
						dropItem(player, item);
				}
				else
				{
					dropItem(player, item);
				}
			}
		}
		
	}
	
	/**
	 * Drop reward item.
	 * @param mainDamageDealer The player who made highest damage.
	 * @param holder The ItemHolder.
	 * @return the dropped item instance.
	 */
	public ItemInstance dropItem(Player mainDamageDealer, IntIntHolder holder)
	{
		ItemInstance item = null;
		// Verifica se drop está em zona com proteção desativada
		boolean noDropProtection = false;
		for (L2TimeFarmZone zone : TimeFarmZoneManager.getZones())
		{
			if (zone.isInsideZone(this))
			{
				if (zone.isNoDropProtection())
				{
					noDropProtection = true;
					break;
				}
			}
		}
		for (int i = 0; i < holder.getValue(); i++)
		{
			// Init the dropped ItemInstance and add it in the world as a visible object at the position where mob was last
			item = ItemTable.getInstance().createItem("Loot", holder.getId(), holder.getValue(), mainDamageDealer, this);
			if (!noDropProtection)
				item.setDropProtection(mainDamageDealer.getObjectId(), isRaid());
			if (holder.getEnchant() > 0)
			{
				if (item.getItem() instanceof Weapon || item.getItem() instanceof Armor)
				{
					double chance = Rnd.get(1, 100);
					if (chance <= holder.getEnchantChance())
						item.setEnchantLevel(holder.getEnchant());
				}
			}
			item.dropMe(this, getX() + Rnd.get(-70, 70), getY() + Rnd.get(-70, 70), Math.max(getZ(), mainDamageDealer.getZ()) + 20);
			
			// If stackable, end loop as entire count is included in 1 instance of item
			if (item.isStackable() || !Config.MULTIPLE_ITEM_DROP)
				break;
		}
		return item;
	}
	
	public void useMagic(L2Skill skill)
	{
		if (skill == null || isAlikeDead())
			return;
		
		if (skill.isPassive())
			return;
		
		if (isCastingNow())
			return;
		
		if (isSkillDisabled(skill))
			return;
		
		if (getCurrentMp() < getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill))
			return;
		
		if (getCurrentHp() <= skill.getHpConsume())
			return;
		
		if (skill.isMagic())
		{
			if (isMuted())
				return;
		}
		else
		{
			if (isPhysicalMuted())
				return;
		}
		
		L2Object target = skill.getFirstOfTargetList(this);
		if (target == null)
			return;
		
		getAI().setIntention(CtrlIntention.CAST, skill, target);
	}
	
	public final boolean returnHome(boolean cleanAggro)
	{
		if (cleanAggro)
			_aggroList.clear();
		
		if (hasAI() && !isDead() && getMoveSpeed() > 0 && getSpawn() != null && !isInsideRadius(getSpawn().getLocX(), getSpawn().getLocY(), getDriftRange(), false))
		{
			setIsReturningToSpawnPoint(true);
			setWalking();
			getAI().setIntention(CtrlIntention.MOVE_TO, getSpawn().getLoc());
			return true;
		}
		return false;
	}
	
	public int getDriftRange()
	{
		return Config.MAX_DRIFT_RANGE;
	}
	
	public final Set<Creature> getAttackByList()
	{
		return _attackByList;
	}
	
	public final Map<Creature, AggroInfo> getAggroList()
	{
		return _aggroList;
	}
	
	public final boolean isReturningToSpawnPoint()
	{
		return _isReturningToSpawnPoint;
	}
	
	public final void setIsReturningToSpawnPoint(boolean value)
	{
		_isReturningToSpawnPoint = value;
	}
	
	public boolean canSeeThroughSilentMove()
	{
		return _seeThroughSilentMove;
	}
	
	public void seeThroughSilentMove(boolean val)
	{
		_seeThroughSilentMove = val;
	}
	
	/**
	 * @return the active weapon of this L2Attackable (= null).
	 */
	public ItemInstance getActiveWeapon()
	{
		return null;
	}
	
	/**
	 * @return true if a Dwarf used Spoil on the L2Attackable.
	 */
	public boolean isSpoiled()
	{
		return !_sweepItems.isEmpty();
	}
	
	/**
	 * @return list containing all ItemHolder that can be spoiled.
	 */
	public List<IntIntHolder> getSweepItems()
	{
		return _sweepItems;
	}
	
	/**
	 * @return list containing all ItemHolder that can be harvested.
	 */
	public List<IntIntHolder> getHarvestItems()
	{
		return _harvestItems;
	}
	
	/**
	 * Set the over-hit flag on the L2Attackable.
	 * @param status The status of the over-hit flag
	 */
	public void overhitEnabled(boolean status)
	{
		_overhit = status;
	}
	
	/**
	 * Set the over-hit values like the attacker who did the strike and the amount of damage done by the skill.
	 * @param attacker The L2Character who hit on the L2Attackable using the over-hit enabled skill
	 * @param damage The ammount of damage done by the over-hit enabled skill on the L2Attackable
	 */
	public void setOverhitValues(Creature attacker, double damage)
	{
		// Calculate the over-hit damage
		// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
		double overhitDmg = ((getCurrentHp() - damage) * (-1));
		if (overhitDmg < 0)
		{
			// we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
			// let's just clear all the over-hit related values
			overhitEnabled(false);
			_overhitDamage = 0;
			_overhitAttacker = null;
			return;
		}
		overhitEnabled(true);
		_overhitDamage = overhitDmg;
		_overhitAttacker = attacker;
	}
	
	/**
	 * @return the L2Character who hit on the L2Attackable using an over-hit enabled skill.
	 */
	public Creature getOverhitAttacker()
	{
		return _overhitAttacker;
	}
	
	/**
	 * @return the amount of damage done on the L2Attackable using an over-hit enabled skill.
	 */
	public double getOverhitDamage()
	{
		return _overhitDamage;
	}
	
	/**
	 * @return True if the L2Attackable was hit by an over-hit enabled skill.
	 */
	public boolean isOverhit()
	{
		return _overhit;
	}
	
	/**
	 * Adds an attacker that successfully absorbed the soul of this L2Attackable into the _absorbersList.
	 * @param user : The L2PcInstance who attacked the monster.
	 * @param crystal : The ItemInstance which was used to register.
	 */
	public void addAbsorber(Player user, ItemInstance crystal)
	{
		// If the L2Character attacker isn't already in the _absorbersList of this L2Attackable, add it
		AbsorbInfo ai = _absorbersList.get(user.getObjectId());
		if (ai == null)
		{
			// Create absorb info.
			_absorbersList.put(user.getObjectId(), new AbsorbInfo(crystal.getObjectId()));
		}
		else
		{
			// Add absorb info, unless already registered.
			if (!ai.isRegistered())
				ai.setItemId(crystal.getObjectId());
		}
	}
	
	public void registerAbsorber(Player user)
	{
		// Get AbsorbInfo for user.
		AbsorbInfo ai = _absorbersList.get(user.getObjectId());
		if (ai == null)
			return;
		
		// Check item being used and register player to mob's absorber list.
		if (user.getInventory().getItemByObjectId(ai.getItemId()) == null)
			return;
		
		// Register AbsorbInfo.
		if (!ai.isRegistered())
		{
			ai.setAbsorbedHpPercent((int) ((100 * getCurrentHp()) / getMaxHp()));
			ai.setRegistered(true);
		}
	}
	
	public void resetAbsorberList()
	{
		_absorbersList.clear();
	}
	
	public AbsorbInfo getAbsorbInfo(int npcObjectId)
	{
		return _absorbersList.get(npcObjectId);
	}
	
	/**
	 * Calculate the Experience and SP to distribute to attacker (L2PcInstance, L2SummonInstance or L2Party) of the L2Attackable.
	 * @param diff The difference of level between attacker (L2PcInstance, L2SummonInstance or L2Party) and the L2Attackable
	 * @param damage The damages given by the attacker (L2PcInstance, L2SummonInstance or L2Party)
	 * @param totalDamage The total damage done.
	 * @return an array consisting of xp and sp values.
	 */
	private int[] calculateExpAndSp(int diff, int damage, long totalDamage)
	{
		if (diff < -5)
			diff = -5;
		
		double xp = (double) getExpReward() * damage / totalDamage;
		double sp = (double) getSpReward() * damage / totalDamage;
		
		final L2Skill hpSkill = getSkill(4408);
		if (hpSkill != null)
		{
			xp *= hpSkill.getPower();
			sp *= hpSkill.getPower();
		}
		
		if (diff > 5) // formula revised May 07
		{
			double pow = Math.pow((double) 5 / 6, diff - 5);
			xp = xp * pow;
			sp = sp * pow;
		}
		
		if (xp <= 0)
		{
			xp = 0;
			sp = 0;
		}
		else if (sp <= 0)
			sp = 0;
		
		int[] tmp =
		{
			(int) xp,
			(int) sp
		};
		
		return tmp;
	}
	
	public long calculateOverhitExp(long normalExp)
	{
		// Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the L2Attackable
		double overhitPercentage = ((getOverhitDamage() * 100) / getMaxHp());
		
		// Over-hit damage percentages are limited to 25% max
		if (overhitPercentage > 25)
			overhitPercentage = 25;
			
		// Get the overhit exp bonus according to the above over-hit damage percentage
		// (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
		double overhitExp = ((overhitPercentage / 100) * normalExp);
		
		// Return the rounded ammount of exp points to be added to the player's normal exp reward
		return Math.round(overhitExp);
	}
	
	/**
	 * Sets state of the mob to seeded.
	 * @param objectId : The player object id to check.
	 */
	public void setSeeded(int objectId)
	{
		if (_seedType != 0 && _seederObjId == objectId)
		{
			int count = 1;
			
			for (int skillId : getTemplate().getSkills().keySet())
			{
				switch (skillId)
				{
					case 4303: // Strong type x2
						count *= 2;
						break;
					case 4304: // Strong type x3
						count *= 3;
						break;
					case 4305: // Strong type x4
						count *= 4;
						break;
					case 4306: // Strong type x5
						count *= 5;
						break;
					case 4307: // Strong type x6
						count *= 6;
						break;
					case 4308: // Strong type x7
						count *= 7;
						break;
					case 4309: // Strong type x8
						count *= 8;
						break;
					case 4310: // Strong type x9
						count *= 9;
						break;
				}
			}
			
			final int diff = (getLevel() - (L2Manor.getInstance().getSeedLevel(_seedType) - 5));
			if (diff > 0)
				count += diff;
			
			_harvestItems.add(new IntIntHolder(L2Manor.getInstance().getCropType(_seedType), count * Config.RATE_DROP_MANOR));
		}
	}
	
	/**
	 * Sets the seed parameters, but not the seed state.
	 * @param id - id of the seed.
	 * @param objectId - the player objectId who is sowing the seed.
	 */
	public void setSeeded(int id, int objectId)
	{
		if (_seedType == 0)
		{
			_seedType = id;
			_seederObjId = objectId;
		}
	}
	
	public int getSeederId()
	{
		return _seederObjId;
	}
	
	public int getSeedType()
	{
		return _seedType;
	}
	
	public boolean isSeeded()
	{
		return _seedType > 0;
	}
	
	protected void setCommandChannelTimer(CommandChannelTimer commandChannelTimer)
	{
		_commandChannelTimer = commandChannelTimer;
	}
	
	public CommandChannelTimer getCommandChannelTimer()
	{
		return _commandChannelTimer;
	}
	
	public L2CommandChannel getFirstCommandChannelAttacked()
	{
		return _firstCommandChannelAttacked;
	}
	
	public void setFirstCommandChannelAttacked(L2CommandChannel firstCommandChannelAttacked)
	{
		_firstCommandChannelAttacked = firstCommandChannelAttacked;
	}
	
	public long getCommandChannelLastAttack()
	{
		return _commandChannelLastAttack;
	}
	
	public void setCommandChannelLastAttack(long channelLastAttack)
	{
		_commandChannelLastAttack = channelLastAttack;
	}
	
	/**
	 * @return leader of this minion or null.
	 */
	public Attackable getLeader()
	{
		return null;
	}
	
	public boolean isGuard()
	{
		return false;
	}
	
	private static class CommandChannelTimer implements Runnable
	{
		private final Attackable _monster;
		
		public CommandChannelTimer(Attackable monster)
		{
			_monster = monster;
		}
		
		@Override
		public void run()
		{
			if ((System.currentTimeMillis() - _monster.getCommandChannelLastAttack()) > 900000)
			{
				_monster.setCommandChannelTimer(null);
				_monster.setFirstCommandChannelAttacked(null);
				_monster.setCommandChannelLastAttack(0);
			}
			else
				ThreadPool.schedule(this, 10000); // 10sec
		}
	}
	
	private static class OnKillNotifyTask implements Runnable
	{
		private final Attackable _attackable;
		private final Quest _quest;
		private final Player _killer;
		private final boolean _isPet;
		
		public OnKillNotifyTask(Attackable attackable, Quest quest, Player killer, boolean isPet)
		{
			_attackable = attackable;
			_quest = quest;
			_killer = killer;
			_isPet = isPet;
		}
		
		@Override
		public void run()
		{
			_quest.notifyKill(_attackable, _killer, _isPet);
		}
	}
	
	public ItemInstance dropItem(Player lastAttacker, int itemId, int itemCount)
	{
		return dropItem(lastAttacker, new IntIntHolder(itemId, itemCount));
	}
}