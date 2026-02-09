package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.l2j.Config;
import net.sf.l2j.event.bossevent.KTBConfig;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.bossevent.KTBManager;
import net.sf.l2j.event.championInvade.ChampionInvade;
import net.sf.l2j.event.championInvade.InitialChampionInvade;
import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMConfig;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.deathmath.DMManager;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.fortress.FOSManager;
import net.sf.l2j.event.lastman.CheckNextEvent;
import net.sf.l2j.event.partyfarm.InitialPartyFarm;
import net.sf.l2j.event.partyfarm.PartyFarm;
import net.sf.l2j.event.rewardsoloevent.InitialSoloEvent;
import net.sf.l2j.event.rewardsoloevent.RewardSoloEvent;
import net.sf.l2j.event.soloboss.InitialSoloBossEvent;
import net.sf.l2j.event.soloboss.SoloBoss;
import net.sf.l2j.event.spoil.InitialSpoilEvent;
import net.sf.l2j.event.spoil.SpoilEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.event.tournament.ArenaEvent;
import net.sf.l2j.event.tournament.ArenaTask;
import net.sf.l2j.event.tvt.TvTConfig;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.events.eventpvp.PvPEvent;
import net.sf.l2j.events.eventpvp.PvPEventNext;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.AutoGoldBar;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.SummonInfo;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CharInfo;
import net.sf.l2j.gameserver.network.serverpackets.DeleteObject;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.mission.MissionReset;

public class VoicedMenu implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"menu",
		"MENU",
		"auction",
		"info",
		"info_pt",
		"info_sp",
		"exp",
		"multisell",
		"optimizeFPS",
		"hideSkillsAnimation",
		"setxpnot",
		"setPartyRefuse",
		"setTradeRefuse",
		"setAntiBuff",
		"vip_",
		"setvip_",
		"hero_",
		"sethero_",
		"setMessageRefuse",
		"eventstime",
		"mission",
		"setAutoPotion",
		"setAutoGoldBar",
		"setAutoGb",
		"skins",
		"trySkin",
		"hair",
		"setViewer",
		"hideEnchantGlow",
		"disable_Helm",
		"disable_skin"
	};
	
	// private static final String ACTIVED = "ON";
	// private static final String DESATIVED = "OFF";
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equals("menu") || command.equals("MENU"))
			showMenuHtml(activeChar);
		
		else if (command.equals("info"))
			showInfoHtml(activeChar);
		
		else if (command.equals("auction"))
			showAuctionHtml(activeChar);
		else if (command.equals("eventstime"))
			showEventTimes(activeChar);
		else if (command.equals("hideEnchantGlow"))
		{
			if (activeChar.isDisableGlowWeapon())
			{
				activeChar.setDisableGlowWeapon(false);
				activeChar.broadcastUserInfoHiden();
			}
			else
			{
				activeChar.setDisableGlowWeapon(true);
				activeChar.broadcastUserInfoHiden();
			}
			showMenuHtml(activeChar);
		}
		else if (command.equals("setViewer"))
		{
			if (activeChar.isInsideZone(ZoneId.PEACE))
			{
				boolean hidePlayers = !activeChar.isHidingPlayers();
				activeChar.setHidingPlayers(hidePlayers);
				
				// Iterar sobre os objetos conhecidos diretamente
				for (L2Object obj : activeChar.getKnownList().getKnownObjects())
				{
					if (obj instanceof Player)
					{
						Player player = (Player) obj;
						if (hidePlayers)
						{
							// Esconde os jogadores do cliente
							activeChar.sendPacket(new DeleteObject(player));
							if (player.getPet() != null)
							{
								activeChar.sendPacket(new DeleteObject(player.getPet()));
								
							}
						}
						else
						{
							// Mostra os jogadores novamente
							activeChar.sendPacket(new CharInfo(player));
							if (player.getPet() != null)
							{
								activeChar.sendPacket(new SummonInfo(player.getPet(), player, 0));
								
							}
							
							player.sendPacket(new CharInfo(activeChar)); // Atualizar o estado do jogador atual
						}
					}
				}
				
				activeChar.sendMessage(hidePlayers ? "Players hidden." : "Players shown.");
			}
			else
			{
				activeChar.sendMessage("[Hide Player] This service can only be used in Peace Zone!");
			}
			showMenuHtml(activeChar);
		}
		
		else if (command.equalsIgnoreCase("setAutoGb"))
		{
			if (activeChar.isAutoGb())
			{
				activeChar.setAutoGb(false);
				AutoGoldBar.getInstance().remove(activeChar);
			}
			else
			{
				activeChar.setAutoGb(true);
				AutoGoldBar.getInstance().add(activeChar);
			}
			VoicedMenu.showMenuHtml(activeChar);
		}
		
		else if (command.equals("mission"))
		{
			showMissionHtml(activeChar);
		}
		
		else if (command.equals("info_pt"))
			showInfoPtHtml(activeChar);
		
		else if (command.equals("info_sp"))
			showInfoSpHtml(activeChar);
		else if (command.equals("setxpnot"))
		{
			if (activeChar.getGainXpSpEnable())
			{
				activeChar.setGainXpSpEnable(false);
				activeChar.sendMessage(Config.MESSAGE_XPOFF);
			}
			else
			{
				activeChar.setGainXpSpEnable(true);
				activeChar.sendMessage(Config.MESSAGE_XPON);
			}
			
			showMenuHtml(activeChar);
		}
		
		else if (command.equals("setPartyRefuse"))
		{
			if (activeChar.isPartyInRefuse())
				activeChar.setIsPartyInRefuse(false);
			else
				activeChar.setIsPartyInRefuse(true);
			showMenuHtml(activeChar);
		}
		else if (command.equals("setTradeRefuse"))
		{
			if (activeChar.getTradeRefusal())
				activeChar.setTradeRefusal(false);
			else
				activeChar.setTradeRefusal(true);
			showMenuHtml(activeChar);
		}
		// else if (command.equals("setPartyRefuse")) repitido
		// {
		// if (activeChar.isPartyInRefuse())
		// activeChar.setIsPartyInRefuse(false);
		// else
		// activeChar.setIsPartyInRefuse(true);
		///		showMenuHtml(activeChar);
		// }
		else if (command.equals("setAntiBuff"))
		{
			activeChar.useMagic(SkillTable.getInstance().getInfo(8000, 1), false, false);
			showMenuHtml(activeChar);
		}
		/*
		 * else if (command.equals("optimizeFPS")) { if (activeChar.isOptimizeFPS()) { activeChar.setOptimizeFPS(false); activeChar.setDisableGlowWeapon(false); activeChar.broadcastUserInfoHiden(); activeChar.broadcastUserInfo(); } else { activeChar.setOptimizeFPS(true);
		 * activeChar.setDisableGlowWeapon(true); activeChar.broadcastUserInfoHiden(); activeChar.broadcastUserInfo(); } showMenuHtml(activeChar); }
		 */
		else if (command.equals("setMessageRefuse"))
		{
			if (activeChar.isInRefusalMode())
				activeChar.setInRefusalMode(false);
			else
				activeChar.setInRefusalMode(true);
			showMenuHtml(activeChar);
		}
		// else if (command.equals("setAutoGoldBar")) nao usado mais
		// {
		// if (activeChar.isInAutoGoldBarMode())
		// activeChar.setInAutoGoldBarMode(false);
		// else
		// activeChar.setInAutoGoldBarMode(true);
		// showMenuHtml(activeChar);
		// }
		return false;
	}
	
	/*
	 * else if (command.startsWith("hero_")) { StringTokenizer st = new StringTokenizer(command); st.nextToken(); try { String type = st.nextToken(); switch (type) { case "1_Days": activeChar._hero_days = 1; Buy_Hero(activeChar); break; case "30_Days": activeChar._hero_days = 30;
	 * Buy_Hero(activeChar); break; case "60_Days": activeChar._hero_days = 60; Buy_Hero(activeChar); break; case "90_Days": activeChar._hero_days = 90; Buy_Hero(activeChar); break; case "Eternal": activeChar._hero_days = 999; Buy_Hero(activeChar); break; default:
	 * activeChar.sendMessage("Please select another value!"); showMenuHtml(activeChar); } } catch (Exception e) { activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR"); } } else if (command.startsWith("sethero_")) { if(activeChar.isHero()) { activeChar.sendMessage("You Are Hero!."); return
	 * true; } if (activeChar._hero_days == 30) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_30_DAYS_PRICE, null, true)) { if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long
	 * duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L + activeChar._hero_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2)
	 * == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) {
	 * calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis(); HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._hero_days
	 * >= 30) { while (activeChar._hero_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); activeChar._hero_days -= 30; } } if ((activeChar._hero_days < 30) && (activeChar._hero_days > 0)) { while (activeChar._hero_days > 0) { if ((calendar.get(5) == 28) &&
	 * (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._hero_days -= 1; } } long end_day = calendar.getTimeInMillis();
	 * HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day); } long now = Calendar.getInstance().getTimeInMillis(); long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L; if (_daysleft
	 * < 270L) { activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + "."); } } else { Incorrect_item(activeChar); } } else if (activeChar._hero_days == 60) { if (activeChar.destroyItemByItemId("Donate Coin",
	 * Config.DONATE_COIN_ID, Config.HERO_60_DAYS_PRICE, null, true)) { if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay =
	 * duration; long _daysleft = (endDay - now) / 86400000L + activeChar._hero_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if
	 * ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft
	 * -= 1L; } } long end_day = calendar.getTimeInMillis(); HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._hero_days >= 30) { while (activeChar._hero_days >= 30) { if (calendar.get(2) == 11) {
	 * calendar.roll(1, true); } calendar.roll(2, true); activeChar._hero_days -= 30; } } if ((activeChar._hero_days < 30) && (activeChar._hero_days > 0)) { while (activeChar._hero_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) ==
	 * 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._hero_days -= 1; } } long end_day = calendar.getTimeInMillis(); HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day); } long now =
	 * Calendar.getInstance().getTimeInMillis(); long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L; if (_daysleft < 270L) { activeChar.sendMessage("Your Hero privileges ends at " + new
	 * SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + "."); } } else { Incorrect_item(activeChar); } } else if (activeChar._hero_days == 90) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_90_DAYS_PRICE, null, true)) { if
	 * (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L +
	 * activeChar._hero_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) {
	 * if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis();
	 * HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._hero_days >= 30) { while (activeChar._hero_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true);
	 * activeChar._hero_days -= 30; } } if ((activeChar._hero_days < 30) && (activeChar._hero_days > 0)) { while (activeChar._hero_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1,
	 * true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._hero_days -= 1; } } long end_day = calendar.getTimeInMillis(); HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day); } long now = Calendar.getInstance().getTimeInMillis(); long duration =
	 * HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L; if (_daysleft < 270L) { activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + "."); }
	 * } else { Incorrect_item(activeChar); } } else if (activeChar._hero_days == 999) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_ETERNAL_PRICE, null, true)) { if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId())) { long now =
	 * Calendar.getInstance().getTimeInMillis(); long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L + activeChar._hero_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) {
	 * while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if
	 * (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis(); HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day); } else { Calendar
	 * calendar = Calendar.getInstance(); if (activeChar._hero_days >= 30) { while (activeChar._hero_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); activeChar._hero_days -= 30; } } if ((activeChar._hero_days < 30) && (activeChar._hero_days > 0)) { while
	 * (activeChar._hero_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._hero_days -= 1; } } long end_day =
	 * calendar.getTimeInMillis(); HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day); } activeChar.sendPacket(new ExShowScreenMessage("Congratulations! You have activated Eternal Hero.", 10000)); activeChar.sendMessage("Congratulations! You have activated Eternal Hero."); } else {
	 * Incorrect_item(activeChar); } } else { activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR"); } }
	 */
	
	// VIP COMPRAS FIXED
	/*
	 * else if (command.startsWith("vip_")) { StringTokenizer st = new StringTokenizer(command); st.nextToken(); try { String type = st.nextToken(); switch (type) { case "30_Days": activeChar._vip_days = 30; Buy_Vip(activeChar); break; case "60_Days": activeChar._vip_days = 60; Buy_Vip(activeChar);
	 * break; case "90_Days": activeChar._vip_days = 90; Buy_Vip(activeChar); break; case "Eternal": activeChar._vip_days = 9999; Buy_Vip(activeChar); break; default: activeChar.sendMessage("Please select another value!"); showMenuHtml(activeChar); } } catch (Exception e) {
	 * activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR"); } } else if (command.startsWith("setvip_")) { if(activeChar.isVip()) { activeChar.sendMessage("You Are Vip!."); return true; } if (activeChar._vip_days == 30) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID,
	 * Config.VIP_30_DAYS_PRICE, null, true)) { if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft =
	 * (endDay - now) / 86400000L + activeChar._vip_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft >
	 * 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day =
	 * calendar.getTimeInMillis(); VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._vip_days >= 30) { while (activeChar._vip_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); }
	 * calendar.roll(2, true); activeChar._vip_days -= 30; } } if ((activeChar._vip_days < 30) && (activeChar._vip_days > 0)) { while (activeChar._vip_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) ==
	 * 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._vip_days -= 1; } } long end_day = calendar.getTimeInMillis(); VipManager.getInstance().addVip(activeChar.getObjectId(), end_day); } long now = Calendar.getInstance().getTimeInMillis(); long duration
	 * = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L; if (_daysleft < 270L) { //activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new
	 * Date(duration)) + ".", 10000)); activeChar.sendMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + "."); } } else { Incorrect_item(activeChar); } } //fim 1 dia vip else if (activeChar._vip_days == 60) { if
	 * (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_60_DAYS_PRICE, null, true)) { if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long duration =
	 * VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L + activeChar._vip_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2) == 11) {
	 * calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) {
	 * calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis(); VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._vip_days >=
	 * 30) { while (activeChar._vip_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); activeChar._vip_days -= 30; } } if ((activeChar._vip_days < 30) && (activeChar._vip_days > 0)) { while (activeChar._vip_days > 0) { if ((calendar.get(5) == 28) &&
	 * (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._vip_days -= 1; } } long end_day = calendar.getTimeInMillis();
	 * VipManager.getInstance().addVip(activeChar.getObjectId(), end_day); } long now = Calendar.getInstance().getTimeInMillis(); long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L; if (_daysleft <
	 * 270L) { //activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000)); activeChar.sendMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) +
	 * "."); } } else { Incorrect_item(activeChar); } } else if (activeChar._vip_days == 90) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_90_DAYS_PRICE, null, true)) { if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId())) { long now =
	 * Calendar.getInstance().getTimeInMillis(); long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L + activeChar._vip_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) {
	 * while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if
	 * (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis(); VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day); } else { Calendar calendar
	 * = Calendar.getInstance(); if (activeChar._vip_days >= 30) { while (activeChar._vip_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); activeChar._vip_days -= 30; } } if ((activeChar._vip_days < 30) && (activeChar._vip_days > 0)) { while
	 * (activeChar._vip_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._vip_days -= 1; } } long end_day =
	 * calendar.getTimeInMillis(); VipManager.getInstance().addVip(activeChar.getObjectId(), end_day); } long now = Calendar.getInstance().getTimeInMillis(); long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) /
	 * 86400000L; if (_daysleft < 270L) { //activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000)); activeChar.sendMessage("Your Vip privileges ends at " + new
	 * SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + "."); } } else { Incorrect_item(activeChar); } } else if (activeChar._vip_days == 9999) { if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_ETERNAL_PRICE, null, true)) { if
	 * (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId())) { long now = Calendar.getInstance().getTimeInMillis(); long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId()); long endDay = duration; long _daysleft = (endDay - now) / 86400000L +
	 * activeChar._vip_days + 1L; Calendar calendar = Calendar.getInstance(); if (_daysleft >= 30L) { while (_daysleft >= 30L) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); _daysleft -= 30L; } } if ((_daysleft < 30L) && (_daysleft > 0L)) { while (_daysleft > 0L) {
	 * if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true); } calendar.roll(5, true); _daysleft -= 1L; } } long end_day = calendar.getTimeInMillis();
	 * VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day); } else { Calendar calendar = Calendar.getInstance(); if (activeChar._vip_days >= 30) { while (activeChar._vip_days >= 30) { if (calendar.get(2) == 11) { calendar.roll(1, true); } calendar.roll(2, true);
	 * activeChar._vip_days -= 30; } } if ((activeChar._vip_days < 30) && (activeChar._vip_days > 0)) { while (activeChar._vip_days > 0) { if ((calendar.get(5) == 28) && (calendar.get(2) == 1)) { calendar.roll(2, true); } if (calendar.get(5) == 30) { if (calendar.get(2) == 11) { calendar.roll(1,
	 * true); } calendar.roll(2, true); } calendar.roll(5, true); activeChar._vip_days -= 1; } } long end_day = calendar.getTimeInMillis(); VipManager.getInstance().addVip(activeChar.getObjectId(), end_day); } activeChar.sendPacket(new
	 * ExShowScreenMessage("Congratulations! You have activated Eternal Vip.", 10000)); activeChar.sendMessage("Congratulations! You have activated Eternal Vip."); } else { Incorrect_item(activeChar); } } else { activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR"); } } return true; }
	 */
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	public static void showMissionHtml(Player activeChar)
	{
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<br>");
		sb.append("<center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
		sb.append("<table width=\"300\" bgcolor=\"000000\">");
		sb.append("<tr>");
		sb.append("<td><center>Server Time: <font color=\"ff4d4d\">" + sdf.format(new Date(System.currentTimeMillis())) + "</font></center></td>");
		sb.append("<td><center></center></td>");
		if (Config.ACTIVE_MISSION)
			sb.append("<td><center> Reset all Mission: <font color=\"ff4d4d\">" + MissionReset.getInstance().getNextTime() + "</font></center></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
		
		sb.append("<table><tr><td height=7>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		sb.append("</td></tr></table>");
		
		if (Config.ACTIVE_MISSION_TVT)
			if (activeChar.isTvTCompleted() || activeChar.check_tvt_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">TvT Event x <font color=\"ff4d4d\">( " + Config.MISSION_TVT_CONT + " ) / (" + activeChar.getTvTCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m tvt\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getTvTCont() >= Config.MISSION_TVT_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">TvT Event x <font color=\"ff4d4d\">( " + Config.MISSION_TVT_CONT + " ) / (" + activeChar.getTvTCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m tvt\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">TvT Event x <font color=\"ff4d4d\">( " + Config.MISSION_TVT_CONT + " ) / (" + activeChar.getTvTCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m tvt\">View</a></td>");
				
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			
		// if (Config.ACTIVE_MISSION_PVP) repor no futuro pvp event
		// if (activeChar.isPvPCompleted() || activeChar.check_pvp_hwid(activeChar.getHWID()))
		// {
		// sb.append("<table width=\"300\">");
		// sb.append("<tr>");
		// sb.append("<td width=\"150\" align=\"left\">PvP Event x <font color=\"ff4d4d\">( " + Config.MISSION_PVP_CONT + " ) / ("+ activeChar.getPvPCont() +")</font></td>");
		// sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m pvp\">View</a></td>");
		// sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
		// sb.append("</tr>");
		// sb.append("</table>");
		// sb.append("<table><tr><td height=7>");
		// sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		// sb.append("</td></tr></table>");
		// }
		// else if (activeChar.getPvPCont() >= Config.MISSION_PVP_CONT)
		// {
		// sb.append("<table width=\"300\">");
		// sb.append("<tr>");
		// sb.append("<td width=\"150\" align=\"left\">PvP Event x <font color=\"ff4d4d\">( " + Config.MISSION_PVP_CONT + " ) / ("+ activeChar.getPvPCont() +")</font></td>");
		// sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m pvp\">View</a></td>");
		// sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
		// sb.append("</tr>");
		// sb.append("</table>");
		// sb.append("<table><tr><td height=7>");
		// sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		// sb.append("</td></tr></table>");
		// }
		// else
		// {
		// sb.append("<table width=\"300\">");
		// sb.append("<tr>");
		// sb.append("<td width=\"150\" align=\"left\">PvP Event x <font color=\"ff4d4d\">( " + Config.MISSION_PVP_CONT + " ) / ("+ activeChar.getPvPCont() +")</font></td>");
		// sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m pvp\">View</a></td>");
		// sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
		// sb.append("</tr>");
		// sb.append("</table>");
		// sb.append("<table><tr><td height=7>");
		// sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		// sb.append("</td></tr></table>");
		// }
		
		if (Config.ACTIVE_MISSION_RAID)
			if (activeChar.isRaidCompleted() || activeChar.check_raid_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">RaidBoss x <font color=\"ff4d4d\">( " + Config.MISSION_RAID_CONT + " ) / (" + activeChar.getRaidCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m raid\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getRaidCont() >= Config.MISSION_RAID_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">RaidBoss x <font color=\"ff4d4d\">( " + Config.MISSION_RAID_CONT + " ) / (" + activeChar.getRaidCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m raid\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">RaidBoss x <font color=\"ff4d4d\">( " + Config.MISSION_RAID_CONT + " ) / (" + activeChar.getRaidCont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m raid\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		
		if (Config.ACTIVE_MISSION_1X1)
			if (activeChar.is1x1Completed() || activeChar.check_1x1_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (1x1) x <font color=\"ff4d4d\">( " + Config.MISSION_1X1_CONT + " ) / (" + activeChar.getTournament1x1Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 1x1\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getTournament1x1Cont() >= Config.MISSION_1X1_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (1x1) x <font color=\"ff4d4d\">( " + Config.MISSION_1X1_CONT + " ) / (" + activeChar.getTournament1x1Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 1x1\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (1x1) x <font color=\"ff4d4d\">( " + Config.MISSION_1X1_CONT + " ) / (" + activeChar.getTournament1x1Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 1x1\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		
		if (Config.ACTIVE_MISSION_3X3)
			if (activeChar.is2x2Completed() || activeChar.check_2x2_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (3x3) x <font color=\"ff4d4d\">( " + Config.MISSION_3X3_CONT + " ) / (" + activeChar.getTournament2x2Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 3x3\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getTournament2x2Cont() >= Config.MISSION_3X3_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (3x3) x <font color=\"ff4d4d\">( " + Config.MISSION_3X3_CONT + " ) / (" + activeChar.getTournament2x2Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 3x3\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (3x3) x <font color=\"ff4d4d\">( " + Config.MISSION_3X3_CONT + " ) / (" + activeChar.getTournament2x2Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 3x3\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		if (Config.ACTIVE_MISSION_5X5)
			if (activeChar.is5x5Completed() || activeChar.check_5x5_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (5x5) x <font color=\"ff4d4d\">( " + Config.MISSION_5X5_CONT + " ) / (" + activeChar.getTournament5x5Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 5x5\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getTournament5x5Cont() >= Config.MISSION_5X5_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (5x5) x <font color=\"ff4d4d\">( " + Config.MISSION_5X5_CONT + " ) / (" + activeChar.getTournament5x5Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 5x5\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (5x5) x <font color=\"ff4d4d\">( " + Config.MISSION_5X5_CONT + " ) / (" + activeChar.getTournament5x5Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 5x5\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		if (Config.ACTIVE_MISSION_9X9)
			if (activeChar.is9x9Completed() || activeChar.check_9x9_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (9x9) x <font color=\"ff4d4d\">( " + Config.MISSION_9X9_CONT + " ) / (" + activeChar.getTournament9x9Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 9x9\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getTournament9x9Cont() >= Config.MISSION_9X9_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (9x9) x <font color=\"ff4d4d\">( " + Config.MISSION_9X9_CONT + " ) / (" + activeChar.getTournament9x9Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 9x9\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Tour (9x9) x <font color=\"ff4d4d\">( " + Config.MISSION_9X9_CONT + " ) / (" + activeChar.getTournament9x9Cont() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m 9x9\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		
		if (Config.ACTIVE_MISSION_PARTY_MOB)
			if (activeChar.isPartyMobCompleted() || activeChar.check_party_mob_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Party Zone x <font color=\"ff4d4d\">( " + Config.MISSION_PARTY_MOB_CONT + " ) / (" + activeChar.getPartyMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m party_mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getPartyMonsterKills() >= Config.MISSION_PARTY_MOB_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Party Zone x <font color=\"ff4d4d\">( " + Config.MISSION_PARTY_MOB_CONT + " ) / (" + activeChar.getPartyMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m party_mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Party Zone x <font color=\"ff4d4d\">( " + Config.MISSION_PARTY_MOB_CONT + " ) / (" + activeChar.getPartyMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m party_mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		
		if (Config.ACTIVE_MISSION_MOB)
			if (activeChar.isMobCompleted() || activeChar.check_mob_hwid(activeChar.getHWID()))
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Solo Zone x <font color=\"ff4d4d\">( " + Config.MISSION_MOB_CONT + " ) / (" + activeChar.getMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"LEVEL\">Received</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else if (activeChar.getMonsterKills() >= Config.MISSION_MOB_CONT)
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Solo Zone x <font color=\"ff4d4d\">( " + Config.MISSION_MOB_CONT + " ) / (" + activeChar.getMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ffffff\">Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
			else
			{
				sb.append("<table width=\"300\">");
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\">Solo Zone x <font color=\"ff4d4d\">( " + Config.MISSION_MOB_CONT + " ) / (" + activeChar.getMonsterKills() + ")</font></td>");
				sb.append("<td width=\"50\" align=\"right\"><a action=\"bypass voiced_select_m mob\">View</a></td>");
				sb.append("<td width=\"100\" align=\"center\"><font color=\"ff0000\">Not Completed</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table><tr><td height=7>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("</td></tr></table>");
			}
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Mission.htm");
		html.replace("%mission%", sb.toString());
		activeChar.sendPacket(html);
	}
	
	/*
	 * public void Buy_Hero(Player player) { player.sendPacket(ActionFailed.STATIC_PACKET); String filename = "data/html/mods/menu/hero.htm"; NpcHtmlMessage html = new NpcHtmlMessage(0); html.setFile(filename); if (player._hero_days == 0) { html.replace("%coin%", "0"); } else if (player._hero_days
	 * == 30) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_30_DAYS_PRICE + "</font>"); } else if (player._hero_days == 60) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_60_DAYS_PRICE + "</font>"); } else if (player._hero_days == 90) { html.replace("%coin%",
	 * "<font color=\"00FF00\">" + Config.HERO_90_DAYS_PRICE + "</font>"); } else if (player._hero_days >= 360) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_ETERNAL_PRICE + "</font>"); } if (player._hero_days == 0) { html.replace("%hero_days%", "0"); } else if (player._hero_days
	 * == 30) { html.replace("%hero_days%", "<font color=\"00FF00\">30</font> Days"); } else if (player._hero_days == 60) { html.replace("%hero_days%", "<font color=\"00FF00\">60</font> Days"); } else if (player._hero_days == 90) { html.replace("%hero_days%",
	 * "<font color=\"00FF00\">90</font> Days"); } else if (player._hero_days >= 360) { html.replace("%hero_days%", "<font color=\"00FF00\">Eternal</font>"); } player.sendPacket(html); }
	 */
	public void Incorrect_item(Player activeChar)
	{
		// activeChar._aio_days = 0;
		// activeChar._vip_days = 0;
		// activeChar._hero_days = 0;
		// activeChar._hero_days = 0;
		// activeChar._class_id = 0;
		// activeChar._sex_id = 0;
		// activeChar._change_Name = "";
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/menu/incorrect.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	/*
	 * public void Buy_Vip(Player player) { player.sendPacket(ActionFailed.STATIC_PACKET); String filename = "data/html/mods/menu/vip.htm"; NpcHtmlMessage html = new NpcHtmlMessage(0); html.setFile(filename); if (player._vip_days == 0) { html.replace("%coin%", "0"); } else if (player._vip_days ==
	 * 30) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_30_DAYS_PRICE + "</font>"); } else if (player._vip_days == 60) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_60_DAYS_PRICE + "</font>"); } else if (player._vip_days == 90) { html.replace("%coin%",
	 * "<font color=\"00FF00\">" + Config.VIP_90_DAYS_PRICE + "</font>"); } else if (player._vip_days >= 360) { html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_ETERNAL_PRICE + "</font>"); } if (player._vip_days == 0) { html.replace("%vip_days%", "0"); } else if (player._vip_days == 30)
	 * { html.replace("%vip_days%", "<font color=\"00FF00\">30</font> Days"); } else if (player._vip_days == 60) { html.replace("%vip_days%", "<font color=\"00FF00\">60</font> Days"); } else if (player._vip_days == 90) { html.replace("%vip_days%", "<font color=\"00FF00\">90</font> Days"); } else if
	 * (player._vip_days >= 360) { html.replace("%vip_days%", "<font color=\"00FF00\">Eternal</font>"); } player.sendPacket(html); }
	 */
	
	public static void showEventTimes(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/EventsTime.htm");
		html.replace("%dat%", (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())));
		html.replace("%time%", (new SimpleDateFormat("HH:mm:ss")).format(new Date(System.currentTimeMillis())));
		html.replace("%name%", activeChar.getName());
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		// Reward Solo Event na htm Event Time.
		if (Config.SOLO_FARM_BY_TIME_OF_DAY)
		{
			if (RewardSoloEvent.is_started())
				html.replace("%solofarm%", "In Progress");
			else
				html.replace("%solofarm%", InitialSoloEvent.getInstance().getRestartNextTime().toString());
		}
		if (Config.CHAMPION_FARM_BY_TIME_OF_DAY)
		{
			if (ChampionInvade.is_started())
				html.replace("%champion%", "In Progress");
			else
				html.replace("%champion%", InitialChampionInvade.getInstance().getRestartNextTime().toString());
		}
		if (Config.SOLO_BOSS_EVENT)
		{
			if (SoloBoss.is_started())
				html.replace("%bossSolo%", "In Progress");
			else
				html.replace("%bossSolo%", InitialSoloBossEvent.getInstance().getRestartNextTime().toString());
		}
		html.replace("%lmTime%", CheckNextEvent.getInstance().getNextLMTime());
		
		// BossEvent na html EventsTime.
		if (KTBConfig.KTB_EVENT_ENABLED)
		{
			if (KTBEvent.isStarted())
				html.replace("%ktbTime%", "In Progress");
			else
				html.replace("%ktbTime%", KTBManager.getInstance().getNextTime());
		}
		if (FOSConfig.FOS_EVENT_ENABLED)
		{
			if (FOSEvent.isStarted())
				html.replace("%FOSTime%", "In Progress");
			else
				html.replace("%FOSTime%", FOSManager.getInstance().getNextTime());
		}
		if (DMConfig.DM_EVENT_ENABLED)
		{
			if (DMEvent.isStarted())
				html.replace("%DMTime%", "In Progress");
			else
				html.replace("%DMTime%", DMManager.getInstance().getNextTime());
		}
		if (TvTConfig.TVT_EVENT_ENABLED)
		{
			if (TvTEvent.isStarted())
			{
				html.replace("%tvt%", "In Progress");
			}
			else
			{
				html.replace("%tvt%", CheckNextEvent.getInstance().getNextTvTTime());
			}
		}
		
		if (CTFConfig.CTF_EVENT_ENABLED)
		{
			if (CTFEvent.isStarted())
			{
				html.replace("%ctf%", "In Progress");
			}
			else
			{
				html.replace("%ctf%", CheckNextEvent.getInstance().getNextCTFTime());
			}
		}
		
		// Spoil Event EventsTime.
		if (Config.START_SPOIL)
		{
			if (SpoilEvent.is_started())
				html.replace("%spoilevent%", "In Progress");
			else
				html.replace("%spoilevent%", InitialSpoilEvent.getInstance().getRestartNextTime().toString());
		}
		
		// Party farm EventsTime.
		if (Config.START_PARTY)
		{
			if (PartyFarm.is_started())
				html.replace("%partyfarm%", "In Progress");
			else
				html.replace("%partyfarm%", InitialPartyFarm.getInstance().getRestartNextTime().toString());
		}
		if (Config.PVP_EVENT_ENABLED)
		{
			if (PvPEvent.getInstance().isActive())
				html.replace("%pvp%", "In Progress");
			else
				html.replace("%pvp%", PvPEventNext.getInstance().getNextTime().toString());
		}
		// //MISSION Na htmTime.
		if (Config.ACTIVE_MISSION)
		{
			html.replace("%mission%", MissionReset.getInstance().getNextTime().toString());
		}
		
		// //CTF na html EventsTime.
		
		// //Tournament na html EventsTime.
		if (ArenaConfig.TOURNAMENT_EVENT_TIME)
		{
			if (ArenaTask.is_started())
				html.replace("%arena%", "In Progress");
			else
				html.replace("%arena%", ArenaEvent.getInstance().getNextTime().toString());
		}
		// VALIDATION
		// Mostrar tempo olympiadas html EventsTime.htm
		// long milliToEnd = Olympiad._period != OlympiadState.COMPETITION ? Olympiad.getMillisToValidationEnd() / 1000L : Olympiad.getMillisToOlympiadEnd() / 1000L;
		long milliToEnd = Olympiad._period != 0 ? Olympiad.getMillisToValidationEnd() / 1000L : Olympiad.getMillisToOlympiadEnd() / 1000L;
		double countDown = (milliToEnd - milliToEnd % 60L) / 60L;
		int numMins = (int) Math.floor(countDown % 60D);
		countDown = (countDown - numMins) / 60D;
		int numHours = (int) Math.floor(countDown % 24D);
		int numDays = (int) Math.floor((countDown - numHours) / 24D);
		
		if (Olympiad._validationEnd == 0 || Olympiad._period == 0)
			// if (Olympiad._validationEnd == 0 || Olympiad._period == OlympiadState.VALIDATION )
			html.replace("%olym%", "Olym over: " + numDays + " day(s), " + numHours + " hour(s), " + numMins + " minute(s).");
		else
			html.replace("%olym%", "Olym start: " + numDays + " day(s), " + numHours + " hour(s), " + numMins + " minute(s).");
		// Fim Events Time
		
		activeChar.sendPacket(html);
	}
	
	public static void showMenuHtml(Player activeChar)
	{
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Menu.htm");
		html.replace("%dat%", (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())));
		html.replace("%time%", (new SimpleDateFormat("HH:mm:ss")).format(new Date(System.currentTimeMillis())));
		html.replace("%cantGainXP%", activeChar.getGainXpSpEnable() ? "checked" : "unable");
		// html.replace("%AutoFarmActived%", AutofarmManager.INSTANCE.isAutofarming(activeChar) ? "checked" : "unable");
		html.replace("%partyRefusal%", activeChar.isPartyInRefuse() ? "checked" : "unable");
		html.replace("%tradeRefusal%", activeChar.getTradeRefusal() ? "checked" : "unable");
		html.replace("%messageRefusal%", activeChar.isInRefusalMode() ? "checked" : "unable");
		html.replace("%setAntiBuff%", activeChar.isBuffProtected() ? "checked" : "unable");
		html.replace("%hidePlayer%", !activeChar.isHidingPlayers() ? "unable" : "checked");
		html.replace("%hideEnchantGlow%", activeChar.isDisableGlowWeapon() ? "checked" : "unable");
		// html.replace("%goldBar%", activeChar.isInAutoGoldBarMode() ? "checked" : "unable");
		html.replace("%setAutoGb%", activeChar.isAutoGb() ? "checked" : "unable");
		// html.replace("%optimize%", activeChar.isOptimizeFPS() ? "checked" : "unable");
		// html.replace("%hideSkillAnimation%", activeChar.isDisableSkillAnimation() ? ACTIVED : DESATIVED);
		// html.replace("%hideSkillAnimationButton%", activeChar.isDisableSkillAnimation() ? ACTIVED : DESATIVED);
		// html.replace("%setAutoPotion%", activeChar.isAutoPotion() ? ACTIVED : DESATIVED);
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%name%", activeChar.getName());
		
		// if (activeChar.isDisableGlowWeapon())
		// html.replace("%html_glow%", "<td align=center width=5><button value=\"\" action=\"bypass voiced_setDisableGlowWeapon\" width=16 height=16 back=\"L2UI_CH3.joypad_lock_down\" fore=\"L2UI_CH3.joypad_lock\"></td>");
		// else
		// html.replace("%html_glow%", "<td align=center width=5><button value=\"\" action=\"bypass voiced_setDisableGlowWeapon\" width=16 height=16 back=\"L2UI_CH3.joypad_unlock_down\" fore=\"L2UI_CH3.joypad_unlock\"></td>");
		
		// BossEvent na html EventsTime.
		/*
		 * if(Config.ENABLE_BOSS_EVENT) { if(BossEvent.started) html.replace("%bossevent%", "In Progress"); else html.replace("%bossevent%", NextBossEvent.getInstance().getNextTime().toString() ); }
		 */
		// == OlympiadState.COMPETITION
		// VALIDATION
		// Mostrar tempo olympiadas html EventsTime.htm
		// long milliToEnd = Olympiad._period != OlympiadState.COMPETITION ? Olympiad.getMillisToValidationEnd() / 1000L : Olympiad.getMillisToOlympiadEnd() / 1000L;
		long milliToEnd = Olympiad._period != 0 ? Olympiad.getMillisToValidationEnd() / 1000L : Olympiad.getMillisToOlympiadEnd() / 1000L;
		double countDown = (milliToEnd - milliToEnd % 60L) / 60L;
		int numMins = (int) Math.floor(countDown % 60D);
		countDown = (countDown - numMins) / 60D;
		int numHours = (int) Math.floor(countDown % 24D);
		int numDays = (int) Math.floor((countDown - numHours) / 24D);
		
		if (Olympiad._validationEnd == 0 || Olympiad._period == 0)
			// if (Olympiad._validationEnd == 0 || Olympiad._period == OlympiadState.VALIDATION )
			html.replace("%olym%", "Olym over: " + numDays + " day(s), " + numHours + " hour(s), " + numMins + " minute(s).");
		else
			html.replace("%olym%", "Olym start: " + numDays + " day(s), " + numHours + " hour(s), " + numMins + " minute(s).");
		// Fim Events Time
		
		activeChar.sendPacket(html);
	}
	
	private static void showAuctionHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/AuctionerManager.htm");
		activeChar.sendPacket(html);
	}
	
	private static void showInfoHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Info_Server.htm");
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%rate_xp%", String.valueOf(Config.RATE_XP));
		html.replace("%rate_sp%", String.valueOf(Config.RATE_SP));
		html.replace("%rate_party_xp%", String.valueOf(Config.RATE_PARTY_XP));
		html.replace("%rate_adena%", String.valueOf(Config.RATE_DROP_ADENA));
		html.replace("%rate_party_sp%", String.valueOf(Config.RATE_PARTY_SP));
		html.replace("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
		html.replace("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
		html.replace("%rate_drop_manor%", String.valueOf(Config.RATE_DROP_MANOR));
		html.replace("%pet_rate_xp%", String.valueOf(Config.PET_XP_RATE));
		html.replace("%sineater_rate_xp%", String.valueOf(Config.SINEATER_XP_RATE));
		html.replace("%pet_food_rate%", String.valueOf(Config.PET_FOOD_RATE));
		activeChar.sendPacket(html);
	}
	
	private static void showInfoPtHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Info_Server.htm");
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%rate_xp%", String.valueOf(Config.RATE_XP));
		html.replace("%rate_sp%", String.valueOf(Config.RATE_SP));
		html.replace("%rate_party_xp%", String.valueOf(Config.RATE_PARTY_XP));
		html.replace("%rate_adena%", String.valueOf(Config.RATE_DROP_ADENA));
		html.replace("%rate_party_sp%", String.valueOf(Config.RATE_PARTY_SP));
		html.replace("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
		html.replace("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
		html.replace("%rate_drop_manor%", String.valueOf(Config.RATE_DROP_MANOR));
		html.replace("%pet_rate_xp%", String.valueOf(Config.PET_XP_RATE));
		html.replace("%sineater_rate_xp%", String.valueOf(Config.SINEATER_XP_RATE));
		html.replace("%pet_food_rate%", String.valueOf(Config.PET_FOOD_RATE));
		activeChar.sendPacket(html);
	}
	
	/*
	 * public static void sendVipWindow(Player activeChar) { String autofarmOn= "<button width=38 height=38 back=\"L2UI_NewTex.AutomaticPlay.CombatBTNOff_Over\" fore=\"L2UI_NewTex.AutomaticPlay.CombatBTNON_Normal\" action=\"bypass voiced_farm\" value=\"\">"; String autofarmOff=
	 * "<button width=38 height=38 back=\"L2UI_NewTex.AutomaticPlay.CombatBTNON_Over\" fore=\"L2UI_NewTex.AutomaticPlay.CombatBTNOff_Normal\" action=\"bypass voiced_farm\" value=\"\">"; NpcHtmlMessage html = new NpcHtmlMessage(0); html.setFile("data/html/mods/menu/AutoFarm.htm");
	 * html.replace("%AutoFarmActived%", AutofarmManager.INSTANCE.isAutofarming(activeChar) ? "<img src=\"panel.online\" width=\"16\" height=\"16\">" : "<img src=\"panel.offline\" width=\"16\" height=\"16\">"); //html.replace("%AutoFarmRange%",AutofarmConstants.targetingRadius.intValue());
	 * html.replace("%autoFarmButton%", AutofarmManager.INSTANCE.isAutofarming(activeChar) ? autofarmOn : autofarmOff); activeChar.sendPacket(html); }
	 */
	private static void showInfoSpHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Info_Server.htm");
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%rate_xp%", String.valueOf(Config.RATE_XP));
		html.replace("%rate_sp%", String.valueOf(Config.RATE_SP));
		html.replace("%rate_party_xp%", String.valueOf(Config.RATE_PARTY_XP));
		html.replace("%rate_adena%", String.valueOf(Config.RATE_DROP_ADENA));
		html.replace("%rate_party_sp%", String.valueOf(Config.RATE_PARTY_SP));
		html.replace("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
		html.replace("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
		html.replace("%rate_drop_manor%", String.valueOf(Config.RATE_DROP_MANOR));
		html.replace("%pet_rate_xp%", String.valueOf(Config.PET_XP_RATE));
		html.replace("%sineater_rate_xp%", String.valueOf(Config.SINEATER_XP_RATE));
		html.replace("%pet_food_rate%", String.valueOf(Config.PET_FOOD_RATE));
		activeChar.sendPacket(html);
	}
	
	// *******Config Section*****************/
	// *********************** Potion ItemID
	// private static final int ID_HEAL_CP = 5592;
	// private static final int ID_HEAL_MP = 728;
	// private static final int ID_HEAL_HP = 1539;
	// *********************** USE FULL
	// Enable/Disable voicecoomand
	// private static final boolean ACP_ON = Config.ENABLE_COMMAND_AUTO_POTION;
	// Min lvl for use ACP
	// private static final int ACP_MIN_LVL = 0;
	// private static final int ACP_HP_LVL = 70;
	// private static final int ACP_CP_LVL = 70;
	// / private static final int ACP_MP_LVL = 70;
	// private static final int ACP_MILI_SECONDS_FOR_LOOP = 1000;
	// Automatic regen : Default ACP/MP/HP
	// private static final boolean ACP_CP = true;
	// private static final boolean ACP_MP = true;
	// private static final boolean ACP_HP = true;
	// static final HashMap<String, Thread> userAcpMap = new HashMap<>();
	/*
	 * private class AcpHealer implements Runnable { L2PcInstance activeChar; public AcpHealer(L2PcInstance activeChar) { this.activeChar = activeChar; }
	 * @Override public void run() { try { while (true) { // Checking the level if (activeChar.getLevel() >= ACP_MIN_LVL) { // Checking if we have at least one can of something ItemInstance cpBottle = activeChar.getInventory().getItemByItemId(ID_HEAL_CP); ItemInstance hpBottle =
	 * activeChar.getInventory().getItemByItemId(ID_HEAL_HP); ItemInstance mpBottle = activeChar.getInventory().getItemByItemId(ID_HEAL_MP); if (hpBottle != null && hpBottle.getCount() > 0) { // Checking our health if ((activeChar.getStatus().getCurrentHp() / activeChar.getMaxHp()) * 100 <
	 * ACP_HP_LVL && ACP_HP) { IItemHandler handlerHP = ItemHandler.getInstance().getItemHandler(hpBottle.getEtcItem()); if(activeChar.isDead()) { activeChar.sendMessage("nao e permitido Auto Potion morto."); activeChar.setAutoPotion(false); return ; } if(activeChar._inEventCTF &&
	 * activeChar._haveFlagCTF) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido usar Auto Potion Com arma Do CTF."); return ; } if(activeChar.isInOlympiadMode()) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido Auto Potion Olympiadas."); return ;
	 * } if (handlerHP != null) { handlerHP.useItem(activeChar, hpBottle, true); //activeChar.sendMessage("ACP: Restored HP"); } } // Checking our CP level if (cpBottle != null && cpBottle.getCount() > 0) { if ((activeChar.getStatus().getCurrentCp() / activeChar.getMaxCp()) * 100 < ACP_CP_LVL &&
	 * ACP_CP) { IItemHandler handlerCP = ItemHandler.getInstance().getItemHandler(cpBottle.getEtcItem()); if(activeChar.isDead()) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido usar Auto Potion morto."); return ; } if(activeChar._inEventCTF && activeChar._haveFlagCTF) {
	 * activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido usar Auto Potion Com arma Do CTF."); return ; } if(activeChar.isInOlympiadMode()) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido usar Auto Potion nas Olympiadas."); return ; } if (handlerCP !=
	 * null) { handlerCP.useItem(activeChar, cpBottle, true); // activeChar.sendMessage("ACP: Restored CP"); } } } // Checking our MP level if (mpBottle != null && mpBottle.getCount() > 0) { if ((activeChar.getStatus().getCurrentMp() / activeChar.getMaxMp()) * 100 < ACP_MP_LVL && ACP_MP) {
	 * IItemHandler handlerMP = ItemHandler.getInstance().getItemHandler(mpBottle.getEtcItem()); if(activeChar.isDead()) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido Auto Potion morto."); return ; } if(activeChar._inEventCTF && activeChar._haveFlagCTF) {
	 * activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido usar Auto Potion Com arma Do CTF."); return ; } if(activeChar.isInOlympiadMode()) { activeChar.setAutoPotion(false); activeChar.sendMessage("nao e permitido Auto Potion Olympiadas."); return ; } if (handlerMP != null) {
	 * handlerMP.useItem(activeChar, mpBottle, true); // activeChar.sendMessage("ACP: Restored MP"); } } } } else { activeChar.sendMessage("[Auto Potion] Incorrect item count"); activeChar.setAutoPotion(false); VoicedMenu.showMenuHtml(activeChar); return; } } else {
	 * activeChar.sendMessage("Available only " + ACP_MIN_LVL + " level!"); return; } Thread.sleep(ACP_MILI_SECONDS_FOR_LOOP); } } catch (InterruptedException e) { //nothing } catch (Exception e) { //_log.warning(e.getMessage()); e.printStackTrace(); Thread.currentThread().interrupt(); } finally {
	 * userAcpMap.remove(activeChar.toString()); } } }
	 */
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}