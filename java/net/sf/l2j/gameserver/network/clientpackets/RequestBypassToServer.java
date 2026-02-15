package net.sf.l2j.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.lang.Tokenizer;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.dailyreward.IBypassHandler;
import net.sf.l2j.dailyreward.PlayerVariables;
import net.sf.l2j.email.items.MailData;
import net.sf.l2j.email.items.MailManager;
import net.sf.l2j.email.items.VoicedMailSend;
import net.sf.l2j.email.items.VoicedMailbox;
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
import net.sf.l2j.event.lastman.LMEvent;
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
import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.datatables.xml.DressMeData;
import net.sf.l2j.gameserver.datatables.xml.IconTable;
import net.sf.l2j.gameserver.datatables.xml.MerchantData;
import net.sf.l2j.gameserver.datatables.xml.RouletteData;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.BypassHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminEditChar;
import net.sf.l2j.gameserver.handler.custom.CustomBypassHandler;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Repair;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBuffs;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2TeleportLocation;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.Roulette;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.model.zone.type.L2TimeFarmZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.OpenUrl;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvPZoneTimeTask.PvPZoneBypass;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.util.ChangeAllyNameLog;
import net.sf.l2j.gameserver.util.FloodProtectors;
import net.sf.l2j.gameserver.util.FloodProtectors.Action;
import net.sf.l2j.gameserver.util.GMAudit;
import net.sf.l2j.log.CheatLog;
import net.sf.l2j.mission.MissionReset;
import net.sf.l2j.timezone.TimeFarmZoneManager;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private String _command;
	private static final long COOLDOWN_MS = 10000L;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.SERVER_BYPASS))
			return;
		
		if (_command.startsWith("custom_"))
		{
			// Player player = getClient().getPlayer();
			CustomBypassHandler.getInstance().handleBypass(activeChar, _command);
		}
		if (_command.isEmpty())
		{
			_log.info(activeChar.getName() + " sent an empty requestBypass packet.");
			activeChar.logout();
			return;
		}
		
		try
		{
			if (_command.startsWith("admin_"))
			{
				if (Config.TRY_USED_ADMIN_COMMANDS)
				{
					if (!activeChar.isGM())
					{
						_log.warning("Player: " + activeChar.getName() + " is trying to execute a GM command!");
						CheatLog.auditGMAction(activeChar.getName());
						activeChar.logout();
						return;
					}
				}
				
				String command = _command.split(" ")[0];
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				if (ach == null)
				{
					if (activeChar.isGM())
						activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
					
					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command.");
					_log.warning(activeChar.getName() + " tried to use admin command " + command + " without proper Access Level.");
					return;
				}
				
				if (Config.GMAUDIT)
					GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));
				
				ach.useAdminCommand(_command, activeChar);
			}
			
			if (_command.startsWith("merchant"))
			{
				final Tokenizer tokenizer = new Tokenizer(_command);
				final String param = tokenizer.getToken(1);
				
				switch (param.toLowerCase())
				{
					case "chat":
					{
						String htmlnavi = tokenizer.getToken(2);
						NpcHtmlMessage html = new NpcHtmlMessage(0);
						html.setFile("data/html/merchant/" + htmlnavi + ".htm");
						html.replace("%BACK%", "bypass merchant chat 55500");
						activeChar.sendPacket(html);
						break;
					}
					
					case "action":
					{
						String category = tokenizer.getToken(2);
						String grade = tokenizer.getToken(3);
						int page = tokenizer.getAsInteger(4, 0); // se não tiver, 1
						MerchantData.getInstance().showList(activeChar, category, grade, page);
						break;
					}
					
					case "show":
					{
						String category = tokenizer.getToken(2);
						String grade = tokenizer.getToken(3);
						int page = tokenizer.getAsInteger(4, 0);
						int index = tokenizer.getAsInteger(5, 0);
						MerchantData.getInstance().showDetail(activeChar, category, grade, page, index);
						break;
					}
					
					case "buy":
					{
						String category = tokenizer.getToken(2);
						String grade = tokenizer.getToken(3);
						int page = tokenizer.getAsInteger(4, 0);
						int index = tokenizer.getAsInteger(5, 0);
						MerchantData.getInstance().buy(activeChar, category, grade, page, index);
						break;
					}
					case "buyreq":
					{
					    String category = tokenizer.getToken(2);
					    String grade = tokenizer.getToken(3);
					    int page = tokenizer.getAsInteger(4, 0);
					    int index = tokenizer.getAsInteger(5, 0);

					    MerchantData.getInstance().buyRequest(activeChar, category, grade, page, index);
					    break;
					}

					case "buyconfirm":
					{
					    String category = tokenizer.getToken(2);
					    String grade = tokenizer.getToken(3);
					    int page = tokenizer.getAsInteger(4, 0);
					    int index = tokenizer.getAsInteger(5, 0);

					    MerchantData.getInstance().buyConfirm(activeChar, category, grade, page, index);
					    break;
					}
					case "buycancel":
					{
					    String category = tokenizer.getToken(2);
					    String grade = tokenizer.getToken(3);
					    int page = tokenizer.getAsInteger(4, 0);
					    int index = tokenizer.getAsInteger(5, 0);
					    MerchantData.getInstance().buyCancel(activeChar, category, grade, page, index);
					    break;
					}

					case "search":
					{
					    String category = tokenizer.getToken(2);
					    String grade = tokenizer.getToken(3);
					    String search = tokenizer.getToken(4);
					    int page = tokenizer.getAsInteger(5, 0);

					    MerchantData.getInstance().showListSearch(activeChar, category, grade, page, search);
					    break;
					}


				}
			}
			
			if (_command.startsWith("dressme"))
			{
				final Tokenizer tokenizer = new Tokenizer(_command);
				final String param = tokenizer.getToken(1);
				switch (param.toLowerCase())
				{
					case "action":
						int actionUse = Integer.parseInt(tokenizer.getToken(2));
						
						// Opção: actionUse aponta direto para a entrada do DressMeData via actionUse
						
						final DressMeHolder dress = DressMeData.getInstance().getByActionId(actionUse);
						
						if (dress == null)
						{
							activeChar.sendMessage("Visual not found for this actionUse.");
							return;
						}
						
						// VIP gate (ajuste o método real)
						if (dress.isVip() && !activeChar.isVip())
						{
							activeChar.sendMessage("This visual is VIP-only.");
							return;
						}
						
						// cooldown
						final long now = System.currentTimeMillis();
						if (now - activeChar.getLastDressMeSummonTime() < COOLDOWN_MS)
						{
							activeChar.sendMessage("You need to wait before using DressMe again.");
							return;
						}
						
						if (activeChar.getWeaponSkin() != null && activeChar.getWeaponSkin().getItemId() == actionUse)
						{
							activeChar.sendMessage("This weapon look is already active.");
							activeChar.removeDressMeWeapon();
							return;
						}
						if (activeChar.getArmorSkin() != null && activeChar.getArmorSkin().getItemId() == actionUse)
						{
							activeChar.sendMessage("This armor look is already active.");
							activeChar.removeDressMeArmor();
							return;
						}
						
						activeChar.applyDressMe(dress, Player.DressMeSource.ACTION, actionUse);
						activeChar.setLastDressMeSummonTime(now);
						openPanelSkin(activeChar);
						break;
					
					case "disable":
						
						if (activeChar.getWeaponSkin() != null)
						{
							activeChar.sendMessage("This weapon look is already active.");
							activeChar.removeDressMeWeapon();
							
						}
						
						if (activeChar.getArmorSkin() != null)
						{
							activeChar.sendMessage("This armor look is already active.");
							activeChar.removeDressMeArmor();
							
						}
						openPanelSkin(activeChar);
						break;
					case "skin":
						NpcHtmlMessage html = new NpcHtmlMessage(0);
						html.setFile("data/html/mods/menu/tryskin.htm");
						activeChar.sendPacket(html);
						break;
					case "hair_disable":
						activeChar.setDressMeDisableHair(!activeChar.isDressMeDisableHair());
						String MEMO_DRESS_HAIR_OFF = "dressme.hairOff";
						activeChar.getMemos().set(MEMO_DRESS_HAIR_OFF, activeChar.isDressMeDisableHair() ? "1" : "0");
						activeChar.broadcastUserInfo();
						openPanelSkin(activeChar);
						break;
				}
			}
			else if (_command.startsWith("broser"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "email_send"
				
				if (!st.hasMoreTokens())
					return;
				
				String receiverName = st.nextToken();
				activeChar.sendPacket(new OpenUrl(receiverName));
			}
			else if (_command.equals("mailbox_receive_all"))
			{
				List<MailData> mails = MailManager.getInstance().getMailsForPlayer(activeChar.getObjectId());
				
				int receivedCount = 0;
				for (MailData mail : mails)
				{
					if (!mail.isClaimed())
					{
						boolean success = MailManager.getInstance().claimMail(activeChar, mail);
						if (success)
							receivedCount++;
					}
				}
				
				activeChar.sendMessage("You received " + receivedCount + " item(s) from your mailbox.");
				VoicedMailbox.showMailbox(activeChar, 1);
			}
			
			else if (_command.startsWith("email_send"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "email_send"
				
				if (!st.hasMoreTokens())
					return;
				
				String receiverName = st.nextToken();
				
				Player player = getClient().getActiveChar();
				if (player == null)
					return;
				
				// Só verifica se existe no banco
				int receiverId = getPlayerObjectIdByName(receiverName);
				
				if (receiverId == 0)
				{
					player.sendMessage("Player " + receiverName + " does not exist.");
					VoicedMailSend.showTargetInput(player);
					return;
				}
				
				PlayerVariables.setVar(player, "MailReceiver", receiverName, 0);
				
				showItemList(player, 1);
				return;
			}
			else if (_command.startsWith("pvpzone"))
			{
				PvPZoneBypass.handlers(activeChar, _command);
			}
			else if (_command.startsWith("email_list"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "email_list"
				
				int page = 1;
				if (st.hasMoreTokens())
					page = Integer.parseInt(st.nextToken());
				
				Player player = getClient().getActiveChar();
				if (player == null)
					return;
				
				showItemList(player, page);
				return;
			}
			else if (_command.startsWith("mailbox_page"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "mailbox_page"
				
				if (!st.hasMoreTokens())
					return;
				
				int page = Integer.parseInt(st.nextToken());
				
				Player player = getClient().getActiveChar();
				if (player == null)
					return;
				
				VoicedMailbox.showMailbox(player, page);
				return;
			}
			else if (_command.startsWith("email_quantity"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "email_quantity"
				
				if (!st.hasMoreTokens())
					return;
				
				int objectId = Integer.parseInt(st.nextToken());
				
				Player player = getClient().getActiveChar();
				if (player == null)
					return;
				
				showItemQuantitySelection(player, objectId);
				return;
			}
			else if (_command.startsWith("email_confirm"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "email_confirm"
				
				if (!st.hasMoreTokens())
					return;
				
				int objectId = Integer.parseInt(st.nextToken());
				long count = Long.parseLong(st.nextToken());
				
				String receiverName = PlayerVariables.getVar(activeChar, "MailReceiver");
				if (receiverName == null || receiverName.isEmpty())
				{
					activeChar.sendMessage("No mail receiver set.");
					return;
				}
				
				// Tenta pegar player online
				Player receiver = L2World.getInstance().getPlayer(receiverName);
				int receiverId = (receiver != null) ? receiver.getObjectId() : getPlayerObjectIdByName(receiverName);
				
				if (receiverId == 0)
				{
					activeChar.sendMessage("Receiver not found.");
					return;
				}
				
				ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
				if (item == null || item.getCount() < count)
				{
					activeChar.sendMessage("Invalid item or insufficient quantity.");
					return;
				}
				
				if (!activeChar.reduceAdena("Mail", Config.MAIL_SEND_TAX, null, true))
				{
					activeChar.sendMessage("You need " + Config.MAIL_SEND_TAX + " adena to send mail.");
					return;
				}
				
				activeChar.getInventory().destroyItem("Mail", item, (int) count, activeChar, null);
				
				long expire = System.currentTimeMillis() + Config.MAIL_ITEM_EXPIRE_DAYS * 86400000L;
				
				MailData mail = new MailData(0, activeChar.getObjectId(), receiverId, item.getObjectId(), item.getItemId(), count, item.getEnchantLevel(), expire, false, false);
				MailManager.getInstance().storeMail(mail);
				
				activeChar.sendMessage("Item sent to " + receiverName + " successfully.");
				
				// NÃO limpa a variável MailReceiver para continuar enviando para o mesmo jogador
				// PlayerVariables.unsetVar(activeChar, "MailReceiver");
				
				// Reabre a lista de itens na página 1 para enviar mais
				showItemList(activeChar, 1);
				
				return;
			}
			
			else if (_command.startsWith("mailbox_receive"))
			{
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken(); // pula "mailbox_receive"
				
				if (!st.hasMoreTokens())
					return;
				
				int mailId = Integer.parseInt(st.nextToken());
				
				MailData mail = MailManager.getInstance().getMailById(mailId);
				if (mail == null || mail.isClaimed() || mail.getReceiverId() != activeChar.getObjectId())
				{
					activeChar.sendMessage("Mail not found or already claimed.");
					return;
				}
				
				// Recupera item do banco e adiciona ao inventário do jogador
				ItemInstance item = ItemTable.getInstance().createItem("Mailbox", mail.getItemId(), (int) mail.getItemCount(), activeChar, null);
				item.setEnchantLevel(mail.getEnchantLevel());
				activeChar.getInventory().addItem("Mailbox", item, activeChar, null);
				
				// Marca como recebido no banco
				mail.claimed = true;
				MailManager.getInstance().updateMail(mail);
				
				activeChar.sendMessage("Item received successfully.");
				
				// Reabre a mailbox para o jogador, primeira página
				VoicedMailbox.showMailbox(activeChar, 1);
				
				return;
			}
			
			else if (_command.startsWith("bp_"))
			{
				String command = _command.split(" ")[0];
				
				IBypassHandler bh = BypassHandler.getInstance().getBypassHandler(command);
				if (bh == null)
				{
					_log.warning("No handler registered for bypass '" + command + "'");
					return;
				}
				
				bh.handleBypass(_command, activeChar);
			}
			else if (_command.startsWith("sellbuffpage"))
			{ // load page number (player or pet) //to sellbuff system. //
				
				int page = 0;
				if (_command.length() == 13 || _command.length() == 17)
				{
					String x = _command.substring(12, 13);
					try
					{
						page = Integer.parseInt(x);
					}
					catch (Exception e)
					{
						_log.info(e.getMessage());
					}
				}
				
				int subPage = 0;
				if (_command.length() == 17)
				{
					String y = _command.substring(16, 17); // zmieniono nie testowano.
					try
					{
						subPage = Integer.parseInt(y);
					}
					catch (Exception e)
					{
						_log.info(e.getMessage());
					}
				}
				
				Player target = null;
				
				if (activeChar.getTarget() instanceof Player)
					target = (Player) activeChar.getTarget();
				
				if (target == null)
					return;
				
				activeChar.getSellBuffMsg().setPage(target, activeChar, page, subPage);
			}
			else if (_command.startsWith("buff") && _command.length() > 4 && !_command.startsWith("buffCommand"))
			{
				// Se o comando começa com "buff" e é maior que 4 caracteres, mas não começa com "buffCommand"
				String x = _command.substring(4).trim(); // Extraímos a parte após "buff"
				
				try
				{
					// Tenta converter o valor extraído para um número (ID do buff)
					int id = Integer.parseInt(x);
					
					// Verifique se o alvo é válido
					Player target = null;
					if (activeChar.getTarget() instanceof Player)
					{
						target = (Player) activeChar.getTarget();
					}
					
					if (target != null)
					{
						
						activeChar.getSellBuffMsg().buffBuyer(target, activeChar, id);
					}
					else
					{
						
						activeChar.sendMessage("Erro: Nenhum alvo valido encontrado.");
					}
				}
				catch (NumberFormatException e)
				{
					
					activeChar.sendMessage("Erro: ID do buff nao e um numero valido. Verifique e tente novamente.");
					
					System.out.println("Comando invalido recebido: " + _command);
				}
			}
			
			else if (_command.startsWith("zbuffset"))
			{ // load buffset from html->bypass. //to sellbuff system. //
				String x = _command.substring(8);
				int id = Integer.parseInt(x);
				Player target = null;
				
				if (activeChar.getTarget() instanceof Player)
					target = (Player) activeChar.getTarget();
				
				if (target == null)
					return;
				
				activeChar.getSellBuffMsg().buffsetBuyer(target, activeChar, id);
			}
			else if (_command.startsWith("petbuff"))
			{ // load buffId from html->bypass. //to sellbuff system. //
				String x = _command.substring(7);
				int id = Integer.parseInt(x);
				Player target = null;
				
				if (activeChar.getTarget() instanceof Player)
					target = (Player) activeChar.getTarget();
				
				if (target == null)
					return;
				
				activeChar.getSellBuffMsg().buffBuyerPet(target, activeChar, id);
			}
			else if (_command.startsWith("zpetbuff"))
			{ // load buff set from html->bypass. //to sellbuff system. //
				String x = _command.substring(8);
				int id = Integer.parseInt(x);
				Player target = null;
				
				if (activeChar.getTarget() instanceof Player)
					target = (Player) activeChar.getTarget();
				
				if (target == null)
					return;
				
				activeChar.getSellBuffMsg().buffsetBuyerPet(target, activeChar, id);
			}
			else if (_command.startsWith("actr"))
			{ // to sellbuff system. //
				try
				{
					String l = _command.substring(5);
					int p = 0;
					
					p = Integer.parseInt(l);
					
					activeChar.getSellBuffMsg().startBuffStore(activeChar, p, true);
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Wrong field value!");
				}
			}
			else if (_command.startsWith("pctra"))
			{ // to sellbuff system. //
				try
				{
					activeChar.getSellBuffMsg().stopBuffStore(activeChar);
				}
				catch (Exception e)
				{
					
				}
			}
			else if (_command.startsWith("droplist"))
			{
				StringTokenizer st = new StringTokenizer(_command, " ");
				st.nextToken();
				
				int npcId = Integer.parseInt(st.nextToken());
				int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				
				L2Npc.ShiffNpcDropList(activeChar, npcId, page);
			}
			else if (_command.startsWith("Roleta_item_"))
			{
				StringTokenizer st = new StringTokenizer(_command, " ");
				st.nextToken();
				String addcmd = _command.substring(12).trim();
				if (addcmd.startsWith("augumentation"))
				{
					if (st.countTokens() < 1)
						return;
					String skillId = st.nextToken();
					
					NpcHtmlMessage htm = new NpcHtmlMessage(0);
					
					if (skillId.equals("passive"))
					{
						htm.setFile("data/html/" + "mods/Roleta/items/AUGUMENTATION_PASSIVE.htm");
						activeChar.sendPacket(htm);
					}
					else if (skillId.equals("active"))
					{
						htm.setFile("data/html/" + "mods/Roleta/items/AUGUMENTATION_ACTIVE.htm");
						activeChar.sendPacket(htm);
					}
					// else
					// new Refine(activeChar, Integer.parseInt(skillId));
				}
				else if (!Config.INTERFACE_KEY.trim().isEmpty() && _command.equals(Config.INTERFACE_KEY))
					activeChar.setVerification(true);
				else if (addcmd.startsWith("roulette"))
				{
					if (st.countTokens() < 1)
						return;
					
					String nextToken = st.nextToken();
					
					NpcHtmlMessage htm = new NpcHtmlMessage(0);
					
					if (nextToken.equals("list"))
					{
						int page = Integer.parseInt(st.nextToken());
						RouletteData.getInstance().sendList(activeChar, htm, page, 5);
					}
					else if (nextToken.equals("menu"))
					{
						htm.setFile("data/html/" + "mods/Roleta/items/ROULETTE_MENU.htm");
						activeChar.sendPacket(htm);
						htm.replace("%ItemNameRoleta%", ItemTable.getInstance().getTemplate(Config.CUSTOM_ITEM_ROULETTE).getName());
						htm.replace("%ItemCountRoleta%", Config.ITEM_COUNT_ROLETA);
					}
					else if (nextToken.equals("start"))
					{
						new Roulette(activeChar);
					}
				}
				
			}
			else if (_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if (_command.startsWith("player_book "))
			{
				playerBook(activeChar, _command.substring(12));
			}
			else if (_command.startsWith("solo_farmzone"))
			{
				TeleportSoloZone(activeChar);
			}
			else if (_command.startsWith("timefarmzone"))
			{
				TeleportTimeFarmZone(activeChar);
			}
			/*
			 * else if (_command.startsWith("npc_")) { if (!activeChar.validateBypass(_command)) return; activeChar.setIsUsingCMultisell(false); int endOfId = _command.indexOf('_', 5); String id; if (endOfId > 0) id = _command.substring(4, endOfId); else id = _command.substring(4); try { final
			 * L2Object object = L2World.getInstance().getObject(Integer.parseInt(id)); if (object != null && object instanceof L2Npc && endOfId > 0 && ((L2Npc) object).canInteract(activeChar)) ((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
			 * activeChar.sendPacket(ActionFailed.STATIC_PACKET); } catch (NumberFormatException nfe) { } }
			 */
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				activeChar.setIsUsingCMultisell(false);
				
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				
				try
				{
					final L2Object object = L2World.getInstance().getObject(Integer.parseInt(id));
					
					if (object != null && object instanceof L2Npc && endOfId > 0 && ((L2Npc) object).canInteract(activeChar))
						((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			else if (this._command.startsWith("classe_change"))
			{
				StringTokenizer st = new StringTokenizer(this._command);
				st.nextToken();
				String type = null;
				type = st.nextToken();
				try
				{
					if (activeChar.getBaseClass() != activeChar.getClassId().getId())
					{
						activeChar.sendMessage("SYS: Voce precisa estar com sua Classe Base para usar este item.");
						activeChar.sendPacket(new ExShowScreenMessage("You is not with its base class.", 6000, 2, true));
						return;
					}
					if (activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
					{
						activeChar.sendMessage("This Item Cannot Be Used On Olympiad Games.");
						return;
					}
					// Verifica se o jogador possui um summon ativo (dragão, pet, etc.)
					if (activeChar.hasPet() || activeChar.isMounted() || activeChar.getPet() != null || activeChar.isCastingNow())
					{
						activeChar.sendMessage("You cannot change class while having an active summon or mount.");
						return;
					}
					ClassChangeCoin(activeChar, type);
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}
			}
			else if (_command.startsWith("classe_index"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/CoinCustom/classes.htm");
				activeChar.sendPacket(html);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
			// else if (_command.startsWith("change_sex"))
			// {
			// if (activeChar.destroyItemByItemId("Sex Change", activeChar.getSexChangeItemId(), 1, null, true))
			// {
			// Sex male = Sex.MALE;
			// Sex female = Sex.FEMALE;
			//
			// if (activeChar.getAppearance().getSex() == male)
			// {
			// activeChar.getAppearance().setSex(female);
			// activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000, 0x02, true));
			// activeChar.broadcastUserInfo();
			// activeChar.decayMe();
			// activeChar.spawnMe();
			// }
			// else if (activeChar.getAppearance().getSex() == female)
			// {
			// activeChar.getAppearance().setSex(male);
			// activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000, 0x02, true));
			// activeChar.broadcastUserInfo();
			// activeChar.decayMe();
			// activeChar.spawnMe();
			// }
			//
			// //for (Player gm : World.getAllGMs())
			// // gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "SYS", activeChar.getName() + " changed your sex."));
			//
			// ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			// {
			// @Override
			// public void run()
			// {
			// activeChar.getClient().closeNow();
			// }
			// }, 2000);
			// }
			//
			// }
			else if (_command.startsWith("change_sex"))
			{
				// Verifica se o jogador possui um summon ativo (dragão, pet, etc.)
				if (activeChar.hasPet() || activeChar.isMounted() || activeChar.getPet() != null || activeChar.isCastingNow())
				{
					activeChar.sendMessage("You cannot change class while having an active summon or mount.");
					return;
				}
				
				// Verificar se o jogador tem o item necessário para trocar de sexo
				if (activeChar.destroyItemByItemId("Sex Change", activeChar.getSexChangeItemId(), 1, null, true))
				{
					Sex male = Sex.MALE;
					Sex female = Sex.FEMALE;
					
					// Trocar o sexo do jogador
					if (activeChar.getAppearance().getSex() == male)
					{
						activeChar.getAppearance().setSex(female);
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000, 0x02, true));
						activeChar.broadcastUserInfo();
						activeChar.decayMe();
						activeChar.spawnMe();
					}
					else if (activeChar.getAppearance().getSex() == female)
					{
						activeChar.getAppearance().setSex(male);
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000, 0x02, true));
						activeChar.broadcastUserInfo();
						activeChar.decayMe();
						activeChar.spawnMe();
					}
					
					// Opcionalmente, podemos exibir uma mensagem para os GMs sobre a troca (comentado no código original)
					// for (Player gm : World.getAllGMs())
					// gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "SYS", activeChar.getName() + " changed your sex."));
					
					// Fechar a conexão do cliente após 2 segundos
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							activeChar.getClient().closeNow();
						}
					}, 2000);
				}
				else
				{
					// Se o jogador não tiver o item necessário, enviar uma mensagem de erro
					activeChar.sendPacket(new ExShowScreenMessage("You do not have the required item for a sex change.", 6000, 0x04, true));
				}
			}
			
			// HTML lista de buffs
			else if (_command.startsWith("buffCommand"))
			{
				VoicedBuffs.OpenHtmlBuffsList(activeChar);
			}
			// Usar Buffs de Profeta
			else if (_command.startsWith("usedBuffs"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlBuffsList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(10);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlBuffsList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(10);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlBuffsList(activeChar);
			}
			// HTML Songs
			else if (_command.startsWith("songCommand"))
			{
				VoicedBuffs.OpenHtmlSongsList(activeChar);
			}
			// Usar Buffs Songs
			else if (_command.startsWith("usedSongs"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlSongsList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(10);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlSongsList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(10);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlSongsList(activeChar);
			}
			
			// HTML Dances
			else if (_command.startsWith("danceCommand"))
			{
				VoicedBuffs.OpenHtmlDancesList(activeChar);
			}
			// Usar Buffs Dances
			else if (_command.startsWith("usedDances"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlDancesList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(11);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlDancesList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(11);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlDancesList(activeChar);
			}
			
			// HTML Chants
			else if (_command.startsWith("chantsCommand"))
			{
				VoicedBuffs.OpenHtmlChantsList(activeChar);
			}
			// Usar Buffs Dances
			else if (_command.startsWith("usedChants"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlChantsList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(11);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlChantsList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(11);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlChantsList(activeChar);
			}
			
			// HTML Special
			else if (_command.startsWith("specialCommand"))
			{
				VoicedBuffs.OpenHtmlSpecialList(activeChar);
			}
			// Usar Buffs Dances
			else if (_command.startsWith("usedSpecial"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlSpecialList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(12);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlSpecialList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(12);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlSpecialList(activeChar);
			}
			
			// HTML Protect
			else if (_command.startsWith("protectCommand"))
			{
				VoicedBuffs.OpenHtmlProtectList(activeChar);
			}
			// Usar Buffs Dances
			else if (_command.startsWith("usedProtect"))
			{
				if (activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.HQ) || activeChar.isInsideZone(ZoneId.SIEGE) || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar) || activeChar.isParalyzed() || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.FLAG) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInJail() || activeChar.isArenaAttack() || activeChar.isArenaProtection() || activeChar.isCastingNow() || activeChar.isOlympiadProtection() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isDead())
				{
					activeChar.sendMessage("You can not do that.");
					VoicedBuffs.OpenHtmlProtectList(activeChar);
					return;
				}
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						activeChar.setIsParalyzed(false);
						String idBuff = _command.substring(12);
						int parseIdBuff = Integer.parseInt(idBuff);
						SkillTable.getInstance().getInfo(parseIdBuff, SkillTable.getInstance().getMaxLevel(parseIdBuff)).getEffects(activeChar, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(parseIdBuff));
						// VoicedBuffs.OpenHtmlProtectList(activeChar);
					}
				}, 500);
				String idBuff = _command.substring(12);
				int parseIdBuff = Integer.parseInt(idBuff);
				activeChar.setTarget(activeChar);
				activeChar.broadcastPacket(new MagicSkillUse(activeChar, parseIdBuff, 1, 500, 0));
				activeChar.sendPacket(new SetupGauge(0, 500));
				activeChar.setIsParalyzed(true);
				
				// VoicedBuffs.OpenHtmlProtectList(activeChar);
			}
			
			else if (_command.startsWith("name_change"))
			{
				try
				{
					String name = _command.substring(12);
					
					if (name.length() < 3)
					{
						activeChar.sendMessage("Your name needs a minimum of 3 letters. Please try again.");
						return;
					}
					
					if (name.length() > 16)
					{
						activeChar.sendMessage("Your name cannot exceed 16 characters in length. Please try again.");
						return;
					}
					
					if (Config.FORBIDDEN_NAMES.length > 1)
					{
						for (String st : Config.FORBIDDEN_NAMES)
						{
							if (name.toLowerCase().contains(st.toLowerCase()))
							{
								activeChar.sendMessage("This name is inappropriate.");
								return;
							}
						}
					}
					
					if (name.equals(activeChar.getName()))
					{
						activeChar.sendMessage("Please, choose a different name.");
						return;
					}
					
					if (!name.matches("^[a-zA-Z0-9]+$"))
					{
						activeChar.sendMessage("Incorrect name. Please try again.");
						return;
					}
					
					if (CharNameTable.getInstance().getIdByName(name) > 0)
					{
						activeChar.sendMessage("The chosen name already exists.");
						return;
					}
					
					int itemId = activeChar.getNameChangeItemId();
					
					// Verifica se o item está no inventário
					if (activeChar.getInventory().getItemByItemId(itemId) == null)
					{
						activeChar.sendMessage("You do not have the required item to change your name.");
						return;
					}
					
					// Tenta destruir o item e mudar o nome
					if (activeChar.destroyItemByItemId("Name Change", itemId, 1, null, true))
					{
						activeChar.setName(name);
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your name has been changed.", 10000));
						activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
						activeChar.broadcastUserInfo();
						activeChar.store();
					}
					else
					{
						activeChar.sendMessage("Something went wrong during the name change process.");
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Fill out the field correctly.");
				}
			}
			
			else if (_command.startsWith("_ally_name_"))
			{
				final String clientInfo = activeChar.getClient().toString();
				final String ip = clientInfo.substring(clientInfo.indexOf(" - IP: ") + 7, clientInfo.lastIndexOf("]"));
				
				try
				{
					String name = _command.substring(12);
					
					// Verifica se o nome da aliança é alfanumérico
					if (!StringUtil.isAlphaNumeric(name))
					{
						activeChar.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
						return;
					}
					
					// Verifica o comprimento do nome
					if (name.length() > 16 || name.length() < 2)
					{
						activeChar.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_LENGTH);
						return;
					}
					
					// Verifica se já existe uma aliança com esse nome
					if (ClanTable.getInstance().isAllyExists(name))
					{
						activeChar.sendPacket(SystemMessageId.ALLIANCE_ALREADY_EXISTS);
						return;
					}
					
					// Verifica se o jogador tem o item necessário para mudar o nome
					int itemId = activeChar.getAllyNameChangeItemId();
					if (activeChar.getInventory().getItemByItemId(itemId) == null)
					{
						activeChar.sendMessage("You do not have the required item to change the alliance name.");
						return;
					}
					
					final L2Clan clanold = activeChar.getClan();
					
					// Tenta destruir o item e mudar o nome da aliança
					if (activeChar.destroyItemByItemId("", itemId, 1, null, true))
					{
						// Registra a ação do GM (se necessário)
						ChangeAllyNameLog.auditGMAction(activeChar.getObjectId(), clanold.getAllyName(), name, ip);
						
						// Informa os GMs sobre a mudança
						for (Player gm : GmListTable.getInstance().getAllGms(true))
							gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "[AllyName]", activeChar.getName() + " mudou o nome da Ally para [" + name + "]"));
						
						// Atualiza o nome da aliança de todos os clãs na aliança
						for (L2Clan clan : ClanTable.getInstance().getClans())
						{
							clan.setAllyName(name);
							clan.updateClanInDB();
						}
						
						// Atualiza a interface do jogador
						activeChar.broadcastUserInfo();
						activeChar.store();
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your Ally name has been changed.", 6000, 0x02, true));
						activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
					}
					else
					{
						activeChar.sendMessage("Something went wrong during the alliance name change process.");
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Fill out the field correctly.");
				}
			}
			
			else if (_command.startsWith("repairchar "))
			{
				String value = _command.substring(11);
				StringTokenizer st = new StringTokenizer(value);
				String repairChar = null;
				
				try
				{
					if (st.hasMoreTokens())
						repairChar = st.nextToken();
				}
				catch (Exception e)
				{
					activeChar.sendMessage("You can't put empty box.");
					return;
				}
				
				if (repairChar == null || repairChar.equals(""))
					return;
				
				if (Repair.checkAcc(activeChar, repairChar))
				{
					if (Repair.checkChar(activeChar, repairChar))
					{
						String htmContent = HtmCache.getInstance().getHtm("data/html/mods/repair/repair-self.htm");
						NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
						npcHtmlMessage.setHtml(htmContent);
						activeChar.sendPacket(npcHtmlMessage);
						return;
					}
					else if (Repair.checkPunish(activeChar, repairChar))
					{
						String htmContent = HtmCache.getInstance().getHtm("data/html/mods/repair/repair-jail.htm");
						NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
						npcHtmlMessage.setHtml(htmContent);
						activeChar.sendPacket(npcHtmlMessage);
						return;
					}
					else if (Repair.checkKarma(activeChar, repairChar))
					{
						activeChar.sendMessage("Selected Char has Karma,Cannot be repaired!");
						return;
					}
					else
					{
						Repair.repairBadCharacter(repairChar);
						String htmContent = HtmCache.getInstance().getHtm("data/html/mods/repair/repair-done.htm");
						NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
						npcHtmlMessage.setHtml(htmContent);
						activeChar.sendPacket(npcHtmlMessage);
						return;
					}
				}
				
				String htmContent = HtmCache.getInstance().getHtm("data/html/mods/repair/repair-error.htm");
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%acc_chars%", Repair.getCharList(activeChar));
				activeChar.sendPacket(npcHtmlMessage);
			}
			else if (_command.startsWith("voiced_"))
			{
				String command = _command.split(" ")[0];
				
				IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getHandler(_command.substring(7));
				
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command.substring(7) + " does not exist!");
					_log.warning("No handler registered for command '" + _command + "'");
					return;
				}
				ach.useVoicedCommand(_command.substring(7), activeChar, null);
			}
			// Navigate throught Manor windows
			else if (_command.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2Npc)
					((L2Npc) object).onBypassFeedback(activeChar, _command);
			}
			else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				String[] str = _command.substring(6).trim().split(" ", 2);
				if (str.length == 1)
					activeChar.processQuestEvent(str[0], "");
				else
					activeChar.processQuestEvent(str[0], str[1]);
			}
			else if (_command.startsWith("_match"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("_diary"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("arenachange")) // change
			{
				final boolean isManager = activeChar.getCurrentFolkNPC() instanceof L2OlympiadManagerInstance;
				if (!isManager)
				{
					// Without npc, command can be used only in observer mode on arena
					if (!activeChar.inObserverMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGameId() < 0)
						return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return;
				}
				if (FOSEvent.isPlayerParticipant(activeChar.getObjectId()) || TvTEvent.isPlayerParticipant(activeChar.getObjectId()) || CTFEvent.isPlayerParticipant(activeChar.getObjectId()) || DMEvent.isPlayerParticipant(activeChar.getObjectId()) || LMEvent.isPlayerParticipant(activeChar.getObjectId()) || KTBEvent.isPlayerParticipant(activeChar.getObjectId()) || activeChar.isArenaAttack())
				{
					activeChar.sendMessage("Impossible observation, You Registed in others events.");
					return;
				}
				
				final int arenaId = Integer.parseInt(_command.substring(12).trim());
				activeChar.enterOlympiadObserverMode(arenaId);
			}
			StringTokenizer st = new StringTokenizer(_command, " ");
			String actualCommand = st.nextToken(); // Get actual command
			if (actualCommand.startsWith("st_goto"))
			{
				if (st.countTokens() <= 0)
					return;
				
				doTeleport(activeChar, Integer.parseInt(st.nextToken()));
				return;
			}
			
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Bad RequestBypassToServer: " + e, e);
		}
	}
	
	private static void doTeleport(Player player, int val)
	{
		L2TeleportLocation list = TeleportLocationTable.getInstance().getTemplate(val);
		
		if (list == null)
		{
			_log.warning("No teleport destination with id: " + val);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInOlympiadMode() || player.getPvpFlag() > 0 || player.isMoving() || player.isDead() || AttackStanceTaskManager.getInstance().isInAttackStance(player) || player.isCursedWeaponEquipped() || player.isInArenaEvent() || OlympiadManager.getInstance().isRegistered(player) || player.getKarma() > 0 || player.inObserverMode() || player.isArenaAttack() || player.isArenaProtection() || player.isInsideZone(ZoneId.ARENA_EVENT) || player.isInsideZone(ZoneId.FLAG_AREA_BOSS) || player.isInsideZone(ZoneId.SIEGE) || player.isInsideZone(ZoneId.PVP_CUSTOM) || player.isInJail() || (CTFEvent.isPlayerParticipant(player.getObjectId()) && CTFEvent.isStarted()) || (TvTEvent.isPlayerParticipant(player.getObjectId()) && TvTEvent.isStarted()) || (LMEvent.isPlayerParticipant(player.getObjectId()) && LMEvent.isStarted()) || (DMEvent.isPlayerParticipant(player.getObjectId()) && DMEvent.isStarted()) || (KTBEvent.isPlayerParticipant(player.getObjectId()) && KTBEvent.isStarted()) || (FOSEvent.isPlayerParticipant(player.getObjectId()) && FOSEvent.isStarted()))
		{
			player.sendMessage("You cannot teleport at this moment.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (Config.OPEN_EFFECT_CLASSIC_TELEPORTER)
		{
			MagicSkillUse msu = new MagicSkillUse(player, player, 2036, 1, 200, 0);
			player.broadcastPacket(msu);
			player.sendPacket(msu);
			
			ThreadPool.schedule(() -> {
				if (player.isDead())
					return;
				
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), 20);
			}, 450);
		}
		else
		{
			player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), 20);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private static final Map<String, Integer> CLASS_MAP = new HashMap<>();
	
	static
	{
		CLASS_MAP.put("Duelist", 88);
		CLASS_MAP.put("DreadNought", 89);
		CLASS_MAP.put("Phoenix_Knight", 90);
		CLASS_MAP.put("Hell_Knight", 91);
		CLASS_MAP.put("Sagittarius", 92);
		CLASS_MAP.put("Adventurer", 93);
		CLASS_MAP.put("Archmage", 94);
		CLASS_MAP.put("Soultaker", 95);
		CLASS_MAP.put("Arcana_Lord", 96);
		CLASS_MAP.put("Cardinal", 97);
		CLASS_MAP.put("Hierophant", 98);
		CLASS_MAP.put("Eva_Templar", 99);
		CLASS_MAP.put("Sword_Muse", 100);
		CLASS_MAP.put("Wind_Rider", 101);
		CLASS_MAP.put("Moonli_Sentinel", 102);
		CLASS_MAP.put("Mystic_Muse", 103);
		CLASS_MAP.put("Elemental_Master", 104);
		CLASS_MAP.put("Eva_Saint", 105);
		CLASS_MAP.put("Shillien_Templar", 106);
		CLASS_MAP.put("Spectral_Dancer", 107);
		CLASS_MAP.put("Ghost_Hunter", 108);
		CLASS_MAP.put("Ghost_Sentinel", 109);
		CLASS_MAP.put("Storm_Screamer", 110);
		CLASS_MAP.put("Spectral_Master", 111);
		CLASS_MAP.put("Shillen_Saint", 112);
		CLASS_MAP.put("Titan", 113);
		CLASS_MAP.put("Grand_Khauatari", 114);
		CLASS_MAP.put("Dominator", 115);
		CLASS_MAP.put("Doomcryer", 116);
		CLASS_MAP.put("Fortune_Seeker", 117);
		CLASS_MAP.put("Maestro", 118);
	}
	
	private static void ClassChangeCoin(Player player, String command)
	{
		String nameclasse = player.getTemplate().getClassName();
		String type = command;
		
		// Check if the selected class is valid
		if (type.equals("---SELECIONE---"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/CoinCustom/classes.htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendMessage("Please select the desired class to continue.");
			return;
		}
		
		// Check if the class exists in the map
		Integer classId = CLASS_MAP.get(type);
		if (classId != null)
		{
			// Check if the player is already in the selected class
			if (player.getClassId().getId() == classId)
			{
				player.sendMessage("Sorry, you are already in the " + nameclasse + " class.");
				return;
			}
			// Bloqueia troca de classe se tiver algum summon ou mount ativo
			if (player.hasPet() || player.isMounted() || player.getPet() != null || player.isCastingNow())
			{
				player.sendMessage("You cannot change class while having an active summon or mount.");
				return;
			}
			// Remove skills, update class, and finish
			RemoverSkills(player);
			player.setClassId(classId);
			if (!player.isSubClassActive())
			{
				player.setBaseClass(classId);
			}
			Finish(player);
		}
		else
		{
			player.sendMessage("Class not found: " + type);
		}
	}
	
	public static void RemoverSkills(Player activeChar)
	{
		
		for (L2Skill s : activeChar.getSkills().values())
			activeChar.removeSkill(s);
		// activeChar.removeSkill(s.getId(), true);
		
		activeChar.destroyItemByItemId("Classe Change", activeChar.getClassChangeItemId(), 1, null, true);
	}
	
	public static void Finish(Player activeChar)
	{
		
		// Verifica se o jogador possui um summon ativo (dragão, pet, etc.)
		if (activeChar.hasPet() || activeChar.isMounted() || activeChar.getPet() != null || activeChar.isCastingNow())
		{
			activeChar.sendMessage("You cannot change class while having an active summon or mount.");
			return;
		}
		
		String newclass = activeChar.getTemplate().getClassName();
		
		activeChar.sendMessage(activeChar.getName() + " is now a " + newclass + ".");
		activeChar.sendPacket(new ExShowScreenMessage("Congratulations. You is now a " + newclass + ".", 6000, 0x02, true));
		
		activeChar.refreshOverloaded();
		activeChar.store();
		activeChar.sendPacket(new HennaInfo(activeChar));
		activeChar.sendSkillList();
		activeChar.broadcastUserInfo();
		
		activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
		
		for (Player gm : GmListTable.getInstance().getAllGms(true))
			gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "Chat Manager", activeChar.getName() + " acabou de trocar sua Classe Base."));
		
		if (activeChar.isNoble())
		{
			StatsSet playerStat = Olympiad.getNobleStats(activeChar.getObjectId());
			if (!(playerStat == null))
			{
				AdminEditChar.updateClasse(activeChar);
				AdminEditChar.DeleteHero(activeChar);
				activeChar.sendMessage("You now has " + Olympiad.getInstance().getNoblePoints(activeChar.getObjectId()) + " Olympiad points.");
			}
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			// Remove all henna info stored for this sub-class.
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, activeChar.getObjectId());
			statement.setInt(2, 0);
			statement.execute();
			statement.close();
			
		}
		catch (Exception e)
		{
			_log.warning("Class Item: " + e);
		}
		ThreadPool.schedule(new Runnable()
		{
			
			@Override
			public void run()
			{
				activeChar.getClient().closeNow();
			}
		}, 6000);
		
	}
	
	private static void playerBook(Player activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;
		
		final StringTokenizer st = new StringTokenizer(path);
		final String[] cmd = st.nextToken().split("#");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/BookTeleport/" + cmd[0]);
		if (cmd.length > 1)
			html.setItemId(Integer.parseInt(cmd[1]));
		html.disableValidation();
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
		activeChar.sendPacket(html);
	}
	
	public static void TeleportSoloZone(Player activeChar)
	{
		// Lista de locais de teleporte
		final int[][] teleportLocations =
		{
			Config.GK_SPAWN_SOLOFARM_1,
			Config.GK_SPAWN_SOLOFARM_2,
			Config.GK_SPAWN_SOLOFARM_3,
			Config.GK_SPAWN_SOLOFARM_4,
			Config.GK_SPAWN_SOLOFARM_5,
			Config.GK_SPAWN_SOLOFARM_6
		};
		
		// Escolhe um local aleatório entre os disponíveis
		int[] loc = teleportLocations[Rnd.get(teleportLocations.length)];
		
		if (Config.OPEN_EFFECT_CLASSIC_TELEPORTER)
		{
			MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2036, 1, 200, 0);
			activeChar.broadcastPacket(MSU);
			activeChar.sendPacket(MSU);
			
			executorService.submit(() -> {
				try
				{
					TimeUnit.MILLISECONDS.sleep(450); // Espera o efeito
					activeChar.teleToLocation(loc[0], loc[1], loc[2], 0);
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			});
		}
		else
		{
			activeChar.teleToLocation(loc[0], loc[1], loc[2], 0);
		}
	}
	
	// Shutdown the executor service properly to avoid memory leaks (in your main game loop or shutdown routine)
	public static void shutdownExecutor()
	{
		executorService.shutdown();
		try
		{
			if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
			{
				executorService.shutdownNow();
			}
		}
		catch (InterruptedException e)
		{
			executorService.shutdownNow();
		}
	}
	
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static void playerHelp(Player activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;
		
		final StringTokenizer st = new StringTokenizer(path);
		final String[] cmd = st.nextToken().split("#");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/help/" + cmd[0]);
		if (cmd.length > 1)
			html.setItemId(Integer.parseInt(cmd[1]));
		html.disableValidation();
		activeChar.sendPacket(html);
	}
	
	private static void TeleportTimeFarmZone(Player player)
	{
		L2TimeFarmZone currentZone = TimeFarmZoneManager.getCurrentZone();
		if (currentZone == null)
		{
			player.sendMessage("There is no active Time Farm Zone at the moment.");
			return;
		}
		
		List<Location> spawns = currentZone.getSpawns();
		Location loc;
		
		if (!spawns.isEmpty())
			loc = spawns.get(Rnd.get(spawns.size())); // 🎯 spawn aleatório da zona
		else
			loc = currentZone.getZoneCenter(); // fallback caso não haja spawns
			
		if (player.isDead())
			player.doRevive();
		
		player.teleToLocation(loc, 20);
	}
	
	private static int getPlayerObjectIdByName(String name)
	{
		try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT obj_id FROM characters WHERE char_name=?"))
		{
			ps.setString(1, name);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
					return rs.getInt("obj_id");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void showItemSelection(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<center><font color=LEVEL size=5>Select Item to Send</font></center><br>");
		
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item.isEquipped() || item.isQuestItem() || item.isHeroItem() || item.isAugmented() || item.isTradable())
				continue;
			
			String iconName = IconTable.getIcon(item.getItemId());
			
			sb.append("<table width=280 cellspacing=1 cellpadding=1><tr>");
			
			sb.append("<td width=36 valign=\"middle\" align=\"center\"><img src=\"").append(iconName).append("\" width=32 height=32></td>");
			
			sb.append("<td width=150 valign=\"middle\"><font color=\"LEVEL\" size=5>").append(item.getItem().getName()).append(" +").append(item.getEnchantLevel()).append(" (").append(item.getCount()).append(")</font></td>");
			
			// Célula quantidade com tabela interna para melhor alinhamento
			sb.append("<td width=60 valign=\"middle\" align=\"center\">");
			sb.append("<table cellspacing=0 cellpadding=0><tr>");
			sb.append("<td><font size=5>Qty:</font></td>");
			sb.append("<td><edit var=\"count_").append(item.getObjectId()).append("\" width=25 height=15 maxlen=5></td>");
			sb.append("</tr></table>");
			sb.append("</td>");
			
			sb.append("<td width=40 valign=\"middle\" align=\"center\">").append("<button value=\"Send\" action=\"bypass -h email_confirm ").append(item.getObjectId()).append(" $count_").append(item.getObjectId()).append("\" width=45 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			
			sb.append("</tr></table>");
			sb.append("<br>");
		}
		
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public static void showItemList(Player player, int page)
	{
		final int ITEMS_PER_PAGE = 6;
		List<ItemInstance> items = new ArrayList<>();
		
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (!item.isEquipped() && !item.isQuestItem() && !item.isHeroItem() && !item.isAugmented() && item.isTradable() && !(player.isActiveAgathion() && Config.AGATHION_LISTID_RESTRICT.contains(item.getItemId())))
			{
				items.add(item);
			}
		}
		
		// Ordena os itens do maior enchant para o menor
		items.sort((item1, item2) -> Integer.compare(item2.getEnchantLevel(), item1.getEnchantLevel()));
		
		int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
		if (page < 1)
			page = 1;
		if (page > totalPages)
			page = totalPages;
		
		int start = (page - 1) * ITEMS_PER_PAGE;
		int end = Math.min(start + ITEMS_PER_PAGE, items.size());
		
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<center><font color=LEVEL size=4><b>Select Item to Send</b></font></center><br>");
		
		for (int i = start; i < end; i++)
		{
			ItemInstance item = items.get(i);
			String iconName = IconTable.getIcon(item.getItemId());
			
			sb.append("<table width=280 cellspacing=1 cellpadding=3><tr>");
			
			// Ícone
			sb.append("<td width=36 align=center><img src=\"").append(iconName).append("\" width=32 height=32></td>");
			
			// Nome + enchant (limitado a 22 caracteres)
			String itemName = item.getItem().getName();
			if (itemName.length() > 22)
				itemName = itemName.substring(0, 22) + "...";
			
			sb.append("<td width=180><font color=LEVEL>").append(itemName);
			if (item.getEnchantLevel() > 0)
				sb.append(" +").append(item.getEnchantLevel());
			sb.append("</font></td>");
			
			// Botão via link
			sb.append("<td width=60 align=center>");
			sb.append("<a action=\"bypass -h email_quantity ").append(item.getObjectId()).append("\"><font color=00FF00><u>Select</u></font></a>");
			sb.append("</td>");
			
			sb.append("</tr></table><br>");
		}
		
		// Paginação
		sb.append("<center>");
		if (page > 1)
			sb.append("<a action=\"bypass email_list ").append(page - 1).append("\">[Previous]</a> ");
		
		sb.append("Page ").append(page).append(" / ").append(totalPages);
		
		if (page < totalPages)
			sb.append(" <a action=\"bypass email_list ").append(page + 1).append("\">[Next]</a>");
		sb.append("</center><br>");
		
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public static void showItemQuantitySelection(Player player, int objectId)
	{
		ItemInstance item = player.getInventory().getItemByObjectId(objectId);
		if (item == null)
		{
			player.sendMessage("Item not found.");
			return;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		StringBuilder sb = new StringBuilder();
		
		String iconName = IconTable.getIcon(item.getItemId());
		
		sb.append("<html><body>");
		sb.append("<center><font color=LEVEL size=4><b>Send Item Confirmation</b></font></center><br>");
		
		sb.append("<table width=280 cellspacing=1 cellpadding=3><tr>");
		sb.append("<td width=36 align=center><img src=\"").append(iconName).append("\" width=32 height=32></td>");
		
		sb.append("<td width=180><font color=LEVEL>").append(item.getItem().getName());
		if (item.getEnchantLevel() > 0)
			sb.append(" +").append(item.getEnchantLevel());
		sb.append("</font><br>");
		
		sb.append("Available: ").append(item.getCount()).append("</td>");
		sb.append("</tr></table><br>");
		
		sb.append("<center>Quantity to send:<br>");
		sb.append("<edit var=\"count\" width=60 height=18 maxlen=5><br><br>");
		sb.append("<a action=\"bypass -h email_confirm ").append(objectId).append(" $count\"><font color=\"LEVEL\">[Send]</font></a>");
		sb.append("</center>");
		
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private static void openPanelSkin(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/tryskin.htm");
		player.sendPacket(html);
	}
	
}