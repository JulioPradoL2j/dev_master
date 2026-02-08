package net.sf.l2j.mods.interfaces;

import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.enums.FakeSpawnCity;
import net.sf.l2j.mods.enums.FakeTeleportPoint;
import net.sf.l2j.mods.spawn.FakeSpawnResolver;
import net.sf.l2j.mods.spawn.SpawnPointGenerator;

 
public interface ITeleport
{
	/* ===================== MEMOS ===================== */
	String VAR_CITY_STATE = "fake_city_state";
	String VAR_CITY_ENTER = "fake_city_enter";
	String VAR_CITY_EXIT = "fake_city_exit";
	String VAR_CITY_LAST_ACTION = "fake_city_last_action";
	String VAR_CITY_TARGET_STAY = "fake_city_target_stay";
	String VAR_FIELD_TARGET_STAY = "fake_field_target_stay";
	
	/* ===================== TEMPOS ===================== */
	long CITY_MIN_STAY = 1000L * 60 * 10;
	long CITY_MAX_STAY = 1000L * 60 * 20;
	
	long FIELD_MIN_TIME = 1000L * 60 * 45;
	long FIELD_MAX_TIME = 1000L * 60 * 60;
	
	enum CityState
	{
		NONE,
		IN_CITY
	}
	
	/* ===================== ENTRY POINT ===================== */
	default void HandlerTeleport(FakePlayer fake)
	{
		if (fake.getMemos().getBool(VAR_TELEPORT_CASTING, false))
			return;
		
		long now = System.currentTimeMillis();
		var memos = fake.getMemos();
		
		/* --------- BLOQUEIOS GLOBAIS --------- */
		if (fake.isDead() || fake.isPickingUp())
			return;
		
		if (fake.isPrivateBuying() || fake.isPrivateSelling() || fake.isPrivateManufactureing())
			return;
		
		CityState state = CityState.valueOf(memos.getString(VAR_CITY_STATE, CityState.NONE.name()));
		
		/*
		 * ================================================== ENTROU NA CIDADE (passivo) ==================================================
		 */
		if (state == CityState.NONE && fake.isInsideZoneTown())
		{
			memos.set(VAR_CITY_STATE, CityState.IN_CITY.name());
			memos.set(VAR_CITY_ENTER, now);
			memos.set(VAR_CITY_LAST_ACTION, now);
			memos.set(VAR_CITY_TARGET_STAY, Rnd.get(CITY_MIN_STAY, CITY_MAX_STAY));
			memos.hasChanges();
			return;
		}
		
		/*
		 * ================================================== ESTÁ NA CIDADE → decidir sair ==================================================
		 */
		if (state == CityState.IN_CITY)
		{
			long enterTime = memos.getLong(VAR_CITY_ENTER, now);
			
			long targetStay = memos.getLong(VAR_CITY_TARGET_STAY, 0);
			if (targetStay <= 0)
			{
				targetStay = Rnd.get(CITY_MIN_STAY, CITY_MAX_STAY);
				memos.set(VAR_CITY_TARGET_STAY, targetStay);
				memos.hasChanges();
			}
			
			if (now - enterTime < targetStay)
				return;
			
			if (Rnd.get(100) > 50)
				return;
			
			teleportOutOfTown(fake);
			
			memos.set(VAR_CITY_STATE, CityState.NONE.name());
			memos.set(VAR_CITY_EXIT, now);
			memos.set(VAR_FIELD_TARGET_STAY, Rnd.get(FIELD_MIN_TIME, FIELD_MAX_TIME));
			memos.hasChanges();
			return;
		}
		
		/*
		 * ================================================== ESTÁ FORA DA CIDADE → decidir voltar ==================================================
		 */
		if (state == CityState.NONE && !fake.isInsideZoneTown())
		{
			long exitTime = memos.getLong(VAR_CITY_EXIT, now);
			
			long targetField = memos.getLong(VAR_FIELD_TARGET_STAY, 0);
			if (targetField <= 0)
			{
				targetField = Rnd.get(FIELD_MIN_TIME, FIELD_MAX_TIME);
				memos.set(VAR_FIELD_TARGET_STAY, targetField);
				memos.hasChanges();
			}
			
			if (now - exitTime < targetField)
				return;
			
			if (Rnd.get(100) > 40)
				return;
			
			memos.set(VAR_CITY_STATE, CityState.IN_CITY.name());
			memos.set(VAR_CITY_ENTER, now);
			memos.set(VAR_CITY_LAST_ACTION, now);
			memos.set(VAR_CITY_TARGET_STAY, Rnd.get(CITY_MIN_STAY, CITY_MAX_STAY));
			memos.hasChanges();
			
			FakeTeleportPoint city = FakeTeleportPoint.getRandomCity();
			
			castTeleportAndThen(fake, new Location(city.getLocation().getX(), city.getLocation().getY(), city.getLocation().getZ()));
			
		}
	}
	
	/* ===================== HELPERS ===================== */
	
	private static void teleportOutOfTown(FakePlayer fake)
	{
		if (isThirdClass(fake.getClassId().getId()))
		{
			FakeTeleportPoint dest = FakeTeleportPoint.getRandomField();
			castTeleportAndThen(fake, new Location(dest.getLocation().getX(), dest.getLocation().getY(), dest.getLocation().getZ()));
			return;
		}
		
		FakeSpawnCity city = FakeSpawnResolver.resolve(fake.getClassId().getId());
		Location spawn = SpawnPointGenerator.findValid(city.getBase().getX(), city.getBase().getY(), city.getBase().getZ());
		
		if (fake.getClassId().getId() == 8)
		{
			castTeleportAndThen(fake, new Location(130936, 114472, -3728));
			
		}
		else if (fake.getClassId().getId() == 9)
		{
			castTeleportAndThen(fake, new Location(122264, 111624, -3840));
			
		}
		else
		{
			castTeleportAndThen(fake, new Location(spawn.getX(), spawn.getY(), spawn.getZ()));
			
		}
	}
	
	int TELEPORT_CAST_TIME = 500; // ms
	String VAR_TELEPORT_CASTING = "fake_teleport_casting";
	
	private static void castTeleportAndThen(FakePlayer fake, Location loc)
	{
		var memos = fake.getMemos();
		
		if (memos.getBool(VAR_TELEPORT_CASTING, false))
			return;
		
		memos.set(VAR_TELEPORT_CASTING, true);
		memos.hasChanges();
		fake.setIsImmobilized(true);
		
		if (Config.OPEN_EFFECT_CLASSIC_TELEPORTER)
		{
			// Efeito visual
			MagicSkillUse msu = new MagicSkillUse(fake, fake, 2036, 1, TELEPORT_CAST_TIME, 0);
			fake.broadcastPacket(msu, 1500);
			fake.sendPacket(msu);
		}
		
		ThreadPool.schedule(() -> {
			// Segurança extra
			if (fake.isDead())
			{
				memos.set(VAR_TELEPORT_CASTING, false);
				memos.hasChanges();
				return;
			}
			
			fake.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), 75);
			
			memos.set(VAR_TELEPORT_CASTING, false);
			memos.hasChanges();
			
			fake.setIsImmobilized(false);
		}, TELEPORT_CAST_TIME + 300);
	}
	
	private static boolean isThirdClass(int classId)
	{
		return classId >= 88 && classId <= 118;
	}
}