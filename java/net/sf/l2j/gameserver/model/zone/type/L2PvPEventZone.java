package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class L2PvPEventZone extends L2SpawnZone
{
	public L2PvPEventZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{ 	
		character.setInsideZone(ZoneId.PVP_CUSTOM, true);
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			
			if (player.getParty() != null)
			{
				final L2Party party = player.getParty();
				party.removePartyMember(player, null);
			}
			 
			if (player.isAio() || player.isSellBuff())
			{
				character.teleToLocation(83597, 147888, -3405, 0);
				character.sendMessage("Your class AIO X or Sell Buffs Character is not allowed in this zone.");
				player.broadcastUserInfo();
				return;
			}
			player.updatePvPFlag(1);
			character.sendMessage("You have entered a PvP Event Zone!");
			player.broadcastUserInfo();
			player.broadcastCharInfo();
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.PVP_CUSTOM, false);
		if (character instanceof Player)
		{
			final Player activeChar = (Player) character;
			character.sendMessage("You have left a PvP Event Zone!");
			activeChar.updatePvPFlag(0);
			activeChar.broadcastUserInfo();
			activeChar.broadcastCharInfo();
		}
	}

	@Override
	public void onDieInside(Creature character)
	{
		
	}
	@Override
	public void onReviveInside(Creature character)
	{
		
	}
	
}