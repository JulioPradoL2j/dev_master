package net.sf.l2j.mods.ai.human.fighter;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;

 
public class SaggitariusAI extends CombatBehaviorAI
{
	
	
	public SaggitariusAI(FakePlayer character)
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
		handleEquipes(_fakePlayer);
		
		handleLevel(_fakePlayer);
		handleConsumable(_fakePlayer, getArrowId());
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handlePetToggleBuff(99, false);
		handlePetToggleBuff(4, false);
		handlePetToggleBuff(312, false);
		handlePetToggleBuff(256, false);
		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
		
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
		return CombatKit.ARCHER_KITE;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	protected void loadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(101)) // Stun
		);
	}
	
}
