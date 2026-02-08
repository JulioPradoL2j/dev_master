package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

public class GainXpSpMod implements IVoicedCommandHandler
{
	
	private static final String[] COMMANDS =
	{
		"xpon",
		"xpoff"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if (player == null)
			return false;
		
		if (command.equalsIgnoreCase("xpon"))
		{
			player.setGainXpSpEnable(true);
			player.sendMessage(Config.MESSAGE_XPON);
			return true;
		}
		
		if (command.equalsIgnoreCase("xpoff"))
		{
			player.setGainXpSpEnable(false);
			player.sendMessage(Config.MESSAGE_XPOFF);
			return true;
		}
		
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
}
