package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.AutoFarm.VoicedAutofarm;
import net.sf.l2j.community.marketplace.CommandMarketPlace;
import net.sf.l2j.email.items.VoicedMailSend;
import net.sf.l2j.email.items.VoicedMailbox;
import net.sf.l2j.event.bossevent.KTBConfig;
import net.sf.l2j.event.bossevent.VoicedEventKTB;
import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.VoicedCTFEvent;
import net.sf.l2j.event.deathmath.DMConfig;
import net.sf.l2j.event.deathmath.VoicedDMEvent;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.VoicedFOSEvent;
import net.sf.l2j.event.lastman.LMConfig;
import net.sf.l2j.event.lastman.VoicedEventLastMan;
import net.sf.l2j.event.tvt.TvTConfig;
import net.sf.l2j.event.tvt.VoicedTvTEvent;
import net.sf.l2j.gameserver.handler.custom.CustomBypassHandler;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.GainXpSpMod;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Repair;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBanking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBossSpawn;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBuffs;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedCastles;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedClanNotice;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedDonate;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedDonateColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedNewColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedPassword;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedRanking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedRankingCustom;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedRemovedBanChat;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedReport;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedStatus;
import net.sf.l2j.mission.VoicedMission;
import net.sf.l2j.sellbuff.SellBuffsCommand;

public class VoicedCommandHandler
{
	private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();
	
	public static VoicedCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected VoicedCommandHandler()
	{
		registerHandler(new VoicedStatus());
		//Codigos para colocar aqui dentro	
		if(Config.CHAT_BAN_HTML)
		{
			registerHandler(new VoicedRemovedBanChat());
		}

		registerHandler(new VoicedMission());
		if (Config.ALLOW_EMAIL_COMMAND)
		{
			registerHandler(new VoicedMailbox());
			registerHandler(new VoicedMailSend());
		}
		
		if (Config.ENABLE_COMMAND_XPON_OFF)
		{
			registerHandler(new GainXpSpMod());
		}
		
		if (Config.ALLOW_NEW_COLOR_MANAGER)
		{
			registerHandler(new VoicedNewColor());
			registerHandler(new VoicedColor());
			registerHandler(new VoicedDonateColor());
		}
		if(Config.SELL_BUFF_ENABLED)
		{
			registerHandler(new SellBuffsCommand());
		}
		if(Config.ENABLE_AUCTION_COMMUNITY)
		{
			registerHandler(new CommandMarketPlace());
		}
		if(FOSConfig.ALLOW_TVTFOS_COMMANDS)
		{
			registerHandler(new VoicedFOSEvent());
		}
		if (TvTConfig.ALLOW_TvT_COMMANDS)
		{
			registerHandler(new VoicedTvTEvent());
		}
		if (CTFConfig.ALLOW_CTF_COMMANDS)
		{
			registerHandler(new VoicedCTFEvent());
		}
		
		if(DMConfig.ALLOW_DM_COMMANDS)
		{
			registerHandler(new VoicedDMEvent());
		}
		if(Config.ENABLE_COMMAND_GOLDBAR)
    	{
    		registerHandler(new VoicedBanking());
    	}
	  	if(Config.ENABLE_COMMAND_PASSWORD)
    	{
    		registerHandler(new VoicedPassword());
    	}
    	if(Config.ENABLE_COMMAND_REPORT_CHAR)
    	{
    		registerHandler(new VoicedReport());
    	}
    	if(Config.ENABLE_COMMAND_REPAIR_CHAR)
    	{
    		registerHandler(new Repair());
    	}
    	if(Config.ENABLE_COMMAND_CASTLES)
    	{
    		registerHandler(new VoicedCastles());
    	}
    	if(Config.ENABLE_COMMAND_RANKING)
    	{
    		registerHandler(new VoicedRanking());
    		registerHandler(new VoicedRankingCustom());
    	}
    	if(Config.ENABLE_COMMAND_CLAN_NOTICE)
    	{
    		registerHandler(new VoicedClanNotice());
    	}
    	if(Config.ENABLE_COMMAND_RAID)
		{
			registerHandler(new VoicedBossSpawn());
		}
 
    	if(Config.ENABLE_AUTO_FARM_COMMAND)
    	{
    		registerHandler(new VoicedAutofarm());	
    	}
    	if(Config.ENABLE_COMMAND_MENU)
		{
			registerHandler(new VoicedMenu());
		}
    	if(Config.ENABLE_VOICED_DONATE)
    	{
    		registerHandler(new VoicedDonate());
    	}
    	if(KTBConfig.ALLOW_EVENT_KTB_COMMANDS)
    	{
    		registerHandler(new VoicedEventKTB());	
    	}
    	if (LMConfig.ALLOW_EVENT_COMMANDS)
    	{
    		registerHandler(new VoicedEventLastMan());
    	}  
    	
//    	 // coloque aqui os comandos
    	if ((Config.BUFFER_COMMAND2 != null && Config.BUFFER_COMMAND2.length() > 0))
		{	
			VoicedBuffs handler = new VoicedBuffs();
			if(Config.BUFFER_USECOMMAND && Config.BUFFER_COMMAND2 != null && Config.BUFFER_COMMAND2.length() > 0)
			{
				registerHandler(new VoicedBuffs());
			}
			CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
			
		}
	}
	
	public void registerHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (int i = 0; i < ids.length; i++)
			_datatable.put(ids[i].hashCode(), handler);
	}
	
	public IVoicedCommandHandler getHandler(String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		
		return _datatable.get(command.hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
	}
}