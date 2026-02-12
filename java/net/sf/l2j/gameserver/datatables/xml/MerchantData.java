package net.sf.l2j.gameserver.datatables.xml;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.MerchantGroupKey;
import net.sf.l2j.gameserver.model.holder.MerchantIntHolder;
import net.sf.l2j.gameserver.model.holder.MerchantProductionHolder;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.templates.StatsSet;

import org.w3c.dom.Document;

public class MerchantData implements IXmlReader
{
	private final HashMap<MerchantGroupKey, List<MerchantProductionHolder>> _groups = new HashMap<>();
	
	public MerchantData()
	{
		load();
	}
	
	public void reload()
	{
		_groups.clear();
		load();
	}
	
	@Override
	public void load()
	{
		_groups.clear();
		parseDirectory("./data/xml/custom/merchant");
		int total = _groups.values().stream().mapToInt(List::size).sum();
		
		LOGGER.info("Loaded {" + total + "} Merchant productions in {" + _groups.size() + "} groups.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "merchantList", listNode -> {
			final StatsSet listAttrs = parseAttributes(listNode);
			
			final String category = listAttrs.getString("category", "none");
			final String grade = listAttrs.getString("grade", "NONE");
			
			final MerchantGroupKey key = new MerchantGroupKey(category, grade);
			final List<MerchantProductionHolder> list = _groups.computeIfAbsent(key, k -> new ArrayList<>());
			
			forEach(listNode, "merchant", prodNode -> {
				final StatsSet prodAttrs = parseAttributes(prodNode);
				list.add(new MerchantProductionHolder(prodAttrs));
			});
		});
	}
	
	public List<MerchantProductionHolder> getProductions(String category, String grade)
	{
		final List<MerchantProductionHolder> list = _groups.get(new MerchantGroupKey(category, grade));
		return (list == null) ? Collections.emptyList() : list;
	}
	
	public MerchantProductionHolder getProduction(String category, String grade, int index)
	{
		final List<MerchantProductionHolder> list = getProductions(category, grade);
		if (index < 0 || index >= list.size())
			return null;
		return list.get(index);
	}
	
	// Paginação pronta: 5 por página (ou qualquer pageSize)
	public List<MerchantProductionHolder> getPage(String category, String grade, int page, int pageSize)
	{
		final List<MerchantProductionHolder> list = getProductions(category, grade);
		if (list.isEmpty())
			return Collections.emptyList();
		
		final int safeSize = Math.max(1, pageSize);
		final int maxPage = (list.size() - 1) / safeSize;
		final int p = Math.max(0, Math.min(page, maxPage));
		
		final int from = p * safeSize;
		final int to = Math.min(from + safeSize, list.size());
		
		return list.subList(from, to);
	}
	
	public int getMaxPage(String category, String grade, int pageSize)
	{
		final List<MerchantProductionHolder> list = getProductions(category, grade);
		if (list.isEmpty())
			return 0;
		final int safeSize = Math.max(1, pageSize);
		return (list.size() - 1) / safeSize;
	}
	
	public void buy(Player activeChar, String category, String grade, int page, int index)
	{
		MerchantProductionHolder p = MerchantData.getInstance().getProduction(category, grade, index);
		if (p == null)
		{
			showList(activeChar, category, grade, page);
			return;
		}
		
		// 1) checar ingredientes
		for (MerchantIntHolder ing : p.getIngredients())
		{
			if (activeChar.getInventory().getInventoryItemCount(ing.getId(), -1) < ing.getValue())
			{
				activeChar.sendMessage("Voce nao tem os ingredientes necessarios.");
				showDetail(activeChar, category, grade, page, index);
				return;
			}
		}
		
		// 2) consumir ingredientes
		for (MerchantIntHolder ing : p.getIngredients())
			activeChar.destroyItemByItemId("MerchantBuy", ing.getId(), ing.getValue(), null, true);
		
		// 3) dar produto
		int productId = p.getProduct().getId();
		int amount = p.getProduct().getValue();
		
		var item = activeChar.addItem("MerchantBuy", productId, amount, null, true);
		
		if (item != null && amount == 1 && p.getEnchantLevel() > 0)
			item.setEnchantLevel(p.getEnchantLevel());
		
		showDetail(activeChar, category, grade, page, index);
	}
	
	public void showDetail(Player activeChar, String category, String grade, int page, int index)
	{
		MerchantProductionHolder p = MerchantData.getInstance().getProduction(category, grade, index);
		if (p == null)
		{
			showList(activeChar, category, grade, page);
			return;
		}
		
		final int itemId = p.getProduct().getId();
		final String icon = IconTable.getIcon(itemId);
		String itemName = ItemTable.getInstance().getTemplate(itemId).getName();
		
		if (itemName.length() > 32)
			itemName = itemName.substring(0, 32) + "...";
		
		StringBuilder detail = new StringBuilder(1024);
		
		// ===== CARD PRINCIPAL =====
		detail.append("<table width=300 bgcolor=000000 cellpadding=6 cellspacing=0>");
		
		// Produto (icon + nome)
		// ===== PRODUTO (layout igual ingrediente) =====
		detail.append("<tr><td colspan=2>");
		detail.append("<table width=294 cellpadding=0 cellspacing=0>");
		detail.append("<tr>");
		
		detail.append("<td width=40 height=40 valign=middle align=center>");
		detail.append("<img src=\"").append(icon).append("\" width=32 height=32>");
		detail.append("</td>");
		
		detail.append("<td width=254 height=40 valign=middle>");
		detail.append("  <font color=LEVEL><br>").append(itemName).append("</font>");
		if (p.getEnchantLevel() > 0)
			detail.append(" <font color=LEVEL>(+").append(p.getEnchantLevel()).append(")</font>");
		detail.append("</td>");
		
		detail.append("</tr>");
		detail.append("</table>");
		detail.append("</td></tr>");
		
		// Separador
		detail.append("<tr><td colspan=2><img src=\"L2UI.SquareGray\" width=300 height=1></td></tr>");
		
		// ===== CUSTO =====
		detail.append("<tr>");
		detail.append("<td colspan=2>");
		detail.append("<font color=A8B0C0>Custo:</font><br1>");
		
		if (p.getIngredients().isEmpty())
		{
			detail.append("<font color=99FF66>Gratis</font>");
		}
		else
		{
			detail.append("<table width=294 cellpadding=0 cellspacing=0>");
			
			for (MerchantIntHolder ing : p.getIngredients())
			{
				final int ingId = ing.getId();
				final int need = ing.getValue();
				final long have = activeChar.getInventory().getInventoryItemCount(ingId, -1);
				
				final String needsnow = formatAmount(need);
				final String havesnow = formatAmount(have);
				
				final String ingIcon = IconTable.getIcon(ingId);
				String ingName = ItemTable.getInstance().getTemplate(ingId).getName();
				
				if (ingName.length() > 19)
					ingName = ingName.substring(0, 16) + "...";
				
				final String color = (have >= need) ? "99FF66" : "FF5555";
				
				detail.append("<tr>");
				
				// ícone
				detail.append("<td width=40 height=40 valign=middle align=center>");
				detail.append("<img src=\"").append(ingIcon).append("\" width=32 height=32>");
				detail.append("</td>");
				
				// nome + have/need embaixo (com <br1>)
				detail.append("<td width=254 height=40 valign=middle>");
				detail.append("<font color=E6EAF2>").append(ingName).append("</font><br1>");
				boolean ok = have >= need;
				
				detail.append("<font color=A8B0C0>TEM:</font> ");
				detail.append("<font color=E6EAF2>").append(havesnow).append("</font>");
				detail.append(" <font color=A8B0C0>| PRECISA:</font> ");
				detail.append("<font color=").append(color).append(">").append(needsnow).append("</font>");
				
				detail.append(" <font color=A8B0C0>|</font> ");
				detail.append(ok ? "<font color=99FF66>OK</font>" : "<font color=FF5555>FALTA</font>");
				
				detail.append("</td>");
				
				detail.append("</tr>");
			}
			
			detail.append("</table>");
			
		}
		
		detail.append("</td>");
		detail.append("</tr>");
		
		detail.append("</table>");
		
		String buyBypass = "bypass merchant buy " + category + " " + grade + " " + page + " " + index;
		String backBypass = "bypass merchant action " + category + " " + grade + " " + page;
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/merchant/show.htm");
		html.replace("%DETAIL%", detail.toString());
		html.replace("%BUY%", buyBypass);
		html.replace("%BACK%", backBypass);
		activeChar.sendPacket(html);
	}
	
	public void showList(Player activeChar, String category, String grade, int page)
	{
		final int pageSize = 7;
		final int maxPage = MerchantData.getInstance().getMaxPage(category, grade, pageSize);
		
		// clamp page
		if (page < 0)
			page = 0;
		if (page > maxPage)
			page = maxPage;
		
		final List<MerchantProductionHolder> pageList = MerchantData.getInstance().getPage(category, grade, page, pageSize);
		
		// para calcular índice global
		final int from = page * pageSize;
		
		StringBuilder items = new StringBuilder();
		for (int i = 0; i < pageList.size(); i++)
		{
			int globalIndex = from + i;
			items.append(buildRow(category, grade, page, globalIndex, pageList.get(i)));
		}
		
		String navi = buildNavi(category, grade, page, maxPage);
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/merchant/list.htm");
		html.replace("%ITEMS%", items.toString());
		html.replace("%NAVI%", navi);
		html.replace("%BACK%", "bypass merchant chat 55500");
		activeChar.sendPacket(html);
	}
	
	private static String buildRow(String category, String grade, int page, int globalIndex, MerchantProductionHolder p)
	{
		int itemId = p.getProduct().getId();
		String icon = IconTable.getIcon(itemId); // IconTable.getIcon(id)
		
		// Nome do item (ajuste conforme seu projeto: ItemTable / ItemData etc.)
		String itemName = ItemTable.getInstance().getTemplate(itemId).getName();
		
		if (itemName.length() > 19)
			itemName = itemName.substring(0, 19);
		
		// Enchant opcional
		String ench = p.getEnchantLevel() > 0 ? (" <font color=LEVEL>(+" + p.getEnchantLevel() + ")</font>") : "";
		
		String showBypass = "bypass merchant show " + category + " " + grade + " " + page + " " + globalIndex;
		
		return "" + "<table width=300 bgcolor=000000 cellpadding=0 cellspacing=0>" + "<tr>" + "<td width=40 height=40><img src=\"" + icon + "\" width=30 height=30></td>" + "<td width=2 height=2><img src=\"L2UI.SquareBlank\" width=2 height=2></td>" + " " + "<td width=180 height=15>" + itemName + ench + "<br1><font color=A8B0C0>" + "" + "</font></td>" + "<td width=125 height=40><button value=\"VER\" action=\"" + showBypass + "\" width=90 height=25 back=\"anim90.Anim\" fore=\"anim90.Anim\"></td>" + "</tr>" + "</table>";
	}
	
	private static String buildNavi(String category, String grade, int page, int maxPage)
	{
		int pageHuman = page + 1;
		int maxHuman = maxPage + 1;
		
		String prev = page > 0 ? "<button value=\"<<\" action=\"bypass merchant action " + category + " " + grade + " " + (page - 1) + "\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">" : "<button value=\"<<\" action=\"\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">";
		
		String next = page < maxPage ? "<button value=\">>\" action=\"bypass merchant action " + category + " " + grade + " " + (page + 1) + "\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">" : "<button value=\">>\" action=\"\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">";
		
		return "" + "<center><table width=300 cellpadding=0 cellspacing=0>" + "<tr>" + "<td align=left>" + prev + "</td>" + "<td align=center><font color=LEVEL>Page [" + pageHuman + "] / [" + maxHuman + "]</font></td>" + "<td align=right>" + next + "</td>" + "</tr>" + "</table></center>";
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
	
	public static MerchantData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MerchantData INSTANCE = new MerchantData();
	}
}
