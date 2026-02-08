package net.sf.l2j.mods.ai.human.mystic;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;
 
public class SoltakerAI extends CombatBehaviorAI
{
	public int SUMMON_SKILL_ID = 1129;
	public int TRANSFERPAIN_SKILL_ID = 1262;
	private int BATTLE_HEAL_SKILL_ID = 1015;
	private int PET_HEAL_THRESHOLD = 30; // %
	private int ARCANE_POWER = 337;
	private long _lastPetHealTime = 0;
	private long PET_HEAL_COOLDOWN = 35000; // 4s
	private int _petHealAttempts = 0;
	private int MAX_PET_HEAL_ATTEMPTS = Rnd.get(8, 12);

	private enum SoulState
	{
		NO_PET,
		FIND_TARGET,
		KILL_TARGET,
		SUMMON,
		HEAL_PET,
		NORMAL_COMBAT
	}
	
	private SoulState _state = SoulState.NO_PET;
	private Creature _lastKilledTarget;
	
	public SoltakerAI(FakePlayer character)
	{
		super(character);
		loadSkills();
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		
		if (handlePickUp(_fakePlayer))
			return;
		
		handleConsumable(_fakePlayer, crystalC);
		handleConsumable(_fakePlayer, boneId);
		
		handleEquipes(_fakePlayer);
		
		handleLevel(_fakePlayer);
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		
		switch (_state)
		{
			case NO_PET:
				if (_fakePlayer.getPet() != null && !_fakePlayer.getPet().isDead())
				{
					_state = SoulState.NORMAL_COMBAT;
					break;
				}
				_state = SoulState.FIND_TARGET;
				break;
			
			case FIND_TARGET:
				handleTargetSelection(_fakePlayer);
				if (_target != null)
					_state = SoulState.KILL_TARGET;
				break;
			
			case KILL_TARGET:
				handleKillTarget();
				break;
			
			case SUMMON:
				handleSummonFromCorpse();
				break;
			
			case HEAL_PET:
				handleHealPet();
				break;
			
			case NORMAL_COMBAT:
				handleNormalCombat();
				break;
		}
		
	}
	
	@Override
	public ShotType getShotType()
	{
		return ShotType.BLESSED_SPIRITSHOT;
	}
	
	@Override
	public boolean isMage()
	{
		return true;
	}
	
	@Override
	public CombatKit getCombatKit()
	{
		return CombatKit.MAGE_NUKE;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	private void handleKillTarget()
	{
		if (_target == null)
		{
			_state = SoulState.FIND_TARGET;
			return;
		}
		
		// Mob acabou de morrer → capturar cadáver
		if (_target.isDead() && _target instanceof L2MonsterInstance)
		{
			_lastKilledTarget = _target;
			_state = SoulState.SUMMON;
			return;
		}
		
		handleAttackTarget(_fakePlayer, _target);
	}
	
	private void handleSummonFromCorpse()
	{
		if (!(_lastKilledTarget instanceof L2MonsterInstance))
		{
			_state = SoulState.NO_PET;
			return;
		}
		
		if (_fakePlayer.isCastingNow())
			return;
		
		if (_fakePlayer.getPet() != null)
		{
			_state = SoulState.NORMAL_COMBAT;
			return;
		}
		
		L2Skill summon = SkillTable.getInstance().getInfo(SUMMON_SKILL_ID, _fakePlayer.getSkillLevel(SUMMON_SKILL_ID));
		
		if (summon == null)
		{
			_state = SoulState.NO_PET;
			return;
		}
		
		setTarget(_lastKilledTarget);
		
		_fakePlayer.useMagic(summon, false, false);
		_lastKilledTarget = null;
		_state = SoulState.NORMAL_COMBAT;
	}
	
	private void handleNormalCombat()
	{
		if (_fakePlayer.getPet() == null || _fakePlayer.getPet().isDead())
		{
			clearTarget();
			_state = SoulState.NO_PET;
			return;
		}
		
		if (isPetNeedHeal() && canHealPetNow() && !isUnderHeavyAttack())
		{
		    clearTarget();
		    _state = SoulState.HEAL_PET;
		    return;
		}

		
		handlePetToggleBuff(ARCANE_POWER, false);
		boolean heavyFight = isUnderHeavyAttack();
		handlePetToggleBuff(TRANSFERPAIN_SKILL_ID, !heavyFight);

		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
		
	}
	
	private void handleHealPet()
	{
		if (_fakePlayer.getPet() == null || _fakePlayer.getPet().isDead())
		{
			_state = SoulState.NO_PET;
			return;
		}
	
		int hpPercent = (int) ((_fakePlayer.getPet().getCurrentHp() * 100) / _fakePlayer.getPet().getMaxHp());
		
		if (hpPercent > Rnd.get(60, 75))
		{
			clearTarget();
			_state = SoulState.NORMAL_COMBAT;
			_petHealAttempts = 0;
			return;
		}
		
		if (_petHealAttempts >= MAX_PET_HEAL_ATTEMPTS)
		{
			clearTarget();
		    _petHealAttempts = 0;
		    _state = SoulState.NORMAL_COMBAT;
		    return;
		}

		L2Skill heal = SkillTable.getInstance().getInfo(BATTLE_HEAL_SKILL_ID, _fakePlayer.getSkillLevel(BATTLE_HEAL_SKILL_ID));
		
		if (heal == null)
		{
			_state = SoulState.NORMAL_COMBAT;
			return;
		}
		setTarget(_fakePlayer.getPet());
		_fakePlayer.useMagic(heal, false, false);
		_petHealAttempts++;
		_lastPetHealTime = System.currentTimeMillis();

	}
	private boolean isUnderHeavyAttack()
	{
	    if (_target == null)
	        return false;

	    // muitos mobs batendo
	    return _fakePlayer.getKnownList().getKnownTypeInRadius(L2MonsterInstance.class, 150).size() >= 2;
	}

	private boolean isPetNeedHeal()
	{
		if (_fakePlayer.getPet() == null || _fakePlayer.getPet().isDead())
			return false;
		
		int hpPercent = (int) ((_fakePlayer.getPet().getCurrentHp() * 100) / _fakePlayer.getPet().getMaxHp());
		
		return hpPercent <= PET_HEAL_THRESHOLD;
	}
	
	protected void loadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(1263)), new SkillAction(getBestSkill(1234)), new SkillAction(getBestSkill(1148)), new SkillAction(getBestSkill(1343)));
	}
	
	private boolean canHealPetNow()
	{
	    return System.currentTimeMillis() - _lastPetHealTime >= PET_HEAL_COOLDOWN;
	}

}