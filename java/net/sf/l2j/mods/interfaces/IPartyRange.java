package net.sf.l2j.mods.interfaces;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.mods.actor.FakePlayer;

 
public interface IPartyRange
{
	String VAR_LAST_PARTY_FOLLOW = "fake_last_party_follow";
	long PARTY_FOLLOW_COOLDOWN = 1000L; // 4 segundos
	
	int PARTY_COMFORT_RANGE = 400;
	int PARTY_MAX_RANGE = 1500;
	
	default void handlePartyCohesion(FakePlayer player)
	{
		if (player.getParty() == null)
			return;
		
		Player leader = player.getParty().getLeader();
		if (leader == null || leader == player)
			return;
		
		long now = System.currentTimeMillis();
		var memos = player.getMemos();
		long lastFollow = memos.getLong(VAR_LAST_PARTY_FOLLOW, 0);
		if (now - lastFollow < PARTY_FOLLOW_COOLDOWN)
			return;
		
		double distance = player.getDistanceSq(leader);
		
		if (distance <= PARTY_COMFORT_RANGE)
			return;
		
		if (distance > PARTY_MAX_RANGE)
		{
			if (Rnd.get(100) < 40)
			{
				player.getAI().startFollow(leader, PARTY_COMFORT_RANGE - 100);
				memos.set(VAR_LAST_PARTY_FOLLOW, now);
			}
			return;
		}
		
	}
}
