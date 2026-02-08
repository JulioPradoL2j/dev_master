package net.sf.l2j.mods.ai.human.mystic;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;

 
public class HumanMysticAI extends CombatBehaviorAI
{
	
	
	public HumanMysticAI(FakePlayer character)
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
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		handleTargetSelection(_fakePlayer);
		handleAttackTarget(_fakePlayer, _target);
		
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
	
	protected void LoadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(1177)));
	}
}