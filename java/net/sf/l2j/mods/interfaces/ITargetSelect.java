package net.sf.l2j.mods.interfaces;

import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2GuardInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;

 
public interface ITargetSelect
{
	int AGGRO_RANGE = 2500;
	long TARGET_LOCK_TIME = 5000; // 5s OFF-like
	int MAX_LEVEL_DIFF = 10; // OFF-like default
	
	/* ========================= */
	/* ===== ENTRY POINT ======= */
	/* ========================= */
	
	default void handleTargetSelection(FakePlayer fakePlayer)
	{
		if (!(fakePlayer.getFakeAi() instanceof CombatBehaviorAI))
			return;
		
		if (fakePlayer.isPrivateBuying() || fakePlayer.isPickingUp() || fakePlayer.isPrivateSelling() || fakePlayer.isPrivateManufactureing())
			return;
		
		CombatBehaviorAI ai = (CombatBehaviorAI) fakePlayer.getFakeAi();
		Creature current = ai.getTarget();
		
		if (current != null)
		{
			long lockedUntil = fakePlayer.getMemos().getLong("target_lock_until", 0);
			if (System.currentTimeMillis() < lockedUntil)
				return;
		}
		
		if (current != null && isTargetStillValid(fakePlayer, current))
			return;
		
		Creature newTarget = findNearestValidTarget(fakePlayer);
		if (newTarget == null)
			return;
		
		if (!GeoEngine.getInstance().canSeeTarget(fakePlayer, newTarget))
		{
			ai.clearTarget();
			return;
		}
		if (GeoEngine.getInstance().canSeeTarget(fakePlayer, newTarget))
		{
			ai.setTarget(newTarget);
			fakePlayer.getMemos().set("target_lock_until", System.currentTimeMillis() + TARGET_LOCK_TIME);
			fakePlayer.getMemos().hasChanges();
		}
	}
	
	/* ========================= */
	/* ==== VALID TARGET ======= */
	/* ========================= */
	
	default boolean isTargetStillValid(FakePlayer player, Creature target)
	{
		boolean inTvT = TvTEvent.isStarted();
		byte myTeam = inTvT ? TvTEvent.getParticipantTeamId(player.getObjectId()) : -1;
		
		
		if (target == null || target.isDead())
			return false;
		
		if (!player.isInsideRadius(target, AGGRO_RANGE, false, false))
			return false;
		
		if (Util.calculateDistance(player, target, false) > AGGRO_RANGE * 0.9)
			return false;
		
		if (target instanceof L2MonsterInstance)
		{
			L2MonsterInstance mob = (L2MonsterInstance) target;
			
			// ainda é ameaça
			if (mob.getHateList().contains(player))
				return true;
			
			// se não for ameaça, respeita diferença de level
			int levelDiff = player.getLevel() - mob.getLevel();
			if (levelDiff > MAX_LEVEL_DIFF)
				return false;
			
			return true;
		}
		
		// OFF-like: mantém guarda hostil
		if (target instanceof L2GuardInstance)
		{
			L2GuardInstance guard = (L2GuardInstance) target;
			
			if (guard.getTarget() == player)
				return true;
			
			if (player.getPet() != null && guard.getTarget() == player.getPet())
				return true;
			
			return false;
		}
		
		// OFF-like: mantém Player apenas se for ameaça ativa e PvP válido
		if (target instanceof Player)
		{
			Player ply = (Player) target;
			
			if (ply.isDead())
				return false;
			
			if (inTvT)
			{
				byte otherTeam = TvTEvent.getParticipantTeamId(player.getObjectId());
				
				if (otherTeam == -1)
					return false;
				
				if (otherTeam == myTeam)
					return false;
			}
			else
			{
				// Ataque legal?
				if (!player.checkIfPvP(ply))
					return false;
				
				// Player ainda é ameaça real?
				if (ply.getTarget() == player)
					return true;
				
				if (player.getPet() != null)
				{
					if (ply.getTarget() == player.getPet())
						return true;
				}
			}
			
			return false;
		}
		
		return false;
	}
	
	/* ========================= */
	/* ===== FIND TARGET ======= */
	/* ========================= */
	
	default Creature findNearestValidTarget(FakePlayer player)
	{
		// 0️⃣ Guarda hostil (prioridade absoluta)
		L2GuardInstance hostileGuard = findHostileGuard(player);
		if (hostileGuard != null)
			return hostileGuard;
		
		// 1️⃣ Mob com hate em mim
		L2MonsterInstance hateMob = findAggressiveMonster(player);
		if (hateMob != null)
			return hateMob;
		
		// 2️⃣ Player que me atacou
		Player hostile = findHostilePlayer(player);
		if (hostile != null)
			return hostile;
		
		// 3️⃣ Mob normal
		L2MonsterInstance mob = findNearestMonster(player);
		if (mob != null)
			return mob;
		
		// 4️⃣ Player neutro (Arena / PvP zone)
		if (canSearchPlayers(player))
			return findNearestPlayer(player);
		
		return null;
	}
	
	default L2MonsterInstance findNearestMonster(FakePlayer player)
	{
		L2MonsterInstance best = null;
		double bestDist = Double.MAX_VALUE;
		
		for (L2MonsterInstance mob : player.getKnownList().getKnownType(L2MonsterInstance.class))
		{
			if (mob == null || mob.isDead())
				continue;
			
			if (!player.isInsideRadius(mob, AGGRO_RANGE, false, false))
				continue;
			
			// 🔥 REGRA 1: Se o mob me atacou, SEMPRE revidar
			if (mob.getHateList().contains(player))
				return mob;
			
			// 🧠 REGRA 2: Ignorar mobs muito fracos
			int levelDiff = player.getLevel() - mob.getLevel();
			if (levelDiff > MAX_LEVEL_DIFF)
				continue;
			
			double dist = Util.calculateDistance(player, mob, false);
			if (dist < bestDist)
			{
				bestDist = dist;
				best = mob;
			}
		}
		return best;
	}
	
	default Player findNearestPlayer(FakePlayer player)
	{
		Player best = null;
		double bestDist = Double.MAX_VALUE;
		
		boolean inTvT = TvTEvent.isStarted();
		byte myTeam = inTvT ? TvTEvent.getParticipantTeamId(player.getObjectId()) : -1;
		
		for (Player other : player.getKnownList().getKnownType(Player.class))
		{
			if (other == null || other == player || other.isDead())
				continue;
			
			if (inTvT)
			{
				byte otherTeam = TvTEvent.getParticipantTeamId(other.getObjectId());
				
				if (otherTeam == -1)
					continue;
				
				if (otherTeam == myTeam)
					continue;
			}
			else
			{
				// PvP normal fora de evento
				if (!player.checkIfPvP(other))
					continue;
			}
			
			if (!player.isInsideRadius(other, AGGRO_RANGE, false, false))
				continue;
			
			double dist = Util.calculateDistance(player, other, false);
			if (dist < bestDist)
			{
				bestDist = dist;
				best = other;
			}
		}
		
		return best;
	}
	
	default L2MonsterInstance findAggressiveMonster(FakePlayer player)
	{
		for (L2MonsterInstance mob : player.getKnownList().getKnownType(L2MonsterInstance.class))
		{
			if (mob == null || mob.isDead())
				continue;
			
			if (!player.isInsideRadius(mob, AGGRO_RANGE, false, false))
				continue;
			
			if (mob.getHateList().contains(player))
				return mob;
		}
		return null;
	}
	
	default Player findHostilePlayer(FakePlayer player)
	{
		for (Player other : player.getKnownList().getKnownType(Player.class))
		{
			if (other == null || other.isDead())
				continue;
			
			if (!player.checkIfPvP(other))
				continue;
			
			if (other.getTarget() == player)
				return other;
			
			if (player.getPet() != null && other.getTarget() == player.getPet())
				return other;
		}
		return null;
	}
	
	default L2GuardInstance findHostileGuard(FakePlayer player)
	{
		for (L2GuardInstance guard : player.getKnownList().getKnownType(L2GuardInstance.class))
		{
			if (guard == null || guard.isDead())
				continue;
			
			if (guard.getTarget() == player)
				return guard;
			
			if (player.getPet() != null && guard.getTarget() == player.getPet())
				return guard;
		}
		return null;
	}
	
	default boolean canSearchPlayers(FakePlayer player)
	{
		// =========================
		// 🟥 CONTEXTO: TvT
		// =========================
		if (TvTEvent.isParticipating() && TvTEvent.isPlayerParticipant(player.getObjectId()))
		{
			return TvTEvent.isStarted();
		}
		
		if (player.isInsideZone(ZoneId.FLAG))
			return true;
		
		if (player.isInsideZone(ZoneId.ARENA_EVENT))
			return true;
		
		if (player.isInsideZone(ZoneId.PVP))
			return true;
		
		return false;
	}
	
}
