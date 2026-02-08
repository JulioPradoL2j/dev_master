package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.dolls.DollsTable;
import net.sf.l2j.event.tournament.Arena3x3;
import net.sf.l2j.event.tournament.Arena5x5;
import net.sf.l2j.event.tournament.Arena9x9;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.RemoteClassMaster;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.SubClass;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scriptings.QuestState;
import net.sf.l2j.gameserver.util.FloodProtectors;
import net.sf.l2j.gameserver.util.FloodProtectors.Action;
 
public final class L2ClassMasterInstance extends L2NpcInstance
{
	public L2ClassMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/classmaster/disabled.htm";
		
		if (Config.ALLOW_CLASS_MASTERS)
			filename = "data/html/classmaster/" + getNpcId() + ".htm";
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!Config.ALLOW_CLASS_MASTERS)
			return;
		
		String[] commandStr = command.split(" ");
		String actualCommand = commandStr[0];
		
		String cmdParams = "";
		String cmdParams2 = "";
		
		if (commandStr.length >= 2)
			cmdParams = commandStr[1];
		if (commandStr.length >= 3)
			cmdParams2 = commandStr[2];
		
		
		if (actualCommand.equalsIgnoreCase("return_back"))
		{
			showChatWindow(player);
		}
		if (actualCommand.equalsIgnoreCase("create_clan"))
		{
			if (cmdParams.isEmpty())
				return;
			
			ClanTable.getInstance().createClan(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("create_academy"))
		{
			if (cmdParams.isEmpty())
				return;
			
			L2VillageMasterInstance.createSubPledge(player, cmdParams, null, L2Clan.SUBUNIT_ACADEMY, 5);
		}
		else if (actualCommand.equalsIgnoreCase("rename_pledge"))
		{
			if (cmdParams.isEmpty() || cmdParams2.isEmpty())
				return;
			
			L2VillageMasterInstance.renameSubPledge(player, Integer.valueOf(cmdParams), cmdParams2);
		}
		else if (actualCommand.equalsIgnoreCase("create_royal"))
		{
			if (cmdParams.isEmpty())
				return;
			
			L2VillageMasterInstance.createSubPledge(player, cmdParams, cmdParams2, L2Clan.SUBUNIT_ROYAL1, 6);
		}
		else if (actualCommand.equalsIgnoreCase("create_knight"))
		{
			if (cmdParams.isEmpty())
				return;
			
			L2VillageMasterInstance.createSubPledge(player, cmdParams, cmdParams2, L2Clan.SUBUNIT_KNIGHT1, 7);
		}
		else if (actualCommand.equalsIgnoreCase("assign_subpl_leader"))
		{
			if (cmdParams.isEmpty())
				return;
			
			L2VillageMasterInstance.assignSubPledgeLeader(player, cmdParams, cmdParams2);
		}
		else if (actualCommand.equalsIgnoreCase("create_ally"))
		{
			if (cmdParams.isEmpty())
				return;
			
			if (player.getClan() == null)
				player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
			else
				player.getClan().createAlly(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_ally"))
		{
			player.getClan().dissolveAlly(player);
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_clan"))
		{
			L2VillageMasterInstance.dissolveClan(player);
		}
		else if (actualCommand.equalsIgnoreCase("change_clan_leader"))
		{
			if (cmdParams.isEmpty())
				return;
			
			L2VillageMasterInstance.changeClanLeader(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("recover_clan"))
		{
			L2VillageMasterInstance.recoverClan(player);
		}
		else if (actualCommand.equalsIgnoreCase("increase_clan_level"))
		{
			if (player.getClan().levelUpClan(player))
				player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 0, 0));
		}
		else if (actualCommand.equalsIgnoreCase("learn_clan_skills"))
		{
			L2VillageMasterInstance.showPledgeSkillList(player);
		}
		else if (command.startsWith("Subclass"))
		{
			// Subclasses may not be changed while a skill is in use.
			if (player.isCastingNow() || player.isAllSkillsDisabled())
			{
				player.sendPacket(SystemMessageId.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
				return;
			}
			if (Arena3x3.getInstance().isRegistered(player) || Arena5x5.getInstance().isRegistered(player) || Arena9x9.getInstance().isRegistered(player))
			{
				player.sendMessage("You can not change subclass during tournament event.");
				return;
			}
			// Affecting subclasses (add/del/change) if registered in Olympiads makes you ineligible to compete.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
				OlympiadManager.getInstance().unRegisterNoble(player);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			int cmdChoice = 0;
			int paramOne = 0;
			int paramTwo = 0;
			
			try
			{
				cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
				
				int endIndex = command.indexOf(' ', 11);
				if (endIndex == -1)
					endIndex = command.length();
				
				paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
				if (command.length() > endIndex)
					paramTwo = Integer.parseInt(command.substring(endIndex).trim());
			}
			catch (Exception NumberFormatException)
			{
			}
			
			StringBuilder sb;
			Set<ClassId> subsAvailable;
			
			switch (cmdChoice)
			{
				case 0: // Subclass change menu
					html.setFile("data/html/villagemaster/SubClass.htm");
					break;
				
				case 1: // Add Subclass - Initial
					// Subclasses may not be added while a summon is active.
					if (player.getPet() != null)
					{
						player.sendPacket(SystemMessageId.CANT_SUBCLASS_WITH_SUMMONED_SERVITOR);
						return;
					}
					
					// Subclasses may not be added while you are over your weight limit.
					if (player.getInventoryLimit() * 0.8 <= player.getInventory().getSize() || player.getWeightPenalty() > 0)
					{
						player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
						return;
					}
					
					// Avoid giving player an option to add a new sub class, if they have three already.
			//		if (player.getSubClasses().size() >= 3)
					if (player.getSubClasses().size() >= Config.ALLOWED_SUBCLASS)
					{
						html.setFile("data/html/villagemaster/SubClass_Fail.htm");
						break;
					}
					
					subsAvailable = getAvailableSubClasses(player);
					if (subsAvailable == null || subsAvailable.isEmpty())
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					
					sb = new StringBuilder(300);
					for (ClassId subClass : subsAvailable)
						StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 4 ", subClass.getId(), "\" msg=\"1268;", subClass, "\">", subClass, "</a><br>");
					
					html.setFile("data/html/villagemaster/SubClass_Add.htm");
					html.replace("%list%", sb.toString());
					break;
				
				case 2: // Change Class - Initial
					// Subclasses may not be changed while a summon is active.
					if (player.getPet() != null)
					{
						player.sendPacket(SystemMessageId.CANT_SUBCLASS_WITH_SUMMONED_SERVITOR);
						return;
					}
					
					// Subclasses may not be changed while a you are over your weight limit.
					if (player.getInventoryLimit() * 0.8 <= player.getInventory().getSize() || player.getWeightPenalty() > 0)
					{
						player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
						return;
					}
					
					if (player.getSubClasses().isEmpty())
						html.setFile("data/html/villagemaster/SubClass_ChangeNo.htm");
					else
					{
						sb = new StringBuilder(300);
						
						if (checkVillageMaster(player.getBaseClass()))
							StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 5 0\">", CharTemplateTable.getInstance().getClassNameById(player.getBaseClass()), "</a><br>");
						
						for (Iterator<SubClass> subList = player.getSubClasses().values().iterator(); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							if (checkVillageMaster(subClass.getClassDefinition()))
								StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 5 ", subClass.getClassIndex(), "\">", subClass.getClassDefinition(), "</a><br>");
						}
						
						if (sb.length() > 0)
						{
							html.setFile("data/html/villagemaster/SubClass_Change.htm");
							html.replace("%list%", sb.toString());
						}
						else
							html.setFile("data/html/villagemaster/SubClass_ChangeNotFound.htm");
					}
					break;
				
				case 3: // Change/Cancel Subclass - Initial
					player.abortAttack();
					player.abortCast();
					
					if (player.isInCombat())
					{
						player.sendMessage("You can not be in combat mode..");			
						return;
					}
					if (player.getSubClasses().isEmpty())
					{
						html.setFile("data/html/villagemaster/SubClass_ModifyEmpty.htm");
						break;
					}
					// custom value
					if (player.getSubClasses().size() > Config.ALLOWED_SUBCLASS)
					{
						sb = new StringBuilder(300);
						int classIndex = 1;
						
						for (Iterator<SubClass> subList = player.getSubClasses().values().iterator(); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							StringUtil.append(sb, "Sub-class ", classIndex++, "<br>", "<a action=\"bypass -h npc_%objectId%_Subclass 6 ", subClass.getClassIndex(), "\">", CharTemplateTable.getInstance().getClassNameById(subClass.getClassId()), "</a><br>");
						}
						
						html.setFile("data/html/villagemaster/SubClass_ModifyCustom.htm");
						html.replace("%list%", sb.toString());
					}
					else
					{
					// retail html contain only 3 subclasses
						html.setFile("data/html/villagemaster/SubClass_Modify.htm");
						if (player.getSubClasses().containsKey(1))
							html.replace("%sub1%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(1).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 1\">%sub1%</a><br>", "");
						
						if (player.getSubClasses().containsKey(2))
							html.replace("%sub2%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(2).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 2\">%sub2%</a><br>", "");
						
						if (player.getSubClasses().containsKey(3))
							html.replace("%sub3%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(3).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 3\">%sub3%</a><br>", "");
						
						
						if (player.getSubClasses().containsKey(4))
							html.replace("%sub4%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(4).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 4\">%sub4%</a><br>", "");
						
						
						if (player.getSubClasses().containsKey(5))
							html.replace("%sub5%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(5).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 5\">%sub5%</a><br>", "");
						
						if (player.getSubClasses().containsKey(6))
							html.replace("%sub6%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(6).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 6\">%sub6%</a><br>", "");
						
						if (player.getSubClasses().containsKey(7))
							html.replace("%sub7%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(7).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 7\">%sub7%</a><br>", "");
						
						if (player.getSubClasses().containsKey(8))
							html.replace("%sub8%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(8).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 8\">%sub8%</a><br>", "");
						
						if (player.getSubClasses().containsKey(9))
							html.replace("%sub9%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(9).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 9\">%sub9%</a><br>", "");
						
						if (player.getSubClasses().containsKey(10))
							html.replace("%sub10%", CharTemplateTable.getInstance().getClassNameById(player.getSubClasses().get(10).getClassId()));
						else
							html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 10\">%sub10%</a><br>", "");
					}
					break;
				
				case 4: // Add Subclass - Action (Subclass 4 x[x])
					if (!FloodProtectors.performAction(player.getClient(), Action.SUBCLASS))
						return;
					
					boolean allowAddition = true;
					
					if (player.getSubClasses().size() >= Config.ALLOWED_SUBCLASS)
						allowAddition = false;
					
					if (player.getLevel() < 75)
						allowAddition = false;
					
					if (allowAddition)
					{
						for (SubClass subclass : player.getSubClasses().values())
						{
							if (subclass.getLevel() < 75)
							{
								allowAddition = false;
								break;
							}
						}
					}
					
					// Verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items.
				//	if (allowAddition && !Config.SUBCLASS_WITHOUT_QUESTS)
					if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
						allowAddition = checkQuests(player);
					
					// If they both exist, remove both unique items and continue with adding the subclass.
					if (allowAddition && isValidNewSubClass(player, paramOne))
					{
						if (!player.addSubClass(paramOne, player.getSubClasses().size() + 1))
							return;
						
						player.setActiveClass(player.getSubClasses().size());
						
						html.setFile("data/html/villagemaster/SubClass_AddOk.htm");
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.
						 
					}
					else
						html.setFile("data/html/villagemaster/SubClass_Fail.htm");
					break;
				
				case 5: // Change Class - Action
					if (!FloodProtectors.performAction(player.getClient(), Action.SUBCLASS))
						return;
					
					if (player.getClassIndex() == paramOne)
					{
						html.setFile("data/html/villagemaster/SubClass_Current.htm");
						break;
					}
					
					if (paramOne == 0)
					{
						if (!checkVillageMaster(player.getBaseClass()))
							return;
					}
					else
					{
						try
						{
							if (!checkVillageMaster(player.getSubClasses().get(paramOne).getClassDefinition()))
								return;
						}
						catch (NullPointerException e)
						{
							return;
						}
					}
					
					player.setActiveClass(paramOne);
					
					player.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED); // Transfer completed.
					return;
					
				case 6: // Change/Cancel Subclass - Choice
					// validity check
				//	if (paramOne < 1 || paramOne > 3)
					if (paramOne < 1 || paramOne > Config.ALLOWED_SUBCLASS)
						return;
					
					subsAvailable = getAvailableSubClasses(player);
					
					// another validity check
					if (subsAvailable == null || subsAvailable.isEmpty())
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					
					sb = new StringBuilder(300);
					for (ClassId subClass : subsAvailable)
						StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 7 ", paramOne, " ", subClass.getId(), "\" msg=\"1445;", "\">", subClass, "</a><br>");
					
					switch (paramOne)
					{
						case 1:
							html.setFile("data/html/villagemaster/SubClass_ModifyChoice1.htm");
							break;
						
						case 2:
							html.setFile("data/html/villagemaster/SubClass_ModifyChoice2.htm");
							break;
						
						case 3:
							html.setFile("data/html/villagemaster/SubClass_ModifyChoice3.htm");
							break;
						
						default:
							html.setFile("data/html/villagemaster/SubClass_ModifyChoice.htm");
					}
					html.replace("%list%", sb.toString());
					break;
				
				case 7: // Change Subclass - Action
					if (!FloodProtectors.performAction(player.getClient(), Action.SUBCLASS))
						return;
					
					if (!isValidNewSubClass(player, paramTwo))
						return;
					
					if (player.modifySubClass(paramOne, paramTwo))
					{
						player.abortCast();
						player.stopAllEffectsExceptThoseThatLastThroughDeath(); // all effects from old subclass stopped!
						player.stopCubics();
						player.setActiveClass(paramOne);
						
						html.setFile("data/html/villagemaster/SubClass_ModifyOk.htm");
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.
						 
					}
					else
					{
						player.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.
						
						player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
						return;
					}
					break;
			}
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		
		if (command.startsWith("ClassChange"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/classmaster/classes.htm");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		if (player.isAio() && !Config.ALLOW_AIO_USE_CM)
		{
			player.sendMessage("Aio Buffers Can't Speak To Class Masters.");
			return;
		}
		if (player.isSellBuff())
		{
			player.sendMessage("Sell buffs Character, Can't Speak To Class Masters.");
			return;
		}
		if (command.startsWith("1stClass"))
			showHtmlMenu(player, getObjectId(), 1);
		else if (command.startsWith("2ndClass"))
			showHtmlMenu(player, getObjectId(), 2);
		else if (command.startsWith("3rdClass"))
			showHtmlMenu(player, getObjectId(), 3);
		else if (command.startsWith("change_class"))
		{
			int val = Integer.parseInt(command.substring(13));
			
			if (checkAndChangeClass(player, val))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile("data/html/classmaster/ok.htm");
				html.replace("%name%", CharTemplateTable.getInstance().getClassNameById(val));
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("become_noble"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (!player.isNoble())
			{
				player.setNoble(true, true);
				player.sendPacket(new UserInfo(player));
				html.setFile("data/html/classmaster/nobleok.htm");
				player.sendPacket(html);
			}
			else
			{
				html.setFile("data/html/classmaster/alreadynoble.htm");
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("learn_skills"))
		{
			DollsTable.refreshAllRuneSkills(player);
			player.giveAvailableSkills();
			player.sendSkillList();
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	/**
	 * Check subclass classId for validity (villagemaster race/type is not contains in previous subclasses, but in allowed subclasses). Base class not added into allowed subclasses.
	 * @param player : The player to check.
	 * @param classId : The class id to check.
	 * @return true if the {@link Player} can pick this subclass id.
	 */
	private final static boolean isValidNewSubClass(Player player, int classId)
	{
		if (!checkVillageMaster(classId))
			return false;
		
		final ClassId cid = ClassId.VALUES[classId];
		for (SubClass subclass : player.getSubClasses().values())
		{
			if (subclass.getClassDefinition().equalsOrChildOf(cid))
				return false;
		}
		
		final Set<ClassId> availSubs = ClassId.getAvailableSubclasses(player);
		if (availSubs == null || availSubs.isEmpty())
			return false;
		
		boolean found = false;
		for (ClassId pclass : availSubs)
		{
			if (pclass.getId() == classId)
			{
				found = true;
				break;
			}
		}
		
		return found;
	}
	protected static boolean checkQuests(Player player)
	{
		// Noble players can add subbclasses without quests
		if (player.isNoble())
			return true;
		
		QuestState qs = player.getQuestState("Q234_FatesWhisper");
		if (qs == null || !qs.isCompleted())
			return false;
		
		qs = player.getQuestState("Q235_MimirsElixir");
		if (qs == null || !qs.isCompleted())
			return false;
		
		return true;
	}
	public final static boolean checkVillageMaster(int classId)
	{
		return checkVillageMaster(ClassId.VALUES[classId]);
	}
	public final static boolean checkVillageMaster(ClassId pclass)
	{
		 if (Config.ALT_GAME_SUBCLASS_EVERYWHERE)
			 return true;
		return checkVillageMasterRace(pclass) && checkVillageMasterTeachType(pclass);
	}
	protected static boolean checkVillageMasterRace(ClassId pclass)
	{
		return true;
	}
	protected static boolean checkVillageMasterTeachType(ClassId pclass)
	{
		return true;
	}
	private final static Set<ClassId> getAvailableSubClasses(Player player)
	{
		Set<ClassId> availSubs = ClassId.getAvailableSubclasses(player);
		
		if (availSubs != null && !availSubs.isEmpty())
		{
			for (Iterator<ClassId> availSub = availSubs.iterator(); availSub.hasNext();)
			{
				ClassId classId = availSub.next();
				
				// check for the village master
				if (!checkVillageMaster(classId))
				{
					availSub.remove();
					continue;
				}
				
				// scan for already used subclasses
				for (SubClass subclass : player.getSubClasses().values())
				{
					if (subclass.getClassDefinition().equalsOrChildOf(classId))
					{
						availSub.remove();
						break;
					}
				}
			}
		}
		
		return availSubs;
	}

	private static final void showHtmlMenu(Player player, int objectId, int level)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(objectId);
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(level))
		{
			final StringBuilder sb = new StringBuilder(100);
			sb.append("<html><body>");
			
			switch (player.getClassId().level())
			{
				case 0:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(1))
						sb.append("Come back here when you reached level 20 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back after your first occupation change.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 1:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back here when you reached level 40 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 2:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back here when you reached level 76 to change your class.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 3:
					sb.append("There is no class change available for you anymore.<br>");
					break;
			}
			sb.append("</body></html>");
			html.setHtml(sb.toString());
		}
		else
		{
			final ClassId currentClassId = player.getClassId();
			if (currentClassId.level() >= level)
				html.setFile("data/html/classmaster/nomore.htm");
			else
			{
				final int minLevel = getMinLevel(currentClassId.level());
				if (player.getLevel() >= minLevel || Config.ALLOW_ENTIRE_TREE)
				{
					final StringBuilder menu = new StringBuilder(100);
					for (ClassId cid : ClassId.VALUES)
					{
						if (cid.level() != level)
							continue;
						
						if (validateClassId(currentClassId, cid))
							StringUtil.append(menu, "<a action=\"bypass -h npc_%objectId%_change_class ", cid.getId(), "\">", CharTemplateTable.getInstance().getClassNameById(cid.getId()), "</a><br>");
					}
					
					if (menu.length() > 0)
					{
						html.setFile("data/html/classmaster/template.htm");
						html.replace("%name%", CharTemplateTable.getInstance().getClassNameById(currentClassId.getId()));
						html.replace("%menu%", menu.toString());
					}
					else
					{
						html.setFile("data/html/classmaster/comebacklater.htm");
						html.replace("%level%", getMinLevel(level - 1));
					}
				}
				else
				{
					if (minLevel < Integer.MAX_VALUE)
					{
						html.setFile("data/html/classmaster/comebacklater.htm");
						html.replace("%level%", minLevel);
					}
					else
						html.setFile("data/html/classmaster/nomore.htm");
				}
			}
		}
		
		html.replace("%objectId%", objectId);
		html.replace("%req_items%", getRequiredItems(level));
		player.sendPacket(html);
	}
	
	private static final boolean checkAndChangeClass(Player player, int val)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.level()) > player.getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return false;
		
		if (!validateClassId(currentClassId, val))
			return false;
		
		int newJobLevel = currentClassId.level() + 1;
		
		// Weight/Inventory check
		if (!Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel).isEmpty())
		{
			if (player.getWeightPenalty() >= 3)
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return false;
			}
		}
		
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(newJobLevel);
		
		// check if player have all required items for class transfer
		for (IntIntHolder item : neededItems)
		{
			if (player.getInventory().getInventoryItemCount(item.getId(), -1) < item.getValue())
			{
				player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return false;
			}
		}
		
		// get all required items for class transfer
		for (IntIntHolder item : neededItems)
		{
			if (!player.destroyItemByItemId("ClassMaster", item.getId(), item.getValue(), player, true))
				return false;
		}
		
		// reward player with items
		for (IntIntHolder item : Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel))
			player.addItem("ClassMaster", item.getId(), item.getValue(), player, true);
		
		player.setClassId(val);
		
		if (player.isSubClassActive())
			player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
		else
			player.setBaseClass(player.getActiveClass());
		DollsTable.refreshAllRuneSkills(player);
		player.removeItens();
		player.removeFakeWeapon();
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		RemoteClassMaster.showQuestionMark(player);
		return true;
	}
	
	/**
	 * @param level - current skillId level (0 - start, 1 - first, etc)
	 * @return minimum player level required for next class transfer
	 */
	private static final int getMinLevel(int level)
	{
		switch (level)
		{
			case 0:
				return 20;
			case 1:
				return 40;
			case 2:
				return 76;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param val new class index
	 * @return
	 */
	private static final boolean validateClassId(ClassId oldCID, int val)
	{
		try
		{
			return validateClassId(oldCID, ClassId.VALUES[val]);
		}
		catch (Exception e)
		{
			// possible ArrayOutOfBoundsException
		}
		return false;
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param newCID new ClassId
	 * @return true if class change is possible
	 */
	private static final boolean validateClassId(ClassId oldCID, ClassId newCID)
	{
		if (newCID == null)
			return false;
		
		if (oldCID == newCID.getParent())
			return true;
		
		if (Config.ALLOW_ENTIRE_TREE && newCID.childOf(oldCID))
			return true;
		
		return false;
	}
	
	private static String getRequiredItems(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td>none</td></r>";
		
		final StringBuilder sb = new StringBuilder();
		for (IntIntHolder item : neededItems)
			StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", item.getValue(), "</font></td><td>", ItemTable.getInstance().getTemplate(item.getId()).getName(), "</td></tr>");
		
		return sb.toString();
	}
 
	public static void onRemoteClassPick(Player player, int newClassId)
	{
	    if (player == null)
	        return;

	    final ClassId current = player.getClassId();
	    final int curTier = current.level();

	    // já é 3rd
	    if (curTier >= 3)
	        return;

	    final ClassId target = ClassId.VALUES[newClassId];
	    if (target == null)
	        return;

	    // só pode escolher o PRÓXIMO tier (não pode pular)
	    final int nextTier = curTier + 1;
	    if (target.level() != nextTier)
	    {
	        player.sendMessage("You can't pick that class now.");
	        return;
	    }

	    // valida árvore (parent/child)
	    if (!validateRemoteClass(current, target))
	    {
	        player.sendMessage("Invalid class change.");
	        return;
	    }

	    // valida nível mínimo do tier atual (20/40/76) - trava HARD
	    final int minLevel = getMinLevel(curTier);
	    if (player.getLevel() < minLevel && !Config.ALLOW_ENTIRE_TREE)
	    {
	        player.sendMessage("You need level " + minLevel + " to change class.");
	        return;
	    }

	    // valida se esse tier está permitido no settings
	    if (!Config.CLASS_MASTER_SETTINGS.isAllowed(nextTier))
	    {
	        player.sendMessage("Class change is disabled.");
	        return;
	    }

	    // aplica exatamente como o ClassMaster normal
	    checkAndChangeClass(player, newClassId);
	}

	 
	private static boolean validateRemoteClass(ClassId oldCID, ClassId newCID)
	{
	    if (newCID == null)
	        return false;

	    // caminho normal (parent direto)
	    if (oldCID == newCID.getParent())
	        return true;

	    // árvore inteira (se você realmente quiser esse modo)
	    if (Config.ALLOW_ENTIRE_TREE && newCID.childOf(oldCID))
	        return true;

	    return false;
	}



}