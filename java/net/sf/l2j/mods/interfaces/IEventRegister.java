package net.sf.l2j.mods.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.event.tvt.TvTEventTeleporter;
import net.sf.l2j.gameserver.model.memo.PlayerMemo;
import net.sf.l2j.mods.actor.FakePlayer;

 
public interface IEventRegister
{
	String VAR_TIME = "fake_tvt_time";
	
	// =========================
	// MAIN ENTRY
	// =========================
	default void handleTvTRegister(FakePlayer fake)
	{
		if (fake == null)
			return;
		
		if (!isThirdClass(fake.getClassId().getId()))
			return;
		
		if (!TvTEvent.isParticipating())
			return;
		
		// Já está registrado
		if (TvTEvent.isPlayerParticipant(fake.getObjectId()))
			return;
		
		PlayerMemo memos = fake.getMemos();
		long now = System.currentTimeMillis();
		
		long nextTry = memos.getLong(VAR_TIME, 0);
		if (now < nextTry)
			return;
		
		long delay = Rnd.get(8_000, 35_000);
		memos.set(VAR_TIME, now + delay);
		
		if (Rnd.get(100) < 20)
			return;
		
		if (!TvTEvent.addParticipant(fake))
			return;
		
		if (TvTEvent.isStarted())
		{
			new TvTEventTeleporter(fake, TvTEvent.getParticipantTeamCoordinates(fake.getObjectId()), true, false, TvTEvent.getObjects());
		}
	}
	
	private static boolean isThirdClass(int classId)
	{
		return classId >= 88 && classId <= 118;
	}
	
}
