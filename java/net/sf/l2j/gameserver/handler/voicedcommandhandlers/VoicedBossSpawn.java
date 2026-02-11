package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.xml.IconTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.RaidBossInfoManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedBossSpawn implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"boss",
		"shifffmodddrop",
		"dropfind"
	};
	
	// Boss list
	private static final int BOSS_PAGE_SIZE = 2;
	
	// Drop list
	private static final int DROP_PAGE_SIZE = 5;
	
	private static final SimpleDateFormat RESPAWN_FMT = new SimpleDateFormat("dd/MM HH:mm");
	private static final DecimalFormat DF_SMALL = new DecimalFormat("#.####");
	private static final DecimalFormat DF_MED = new DecimalFormat("#.###");
	private static final DecimalFormat DF_BIG = new DecimalFormat("##.##");
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command == null || activeChar == null)
			return false;
		
		if (command.startsWith("boss"))
		{
			int page = 1;
			final String[] parts = command.split(" ");
			if (parts.length > 1)
			{
				try
				{
					page = Integer.parseInt(parts[1]);
				}
				catch (Exception ignored)
				{
					page = 1;
				}
			}
			showBossListWindow(activeChar, page);
			return true;
		}
		
		if (command.startsWith("shifffmodddrop"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // command
			
			if (!st.hasMoreTokens())
				return true;
			
			final int npcId = Integer.parseInt(st.nextToken());
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			showNpcDropList(activeChar, npcId, page);
			return true;
		}
		if (command.startsWith("dropfind"))
		{
			// dropfind <npcId> <query...> [page]
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // dropfind
			
			if (!st.hasMoreTokens())
				return true;
			
			final int npcId = Integer.parseInt(st.nextToken());
			
			// o resto pode ser query e/ou page no final
			final List<String> rest = new ArrayList<>();
			while (st.hasMoreTokens())
				rest.add(st.nextToken());
			
			int page = 1;
			String query = "";
			
			if (!rest.isEmpty())
			{
				// se o último token for número, é page
				final String last = rest.get(rest.size() - 1);
				if (isInteger(last))
				{
					page = Integer.parseInt(last);
					rest.remove(rest.size() - 1);
				}
				query = String.join(" ", rest).trim();
			}
			
			showNpcDropListFiltered(activeChar, npcId, page, query);
			return true;
		}
		
		return false;
	}
	
	private static void showNpcDropListFiltered(Player player, int npcId, int page, String query)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
		if (template == null)
			return;
		
		if (template.getDropData().isEmpty())
		{
			player.sendMessage("This target have not drop info.");
			return;
		}
		
		final String q = (query == null) ? "" : query.trim();
		final boolean hasFilter = !q.isEmpty();
		
		final boolean filterById = hasFilter && isInteger(q);
		final int wantedId = filterById ? Integer.parseInt(q) : 0;
		final String wantedName = hasFilter ? normalize(q) : "";
		
		final List<DropLine> lines = new ArrayList<>(256);
		
		final List<DropCategory> categories = new ArrayList<>();
		template.getDropData().forEach(categories::add);
		Collections.reverse(categories);
		
		for (DropCategory cat : categories)
		{
			for (DropData drop : cat.getAllDrops())
			{
				final Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
				if (item == null)
					continue;
				
				// aplica filtro
				if (hasFilter)
				{
					if (filterById)
					{
						if (drop.getItemId() != wantedId)
							continue;
					}
					else
					{
						final String itemName = normalize(item.getName());
						if (!itemName.contains(wantedName))
							continue;
					}
				}
				
				final boolean sweep = cat.isSweep();
				
				// mantenho a sua lógica atual (ajuste para 1e6 se seu pack for assim)
				final double chance = drop.getChance() / 10000.0;
				
				final long min = Math.round(Config.RATE_DROP_ITEMS * drop.getMinDrop());
				final long max = Math.round(Config.RATE_DROP_ITEMS * drop.getMaxDrop());
				
				lines.add(new DropLine(item.getItemId(), item.getName(), sweep, chance, min, max));
			}
		}
		
		final int maxPage = Math.max(1, (int) Math.ceil(lines.size() / (double) DROP_PAGE_SIZE));
		page = clamp(page, 1, maxPage);
		
		final int start = (page - 1) * DROP_PAGE_SIZE;
		final int end = Math.min(start + DROP_PAGE_SIZE, lines.size());
		
		final String npcName = template.getName();
		
		final StringBuilder sb = new StringBuilder(4096);
		sb.append("<html><title>Boss Info</title><body>");
		
		sb.append(uiHeader("Drop List", "icon.etc_skull_black_i00", "Target: <font color=LEVEL>" + truncate(escape(npcName), 32) + "</font>"));
		
		sb.append("<br>");
		
		// barra de "filtro"
		sb.append("<table width=300 cellpadding=4 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=160 align=left>");
		sb.append("<font color=b0b0b0>Filter:</font> ");
		
		if (hasFilter)
			sb.append("<font color=FFD36B><b>").append(escape(q)).append("</b></font>");
		else
			sb.append("<font color=404040>none</font>");
		
		sb.append("</td>");
		sb.append("<td align=right>");
		if (hasFilter)
		{
			// limpar filtro volta pra lista normal
			sb.append("<a action=\"bypass -h voiced_shifffmodddrop ").append(npcId).append(" 1\">").append("<font color=ffffff>Clear</font></a>");
		}
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		
		if (lines.isEmpty())
		{
			sb.append(emptyState("No drops matched your filter."));
		}
		else
		{
			for (int i = start; i < end; i++)
				sb.append(dropCard(lines.get(i)));
		}
		
		// paginação: precisa manter o filtro no bypass
		final String base = hasFilter ? ("voiced_dropfind " + npcId + " " + escapeBypass(q)) : ("voiced_shifffmodddrop " + npcId);
		
		sb.append(pagination(base, page, maxPage));
		
		sb.append("<br>");
		sb.append("<table width=300 cellpadding=0 cellspacing=0>");
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Back\" action=\"bypass -h voiced_boss 1\" width=70 height=22 back=\"L2UI_CH3.smallbutton2\" fore=\"L2UI_CH3.smallbutton2\">");
		sb.append("</td></tr>");
		sb.append("</table>");
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setHtml(sb.toString());
		player.sendPacket(htm);
	}
	
	private static boolean isInteger(String s)
	{
		if (s == null || s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++)
			if (!Character.isDigit(s.charAt(i)))
				return false;
		return true;
	}
	
	private static String normalize(String s)
	{
		return s == null ? "" : s.toLowerCase();
	}
	
	private static String escapeBypass(String s)
	{
		if (s == null)
			return "";
		// mantém letras/números/espaço e alguns básicos
		String out = s.replaceAll("[^a-zA-Z0-9 _\\-\\.]", "");
		out = out.trim().replaceAll("\\s+", " ");
		return out;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	// =========================
	// Boss List UI
	// =========================
	public static void showBossListWindow(Player player, int page)
	{
		final List<Integer> raidBosses = new ArrayList<>(Config.LIST_RAID_BOSS_IDS);
		final List<Integer> grandBosses = new ArrayList<>(Config.LIST_GRAND_BOSS_IDS);
		
		final List<BossRow> raidRows = buildBossRows(raidBosses);
		final List<BossRow> grandRows = buildBossRows(grandBosses);
		
		// page clamp based on RAID list (the one paginated)
		final int maxPage = Math.max(1, (int) Math.ceil(raidRows.size() / (double) BOSS_PAGE_SIZE));
		page = clamp(page, 1, maxPage);
		
		final int start = (page - 1) * BOSS_PAGE_SIZE;
		final int end = Math.min(start + BOSS_PAGE_SIZE, raidRows.size());
		
		final StringBuilder sb = new StringBuilder(4096);
		sb.append("<html><title>Boss Info</title><body>");
		sb.append(uiHeader("Boss Tracker", "icon.etc_skull_black_i00", "Hello, <font color=LEVEL>" + player.getName()).append(escape("")).append("</font>").toString());
		sb.append("<br>");
		
		// RAID section
		sb.append(sectionTitle("Raid Bosses", raidRows.size()));
		sb.append("<table width=300 cellpadding=0 cellspacing=0 bgcolor=000000>");
		sb.append("<tr><td>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		sb.append("</td></tr>");
		sb.append("</table>");
		
		if (raidRows.isEmpty())
		{
			sb.append(emptyState("No raid bosses configured."));
		}
		else
		{
			for (int i = start; i < end; i++)
				sb.append(bossLine(raidRows.get(i)));
		}
		
		sb.append(pagination("voiced_boss", page, maxPage));
		
		sb.append("<br1>");
		
		// GRAND section (not paginated here; you can paginate too if quiser)
		sb.append(sectionTitle("Grand Bosses", grandRows.size()));
		sb.append("<table width=300 cellpadding=0 cellspacing=0 bgcolor=000000>");
		sb.append("<tr><td><img src=\"L2UI.SquareGray\" width=300 height=1></td></tr>");
		sb.append("</table>");
		
		if (grandRows.isEmpty())
		{
			sb.append(emptyState("No grand bosses configured."));
		}
		else
		{
			for (BossRow row : grandRows)
				sb.append(bossLine(row));
		}
		
		sb.append("<br1>");
		sb.append("<table width=300 cellpadding=0 cellspacing=0>");
		sb.append("<tr>");
		sb.append("<td align=center>");
		sb.append("<button value=\"Back     \" action=\"bypass -h voiced_menu\" width=90 height=22 back=\"L2UI_CH3.smallbutton2\" fore=\"L2UI_CH3.smallbutton2\">");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setHtml(sb.toString());
		player.sendPacket(htm);
	}
	
	private static List<BossRow> buildBossRows(List<Integer> bossIds)
	{
		final List<BossRow> rows = new ArrayList<>();
		if (bossIds == null || bossIds.isEmpty())
			return rows;
		
		for (int bossId : bossIds)
		{
			final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
			if (template == null)
				continue;
			
			final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
			final boolean alive = respawnTime <= System.currentTimeMillis();
			final String name = template.getName();
			
			rows.add(new BossRow(bossId, name, alive, respawnTime));
		}
		
		// Alive first, then by name
		rows.sort(Comparator.comparing((BossRow r) -> !r.alive).thenComparing(r -> r.name, String.CASE_INSENSITIVE_ORDER));
		
		return rows;
	}
	
	private static String bossLine(BossRow row)
	{
		final String safeName = truncate(escape(row.name), 30);
		
		final String status;
		if (row.alive)
		{
			status = "<font color=9CC300><b>ALIVE</b></font>";
		}
		else
		{
			status = "<font color=FB5858><b>DEAD</b></font> <font color=b0b0b0>(" + RESPAWN_FMT.format(new Date(row.respawnTime)) + ")</font>";
		}
		
		final StringBuilder sb = new StringBuilder(512);
		sb.append("<table width=280 cellpadding=4 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=180 align=left>");
		sb.append("<a action=\"bypass -h voiced_shifffmodddrop ").append(row.npcId).append(" 1\">");
		sb.append("<font color=ffffff>").append(safeName).append("</font>");
		sb.append("</a>");
		sb.append("</td>");
		sb.append("<td width=90 align=right>").append(status).append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=280 height=1>");
		return sb.toString();
	}
	
	// =========================
	// Drop List UI
	// =========================
	private static void showNpcDropList(Player player, int npcId, int page)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
		if (template == null)
			return;
		
		if (template.getDropData().isEmpty())
		{
			player.sendMessage("This target have not drop info.");
			return;
		}
		
		final List<DropLine> lines = new ArrayList<>(256);
		
		final List<DropCategory> categories = new ArrayList<>();
		template.getDropData().forEach(categories::add);
		
		// Mantém seu reverse (se você queria a ordem “mais interessante primeiro”)
		Collections.reverse(categories);
		
		for (DropCategory cat : categories)
		{
			for (DropData drop : cat.getAllDrops())
			{
				final Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
				if (item == null)
					continue;
				
				final boolean sweep = cat.isSweep();
				final double chance = drop.getChance() / 10000.0;
				
				final long min = Math.round(Config.RATE_DROP_ITEMS * drop.getMinDrop());
				final long max = Math.round(Config.RATE_DROP_ITEMS * drop.getMaxDrop());
				
				lines.add(new DropLine(item.getItemId(), item.getName(), sweep, chance, min, max));
			}
		}
		
		final int maxPage = Math.max(1, (int) Math.ceil(lines.size() / (double) DROP_PAGE_SIZE));
		page = clamp(page, 1, maxPage);
		
		final int start = (page - 1) * DROP_PAGE_SIZE;
		final int end = Math.min(start + DROP_PAGE_SIZE, lines.size());
		
		final String npcName = template.getName();
		
		final StringBuilder sb = new StringBuilder(4096);
		sb.append("<html><title>Boss Drop</title><body>");
		
		sb.append(uiHeader("Drop List", "icon.etc_skull_black_i00", "Target: <font color=LEVEL>" + npcName).append(truncate(escape(""), 32)).append("</font>").toString());
		
		// ===== Search Bar =====
		sb.append("<table width=270 cellpadding=4 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=55><font color=b0b0b0>Search:</font></td>");
		
		// edit
		sb.append("<td width=150 align=left>");
		sb.append("<edit var=\"search\" width=150 height=15 length=24>");
		sb.append("</td>");
		
		// button
		sb.append("<td width=70 align=right>");
		sb.append("<button value=\"Find\" action=\"bypass -h voiced_dropfind ").append(npcId).append(" $search 1\" width=60 height=18 back=\"L2UI_CH3.smallbutton2\" fore=\"L2UI_CH3.smallbutton2\">");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		
		sb.append(pagination("voiced_shifffmodddrop " + npcId, page, maxPage));
		
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		
		if (lines.isEmpty())
		{
			sb.append(emptyState("No drops were found for this NPC."));
		}
		else
		{
			for (int i = start; i < end; i++)
				sb.append(dropCard(lines.get(i)));
		}
		
		sb.append("<table width=300 cellpadding=0 cellspacing=0>");
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Back\" action=\"bypass -h voiced_boss 1\" width=70 height=22 back=\"L2UI_CH3.smallbutton2\" fore=\"L2UI_CH3.smallbutton2\">");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</body></html>");
		
		final NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setHtml(sb.toString());
		player.sendPacket(htm);
	}
	
	private static String dropCard(DropLine d)
	{
		final String icon = IconTable.getIcon(d.itemId);
		final String name = truncate(escape(d.name), 34);
		
		final String typeTag = d.sweep ? "<font color=FF4FA3><b>SWEEP</b></font>" : "<font color=6CFF6C><b>DROP</b></font>";
		
		final String chance = formatChancePercent(d.chance);
		final String min = formatAmount(d.min);
		final String max = formatAmount(d.max);
		
		final StringBuilder sb = new StringBuilder(768);
		
		sb.append("<table width=300 cellpadding=4 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=36 valign=top>");
		sb.append("<button width=32 height=32 back=\"").append(icon).append("\" fore=\"").append(icon).append("\">");
		sb.append("</td>");
		
		sb.append("<td width=264>");
		sb.append("<table width=264 cellpadding=0 cellspacing=0>");
		sb.append("<tr><td>");
		sb.append("<font color=ffffff><b>").append(name).append("</b></font>");
		sb.append("</td></tr>");
		
		sb.append("<tr><td>");
		sb.append(typeTag);
		sb.append("<font color=b0b0b0>  •  Chance:</font> <font color=ffffff>").append(chance).append("</font>");
		sb.append("</td></tr>");
		
		sb.append("<tr><td>");
		sb.append("<font color=b0b0b0>Min:</font> <font color=00ECFF>").append(min).append("</font>");
		sb.append("<font color=b0b0b0>  Max:</font> <font color=FF6B6B>").append(max).append("</font>");
		sb.append("</td></tr>");
		
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		
		return sb.toString();
	}
	
	// =========================
	// UI Helpers
	// =========================
	private static StringBuilder uiHeader(String title, String icon, String subtitleHtml)
	{
		final StringBuilder sb = new StringBuilder(512);
		sb.append("<table width=300 cellpadding=6 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=32 valign=top>");
		sb.append("<img src=\"").append(icon).append("\" width=32 height=32>");
		sb.append("</td>");
		sb.append("<td width=268>");
		sb.append("<font color=FFD36B><b>").append(escape(title)).append("</b></font><br1>");
		sb.append("<font color=b0b0b0>").append(subtitleHtml).append("</font>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		return sb;
	}
	
	private static String sectionTitle(String title, int count)
	{
		final StringBuilder sb = new StringBuilder(256);
		sb.append("<table width=300 cellpadding=4 cellspacing=0 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td align=left>");
		sb.append("<font color=FF8C00><b>").append(escape(title)).append("</b></font> ");
		sb.append("<font color=b0b0b0>(").append(count).append(")</font>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	private static String emptyState(String msg)
	{
		final StringBuilder sb = new StringBuilder(256);
		sb.append("<table width=300 cellpadding=8 cellspacing=0 bgcolor=000000>");
		sb.append("<tr><td align=center><font color=b0b0b0>").append(escape(msg)).append("</font></td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		return sb.toString();
	}
	
	private static String pagination(String baseBypass, int page, int maxPage)
	{
		page = clamp(page, 1, Math.max(1, maxPage));
		
		final StringBuilder sb = new StringBuilder(256);
		sb.append("<br>");
		sb.append("<table width=280 cellpadding=2 cellspacing=0>");
		sb.append("<tr>");
		
		// Prev
		sb.append("<td width=60 align=left>");
		if (page > 1)
			sb.append("<a action=\"bypass -h ").append(baseBypass).append(" ").append(page - 1).append("\"><font color=ffffff>Prev</font></a>");
		else
			sb.append("<font color=404040>Prev</font>");
		sb.append("</td>");
		
		// Center: ONLY current page (no window)
		sb.append("<td width=160 align=center nowrap>");
		sb.append("<font color=b0b0b0>Page </font>");
		sb.append("<font color=FFD36B><b>[").append(page).append("]</b></font>");
		sb.append("<font color=b0b0b0> / </font>");
		sb.append("<font color=ffffff>").append(maxPage).append("</font>");
		sb.append("</td>");
		
		// Next
		sb.append("<td width=60 align=right>");
		if (page < maxPage)
			sb.append("<a action=\"bypass -h ").append(baseBypass).append(" ").append(page + 1).append("\"><font color=ffffff>Next »</font></a>");
		else
			sb.append("<font color=404040>Next</font>");
		sb.append("</td>");
		
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	// =========================
	// Formatting / Safety
	// =========================
	private static String formatChancePercent(double chance)
	{
		// chance is [0..100] in your display? Actually drop.getChance()/10000 => percent already (e.g., 25.0)
		// In your old code you printed drops + "%" directly, keep same semantics.
		// If your drop chance is 0.25 meaning 0.25%, adjust here. For aCis, drop.getChance() is usually out of 1_000_000.
		// You are dividing by 10000, which produces "percent-like". We'll keep it and just format cleanly.
		if (chance <= 0.001)
			return DF_SMALL.format(chance) + "%";
		if (chance <= 0.01)
			return DF_MED.format(chance) + "%";
		return DF_BIG.format(chance) + "%";
	}
	
	private static String formatAmount(long value)
	{
		if (value >= 1_000_000_000L)
			return new DecimalFormat("###.#").format(value / 1_000_000_000.0) + "B";
		if (value >= 1_000_000L)
			return new DecimalFormat("###.#").format(value / 1_000_000.0) + "KK";
		if (value >= 1_000L)
			return new DecimalFormat("###.#").format(value / 1_000.0) + "K";
		return Long.toString(value);
	}
	
	private static int clamp(int v, int min, int max)
	{
		return Math.max(min, Math.min(max, v));
	}
	
	private static String truncate(String s, int max)
	{
		if (s == null)
			return "";
		if (s.length() <= max)
			return s;
		return s.substring(0, max - 3) + "...";
	}
	
	private static String escape(String s)
	{
		if (s == null)
			return "";
		// Minimal escape for L2 HTML
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
	
	// =========================
	// Models
	// =========================
	private static final class BossRow
	{
		final int npcId;
		final String name;
		final boolean alive;
		final long respawnTime;
		
		BossRow(int npcId, String name, boolean alive, long respawnTime)
		{
			this.npcId = npcId;
			this.name = name;
			this.alive = alive;
			this.respawnTime = respawnTime;
		}
	}
	
	private static final class DropLine
	{
		final int itemId;
		final String name;
		final boolean sweep;
		final double chance;
		final long min;
		final long max;
		
		DropLine(int itemId, String name, boolean sweep, double chance, long min, long max)
		{
			this.itemId = itemId;
			this.name = name;
			this.sweep = sweep;
			this.chance = chance;
			this.min = min;
			this.max = max;
		}
	}
}
