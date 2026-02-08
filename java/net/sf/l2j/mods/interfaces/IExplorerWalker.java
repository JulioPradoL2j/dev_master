package net.sf.l2j.mods.interfaces;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.ExplorerContext;

public interface IExplorerWalker
{
	static final long GLOBAL_CHAT_COOLDOWN = 1000 * 15; // 1 minuto
	
	/* =============================== STATE =============================== */
	
	class ExplorerState
	{
		Location macroTarget;
		Location microTarget;
		
		long lastDecision;
		long lastMove;
		
		int lastX, lastY;
		int stuckCounter;
		
		long idleUntil;
	}
	
	/* =============================== PROFILE =============================== */
	
	class ExplorerProfile
	{
		int macroMin;
		int macroMax;
		
		int microMin;
		int microMax;
		
		long decisionDelay;
		long moveDelay;
		
		int arrivalDistance;
		
		int idleMin;
		int idleMax;
		
		boolean allowMacro;
	}
	
	/* =============================== STORAGE =============================== */
	
	Map<FakePlayer, ExplorerState> STATES = new ConcurrentHashMap<>();
	
	/* =============================== PROFILE RESOLUTION =============================== */
	
	default ExplorerProfile getProfile(ExplorerContext ctx)
	{
		ExplorerProfile p = new ExplorerProfile();
		
		switch (ctx)
		{
			case CITY_WALK:
				p.macroMin = 800;
				p.macroMax = 2200;
				
				p.microMin = 350;
				p.microMax = 750;
				
				p.decisionDelay = 6000;
				p.moveDelay = 14000;
				
				p.arrivalDistance = 280;
				
				p.idleMin = 2000;
				p.idleMax = 6000;
				
				p.allowMacro = true;
				break;
			
			case FARM_WALK:
				p.macroMin = 3200;
				p.macroMax = 8200;
				
				p.microMin = 900;
				p.microMax = 1800;
				
				p.decisionDelay = 4500;
				p.moveDelay = 9000;
				
				p.arrivalDistance = 420;
				
				p.idleMin = 300;
				p.idleMax = 1200;
				
				p.allowMacro = true;
				break;
			
			case SOCIAL:
				p.macroMin = 600;
				p.macroMax = 1600;
				
				p.microMin = 280;
				p.microMax = 520;
				
				p.decisionDelay = 8000;
				p.moveDelay = 18000;
				
				p.arrivalDistance = 260;
				
				p.idleMin = 4000;
				p.idleMax = 12000;
				
				p.allowMacro = false;
				break;
			
			default:
				return null;
		}
		
		return p;
	}
	
	/* =============================== ENTRY POINT =============================== */
	
	default void handleExplorer(FakePlayer fake, Creature target)
	{
		ExplorerContext ctx = fake.resolveExplorerContext();
		
		if (ctx == ExplorerContext.IDLE)
			return;
		
		if (fake.isDead() || fake.isPickingUp())
			return;
		
		if (fake.isPrivateBuying() || fake.isPrivateSelling() || fake.isPrivateManufactureing())
			return;
		
		ExplorerProfile profile = getProfile(ctx);
		if (profile == null)
			return;
		
		ExplorerState st = STATES.computeIfAbsent(fake, f -> new ExplorerState());
		long now = System.currentTimeMillis();
		
		if (now < st.idleUntil)
			return;
		
		if (ctx == ExplorerContext.SOCIAL && Rnd.get(100) < 30)
		{
			st.idleUntil = now + Rnd.get(profile.idleMin, profile.idleMax);
			if (now - fake.getLastChatGlobalTime() > GLOBAL_CHAT_COOLDOWN)
				((CombatBehaviorAI) fake.getFakeAi()).onGlobalChat();
			return;
		}
		
		if (ctx == ExplorerContext.CITY_WALK && Rnd.get(100) > 97)
		{
			if (now - fake.getLastChatGlobalTime() > GLOBAL_CHAT_COOLDOWN)
				((CombatBehaviorAI) fake.getFakeAi()).onGlobalChat();
		}
		
		if (ctx == ExplorerContext.DANGEROUS)
		{
			if (now - st.lastDecision > profile.decisionDelay)
			{
				st.lastDecision = now;
				
				if (profile.allowMacro)
				{
					if (st.macroTarget == null || reached(fake, st.macroTarget, profile.arrivalDistance))
						st.macroTarget = pickMacroTarget(fake, profile);
				}
				
				st.microTarget = pickMicroTarget(fake, st.macroTarget, profile);
			}
			
			if (st.microTarget == null)
				return;
			
			if (reached(fake, st.microTarget, profile.arrivalDistance))
			{
				st.microTarget = null;
				st.idleUntil = now + Rnd.get(profile.idleMin, profile.idleMax);
				return;
			}
			
			if (detectStuck(fake, st))
			{
				recover(fake, st);
				return;
			}
			
			if (now - st.lastMove > profile.moveDelay)
			{
				move(fake, st.microTarget);
				st.lastMove = now;
			}
			return;
		}
		
		if (now - st.lastDecision > profile.decisionDelay)
		{
			st.lastDecision = now;
			
			if (profile.allowMacro)
			{
				if (st.macroTarget == null || reached(fake, st.macroTarget, profile.arrivalDistance))
					st.macroTarget = pickMacroTarget(fake, profile);
			}
			
			st.microTarget = pickMicroTarget(fake, st.macroTarget, profile);
		}
		
		if (st.microTarget == null)
			return;
		
		if (reached(fake, st.microTarget, profile.arrivalDistance))
		{
			st.microTarget = null;
			st.idleUntil = now + Rnd.get(profile.idleMin, profile.idleMax);
			return;
		}
		
		if (detectStuck(fake, st))
		{
			recover(fake, st);
			return;
		}
		
		if (now - st.lastMove > profile.moveDelay)
		{
			move(fake, st.microTarget);
			st.lastMove = now;
		}
	}
	
	/* =============================== TARGET SELECTION =============================== */
	
	default Location pickMacroTarget(FakePlayer fake, ExplorerProfile p)
	{
		for (int i = 0; i < 10; i++)
		{
			double angle = Rnd.nextDouble() * Math.PI * 2;
			int dist = Rnd.get(p.macroMin, p.macroMax);
			
			int x = fake.getX() + (int) (Math.cos(angle) * dist);
			int y = fake.getY() + (int) (Math.sin(angle) * dist);
			int z = fake.getZ();
			
			if (GeoEngine.getInstance().canMoveToTarget(fake.getX(), fake.getY(), fake.getZ(), x, y, z))
				return new Location(x, y, z);
		}
		return null;
	}
	
	default Location pickMicroTarget(FakePlayer fake, Location macro, ExplorerProfile p)
	{
		if (macro == null)
			return null;
		
		double dx = macro.getX() - fake.getX();
		double dy = macro.getY() - fake.getY();
		
		double angle = Math.atan2(dy, dx);
		angle += Rnd.nextDouble() * 0.5 - 0.25;
		
		int step = Rnd.get(p.microMin, p.microMax);
		
		int x = fake.getX() + (int) (Math.cos(angle) * step);
		int y = fake.getY() + (int) (Math.sin(angle) * step);
		int z = fake.getZ();
		
		if (!GeoEngine.getInstance().canMoveToTarget(fake.getX(), fake.getY(), fake.getZ(), x, y, z))
			return null;
		
		return new Location(x, y, z);
	}
	
	/* =============================== MOVEMENT =============================== */
	
	default void move(FakePlayer fake, Location loc)
	{
		if (!fake.isImmobilized())
			fake.getAI().setIntention(CtrlIntention.MOVE_TO, loc);
	}
	
	/* =============================== STUCK & RECOVER =============================== */
	
	default boolean detectStuck(FakePlayer fake, ExplorerState st)
	{
		int dx = fake.getX() - st.lastX;
		int dy = fake.getY() - st.lastY;
		
		if (dx * dx + dy * dy < 20 * 20)
			st.stuckCounter++;
		else
			st.stuckCounter = 0;
		
		st.lastX = fake.getX();
		st.lastY = fake.getY();
		
		return st.stuckCounter >= 6;
	}
	
	default void recover(FakePlayer fake, ExplorerState st)
	{
		st.stuckCounter = 0;
		st.microTarget = null;
		st.macroTarget = null;
		
		st.idleUntil = System.currentTimeMillis() + Rnd.get(1200, 3500);
	}
	
	/* =============================== UTILS =============================== */
	
	default boolean reached(FakePlayer fake, Location loc, int dist)
	{
		int dx = fake.getX() - loc.getX();
		int dy = fake.getY() - loc.getY();
		return dx * dx + dy * dy <= dist * dist;
	}
}
