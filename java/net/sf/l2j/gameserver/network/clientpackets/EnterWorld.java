package net.sf.l2j.gameserver.network.clientpackets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

import net.sf.l2j.Config;
import net.sf.l2j.dolls.DollsTable;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.championInvade.ChampionInvade;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.lastman.LMEvent;
import net.sf.l2j.event.partyfarm.PartyFarm;
import net.sf.l2j.event.rewardsoloevent.RewardSoloEvent;
import net.sf.l2j.event.spoil.SpoilEvent;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.communitybbs.Manager.MailBBSManager;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportWhereType;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.CrownManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.PetitionManager;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.custom.HeroManagerCustom;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Clan.SubPledge;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.RemoteClassMaster;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Player.PunishLevel;
import net.sf.l2j.gameserver.model.base.ClassRace;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.entity.Couple;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExMailArrived;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.FriendList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.network.serverpackets.PledgeStatusChanged;
import net.sf.l2j.gameserver.network.serverpackets.QuestList;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scriptings.Quest;
import net.sf.l2j.gameserver.scriptings.QuestState;
import net.sf.l2j.gameserver.scriptings.ScriptManager;
import net.sf.l2j.gameserver.skills.AbnormalEffect;
import net.sf.l2j.gameserver.taskmanager.GameTimeController;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.gameserver.util.HWID;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.hwid.Hwid;

public class EnterWorld extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// this is just a trigger packet. it has no content
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar is null...");
			getClient().closeNow();
			return;
		}
		
		if (activeChar.isGM())
		{
			activeChar.getAppearance().setNameColor(Config.MASTERACCESS_NAME_COLOR);
			activeChar.getAppearance().setTitleColor(Config.MASTERACCESS_TITLE_COLOR);
			
			if (Config.ENABLE_NAME_GMS_CHECK)
			{
				if (!Util.contains(Config.GM_NAMES, activeChar.getName()))
				{
					activeChar.setPunishLevel(PunishLevel.ACC, 0);
				}
			}
			
			if (Config.GM_SUPER_HASTE)
				SkillTable.getInstance().getInfo(7029, 4).getEffects(activeChar, activeChar);
			
			if (Config.GM_STARTUP_INVULNERABLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel()))
				activeChar.setIsInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminCommandAccessRights.getInstance().hasAccess("admin_hide", activeChar.getAccessLevel()))
				activeChar.getAppearance().setInvisible();
			
			if (Config.GM_STARTUP_SILENCE && AdminCommandAccessRights.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel()))
				activeChar.setInRefusalMode(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminCommandAccessRights.getInstance().hasAccess("admin_gmlist", activeChar.getAccessLevel()))
				GmListTable.getInstance().addGm(activeChar, false);
			else
				GmListTable.getInstance().addGm(activeChar, true);
		}
		
		// Set dead status if applies
		if (activeChar.getCurrentHp() < 0.5)
			activeChar.setIsDead(true);
		
		final L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			activeChar.sendPacket(new PledgeSkillList(clan));
			notifyClanMembers(activeChar);
			notifySponsorOrApprentice(activeChar);
			
			// Add message at connexion if clanHall not paid.
			final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (clanHall != null)
			{
				if (!clanHall.getPaid())
					activeChar.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			}
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;
				
				if (siege.checkIsAttacker(clan))
				{
					activeChar.setSiegeState((byte) 1);
					activeChar.setSiegeSide(castle.getCastleId());
				}
				else if (siege.checkIsDefender(clan))
				{
					activeChar.setSiegeState((byte) 2);
					activeChar.setSiegeSide(castle.getCastleId());
				}
				
			}
			
			activeChar.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				activeChar.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
			
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.sendPacket(new PledgeStatusChanged(clan));
			CrownManager.getInstance().checkCrowns(activeChar);
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSigns.getInstance().isSealValidationPeriod() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) != SevenSigns.CABAL_NULL)
		{
			int cabal = SevenSigns.getInstance().getPlayerCabal(activeChar.getObjectId());
			if (cabal != SevenSigns.CABAL_NULL)
			{
				if (cabal == SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
					activeChar.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
				else
					activeChar.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
			}
		}
		else
		{
			activeChar.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
			activeChar.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
		}
		
		if (Config.BLOCK_SERVER_PLAYERS)
		{
			if (!activeChar.isGM())
			{
				activeChar.setIsParalyzed(true);
				activeChar.startAbnormalEffect(2048);
				activeChar.startAbnormalEffect(AbnormalEffect.ROOT);
				this.initCountdown(activeChar, 5);
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.getClient().closeNow();
					}
				}, 5000L);
			}
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
		activeChar.spawnMe();
		
		// engage and notify Partner
		if (Config.ALLOW_WEDDING)
			engage(activeChar);
		
		// Announcements, welcome & Seven signs period messages
		activeChar.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		AnnouncementTable.getInstance().showAnnouncements(activeChar, false);
		
		DollsTable.refreshAllRuneSkills(activeChar);
		
		if (Config.WAIT_TIME_FOR_SPAWN_ALL_NPCS)
		{
			LocalTime now = LocalTime.now();
			LocalTime spawnTime = LocalTime.parse(Config.SPAWN_ALL_NPCS_TIME_OPEN_SERVER);
			
			if (now.isBefore(spawnTime))
			{
				String msg = "All server NPCs will be spawned at " + Config.SPAWN_ALL_NPCS_TIME_OPEN_SERVER + ".";
				
				// Mensagem bonita no centro da tela
				ExShowScreenMessage screenMsg = new ExShowScreenMessage(msg, // texto
					10000, // duração em milissegundos (10 seg)
					ExShowScreenMessage.SMPOS.TOP_CENTER, // posição
					false // scroll (false = fixa)
				);
				activeChar.sendPacket(screenMsg);
				activeChar.sendChatMessage(0, Say2.TELL, "Server", msg);
			}
		}
		
		if (!activeChar.isInOlympiadMode())
		{
			activeChar.removeItensFinishOly();
			// return;
		}
		
		if (!activeChar.checkMultiBox() && Config.ALLOW_DUALBOX)
		{
			activeChar.sendPacket(new ExShowScreenMessage("I'm sorry, but multibox is not allowed here, Disconnect 5 Segunds", 5000));
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					activeChar.logout(true);
				}
			}, 5000);
			
		}
		
	 
		// IPLog.auditGMAction(activeChar.getName(), activeChar.getClient().getConnection().getInetAddress().getHostAddress(), activeChar.getHWID());
		HWID.auditGMAction(activeChar.getHWID(), activeChar.getName());
		Hwid.enterlog(activeChar, getClient());
		
		TvTEvent.onLogin(activeChar);
		CTFEvent.onLogin(activeChar);
		DMEvent.onLogin(activeChar);
		KTBEvent.onLogin(activeChar);
		LMEvent.onLogin(activeChar);
		FOSEvent.onLogin(activeChar);
		if (Config.ENABLE_VIP_SYSTEM)
		{
			if (activeChar.isVip())
			{
				onEnterVip(activeChar);
				activeChar.broadcastUserInfo();
			}
			ThreadPool.scheduleAtFixedRate(() -> {
				for (Player p : L2World.getInstance().getPlayers())
				{
					if (p != null && p.isVip())
					{
						checkVipExpiration(p);
					}
				}
			}, 0, 60_000);
		}
		if (Config.ENABLE_AIO_SYSTEM)
		{
			if (activeChar.isAio())
			{
				onEnterAio(activeChar);
				activeChar.broadcastUserInfo();
			}
			ThreadPool.scheduleAtFixedRate(() -> {
				for (Player p : L2World.getInstance().getPlayers())
				{
					if (p != null && p.isAio())
					{
						checkAioExpiration(p);
					}
				}
			}, 0, 60_000);
		}
		
		if (Config.ALLOW_VIP_NCOLOR && activeChar.isVip())
		{
			activeChar.getAppearance().setNameColor(Config.VIP_NCOLOR);
			activeChar.broadcastUserInfo();
		}
		
		if (Config.ALLOW_VIP_TCOLOR && activeChar.isVip())
		{
			activeChar.getAppearance().setTitleColor(Config.VIP_TCOLOR);
			activeChar.broadcastUserInfo();
		}
		if ((SpoilEvent.is_started() && Config.SPOIL_MESSAGE_ENABLED))
		{
			activeChar.sendPacket(new CreatureSay(0, 3, ".", "" + Config.SPOIL_FARM_MESSAGE_TEXT + ":."));
		}
		if ((RewardSoloEvent.is_started()) && (Config.SOLO_MESSAGE_ENABLED))
		{
			activeChar.sendPacket(new CreatureSay(0, 3, ".", "" + Config.SOLO_FARM_MESSAGE_TEXT + ":."));
		}
		if ((ChampionInvade.is_started()) && (Config.CHAMPION_MESSAGE_ENABLED))
		{
			activeChar.sendPacket(new CreatureSay(0, 3, ".", "" + Config.CHAMPION_FARM_MESSAGE_TEXT + ":."));
		}
		if ((PartyFarm.is_started() && Config.PARTY_MESSAGE_ENABLED))
		{
			activeChar.sendPacket(new CreatureSay(0, 3, ".", "" + Config.PARTY_FARM_MESSAGE_TEXT + ":."));
		}
		if (Config.PCB_ENABLE)
		{
			activeChar.showPcBangWindow();
		}
		if (Config.ANNOUNCE_CASTLE_LORDS && !activeChar.isGM())
		{
			notifyCastleOwner(activeChar);
		}
		if (Config.ALT_OLY_END_ANNOUNCE)
		{
			Olympiad.olympiadEnd(activeChar);
		}
		
		if (Config.ANNOUNCE_HERO && ((HeroManagerCustom.getInstance().hasHeroPrivileges(activeChar.getObjectId()) || activeChar.isHero())))
		{
			if (!activeChar.isGM())
				Broadcast.gameAnnounceToOnlinePlayers("[HERO]-" + activeChar.getName() + " Is Online!");
		}
		if (Config.ANNOUNCE_VIP && activeChar.isVip())
		{
			if (!activeChar.isGM())
				Broadcast.gameAnnounceToOnlinePlayers("[VIP]-" + activeChar.getName() + " Is Online!");
		}
		// if player is DE, check for shadow sense skill at night
		if (activeChar.getRace() == ClassRace.DARK_ELF && activeChar.getSkillLevel(294) == 1)
			activeChar.sendPacket(SystemMessage.getSystemMessage((GameTimeController.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(294));
		
		activeChar.getMacroses().sendUpdate();
		activeChar.sendPacket(new UserInfo(activeChar));
		activeChar.sendPacket(new HennaInfo(activeChar));
		activeChar.sendPacket(new FriendList(activeChar));
		// activeChar.queryGameGuard();
		activeChar.sendPacket(new ItemList(activeChar, false));
		activeChar.sendPacket(new ShortCutInit(activeChar));
		activeChar.sendPacket(new ExStorageMaxCount(activeChar));
		activeChar.updateEffectIcons();
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		activeChar.sendSkillList();
		
		Quest.playerEnter(activeChar);
		if (!Config.DISABLE_TUTORIAL)
			loadTutorial(activeChar);
		
		for (Quest quest : ScriptManager.getInstance().getQuests())
		{
			if (quest != null && quest.getOnEnterWorld())
				quest.notifyEnterWorld(activeChar);
		}
		activeChar.sendPacket(new QuestList(activeChar));
		
		// Unread mails make a popup appears.
		if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkUnreadMail(activeChar) > 0)
		{
			activeChar.sendPacket(SystemMessageId.NEW_MAIL);
			activeChar.sendPacket(new PlaySound("systemmsg_e.1233"));
			activeChar.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		if (clan != null && clan.isNoticeEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/clan_notice.htm");
			html.replace("%clan_name%", clan.getName());
			html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(html);
		}
		else if (Config.SERVER_NEWS)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		if (activeChar.getOnlineTime() < 10 && Config.NEW_PLAYER_EFFECT)
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(2025, 1);
			if (skill != null)
			{
				final MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
				activeChar.sendPacket(MSU);
				activeChar.broadcastPacket(MSU);
				activeChar.useMagic(skill, false, false);
			}
		}
		if (Config.ESPECIAL_VIP_LOGIN)
		{
			if (activeChar.isVip())
			{
				final L2Skill skill = SkillTable.getInstance().getInfo(2025, 1);
				if (skill != null)
				{
					final MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
					activeChar.sendPacket(MSU);
					activeChar.broadcastPacket(MSU);
					activeChar.broadcastPacket(new SocialAction(activeChar, 3));
					activeChar.useMagic(skill, false, false);
				}
			}
		}
		PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		// no broadcast needed since the player will already spawn dead to others
		if (activeChar.isAlikeDead())
			sendPacket(new Die(activeChar));
		
		activeChar.onPlayerEnter();
		if (Config.TALK_CHAT_ALL_CONFIG)
		{
			int _calcule = (int) arredondaValor(1, activeChar.getOnlineTime() / 60);
			
			if (_calcule < Config.TALK_CHAT_ALL_TIME)
			{
				long currentTime = System.currentTimeMillis();
				currentTime += (Config.TALK_CHAT_ALL_TIME - _calcule) * 60000;
				activeChar.setChatAllTimer(currentTime);
			}
		}
		sendPacket(new SkillCoolTime(activeChar));
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			activeChar.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.TOWN);
		RemoteClassMaster.showQuestionMark(activeChar);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private static void engage(Player cha)
	{
		int _chaid = cha.getObjectId();
		
		for (Couple cl : CoupleManager.getInstance().getCouples())
		{
			if (cl.getPlayer1Id() == _chaid || cl.getPlayer2Id() == _chaid)
			{
				if (cl.getMaried())
					cha.setMarried(true);
				
				cha.setCoupleId(cl.getId());
			}
		}
	}
	
	private static void notifyClanMembers(Player activeChar)
	{
		final L2Clan clan = activeChar.getClan();
		
		// Refresh player instance.
		clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
		
		final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addPcName(activeChar);
		final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(activeChar);
		
		// Send packet to others members.
		for (Player member : clan.getOnlineMembers())
		{
			if (member == activeChar)
				continue;
			
			member.sendPacket(msg);
			member.sendPacket(update);
		}
	}
	
	private static void notifySponsorOrApprentice(Player activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			Player sponsor = L2World.getInstance().getPlayer(activeChar.getSponsor());
			if (sponsor != null)
				sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
		else if (activeChar.getApprentice() != 0)
		{
			Player apprentice = L2World.getInstance().getPlayer(activeChar.getApprentice());
			if (apprentice != null)
				apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
	}
	
	private static void loadTutorial(Player player)
	{
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
	}
	
	/*
	 * @SuppressWarnings("deprecation") public static double arredondaValor(int casasDecimais, double valor) { BigDecimal decimal = new BigDecimal(valor); return decimal.setScale(casasDecimais, 3).doubleValue(); }
	 */
	public static double arredondaValor(int casasDecimais, double valor)
	{
		BigDecimal decimal = new BigDecimal(valor);
		return decimal.setScale(casasDecimais, RoundingMode.HALF_UP).doubleValue();
	}
	
	// private static void onEnterVip(L2PcInstance activeChar)
	// {
	// long now = Calendar.getInstance().getTimeInMillis();
	// long endDay = activeChar.getVipEndTime();
	// if(now > endDay)
	// {
	// activeChar.setVip(false);
	// activeChar.setVipEndTime(0);
	// activeChar.sendPacket(new CreatureSay(0,Say2.PARTY,Config.MESSAGE_VIP_EXIT, ""));
	// //activeChar.sendMessage(Config.MESSAGE_VIP_EXIT);
	// activeChar.getAppearance().setNameColor(0xFFFF77);
	// activeChar.getAppearance().setTitleColor(0xFFFF77);
	// activeChar.broadcastUserInfo();
	//
	// }
	// else
	// {
	// Date dt = new Date(endDay);
	// if(activeChar.isVip())
	// //activeChar.sendMessage("Your VIP period ends at: " + dt);
	// activeChar.sendPacket(new CreatureSay(0,Say2.PARTY,Config.MESSAGE_VIP_ENTER + dt, ""));
	// activeChar.broadcastUserInfo();
	// //activeChar.sendMessage(Config.MESSAGE_VIP_ENTER + dt);
	// }
	// }
	public static void onEnterVip(Player player)
	{
		long now = System.currentTimeMillis();
		long endDay = player.getVipEndTime();
		
		if (now > endDay)
		{
			player.setVip(false);
			player.setVipEndTime(0);
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your VIP period has ended."));
			if (Config.ALLOW_VIP_NCOLOR)
			{
				player.getAppearance().setNameColor(0xFFFF77);
			}
			if (Config.ALLOW_VIP_TCOLOR)
			{
				player.getAppearance().setTitleColor(0xFFFF77);
			}
			player.broadcastUserInfo();
		}
		else if (player.isVip())
		{
			Date dt = new Date(endDay);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String formattedDate = sdf.format(dt);
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your VIP period ends at " + formattedDate));
			player.broadcastUserInfo();
		}
	}
	
	public static void checkVipExpiration(Player player)
	{
		if (player.isVip() && System.currentTimeMillis() > player.getVipEndTime())
		{
			player.setVip(false);
			player.setVipEndTime(0);
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your VIP period has ended."));
			player.getAppearance().setNameColor(0xFFFF77);
			player.getAppearance().setTitleColor(0xFFFF77);
			player.broadcastUserInfo();
		}
	}
	
	public static void checkAioExpiration(Player player)
	{
		if (player.isAio() && System.currentTimeMillis() > player.getAioEndTime())
		{
			player.setAio(false);
			player.setAioEndTime(0);
			player.lostAioSkills();
			
			if (Config.ALLOW_AIO_ITEM)
			{
				player.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
				player.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
			}
			
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your AIO period has ended."));
			player.broadcastUserInfo();
		}
	}
	
	// private static void onEnterAio(L2PcInstance activeChar)
	// {
	// long now = Calendar.getInstance().getTimeInMillis();
	// long endDay = activeChar.getAioEndTime();
	// if(now > endDay)
	// {
	// activeChar.setAio(false);
	// activeChar.setAioEndTime(0);
	// activeChar.lostAioSkills();
	// if(Config.ALLOW_AIO_ITEM)
	// {
	// activeChar.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	// activeChar.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	// }
	// activeChar.sendPacket(new CreatureSay(0,Say2.PARTY,"System","Your AIO period ends."));
	// activeChar.broadcastUserInfo();
	// }
	// else
	// {
	// Date dt = new Date(endDay);
	// if(activeChar.isAio())
	// activeChar.sendMessage("Your AIO period ends at: " + dt);
	// activeChar.broadcastUserInfo();
	// }
	// }
	public static void onEnterAio(Player player)
	{
		long now = System.currentTimeMillis();
		long endDay = player.getAioEndTime();
		
		if (now > endDay)
		{
			player.setAio(false);
			player.setAioEndTime(0);
			player.lostAioSkills();
			
			if (Config.ALLOW_AIO_ITEM)
			{
				player.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
				player.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
			}
			if (Config.ALLOW_AIO_TCOLOR)
			{
				player.getAppearance().setTitleColor(0xFFFF77);
			}
			if (Config.ALLOW_AIO_NCOLOR)
			{
				player.getAppearance().setNameColor(0xFFFF77);
			}
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your AIO period has ended."));
			player.broadcastUserInfo();
		}
		else if (player.isAio())
		{
			Date dt = new Date(endDay);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String formattedDate = sdf.format(dt);
			
			player.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your AIO period ends at " + formattedDate));
			player.broadcastUserInfo();
		}
	}
	
	// private static void onEnterAio(L2PcInstance activeChar)
	// {
	// long now = Calendar.getInstance().getTimeInMillis();
	// long endDay = activeChar.getAioEndTime();
	//
	// if (now > endDay)
	// {
	// activeChar.setAio(false);
	// activeChar.setAioEndTime(0);
	// activeChar.lostAioSkills();
	//
	// if (Config.ALLOW_AIO_ITEM)
	// {
	// activeChar.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	// activeChar.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	// }
	//
	// activeChar.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your AIO period has ended."));
	// activeChar.broadcastUserInfo();
	// }
	// else
	// {
	// if (activeChar.isAio())
	// {
	// Date dt = new Date(endDay);
	// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	// String formattedDate = sdf.format(dt);
	//
	// activeChar.sendPacket(new CreatureSay(0, Say2.PARTY, "System", "Your AIO period ends at " + formattedDate));
	// activeChar.broadcastUserInfo();
	// }
	// }
	// }
	private static void notifyCastleOwner(final Player activeChar)
	{
		final L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			if (clan.hasCastle())
			{
				final Castle castle = CastleManager.getInstance().getCastleById(clan.getCastleId());
				// final Castle castle = CastleManager.getInstance().getCastleById(clan.hasCastle());
				if ((castle != null) && (activeChar.getObjectId() == clan.getLeaderId()))
					Broadcast.gameAnnounceToOnlinePlayers("[Lord]-" + activeChar.getName() + " Ruler Of " + castle.getName() + " Castle is now Online!");
			}
		}
	}
	
	public void initCountdown(final Player activeChar, final int duration)
	{
		if (activeChar != null && activeChar.isOnline())
		{
			ThreadPool.schedule(new Countdown(activeChar, duration), 0L);
		}
	}
	
	class Countdown implements Runnable
	{
		private final Player _player;
		private int _time;
		
		public Countdown(final Player player, final int time)
		{
			this._time = time;
			this._player = player;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			if (this._player.isOnline())
			{
				switch (this._time)
				{
					case 5:
					{
						_player.sendMessage("Server Online Is GM Only, You Disconnected in 5!");
						break;
					}
					case 4:
					{
						_player.sendMessage("Server Online Is GM Only, You Disconnected in 4!");
						break;
					}
					case 3:
					{
						_player.sendMessage("Server Online Is GM Only, You Disconnected in 3!");
						break;
					}
					case 2:
					{
						_player.sendMessage("Server Online Is GM Only, You Disconnected in 2!");
						break;
					}
					case 1:
					{
						_player.sendMessage("Server Online Is GM Only, You Disconnected in 1!");
						break;
					}
				}
				if (this._time > 1)
				{
					ThreadPool.schedule(new Countdown(this._player, this._time - 1), 1000L);
				}
			}
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}