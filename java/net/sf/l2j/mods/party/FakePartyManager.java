package net.sf.l2j.mods.party;

import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.mods.actor.FakePlayer;

 
public class FakePartyManager
{
	private static final int MAX_PARTY_SIZE = 8;
	private static final int MAX_FAKE_MEMBERS = 7;
	
	/*
	 * =============================== Player → Fake (AUTO ACCEPT) ===============================
	 */
	public boolean requestPlayer(Player leader, FakePlayer target)
	{
		if (leader == null || target == null)
			return false;
		
		L2Party party = leader.getParty();
		if (party == null)
		{
			L2Party partyCreate = new L2Party(leader, 0);
			leader.setParty(partyCreate);
			
			target.joinParty(partyCreate);
			target.setActiveRequester(null);
			if (leader.isInParty())
				leader.getParty().setPendingInvitation(false);
			leader.onTransactionResponse();
			
			return false;
		}
		
		if (!leader.isPartyLeader())
			return false;
		
		if (target.getParty() != null)
			return false;
		
		if (party.getMemberCount() >= MAX_PARTY_SIZE)
			return false;
		
		if (countFakeMembers(party) >= MAX_FAKE_MEMBERS)
			return false;
		
		target.joinParty(party);
		
		target.setActiveRequester(null);
		if (leader.isInParty())
			leader.getParty().setPendingInvitation(false);
		leader.onTransactionResponse();
		return true;
	}
	
	/*
	 * =============================== UTIL ===============================
	 */
	public int countFakeMembers(L2Party party)
	{
		int count = 0;
		for (Player member : party.getPartyMembers())
		{
			if (member instanceof FakePlayer)
				count++;
		}
		return count;
	}
	
	public static FakePartyManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakePartyManager INSTANCE = new FakePartyManager();
	}
}
