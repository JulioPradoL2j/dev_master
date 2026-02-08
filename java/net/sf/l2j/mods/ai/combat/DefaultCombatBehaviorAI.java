package net.sf.l2j.mods.ai.combat;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillCombo;

 

public class DefaultCombatBehaviorAI extends CombatBehaviorAI
{
	
	
	public DefaultCombatBehaviorAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void onAiTick()
	{
		
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
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
}
