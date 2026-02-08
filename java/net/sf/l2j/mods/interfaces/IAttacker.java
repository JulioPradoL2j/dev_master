package net.sf.l2j.mods.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;

public interface IAttacker
{
	final int AGGRO_RANGE = 2500;
	final int ARCHER_SAFE_RANGE = 700;
	final int ARCHER_MID_RANGE = 450;
	final int ARCHER_CLOSE_RANGE = 250;
	
	final int ARCHER_SAFE_RANGE_SQ = ARCHER_SAFE_RANGE * ARCHER_SAFE_RANGE;
	final int ARCHER_MID_RANGE_SQ = ARCHER_MID_RANGE * ARCHER_MID_RANGE;
	final int ARCHER_CLOSE_RANGE_SQ = ARCHER_CLOSE_RANGE * ARCHER_CLOSE_RANGE;
	double MAGE_PANIC_RANGE_SQ = 120 * 120;
	double MAGE_SAFE_RANGE_SQ = 600 * 600;
	double MAGE_CAST_RANGE_SQ = 900 * 900;
	int PARTY_ATTACK_COMFORT_RANGE = 650;
	int PARTY_ATTACK_MAX_RANGE = 1000;
	
	/* ========================= */
	/* ===== ENTRY POINT ======= */
	/* ========================= */
	
	default void handleAttackTarget(FakePlayer fakePlayer, Creature target)
	{
		if (!(fakePlayer.getFakeAi() instanceof CombatBehaviorAI))
			return;
		
		if (fakePlayer.isPrivateBuying() || fakePlayer.isPrivateSelling() || fakePlayer.isPickingUp() || fakePlayer.isPrivateManufactureing())
			return;
		CombatBehaviorAI ai = (CombatBehaviorAI) fakePlayer.getFakeAi();
		
		if (shouldWaitForParty(fakePlayer))
		{
			ai.handlePartyCohesion(fakePlayer);
			fakePlayer.abortAttack();
			fakePlayer.abortCast();
			return;
		}
		
		if (!isTargetValid(fakePlayer, target))
		{
			fakePlayer.abortAttack();
			fakePlayer.abortCast();
			ai.clearTarget();
			ai.resetSkillStreak();
			return;
		}
		
		switch (ai.getCombatKit())
		{
			case MAGE_NUKE:
				handleMage(ai, target);
				break;
			
			case ARCHER_KITE:
				handleArcher(ai, target);
				break;
			
			case DAGGER:
				handleDagger(ai, target);
				break;
			
			case FIGHTER_BURST:
			case FIGHTER_DPS:
				handleFighter(ai, target);
				break;
		}
	}
	
	/* ========================= */
	/* ===== KIT LOGIC ========= */
	/* ========================= */
	
	private static void handleMage(CombatBehaviorAI ai, Creature target)
	{
		FakePlayer player = ai.getPlayer();
		
		if (!ai.canReposition())
		{
			tryMageCast(ai, target);
			return;
		}
		
		double distSq = player.getDistanceSq(target);
		
		if (distSq < MAGE_PANIC_RANGE_SQ)
		{
			
			if (Rnd.get(100) < 25)
			{
				tryMageCast(ai, target);
				return;
			}
			
			// Tenta sair
			Location escape = Rnd.get(100) < 70 ? getBackPosition(player, target) : getSidePosition(player, target);
			
			player.getAI().setIntention(CtrlIntention.MOVE_TO, escape);
			ai.lockReposition();
			return;
		}
		
		if (distSq <= MAGE_CAST_RANGE_SQ && distSq >= MAGE_SAFE_RANGE_SQ)
		{
			tryMageCast(ai, target);
			
			if (Rnd.get(100) < 25)
			{
				Location side = getSidePosition(player, target);
				player.getAI().setIntention(CtrlIntention.MOVE_TO, side);
				ai.lockReposition();
			}
			return;
		}
		
		// 🏹 LONGE DEMAIS → APROXIMA
		if (distSq > MAGE_CAST_RANGE_SQ)
		{
			player.getAI().setIntention(CtrlIntention.MOVE_TO, getFrontPosition(player, target));
			ai.lockReposition();
			return;
		}
		
		tryMageCast(ai, target);
		
		if (Rnd.get(100) < 40)
		{
			player.getAI().setIntention(CtrlIntention.MOVE_TO, getBackPosition(player, target));
			ai.lockReposition();
		}
	}
	
	private static boolean tryMageCast(CombatBehaviorAI ai, Creature target)
	{
		SkillCombo combo = ai.getCombatCombo();
		if (combo == null || combo.isEmpty())
			return false;
		
		SkillAction step = combo.current();
		if (step == null)
			return false;
		
		if (Rnd.get(100) < 10)
			return false;
		
		if (tryCastSkill(ai, target, step))
		{
			combo.advance();
			ai.incSkillStreak();
			return true;
		}
		
		return false;
	}
	
	private static void handleArcher(CombatBehaviorAI ai, Creature target)
	{
		FakePlayer player = ai.getPlayer();
		
		// não muda decisão toda hora
		if (!ai.canReposition())
		{
			physicalAttack(ai, target);
			return;
		}
		
		double distSq = player.getDistanceSq(target);
		
		if (distSq < ARCHER_CLOSE_RANGE_SQ && Rnd.get(100) < 30)
		{
			// aceita trocar porrada
			physicalAttack(ai, target);
			return;
		}
		
		// 1️⃣ LONGE
		if (distSq > ARCHER_SAFE_RANGE_SQ)
		{
			tryArcherSkill(ai, target);
			physicalAttack(ai, target);
			return;
		}
		
		// 2️⃣ MÉDIA
		if (distSq > ARCHER_MID_RANGE_SQ)
		{
			tryArcherSkill(ai, target);
			
			Location side = getSidePosition(player, target);
			
			player.getAI().setIntention(CtrlIntention.MOVE_TO, side);
			
			ai.lockReposition();
			return;
		}
		
		// 3️⃣ MUITO PERTO → PANIC MODE
		tryArcherSkill(ai, target);
		physicalAttack(ai, target); // NÃO PARA DE ATACAR
		
		Location escape = Rnd.get(100) < 60 ? getBackPosition(player, target) : getFrontPosition(player, target);
		
		player.getAI().setIntention(CtrlIntention.MOVE_TO, escape);
		ai.lockReposition();
	}
	
	private static void handleFighter(CombatBehaviorAI ai, Creature target)
	{
		// Fighter: skill ocasional
		if (ai.getSkillStreak() < 1 && Rnd.get(100) < 20)
		{
			SkillCombo combo = ai.getCombatCombo();
			if (combo != null && !combo.isEmpty())
			{
				SkillAction step = combo.current();
				if (step != null && tryCastSkill(ai, target, step))
				{
					combo.advance();
					ai.incSkillStreak();
					return;
				}
			}
		}
		
		ai.resetSkillStreak();
		physicalAttack(ai, target);
	}
	
	private static void handleDagger(CombatBehaviorAI ai, Creature target)
	{
		FakePlayer player = ai.getPlayer();
		boolean isPlayerTarget = target instanceof Player;
		
		if (player.isInFrontOf(target) && Rnd.get(100) > 50)
		{
			// aceita lutar errado
			attackWithDagger(ai, target);
			return;
		}
		
		if (!player.isBehind(target) && ai.canTryBackstab() && !player.isCastingNow() && !player.isStunned() && Rnd.get(100) < (isPlayerTarget ? 15 : 5))
		{
			Location pos = getBehindPosition(target);
			player.getAI().setIntention(CtrlIntention.MOVE_TO, pos);
			ai.markBackstabTry();
		}
		
		// ===== Combate NORMAL nunca para =====
		attackWithDagger(ai, target);
	}
	
	/* ========================= */
	/* ==== PHYSICAL ATTACK ==== */
	/* ========================= */
	
	private static void physicalAttack(CombatBehaviorAI ai, Creature target)
	{
		FakePlayer player = ai.getPlayer();
		int range = player.getPhysicalAttackRange();
		
		if (!moveOrCheckRange(player, target, range))
			return;
		if (!player.isCastingNow())
			player.doAttack(target);
	}
	
	private static void attackWithDagger(CombatBehaviorAI ai, Creature target)
	{
		SkillCombo combo = ai.getCombatCombo();
		
		if (combo != null && !combo.isEmpty())
		{
			SkillAction step = combo.current();
			if (step != null && tryCastSkill(ai, target, step))
			{
				combo.advance();
				return;
			}
		}
		
		physicalAttack(ai, target);
	}
	
	private static void tryArcherSkill(CombatBehaviorAI ai, Creature target)
	{
		if (ai.getSkillStreak() >= 1)
			return;
		
		SkillCombo combo = ai.getCombatCombo();
		if (combo == null || combo.isEmpty())
			return;
		
		SkillAction step = combo.current();
		if (step != null && tryCastSkill(ai, target, step))
		{
			combo.advance();
			ai.incSkillStreak();
		}
	}
	
	/* ========================= */
	/* ===== MOVE / RANGE ====== */
	/* ========================= */
	
	private static boolean moveOrCheckRange(FakePlayer player, Creature target, int range)
	{
		if (!player.isInsideRadius(target, range, true, false))
		{
			Location pos = getCombatPosition(target, range);
			player.getAI().setIntention(CtrlIntention.MOVE_TO, pos);
			return false;
		}
		return true;
	}
	
	static Location getCombatPosition(Creature target, int range)
	{
		double angle = Math.toRadians(Rnd.get(0, 360));
		int x = (int) (target.getX() + Math.cos(angle) * range);
		int y = (int) (target.getY() + Math.sin(angle) * range);
		return new Location(x, y, target.getZ());
	}
	
	static Location getBehindPosition(Creature target)
	{
		double angle = Math.toRadians(Util.convertHeadingToDegree(target.getHeading()) + 180);
		int x = (int) (target.getX() + Math.cos(angle) * 40);
		int y = (int) (target.getY() + Math.sin(angle) * 40);
		return new Location(x, y, target.getZ());
	}
	
	static Location getSidePosition(FakePlayer player, Creature target)
	{
		double base = Util.calculateAngleFrom(target, player);
		double side = base + (Rnd.get(2) == 0 ? 90 : -90);
		
		double rad = Math.toRadians(side);
		int dist = 200;
		
		int x = (int) (target.getX() + Math.cos(rad) * dist);
		int y = (int) (target.getY() + Math.sin(rad) * dist);
		
		return new Location(x, y, target.getZ());
	}
	
	static Location getFrontPosition(FakePlayer player, Creature target)
	{
		double angle = Util.calculateAngleFrom(player, target);
		double rad = Math.toRadians(angle);
		
		int x = (int) (player.getX() + Math.cos(rad) * 300);
		int y = (int) (player.getY() + Math.sin(rad) * 300);
		
		return new Location(x, y, player.getZ());
	}
	
	static Location getBackPosition(FakePlayer player, Creature target)
	{
		double angle = Util.calculateAngleFrom(target, player);
		double rad = Math.toRadians(angle);
		
		int x = (int) (player.getX() + Math.cos(rad) * 300);
		int y = (int) (player.getY() + Math.sin(rad) * 300);
		
		return new Location(x, y, player.getZ());
	}
	
	/* ========================= */
	/* ===== TARGET VALID ====== */
	/* ========================= */
	
	default boolean isTargetValid(FakePlayer player, Creature target)
	{
		if (target == null || target.isDead())
			return false;
		
		if (!player.isInsideRadius(target, AGGRO_RANGE, true, false))
			return false;
		
		return player.isInsideRadius(target, AGGRO_RANGE, true, false);
	}
	/* ========================= */
	/* ===== SKILL CAST ======== */
	/* ========================= */
	
	private static boolean tryCastSkill(CombatBehaviorAI ai, Creature target, SkillAction action)
	{
		FakePlayer player = ai.getPlayer();
		L2Skill skill = action.getSkill();
		
		if (skill == null)
			return false;
		
		boolean ctrl = !target.isInsideZone(ZoneId.PEACE) && !player.isInsideZone(ZoneId.PEACE);
		
		player.useMagic(skill, ctrl, false);
		
		return true;
	}
	
	default boolean shouldWaitForParty(FakePlayer player)
	{
		if (player.getParty() == null)
			return false;
		
		Player leader = player.getParty().getLeader();
		if (leader == null || leader == player)
			return false;
		
		double distSq = player.getDistanceSq(leader);
		
		// Muito longe → SEMPRE espera
		if (distSq > PARTY_ATTACK_MAX_RANGE * PARTY_ATTACK_MAX_RANGE)
			return true;
		
		// Zona intermediária → chance de esperar
		if (distSq > PARTY_ATTACK_COMFORT_RANGE * PARTY_ATTACK_COMFORT_RANGE)
		{
			// 65% de chance de esperar
			return Rnd.get(100) < 65;
		}
		
		return false;
	}
	
}
