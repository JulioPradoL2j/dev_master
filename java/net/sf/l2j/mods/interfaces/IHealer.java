package net.sf.l2j.mods.interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.mods.actor.FakePlayer;

 
public interface IHealer
{
	/*
	 * ========================= Heal coordination =========================
	 */
	Map<Creature, FakePlayer> HEAL_TARGETS = new ConcurrentHashMap<>();
	Map<Creature, Long> HEAL_LOCK_TIME = new ConcurrentHashMap<>();
	long HEAL_LOCK_DURATION = 4000;
	
	/*
	 * ========================= Target selection =========================
	 */
	default Creature getMostInjuredTarget(FakePlayer healer, int radius)
	{
		return getHealCandidates(healer, radius).stream().filter(c -> !isTargetAlreadyHealedByOther(healer, c)).min(Comparator.comparingDouble(IHealer::safeHpPercent).thenComparingInt(c -> getPriority(c, healer))).orElse(null);
	}
	
	private static int getPriority(Creature c, FakePlayer healer)
	{
		if (c == healer)
			return 3; // healer por último
		if (c instanceof FakePlayer)
		{
			FakePlayer fp = (FakePlayer) c;
			if (fp.isPartyLeader())
				return 0; // leader cardinal
			if (fp.isDagger())
				return 1; // depois dagger
			if (fp.isWarrior())
				return 2; // depois guerreiro
			if (fp.isMage())
				return 3; // depois mage
				
		}
		return 4; // resto
	}
	
	default List<Creature> getHealCandidates(FakePlayer healer, int radius)
	{
		if (healer.getParty() != null)
		{
			return healer.getParty().getPartyMembers().stream().filter(m -> !m.isDead()).filter(m -> canHealTarget(healer, m)).filter(m -> Util.checkIfInRange(radius, healer, m, true)).collect(Collectors.toList());
		}
		
		return healer.getKnownList().getKnownTypeInRadius(FakePlayer.class, radius).stream().filter(c -> !c.isDead()).filter(c -> canHealTarget(healer, c)).collect(Collectors.toList());
	}
	
	/*
	 * ========================= Public entry point =========================
	 */
	default void tryHealing(FakePlayer healer, Creature target)
	{
		if (healer.isPrivateBuying() || healer.isPrivateSelling() || healer.isPickingUp() || healer.isPrivateManufactureing())
			return;
		
		if (tryPartyHeal(healer))
			return;
		if (tryTargetHeal(healer, target))
			return;
		
		trySelfHeal(healer);
		idleBehavior(healer);
	}
	
	default boolean canHealTarget(FakePlayer healer, Creature target)
	{
		if (target == null || target.isDead())
			return false;
			
		// =========================
		// 🟥 CONTEXTO: TvT
		// =========================
		if (TvTEvent.isParticipating() || TvTEvent.isStarted())
		{
			byte myTeam = TvTEvent.getParticipantTeamId(healer.getObjectId());
			if (myTeam == -1)
				return false; // healer fora do evento
				
			// Cura sempre a si mesmo
			if (target == healer)
				return true;
			
			if (target instanceof Player)
			{
				Player other = (Player) target;
				byte otherTeam = TvTEvent.getParticipantTeamId(other.getObjectId());
				return otherTeam == myTeam; // SÓ MESMO TIME
			}
			
			return false;
		}
		
		// =========================
		// 🟦 CONTEXTO: Normal
		// =========================
		return true;
	}
	
	/*
	 * ========================= Party / Group heal =========================
	 */
	default boolean tryPartyHeal(FakePlayer healer)
	{
		if (healer.getParty() == null)
			return false;
		
		long lowHp = healer.getParty().getPartyMembers().stream().filter(m -> !m.isDead()).filter(m -> safeHpPercent(m) < 0.65).count();
		
		if (lowHp < 2)
			return false;
		
		L2Skill skill = healer.getSkill(1219);
		if (skill != null)
		{
			healer.setTarget(healer);
			healer.getAI().setIntention(CtrlIntention.CAST, skill, healer);
			return true;
		}
		
		return false;
	}
	
	/*
	 * ========================= Target-based heal =========================
	 */
	default boolean tryTargetHeal(FakePlayer healer, Creature target)
	{
		if (!canHealTarget(healer, target))
			return false;
		
		double hp = safeHpPercent(target);
		if (hp > 0.90)
			return false;
		
		if (!tryReserveHealTarget(healer, target))
			return false;
		
		L2Skill heal = hp < 0.35 ? healer.getSkill(1401) : hp < 0.70 ? healer.getSkill(1218) : null;
		
		if (heal == null)
			return false;
		
		if (!Util.checkIfInRange(heal.getCastRange(), healer, target, true))
		{
			healer.getAI().startFollow(target, heal.getCastRange() - 150);
			return true;
		}
		
		healer.setTarget(target);
		healer.getAI().setIntention(CtrlIntention.CAST, heal, target);
		return true;
	}
	
	/*
	 * ========================= Self heal =========================
	 */
	default void trySelfHeal(FakePlayer healer)
	{
		if (safeHpPercent(healer) > 0.45)
			return;
		
		L2Skill skill = healer.getSkill(1402);
		if (skill != null)
		{
			healer.setTarget(healer);
			healer.getAI().setIntention(CtrlIntention.CAST, skill, healer);
		}
	}
	
	Map<FakePlayer, FakePlayer> FOLLOW_TARGET = new ConcurrentHashMap<>();
	
	default void idleBehavior(FakePlayer healer)
	{
		if (healer.getAI() == null)
			return;
		
		if (healer.getParty() == null)
			return;
		
		FakePlayer current = FOLLOW_TARGET.get(healer);
		
		if (current != null && !current.isDead() && Util.checkIfInRange(600, healer, current, true))
		{
			healer.getAI().startFollow(current, 150);
			return;
		}
		FakePlayer next = healer.getParty().getPartyMembers().stream().filter(p -> p instanceof FakePlayer).map(p -> (FakePlayer) p).filter(p -> p != healer).filter(p -> !p.isDead()).sorted(Comparator.comparingInt(IHealer::getFollowPriority)).findFirst().orElse(null);
		
		if (next != null)
		{
			FOLLOW_TARGET.put(healer, next);
			int followRange = 150;
			
			if (healer.getLastSkillCast() != null)
			{
				followRange = healer.getLastSkillCast().getCastRange() + (int) healer.getTemplate().getCollisionRadius();
			}
			
			healer.getAI().startFollow(next, followRange);
			
		}
	}
	
	private static int getFollowPriority(FakePlayer fp)
	{
		if (fp.isDagger())
			return 0;
		if (fp.isMage())
			return 1;
		if (fp.isWarrior())
			return 2;
		return 3;
	}
	
	/*
	 * ========================= Heal lock control =========================
	 */
	static boolean tryReserveHealTarget(FakePlayer healer, Creature target)
	{
		long now = System.currentTimeMillis();
		
		Long lockUntil = HEAL_LOCK_TIME.get(target);
		if (lockUntil != null && lockUntil > now)
			return false;
		
		HEAL_LOCK_TIME.put(target, now + HEAL_LOCK_DURATION);
		HEAL_TARGETS.put(target, healer);
		return true;
	}
	
	static boolean isTargetAlreadyHealedByOther(FakePlayer healer, Creature target)
	{
		FakePlayer owner = HEAL_TARGETS.get(target);
		return owner != null && owner != healer;
	}
	
	/*
	 * ========================= Utils =========================
	 */
	static double safeHpPercent(Creature c)
	{
		if (c == null || c.getMaxHp() <= 0)
			return 1.0;
		
		return c.getCurrentHp() / c.getMaxHp();
	}
}
