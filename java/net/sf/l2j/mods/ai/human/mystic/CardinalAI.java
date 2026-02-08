package net.sf.l2j.mods.ai.human.mystic;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.interfaces.IHealer;
import net.sf.l2j.mods.skills.SkillCombo;

 
public class CardinalAI extends CombatBehaviorAI implements IHealer
{

	public CardinalAI(FakePlayer character)
	{
		super(character);
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
		handleConsumable(_fakePlayer, _spiritOre);
		handleConsumable(_fakePlayer, _einhassad);
		Creature target = getMostInjuredTarget(_fakePlayer, 2500);
		tryHealing(_fakePlayer, target);
		
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
		return CombatKit.HEALER_SUPPORT;
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	

}
