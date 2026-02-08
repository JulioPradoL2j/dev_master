package net.sf.l2j.mods.interfaces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	
	long TARGET_LOCK_TIME = 5000; // lock no Fake (evita trocar a cada tick)
	long TARGET_CLAIM_TIME = 8000; // lease do mob (anti-stack real)
	
	int MAX_LEVEL_DIFF = 10;
	
	// memos do Fake (ok, só Fake tem memos)
	String MEMO_LAST_CLAIMED_OID = "fp_last_claimed_oid";
	
	/*
	 * ========================= CLAIM STORE (GLOBAL) =========================
	 */
	
	final class Claim
	{
		final int ownerId;
		final long until;
		
		Claim(int ownerId, long until)
		{
			this.ownerId = ownerId;
			this.until = until;
		}
	}
	
	// mobObjectId -> Claim
	ConcurrentMap<Integer, Claim> CLAIMS = new ConcurrentHashMap<>();
	
	/* ========================= */
	/* ===== ENTRY POINT ======= */
	/* ========================= */
	
	default void handleTargetSelection(FakePlayer fakePlayer)
	{
		if (!(fakePlayer.getFakeAi() instanceof CombatBehaviorAI))
			return;
		
		if (fakePlayer.isPrivateBuying() || fakePlayer.isPickingUp() || fakePlayer.isPrivateSelling() || fakePlayer.isPrivateManufactureing())
			return;
		
		final CombatBehaviorAI ai = (CombatBehaviorAI) fakePlayer.getFakeAi();
		final Creature current = ai.getTarget();
		
		// 0) lock do alvo atual
		if (current != null)
		{
			final long lockedUntil = fakePlayer.getMemos().getLong("target_lock_until", 0);
			if (System.currentTimeMillis() < lockedUntil)
				return;
		}
		
		// 1) mantém alvo atual se válido
		if (current != null && isTargetStillValid(fakePlayer, current))
			return;
		
		// 2) vai trocar: solta claim anterior (se aplicável)
		releasePreviousClaimIfNeeded(fakePlayer, current);
		
		// 3) busca novo
		final Creature newTarget = findNearestValidTarget(fakePlayer);
		if (newTarget == null)
			return;
		
		// 4) LOS
		if (!GeoEngine.getInstance().canSeeTarget(fakePlayer, newTarget))
		{
			ai.clearTarget();
			return;
		}
		
		// 5) se for mob, tenta claimar (anti-stack real)
		if (newTarget instanceof L2MonsterInstance)
		{
			final L2MonsterInstance mob = (L2MonsterInstance) newTarget;
			
			// Exceção: se o mob tem hate em mim, eu posso pegar mesmo se claimado por outro.
			if (!mob.getHateList().contains(fakePlayer))
			{
				if (!tryClaimTarget(fakePlayer, mob))
				{
					ai.clearTarget();
					return;
				}
			}
			else
			{
				// se tem hate em mim, eu renovo/garanto claim também (melhora estabilidade)
				tryClaimTarget(fakePlayer, mob);
			}
		}
		
		ai.setTarget(newTarget);
		fakePlayer.getMemos().set("target_lock_until", System.currentTimeMillis() + TARGET_LOCK_TIME);
		fakePlayer.getMemos().hasChanges();
	}
	
	/* ========================= */
	/* ==== CLAIM / RELEASE ==== */
	/* ========================= */
	
	default boolean tryClaimTarget(FakePlayer fp, L2MonsterInstance mob)
	{
		if (mob == null || mob.isDead())
			return false;
		
		final long now = System.currentTimeMillis();
		final int mobOid = mob.getObjectId();
		final int myId = fp.getObjectId();
		
		// remove claim expirado (fast path)
		final Claim cur = CLAIMS.get(mobOid);
		if (cur != null && now >= cur.until)
			CLAIMS.remove(mobOid, cur);
		
		// tenta adquirir/renovar de forma atômica
		Claim updated = CLAIMS.compute(mobOid, (k, old) -> {
			if (old == null || now >= old.until || old.ownerId == myId)
				return new Claim(myId, now + TARGET_CLAIM_TIME);
			
			// ainda claimado por outro
			return old;
		});
		
		if (updated == null || updated.ownerId != myId)
			return false;
		
		// registra no Fake o último mob claimado (pra soltar ao trocar)
		fp.getMemos().set(MEMO_LAST_CLAIMED_OID, mobOid);
		fp.getMemos().hasChanges();
		
		return true;
	}
	
	default boolean isClaimedByOther(FakePlayer fp, L2MonsterInstance mob)
	{
		if (mob == null || mob.isDead())
			return false;
		
		final long now = System.currentTimeMillis();
		final Claim c = CLAIMS.get(mob.getObjectId());
		if (c == null)
			return false;
		
		if (now >= c.until)
		{
			// expirou, limpa e considera livre
			CLAIMS.remove(mob.getObjectId(), c);
			return false;
		}
		
		return c.ownerId != 0 && c.ownerId != fp.getObjectId();
	}
	
	default void releasePreviousClaimIfNeeded(FakePlayer fp, Creature currentTarget)
	{
		final int lastClaimedOid = fp.getMemos().getInteger(MEMO_LAST_CLAIMED_OID, 0);
		if (lastClaimedOid <= 0)
			return;
		
		// se o alvo atual não é o claimado, não mexe (evita soltar claim errado)
		if (currentTarget instanceof L2MonsterInstance)
		{
			final L2MonsterInstance mob = (L2MonsterInstance) currentTarget;
			if (mob.getObjectId() != lastClaimedOid)
				return;
			
			// se ele tem hate em mim, não solta (estou em combate real)
			if (mob.getHateList().contains(fp))
				return;
		}
		
		// solta apenas se for meu
		
		final Claim c = CLAIMS.get(lastClaimedOid);
		if (c != null && c.ownerId == fp.getObjectId())
		{
			// remove claim (ou deixa expirar, mas remover é melhor pra distribuir rápido)
			CLAIMS.remove(lastClaimedOid, c);
		}
		
		fp.getMemos().set(MEMO_LAST_CLAIMED_OID, 0);
		fp.getMemos().hasChanges();
		
		// limpeza opcional: se map crescer demais, pode expurgar expirados de tempos em tempos
		// (não obrigatório; TTL já controla)
	}
	
	/* ========================= */
	/* ==== VALID TARGET ======= */
	/* ========================= */
	
	default boolean isTargetStillValid(FakePlayer player, Creature target)
	{
		final boolean inTvT = TvTEvent.isStarted();
		final byte myTeam = inTvT ? TvTEvent.getParticipantTeamId(player.getObjectId()) : -1;
		
		if (target == null || target.isDead())
			return false;
		
		if (!player.isInsideRadius(target, AGGRO_RANGE, false, false))
			return false;
		
		if (Util.calculateDistance(player, target, false) > AGGRO_RANGE * 0.9)
			return false;
		
		if (target instanceof L2MonsterInstance)
		{
			final L2MonsterInstance mob = (L2MonsterInstance) target;
			
			// se o mob tem hate em mim, mantém sempre
			if (mob.getHateList().contains(player))
				return true;
			
			// se está claimado por outro, inválido (anti-stack)
			if (isClaimedByOther(player, mob))
				return false;
			
			// respeita diferença de level
			final int levelDiff = player.getLevel() - mob.getLevel();
			if (levelDiff > MAX_LEVEL_DIFF)
				return false;
			
			// renova claim enquanto mantém alvo (stability)
			tryClaimTarget(player, mob);
			return true;
		}
		
		if (target instanceof L2GuardInstance)
		{
			final L2GuardInstance guard = (L2GuardInstance) target;
			
			if (guard.getTarget() == player)
				return true;
			
			if (player.getPet() != null && guard.getTarget() == player.getPet())
				return true;
			
			return false;
		}
		
		if (target instanceof Player)
		{
			final Player ply = (Player) target;
			
			if (ply.isDead())
				return false;
			
			if (inTvT)
			{
				final byte otherTeam = TvTEvent.getParticipantTeamId(player.getObjectId());
				if (otherTeam == -1 || otherTeam == myTeam)
					return false;
			}
			else
			{
				if (!player.checkIfPvP(ply))
					return false;
				
				if (ply.getTarget() == player)
					return true;
				
				if (player.getPet() != null && ply.getTarget() == player.getPet())
					return true;
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
		final L2GuardInstance hostileGuard = findHostileGuard(player);
		if (hostileGuard != null)
			return hostileGuard;
		
		final L2MonsterInstance hateMob = findAggressiveMonster(player);
		if (hateMob != null)
			return hateMob;
		
		final Player hostile = findHostilePlayer(player);
		if (hostile != null)
			return hostile;
		
		final L2MonsterInstance mob = findNearestMonster(player);
		if (mob != null)
			return mob;
		
		if (canSearchPlayers(player))
			return findNearestPlayer(player);
		
		return null;
	}
	
	default L2MonsterInstance findNearestMonster(FakePlayer player)
	{
		L2MonsterInstance best = null;
		double bestScore = Double.MAX_VALUE;
		
		// jitter estável por fake (0..~60)
		final double jitter = stableJitter(player);
		
		for (L2MonsterInstance mob : player.getKnownList().getKnownType(L2MonsterInstance.class))
		{
			if (mob == null || mob.isDead())
				continue;
			
			if (!player.isInsideRadius(mob, AGGRO_RANGE, false, false))
				continue;
			
			// 1) Se me atacou, SEMPRE revida (prioridade absoluta)
			if (mob.getHateList().contains(player))
				return mob;
			
			// 2) Ignorar mobs muito fracos
			final int levelDiff = player.getLevel() - mob.getLevel();
			if (levelDiff > MAX_LEVEL_DIFF)
				continue;
				
			// 3) Anti-stack hard: se claimado por outro, ainda pode considerar,
			// mas com penalidade alta. (Se você quiser HARD ignore, troque por "continue;")
			final boolean claimedByOther = isClaimedByOther(player, mob);
			
			// 4) Score: distância + densidade + disputa + jitter
			final double dist = Util.calculateDistance(player, mob, false);
			
			// densidade de fakes próximos desse mob (evita "todo mundo no mesmo pack")
			final int nearbyFakes = countNearbyFakePlayers(player, mob, 650); // raio de crowd
			final double crowdPenalty = nearbyFakes * 120.0; // ajuste fino
			
			// penalidade de disputa (claim)
			final double claimPenalty = claimedByOther ? 900.0 : 0.0; // ajuste fino
			
			// penalidade pequena para mobs "muito longe do centro" (opcional)
			// (mantém o fake num “pocket”, reduz ziguezague)
			final double pocketPenalty = pocketPenalty(player, mob); // 0..~150
			
			// jitter estável quebra empate e distribui melhor
			final double score = dist + crowdPenalty + claimPenalty + pocketPenalty + jitter;
			
			if (score < bestScore)
			{
				bestScore = score;
				best = mob;
			}
		}
		
		// Se escolheu um mob claimado por outro, tenta claimar antes de retornar.
		// Se falhar, devolve null para o fluxo procurar outro (ou vai cair no próximo ciclo).
		if (best != null && isClaimedByOther(player, best))
		{
			if (!tryClaimTarget(player, best))
				return null;
		}
		
		return best;
	}
	
	/* ===== resto igual ao seu (players/guards) ===== */
	
	default Player findNearestPlayer(FakePlayer player)
	{
		Player best = null;
		double bestDist = Double.MAX_VALUE;
		
		final boolean inTvT = TvTEvent.isStarted();
		final byte myTeam = inTvT ? TvTEvent.getParticipantTeamId(player.getObjectId()) : -1;
		
		for (Player other : player.getKnownList().getKnownType(Player.class))
		{
			if (other == null || other == player || other.isDead())
				continue;
			
			if (inTvT)
			{
				final byte otherTeam = TvTEvent.getParticipantTeamId(other.getObjectId());
				if (otherTeam == -1 || otherTeam == myTeam)
					continue;
			}
			else
			{
				if (!player.checkIfPvP(other))
					continue;
			}
			
			if (!player.isInsideRadius(other, AGGRO_RANGE, false, false))
				continue;
			
			final double dist = Util.calculateDistance(player, other, false);
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
		if (TvTEvent.isParticipating() && TvTEvent.isPlayerParticipant(player.getObjectId()))
			return TvTEvent.isStarted();
		
		if (player.isInsideZone(ZoneId.FLAG))
			return true;
		
		if (player.isInsideZone(ZoneId.ARENA_EVENT))
			return true;
		
		if (player.isInsideZone(ZoneId.PVP))
			return true;
		
		return false;
	}
	
	default double stableJitter(FakePlayer player)
	{
		// jitter determinístico por fake, para não ficar mudando a cada tick
		// ~0..60
		final int id = player.getObjectId();
		final int x = player.getX();
		final int y = player.getY();
		
		int h = id;
		h = 31 * h + x;
		h = 31 * h + y;
		h ^= (h >>> 16);
		
		final int v = (h & 0x7fffffff) % 61; // 0..60
		return v;
	}
	
	default int countNearbyFakePlayers(FakePlayer me, L2MonsterInstance mob, int radius)
	{
		int count = 0;
		
		for (Player other : me.getKnownList().getKnownType(Player.class))
		{
			if (other == null || other == me)
				continue;
			
			// conta só fakes
			if (!(other instanceof FakePlayer))
				continue;
			
			if (other.isDead())
				continue;
			
			if (!other.isInsideRadius(mob, radius, false, false))
				continue;
				
			// opcional: só conta quem está “em modo combate”
			// se ( ( (FakePlayer) other).getFakeAi() instanceof CombatBehaviorAI ) ...
			
			count++;
		}
		
		return count;
	}
	
	default double pocketPenalty(FakePlayer fp, L2MonsterInstance mob)
	{
		// desligue fácil:
		// return 0;
		
		final long now = System.currentTimeMillis();
		
		long until = fp.getMemos().getLong("fp_pocket_until", 0);
		int cx = fp.getMemos().getInteger("fp_pocket_x", 0);
		int cy = fp.getMemos().getInteger("fp_pocket_y", 0);
		
		if (now >= until || cx == 0 || cy == 0)
		{
			// cria novo pocket baseado na posição atual
			fp.getMemos().set("fp_pocket_x", fp.getX());
			fp.getMemos().set("fp_pocket_y", fp.getY());
			fp.getMemos().set("fp_pocket_until", now + 30000L); // 30s
			fp.getMemos().hasChanges();
			
			cx = fp.getX();
			cy = fp.getY();
		}
		
		// distância 2D ao pocket
		final long dx = (long) mob.getX() - cx;
		final long dy = (long) mob.getY() - cy;
		final double d = Math.sqrt((dx * dx) + (dy * dy));
		
		// até ~1200 sem penalidade; depois começa a penalizar leve
		if (d <= 1200.0)
			return 0.0;
		
		return Math.min(150.0, (d - 1200.0) * 0.08); // 0..150 aprox
	}
	
}
