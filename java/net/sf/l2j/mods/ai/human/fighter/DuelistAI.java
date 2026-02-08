package net.sf.l2j.mods.ai.human.fighter;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;
 

public class DuelistAI extends CombatBehaviorAI
{
	
	private final int SONIC_FOCUS_ID = 8;
	private final int MIN_SONIC_FOR_COMBO = 3;
	private final int TARGET_SONIC_CHARGE = 7;
	private long _lastSonicTry = 0;
	private final long SONIC_RETRY_DELAY = 1200; // ms
	private final int RIPOSTE_STANCE = 340;
	
	private int getSonicCharge()
	{
		L2Skill sonic = SkillTable.getInstance().getInfo(SONIC_FOCUS_ID, _fakePlayer.getSkillLevel(SONIC_FOCUS_ID));
		if (sonic == null)
			return 0;
		
		var effect = _fakePlayer.getFirstEffect(sonic);
		if (effect == null)
			return 0;
		
		return effect.getLevel();
	}
	
	public DuelistAI(FakePlayer character)
	{
		super(character);
		LoadSkills();
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		if (handlePickUp(_fakePlayer))
			return;
		handleEquipes(_fakePlayer);
		
		handleLevel(_fakePlayer);
		handleConsumable(_fakePlayer, energyStone);
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handleTargetSelection(_fakePlayer);
		handlePetToggleBuff(RIPOSTE_STANCE, false);
		int sonic = getSonicCharge();
		
		// 🔋 abaixo do mínimo → tenta carregar, mas não bloqueia ataque
		if (sonic < MIN_SONIC_FOR_COMBO)
		{
			tryChargeSonic();
		}
		
		// ⚔️ ataque SEMPRE acontece
		handleAttackTarget(_fakePlayer, _target);
		
	}
	
	private boolean tryChargeSonic()
	{
		if (_fakePlayer.isCastingNow())
			return false;
		
		long now = System.currentTimeMillis();
		if (now - _lastSonicTry < SONIC_RETRY_DELAY)
			return false;
		
		L2Skill sonic = SkillTable.getInstance().getInfo(SONIC_FOCUS_ID, _fakePlayer.getSkillLevel(SONIC_FOCUS_ID));
		
		if (sonic == null)
			return false;
		
		var effect = _fakePlayer.getFirstEffect(sonic);
		if (effect != null && effect.getLevel() >= TARGET_SONIC_CHARGE)
			return false;
		
		_lastSonicTry = now;
		_fakePlayer.useMagic(sonic, false, false);
		return true;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	@Override
	public ShotType getShotType()
	{
		return ShotType.SOULSHOT;
	}
	
	@Override
	public boolean isMage()
	{
		return false;
	}
	
	@Override
	public CombatKit getCombatKit()
	{
		return CombatKit.FIGHTER_DPS;
	}
	
	protected void LoadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(6)), new SkillAction(getBestSkill(345)), new SkillAction(getBestSkill(1)), new SkillAction(getBestSkill(5)));
	}
	
}
