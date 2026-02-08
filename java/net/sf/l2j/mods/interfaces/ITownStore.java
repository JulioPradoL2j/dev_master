package net.sf.l2j.mods.interfaces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.TownActivityMode;
import net.sf.l2j.mods.spawn.TownActivityState;

 
public interface ITownStore
{
	ConcurrentMap<FakePlayer, TownActivityState> TOWN_STATES = new ConcurrentHashMap<>();
	
	String VAR_TOWN_ACTIVITY_DELAY = "fake_town_activity_delay";
	long TOWN_ACTIVITY_WAIT = 20 * 60 * 1000L;
	
	default void handleTownActivity(FakePlayer player)
	{
		var memos = player.getMemos();
		long now = System.currentTimeMillis();
		
		TownActivityState st = TOWN_STATES.computeIfAbsent(player, p -> new TownActivityState());
		
		// ===============================
		// JÁ EXISTE MODO ATIVO
		// ===============================
		if (st.isActive())
		{
			executeActiveMode(player, st.getMode());
			return;
		}
		
		// ===============================
		// DELAY HUMANO ANTES DE ESCOLHER
		// ===============================
		long delayUntil = memos.getLong(VAR_TOWN_ACTIVITY_DELAY, 0);
		
		if (delayUntil == 0)
		{
			// primeira vez que entra na cidade
			memos.set(VAR_TOWN_ACTIVITY_DELAY, now + TOWN_ACTIVITY_WAIT);
			memos.hasChanges();
			return;
		}
		
		if (now < delayUntil)
			return;
			
		// ===============================
		// DELAY CONSUMIDO → LIMPA MEMO
		// ===============================
		memos.unset(VAR_TOWN_ACTIVITY_DELAY);
		memos.hasChanges();
		
		// ===============================
		// SORTEIA UM ÚNICO MODO
		// ===============================
		int roll = Rnd.get(100);
		
		if (roll < 34)
			st.activate(TownActivityMode.PRIVATE_SELL);
		else if (roll < 67)
			st.activate(TownActivityMode.PRIVATE_BUY);
		else
			st.activate(TownActivityMode.CRAFT);
		
		executeActiveMode(player, st.getMode());
	}
	
	private static void executeActiveMode(FakePlayer player, TownActivityMode mode)
	{
		CombatBehaviorAI ai = (CombatBehaviorAI) player.getFakeAi();
		
		switch (mode)
		{
			case PRIVATE_SELL:
				ai.handlePrivateSell(player);
				if (!player.isPrivateSelling())
					TOWN_STATES.get(player).reset();
				break;
			
			case PRIVATE_BUY:
				ai.handlePrivateBuy(player);
				if (!player.isPrivateBuying())
					TOWN_STATES.get(player).reset();
				break;
			
			case CRAFT:
				ai.handleManufacture(player);
				if (!player.isPrivateManufactureing())
					TOWN_STATES.get(player).reset();
				break;
			
			default:
				break;
		}
	}
}
