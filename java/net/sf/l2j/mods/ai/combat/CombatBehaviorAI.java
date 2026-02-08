package net.sf.l2j.mods.ai.combat;

import java.util.HashSet;
import java.util.Set;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.PrivateMessageManager.PrivateMessage;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.AbstractFakePlayerAI;
import net.sf.l2j.mods.data.FakeChatData;
import net.sf.l2j.mods.data.FakeChatData.ChatContext;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.interfaces.IAttacker;
import net.sf.l2j.mods.interfaces.IBufferSpender;
import net.sf.l2j.mods.interfaces.IConsumableSpender;
import net.sf.l2j.mods.interfaces.ICrafter;
import net.sf.l2j.mods.interfaces.IDeath;
import net.sf.l2j.mods.interfaces.IEquipes;
import net.sf.l2j.mods.interfaces.IEventRegister;
import net.sf.l2j.mods.interfaces.IExplorerWalker;
import net.sf.l2j.mods.interfaces.ILevel;
import net.sf.l2j.mods.interfaces.IPartyRange;
import net.sf.l2j.mods.interfaces.IPickup;
import net.sf.l2j.mods.interfaces.IPotionSpender;
import net.sf.l2j.mods.interfaces.IPrivateBuy;
import net.sf.l2j.mods.interfaces.IPrivateSeller;
import net.sf.l2j.mods.interfaces.IShotsSpender;
import net.sf.l2j.mods.interfaces.ITargetSelect;
import net.sf.l2j.mods.interfaces.ITeleport;
import net.sf.l2j.mods.interfaces.ITownStore;
import net.sf.l2j.mods.skills.SkillCombo;
 

public abstract class CombatBehaviorAI extends AbstractFakePlayerAI implements IDeath, ITownStore, IEventRegister, IPartyRange, IExplorerWalker, ITeleport, ICrafter, IPickup, ILevel, IShotsSpender, IPrivateSeller, IPrivateBuy, IPotionSpender, IEquipes, IConsumableSpender, IBufferSpender, IAttacker, ITargetSelect
{
	protected Creature _target;
	protected SkillCombo combo;
	protected int skillStreak = 0;
	protected long lastBackstabTry = 0;
	protected long _lastChatTime = 0;
	protected long nextArcherDecision = 0;
	protected long ARCHER_DECISION_DELAY = 1500; // ms
	protected Set<String> _usedResponses = new HashSet<>();
	protected final Set<String> _used = new HashSet<>();
	
	public CombatBehaviorAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void onAiTick()
	{
		if (handleDeath(_fakePlayer))
			return;
		
		handlePartyCohesion(_fakePlayer);
		handleExplorer(_fakePlayer, _target);
		HandlerTeleport(_fakePlayer);
		handleTvTRegister(_fakePlayer);
		handleTownActivity(_fakePlayer);
	}
	
	public void clearTarget()
	{
		_target = null;
		_fakePlayer.setTarget(null);
	}
	
	public Creature getTarget()
	{
		return _target;
	}
	
	public void setTarget(Creature target)
	{
		_target = target;
		_fakePlayer.setTarget(target);
	}
	
	public FakePlayer getPlayer()
	{
		return _fakePlayer;
	}
	
	public abstract ShotType getShotType();
	
	public abstract boolean isMage();
	
	public abstract CombatKit getCombatKit();
	
	public abstract SkillCombo getCombatCombo();
	
	protected int getArrowId()
	{
		int playerLevel = _fakePlayer.getLevel();
		if (playerLevel < 20)
			return 17; // wooden arrow
		if (playerLevel >= 20 && playerLevel < 40)
			return 1341; // bone arrow
		if (playerLevel >= 40 && playerLevel < 52)
			return 1342; // steel arrow
		if (playerLevel >= 52 && playerLevel < 61)
			return 1343; // Silver arrow
		if (playerLevel >= 61 && playerLevel < 76)
			return 1344; // Mithril Arrow
		if (playerLevel >= 76)
			return 1345; // shining
			
		return 0;
	}
	
	protected L2Skill getBestSkill(int skillId)
	{
		int maxLevel = SkillTable.getInstance().getMaxLevel(skillId);
		if (maxLevel <= 0)
			return null;
		return SkillTable.getInstance().getInfo(skillId, maxLevel);
	}
	
	protected void handlePetToggleBuff(int skillId, boolean checkPet)
	{
		if (checkPet)
		{
			if (_fakePlayer.getPet() == null || _fakePlayer.getPet().isDead())
				return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, _fakePlayer.getSkillLevel(skillId));
		
		if (skill == null)
			return;
		
		if (_fakePlayer.getFirstEffect(skill) != null)
			return;
		
		_fakePlayer.useMagic(skill, false, false);
	}
	
	public void onPrivateMessage(PrivateMessage msg)
	{
		if (_fakePlayer.isPrivateBuying() || _fakePlayer.isPrivateSelling() || _fakePlayer.isPrivateManufactureing())
			return;
		
		if (msg.isAnswered())
			return;
		
		long now = System.currentTimeMillis();
		
		// Cooldown global
		if (now - _lastChatTime < 8000)
			return;
		
		// Chance de responder
		if (Rnd.get(100) < 5)
			return;
		
		ChatContext ctx = _fakePlayer.resolveContext();
		
		String response = FakeChatData.getInstance().getRandomResponse(msg.getTypeId(), _fakePlayer.getLang(), ctx, _usedResponses);
		
		if (response == null)
			return;
		
		_lastChatTime = now;
		_usedResponses.add(response);
		
		msg.markSeen();
		msg.markAnswered();
		
		_fakePlayer.sendTell(msg.getSenderName(), response, msg.getTypeId());
	}
	
	public void onGlobalChat()
	{
		if (_fakePlayer.isPrivateBuying() || _fakePlayer.isPrivateSelling() || _fakePlayer.isPrivateManufactureing())
			return;
		
		long now = System.currentTimeMillis();
		
		int chatId = Say2.ALL;
		
		if (Rnd.get(100) > 50)
		{
			chatId = Say2.SHOUT;
		}
		
		ChatContext ctx = _fakePlayer.resolveContext();
		
		String response = FakeChatData.getInstance().getRandomResponse(chatId, _fakePlayer.getLang(), ctx, _used);
		
		if (response == null)
			return;
		
		_used.add(response);
		
		_fakePlayer.setLastChatGlobalTime(now);
		_fakePlayer.sendGlobalMessage(response, chatId);
		
		if (_used.size() > 30)
			_used.clear();
		
	}
	
	public void resetSkillStreak()
	{
		skillStreak = 0;
	}
	
	public void incSkillStreak()
	{
		skillStreak++;
	}
	
	public int getSkillStreak()
	{
		return skillStreak;
	}
	
	public boolean canTryBackstab()
	{
		return System.currentTimeMillis() - lastBackstabTry > 1500;
	}
	
	public void markBackstabTry()
	{
		lastBackstabTry = System.currentTimeMillis();
	}
	
	public boolean canReposition()
	{
		return System.currentTimeMillis() >= nextArcherDecision;
	}
	
	public void lockReposition()
	{
		nextArcherDecision = System.currentTimeMillis() + ARCHER_DECISION_DELAY;
	}
	
}
