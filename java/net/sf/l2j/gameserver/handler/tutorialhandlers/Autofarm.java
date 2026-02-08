package net.sf.l2j.gameserver.handler.tutorialhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.AutoFarm.AutofarmManager;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.ITutorialHandler;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author Chris
 */
public class Autofarm implements ITutorialHandler
{
	private static final String[] LINK_COMMANDS =
	{
		"start"
	};
	
	@Override
	public boolean useLink(String command, Player player, String params)
	{
		if (command.startsWith("start"))
			handleCommands(player, params);
		
		return true;
	}
	
	public static void handleCommands(Player player, String command)
	{
		
		showAutoFarm(player);
		
		// if (command.startsWith("inc_radius"))
		// {
		// player.setRadius(player.getRadius() + 400);
		// showAutoFarm(player);
		// }
		if (command.startsWith("inc_radius"))
		{
			if (player.getRadius() < 2500)
			{
				player.setRadius(player.getRadius() + 400);
			}
			else
			{
				player.sendMessage("You have already reached the maximum search radius.");
			}
			showAutoFarm(player);
		}
		
		if (command.startsWith("SkillsSelected"))
		{
			ShowSkillsAutoFarm(player);
		}
		if (command.startsWith("enableAntiKs"))
		{
			player.setAntiKsProtection(!player.isAntiKsProtected());
			showAutoFarm(player);
		}
		if (command.startsWith("enableAssistParty"))
		{
			player.setAssistParty(!player.isAssistParty());
			showAutoFarm(player);
		}
		if (command.startsWith("dec_radius"))
		{
			if (player.getRadius() > 1000)
			{
				player.setRadius(player.getRadius() - 400);
			}
			else
			{
				player.sendMessage("You have already reached the minimum search radius.");
			}
			showAutoFarm(player);
		}
		
		if (command.startsWith("inc_page"))
		{
			player.setPage(player.getPage() + 1);
			showAutoFarm(player);
		}
		
		if (command.startsWith("dec_page"))
		{
			player.setPage(player.getPage() - 1);
			showAutoFarm(player);
		}
		
		if (command.startsWith("inc_heal"))
		{
			player.setHealPercent(player.getHealPercent() + 10);
			showAutoFarm(player);
		}
		
		if (command.startsWith("dec_heal"))
		{
			player.setHealPercent(player.getHealPercent() - 10);
			showAutoFarm(player);
		}
		
		if (command.startsWith("enableAutoFarm"))
		{
			if (Config.ENABLE_COMMAND_VIP_AUTOFARM)
			{
				if (!player.isVip())
				{
					VoicedMenu.showMenuHtml(player);
					player.sendMessage("You are not VIP member.");
					return;
				}
			}
			if (Config.NO_USE_FARM_IN_PEACE_ZONE)
			{
				if (player.isInsideZone(ZoneId.PEACE))
				{
					player.sendMessage("No Use Auto farm in Peace Zone.");
					AutofarmManager.INSTANCE.stopFarm(player);
					player.setAutoFarm(false);
					player.broadcastUserInfo();
					return;
				}
			}
			if (player.isAutoFarm())
			{
				AutofarmManager.INSTANCE.stopFarm(player);
				player.setAutoFarm(false);
			}
			else
			{
				AutofarmManager.INSTANCE.startFarm(player);
				player.setAutoFarm(true);
			}
			
			showAutoFarm(player);
		}
		
		if (command.startsWith("enableBuffProtect"))
		{
			if (Config.ENABLE_COMMAND_VIP_AUTOFARM)
			{
				if (!player.isVip())
				{
					VoicedMenu.showMenuHtml(player);
					player.sendMessage("You are not VIP member.");
					return;
				}
			}
			
			player.setNoBuffProtection(!player.isNoBuffProtected());
			showAutoFarm(player);
		}
		if (command.startsWith("close"))
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static void showAutoFarm(Player activeChar)
	{
		if (Config.ENABLE_COMMAND_VIP_AUTOFARM)
		{
			if (!activeChar.isVip())
			{
				VoicedMenu.showMenuHtml(activeChar);
				activeChar.sendMessage("You are not VIP member.");
				return;
			}
		}
		String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/AutoFarm.htm");
		
		msg = msg.replace("%player%", activeChar.getName());
		msg = msg.replace("%page%", StringUtil.formatNumber(activeChar.getPage() + 1));
		msg = msg.replace("%heal%", StringUtil.formatNumber(activeChar.getHealPercent()));
		msg = msg.replace("%radius%", StringUtil.formatNumber(activeChar.getRadius()));
		msg = msg.replace("%noBuff%", activeChar.isNoBuffProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		
		// Botao
		msg = msg.replace("%button%", activeChar.isAutoFarm() ? "value=\"Stop\" action=\"link -h_start_enableAutoFarm\"" : "value=\"Start\" action=\"link -h_start_enableAutoFarm\"");
		// Auto Farm ativar
		msg = msg.replace("%autofarm%", activeChar.isAutoFarm() ? "<font color=00FF00>Active</font>" : "<font color=FF0000>Inactive</font>");
		
		// Botao anti ks players auto farm
		msg = msg.replace("%antiKs%", activeChar.isAntiKsProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		
		// botao assist party
		msg = msg.replace("%enableAssistParty%", activeChar.isAssistParty() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		
		// Novo
		activeChar.sendPacket(new TutorialShowHtml(msg));
	}
	 
	public static void ShowSkillsAutoFarm(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/AutoFarmSkills.htm");
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getLinkList()
	{
		return LINK_COMMANDS;
	}
}
