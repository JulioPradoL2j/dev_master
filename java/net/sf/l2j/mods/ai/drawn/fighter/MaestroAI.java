package net.sf.l2j.mods.ai.drawn.fighter;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;
 

public class MaestroAI extends CombatBehaviorAI
{
	
	
	public MaestroAI(FakePlayer character)
	{
		super(character);
		LoadSkills();
		_fakePlayer.registerRecipes();
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
		
		handleManufacture(_fakePlayer);
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
		combo = new SkillCombo(new SkillAction(getBestSkill(100)));
	}
}
