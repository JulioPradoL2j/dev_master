package net.sf.l2j.mods.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.FakeTeleportPoint;

 
public interface IDeath
{
	String VAR_DEATH_STATE = "fake_death_state";
	String VAR_DEATH_TIME = "fake_death_time";
	String VAR_REVIVE_TIME = "fake_revive_time";
	
	static long getDeathDelay()
	{
		return Rnd.get(5000, 15000);
	}
	
	enum DeathState
	{
		NONE,
		DEAD,
		REVIVING
	}
	
	default boolean handleDeath(FakePlayer fake)
	{
		if(TvTEvent.isPlayerParticipant(fake.getObjectId()))
			return false;
		
		long now = System.currentTimeMillis();
		var memos = fake.getMemos();
		
		DeathState state = DeathState.valueOf(memos.getString(VAR_DEATH_STATE, DeathState.NONE.name()));
		
		// 1. Detecta morte apenas uma vez
		if (fake.isDead() && state == DeathState.NONE)
		{
			memos.set(VAR_DEATH_STATE, DeathState.DEAD.name());
			memos.set(VAR_DEATH_TIME, now);
			memos.hasChanges();
			
			if (fake.getFakeAi() instanceof CombatBehaviorAI)
			{
				CombatBehaviorAI ai = (CombatBehaviorAI) fake.getFakeAi();
				ai.clearTarget();
			}
			
			return true;
		}
		
		// 2. Aguarda tempo morto
		if (state == DeathState.DEAD)
		{
			long deathTime = memos.getLong(VAR_DEATH_TIME);
			if (now - deathTime < getDeathDelay())
				return true;
			memos.set(VAR_DEATH_STATE, DeathState.REVIVING.name());
			memos.set(VAR_REVIVE_TIME, now);
			memos.hasChanges();
			return true;
		}
		
		// 3. Delay pós revive
		if (state == DeathState.REVIVING)
		{
			long reviveTime = memos.getLong(VAR_REVIVE_TIME);
			if (now - reviveTime < 3000)
				return true;
			
			FakeTeleportPoint dest = FakeTeleportPoint.getRandomCity();
			
			fake.teleToLocation(dest.getLocation().getX(), dest.getLocation().getY(), dest.getLocation().getZ(), 75);
			fake.doRevive();
			memos.set(VAR_DEATH_STATE, DeathState.NONE.name());
			memos.hasChanges();
			return true;
		}
		
		return false;
	}
	
}
