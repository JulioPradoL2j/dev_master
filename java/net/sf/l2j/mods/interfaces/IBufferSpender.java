package net.sf.l2j.mods.interfaces;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;

public interface IBufferSpender
{
	default void handleBuffers(FakePlayer fakePlayer)
	{
		if (!(fakePlayer.getFakeAi() instanceof CombatBehaviorAI))
			return;
		
		CombatBehaviorAI ai = (CombatBehaviorAI) fakePlayer.getFakeAi();
		
		if (fakePlayer.isDead())
			return;
		
		var buffList = ai.isMage() ? Config.MAGE_BUFF_LIST : Config.FIGHTER_BUFF_LIST;
		
		for (Integer skillId : buffList)
		{
			if (skillId == 0)
				continue;
			
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId));
			
			if (skill == null)
				continue;
			
			if (fakePlayer.getFirstEffect(skillId) != null)
				continue;
			
			skill.getEffects(fakePlayer, fakePlayer);
		}
	}
}
