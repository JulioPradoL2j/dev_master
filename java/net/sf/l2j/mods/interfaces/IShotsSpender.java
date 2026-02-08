package net.sf.l2j.mods.interfaces;

import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;

 
public interface IShotsSpender
{
	default void handleShots(FakePlayer fakePlayer)
	{
		if (fakePlayer.getInventory().getItemByItemId(getShotId(fakePlayer)) != null)
		{
			if (fakePlayer.getInventory().getItemByItemId(getShotId(fakePlayer)).getCount() <= 20)
			{
				fakePlayer.getInventory().addItem("", getShotId(fakePlayer), 100, fakePlayer, null);
			}
		}
		else
		{
			fakePlayer.getInventory().addItem("", getShotId(fakePlayer), 100, fakePlayer, null);
		}
		
		if (fakePlayer.getAutoSoulShot().isEmpty())
		{
			fakePlayer.addAutoSoulShot(getShotId(fakePlayer));
			fakePlayer.rechargeShots(true, true);
		}
	}
	
	static int getShotId(FakePlayer fakePlayer)
	{
		int playerLevel = fakePlayer.getStat().getLevel();
		
		if (fakePlayer.getFakeAi() instanceof CombatBehaviorAI)
		{
			CombatBehaviorAI ai = (CombatBehaviorAI) fakePlayer.getFakeAi();
			
			if (playerLevel < 20)
				return ai.getShotType() == ShotType.SOULSHOT ? 1835 : 3947;
			if (playerLevel >= 20 && playerLevel < 40)
				return ai.getShotType() == ShotType.SOULSHOT ? 1463 : 3948;
			if (playerLevel >= 40 && playerLevel < 52)
				return ai.getShotType() == ShotType.SOULSHOT ? 1464 : 3949;
			if (playerLevel >= 52 && playerLevel < 61)
				return ai.getShotType() == ShotType.SOULSHOT ? 1465 : 3950;
			if (playerLevel >= 61 && playerLevel < 76)
				return ai.getShotType() == ShotType.SOULSHOT ? 1466 : 3951;
			if (playerLevel >= 76)
				return ai.getShotType() == ShotType.SOULSHOT ? 1467 : 3952;
			
		}
		
		return 0;
	}
	
}