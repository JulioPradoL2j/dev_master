package net.sf.l2j.mods.task;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.AbstractFakePlayerAI;
import net.sf.l2j.mods.engine.FakePlayerRestoreEngine;
import net.sf.l2j.mods.engine.FakePlayerRestoreQueue;
import net.sf.l2j.mods.manager.FakePlayerManager;

public final class FakePlayerAiEngine
{
	private static final int TICK_INTERVAL_MS = 1000;
	private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static final int RESTORE_BATCH = 1; // por tick
	
	private static ScheduledFuture<?> scheduler;
	
	public static void start()
	{
		if (scheduler != null)
			return;
		
		scheduler = ThreadPool.scheduleAtFixedRate(FakePlayerAiEngine.getInstance()::tick, TICK_INTERVAL_MS, TICK_INTERVAL_MS);
		
	}
	
	/* ---------------- TICK ---------------- */
	
	public void tick()
	{
		int restored = 0;
		
		while (restored < RESTORE_BATCH)
		{
			Integer objectId = FakePlayerRestoreQueue.poll();
			if (objectId == null)
				break;
			
			if (FakePlayerManager.getInstance().getPlayer(objectId) != null)
				continue;
			
			FakePlayerRestoreEngine.getInstance().restoreSingle(objectId);
			restored++;
		}
		
		List<FakePlayer> players = FakePlayerManager.getInstance().getFakePlayers();
		int total = players.size();
		
		if (total == 0)
			return;
		
		int chunk = (total + THREADS - 1) / THREADS;
		
		for (int i = 0; i < THREADS; i++)
		{
			int from = i * chunk;
			int to = Math.min(from + chunk, total);
			
			if (from >= to)
				break;
			
			ThreadPool.execute(() -> process(players, from, to));
		}
	}
	
	public static void process(List<FakePlayer> list, int from, int to)
	{
		for (int i = from; i < to; i++)
		{
			FakePlayer fp = list.get(i);
			AbstractFakePlayerAI ai = fp.getFakeAi();
			
			if (ai == null || !ai.canThink() || !ai.tryThink())
				continue;
			
			try
			{
				ai.onAiTick();
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
			finally
			{
				ai.doneThinking();
			}
		}
	}
	
	public static FakePlayerAiEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakePlayerAiEngine INSTANCE = new FakePlayerAiEngine();
	}
}
