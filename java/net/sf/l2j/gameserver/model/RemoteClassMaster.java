package net.sf.l2j.gameserver.model;

import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2ClassMasterInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowQuestionMark;
import net.sf.l2j.gameserver.util.FloodProtectors;
import net.sf.l2j.gameserver.util.FloodProtectors.Action;

public final class RemoteClassMaster
{
	private static final int REMOTE_QM_ID = 1001;
	
	private RemoteClassMaster()
	{
	}
	
	public static boolean onTutorialLink(Player player, String request)
	{
		if (!Config.ALTERNATE_CLASS_MASTER || player == null || request == null)
			return false;
		
		if ("close".equalsIgnoreCase(request))
		{
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			return true;
		}
		
		if (!request.startsWith("CO"))
			return false;
		
		if (!FloodProtectors.performAction(player.getClient(), Action.SERVER_BYPASS))
			return true; // intercepta mesmo assim (anti-flood)
			
		try
		{
			final int classId = Integer.parseInt(request.substring(2));
			L2ClassMasterInstance.onRemoteClassPick(player, classId); // método helper que você vai adicionar (abaixo)
		}
		catch (Exception e)
		{
			// ignore
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		return true;
	}
	
	public static boolean onTutorialQuestionMark(Player player, int number)
	{
		if (!Config.ALTERNATE_CLASS_MASTER || player == null)
			return false;
		
		if (number != REMOTE_QM_ID)
			return false;
		
		showTutorialHtml(player);
		return true;
	}
	
	public static void showQuestionMark(Player player)
	{
	    if (player == null || !Config.ALTERNATE_CLASS_MASTER)
	        return;

	    final ClassId current = player.getClassId();
	    final int curTier = current.level();

	    // já é 3rd
	    if (curTier >= 3)
	        return;

	    // HARD RULE: Remote só no padrão 20/40/76 (sem AllowEntireTree aqui)
	    final int minLevel = getMinLevelForTier(curTier);
	    if (player.getLevel() < minLevel)
	        return;

	    // tier seguinte precisa estar habilitado
	    final int nextTier = curTier + 1;
	    if (!Config.CLASS_MASTER_SETTINGS.isAllowed(nextTier))
	        return;

	    player.sendPacket(new TutorialShowQuestionMark(REMOTE_QM_ID));
	}

	
	private static final String REMOTE_HTML = "data/html/classmaster/template.htm";
	
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
	
	private static void showTutorialHtml(Player player)
	{
		final ClassId current = player.getClassId();
		final int curTier = current.level();
		
		// já é 3rd class
		if (curTier >= 3)
		{
			player.sendPacket(new TutorialShowHtml(simple("There is no class change available for you anymore.")));
			return;
		}
		
		final int nextTier = curTier + 1;
		final int minLevel = getMinLevelForTier(curTier); // 20/40/76
		
		if (player.getLevel() < minLevel && !Config.ALLOW_ENTIRE_TREE)
		{
			player.sendPacket(new TutorialShowHtml(simple("Come back when you reached level " + minLevel + " to change your class.")));
			return;
		}
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(nextTier))
		{
			player.sendPacket(new TutorialShowHtml(simple("Class change is currently disabled.")));
			return;
		}
		
		// monta menu SOMENTE do próximo tier
		final StringBuilder menu = new StringBuilder(512);
		for (ClassId cid : ClassId.VALUES)
		{
			if (cid == null)
				continue;
			
			if (cid.level() != nextTier)
				continue;
			
			if (!validateClassId(current, cid))
				continue;
			
			final String className = CharTemplateTable.getInstance().getClassNameById(cid.getId());
			StringUtil.append(menu, "<a action=\"link CO", cid.getId(), "\"><font color=\"FFFFFF\">", escapeHtml(className), "</font></a><br>");
		}
		
		if (menu.length() == 0)
		{
			player.sendPacket(new TutorialShowHtml(simple("There are no available class changes at this time.")));
			return;
		}
		
		// carrega html do data/
		String msg = HtmCache.getInstance().getHtm(REMOTE_HTML);
		
		// se não achou/veio vazio, nunca manda branco
		if (msg == null || msg.isEmpty())
		{
			player.sendPacket(new TutorialShowHtml(simple("Missing html: " + REMOTE_HTML)));
			return;
		}
		
		final String currentName = CharTemplateTable.getInstance().getClassNameById(current.getId());
		
		msg = msg.replace("%name%", escapeHtml(currentName));
		msg = msg.replace("%menu%", menu.toString());
		msg = msg.replace("%req_items%", getRequiredItemsColored(nextTier));
		
		player.sendPacket(new TutorialShowHtml(msg));
	}
	
	// mantém seus helpers:
	private static String escapeHtml(String s)
	{
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	private static String simple(String text)
	{
		return "<html><body><center><font color=\"FFFFFF\">" + escapeHtml(text) + "</font></center><br><center><a action=\"link close\"><font color=\"AAAAAA\">Close</font></a></center></body></html>";
	}
	
	private static int getMinLevelForTier(int curTier)
	{
		switch (curTier)
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
	
	private static String getRequiredItemsColored(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td><font color=\"AAAAAA\">none</font></td></tr>";
		
		final StringBuilder sb = new StringBuilder(256);
		for (IntIntHolder item : neededItems)
		{
			final String itemName = ItemTable.getInstance().getTemplate(item.getId()).getName();
			StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", item.getValue(), "</font></td><td><font color=\"FFFFFF\">", escape(itemName), "</font></td></tr>");
		}
		return sb.toString();
	}
	
	private static String escape(String s)
	{
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
